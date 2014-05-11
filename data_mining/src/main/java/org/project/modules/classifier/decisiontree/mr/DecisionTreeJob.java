package org.project.modules.classifier.decisiontree.mr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeRWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeJob {
	
	private Configuration conf = null;
	
	private Data data = null;
	
	static {
//		conf = new Configuration();
//		conf.addResource(new Path("D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
	}
	
	/**
	 * 对数据集做预处理
	 * @param input
	 * @return
	 */
	public String prepare(Path input) {
		String path = null;
		try {
			FileSystem fs = input.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, input);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			data = DataLoader.load(fsInputStream, true);
			DataHandler.fill(data, 0);
			String[] tmpPaths = splitDataSet(data.getAttributes(), null);
			System.out.println(tmpPaths[0]);
			String name = tmpPaths[0].substring(tmpPaths[0].lastIndexOf(File.separator) + 1);
			path = HDFSUtils.HDFS_URL + "dt/temp/" + name;
			HDFSUtils.copyFromLocalFile(conf, tmpPaths[0], path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/**
	 * 分割数据集
	 * @param attribute
	 * @param splitPoints
	 * @return
	 */
	public String[] splitDataSet(String[] attributes, String[] splitPoints) {
		String os = System.getProperty("os.name").toLowerCase();
		Set<String> attributeSet = new HashSet<String>();
		for (String attribute : attributes) {
			attributeSet.add(attribute);
		}
		System.out.println("os : " + os);
		String tmpPath = null;
		if (os.contains("windows")) {
			tmpPath = System.getProperty("java.io.tmpdir");
		} else if (os.contains("linux")) {
			tmpPath = System.getProperty("user.home") + File.separator 
					+ "temp" + File.separator;
		}
		System.out.println("tmpPath: " + tmpPath);
		String[] paths = new String[null == splitPoints || 
				splitPoints.length == 0 ? 1 : splitPoints.length];
		String path = null;
		for (int i = 0, len = paths.length; i < len; i++) {
			path = tmpPath + "data_" + i + ".txt";
			paths[i] = path;
			OutputStream out = null;
			BufferedWriter writer = null;
			try {
				File file = new File(path);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdir();
				}
				out = new FileOutputStream(file);
				writer = new BufferedWriter(new OutputStreamWriter(out));
				StringBuilder sb = null;
				for (Instance instance : data.getInstances()) {
					sb = new StringBuilder();
					sb.append(instance.getId()).append("\t");
					sb.append(instance.getCategory()).append("\t");
					boolean isWrite = false;
					for (Map.Entry<String, Object> entry : 
						instance.getAttributes().entrySet()) {
						String attr = entry.getKey();
						Object attrValue = entry.getValue();
						if (null != splitPoints && splitPoints.length != 0
								&& splitPoints[i].equals(attrValue)) {
							isWrite = true;
						}
//						if (null != attribute && attribute.equals(attr)) {
//							continue;
//						}
						if (!attributeSet.contains(attr)) {
							continue;
						}
						sb.append(attr).append(":");
						sb.append(attrValue).append("\t");
					}
					if (isWrite || null == splitPoints) {
						writer.write(sb.toString());
						writer.newLine();
					}
				}
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(writer);
			}
		}
		return paths;
	}
	
	/**
	 * 选择最佳属性
	 * @param output
	 * @return
	 */
	public AttributeRWritable chooseBestAttribute(String output) {
		AttributeRWritable maxAttribute = null;
		Path path = new Path(output);
		try {
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			ShowUtils.print(paths);
			List<AttributeRWritable> values = 
					new ArrayList<AttributeRWritable>();
			SequenceFile.Reader reader = null;
			for (Path p : paths) {
				reader = new SequenceFile.Reader(fs, p, conf);
				Text key = (Text) ReflectionUtils.newInstance(
						reader.getKeyClass(), conf);
				AttributeRWritable value = new AttributeRWritable();
				while (reader.next(key, value)) {
					values.add(value);
					value = new AttributeRWritable();
				}
				IOUtils.closeQuietly(reader);
			}
			double maxGainRatio = 0.0;
			for (AttributeRWritable attribute : values) {
				double gainRatio = attribute.getGainRatio();
				if (gainRatio >= maxGainRatio) {
					maxGainRatio = gainRatio;
					maxAttribute = attribute;
				}
			}
			System.out.println("output: " + path.toString());
			HDFSUtils.delete(conf, path);
			System.out.println("hdfs delete file : " + path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return maxAttribute;
	}
	
	/**
	 * 构造决策树
	 * @param input
	 * @return
	 */
	public Object build(String input, String[] attributes) {
		String output = HDFSUtils.HDFS_URL + "dt/temp/output";
		try {
			HDFSUtils.delete(conf, new Path(output));
			System.out.println("delete path : " + output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] paths = new String[]{input, output};
		DecisionTreeMR.main(paths);
		
		AttributeRWritable bestAttr = chooseBestAttribute(output);
		System.out.println("best attribute: " + bestAttr.getAttribute());
		String[] splitPoints = bestAttr.obtainSplitPoints();
		ShowUtils.print(splitPoints);
		if (null != splitPoints && splitPoints.length == 1) {
			return splitPoints[0];
		}
		String attribute = bestAttr.getAttribute();
		TreeNode treeNode = new TreeNode(attribute);
		String[] subAttributes = new String[attributes.length - 1];
		for (int i = 0, j = 0; i < attributes.length; i++) {
			if (!attribute.equals(attributes[i])) {
				subAttributes[j++] = attributes[i];
			}
		}
		if (subAttributes.length == 0) {
			return null;
		}
		String[] tmpPaths = splitDataSet(subAttributes, splitPoints);
		for (int i = 0, len = tmpPaths.length; i < len; i++) {
			String name = tmpPaths[0].substring(tmpPaths[i].lastIndexOf(File.separator) + 1);
			String hdfsPath = HDFSUtils.HDFS_URL + "dt/temp/" + name;
			HDFSUtils.copyFromLocalFile(conf, tmpPaths[i], hdfsPath);
			treeNode.setChild(splitPoints[i], build(hdfsPath, subAttributes));
		}
		return treeNode;
	}
	
	public void run(String[] args) {
		try {
			if (null == conf) conf = new Configuration();
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			String input = prepare(new Path(inputArgs[0]));
			TreeNode treeNode = (TreeNode) build(input, data.getAttributes());
			TreeNodeHelper.print(treeNode, 0, null);
			Object result = treeNode.classify(data);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeJob job = new DecisionTreeJob();
		job.run(args);
	}

}
