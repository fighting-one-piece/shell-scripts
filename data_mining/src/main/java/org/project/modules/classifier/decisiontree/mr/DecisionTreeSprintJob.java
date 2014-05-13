package org.project.modules.classifier.decisiontree.mr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
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
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGiniWritable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

public class DecisionTreeSprintJob {
	
	private Configuration conf = null;
	
	private Data data = null;
	
	private Map<String, Set<String>> attrName2Values = null;
	
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
			String[] tmpPaths = DataHandler.splitDataSet(
					data, data.getAttributes(), null);
			System.out.println(tmpPaths[0]);
			String name = tmpPaths[0].substring(tmpPaths[0].lastIndexOf(File.separator) + 1);
			path = HDFSUtils.HDFS_URL + "dt/temp/" + name;
			HDFSUtils.copyFromLocalFile(conf, tmpPaths[0], path);
			attrName2Values = new HashMap<String, Set<String>>();
			for (Instance instance : data.getInstances()) {
				for (Map.Entry<String, Object> entry : 
					instance.getAttributes().entrySet()) {
					String attrName = entry.getKey();
					Set<String> values = attrName2Values.get(attrName);
					if (null == values) {
						values = new HashSet<String>();
						attrName2Values.put(attrName, values);
					}
					values.add(String.valueOf(entry.getValue()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/**
	 * 选择最佳属性
	 * @param output
	 * @return
	 */
	public AttributeGiniWritable chooseBestAttribute(String output) {
		AttributeGiniWritable minSplitAttribute = null;
		Path path = new Path(output);
		try {
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			ShowUtils.print(paths);
			double minSplitPointGini = 1.0;
			SequenceFile.Reader reader = null;
			for (Path p : paths) {
				reader = new SequenceFile.Reader(fs, p, conf);
				Text key = (Text) ReflectionUtils.newInstance(
						reader.getKeyClass(), conf);
				AttributeGiniWritable value = new AttributeGiniWritable();
				while (reader.next(key, value)) {
					double gini = value.getGini();
					if (gini < minSplitPointGini) {
						minSplitPointGini = gini;
						minSplitAttribute = value;
					}
					value = new AttributeGiniWritable();
				}
				IOUtils.closeQuietly(reader);
			}
			System.out.println("output: " + path.toString());
			HDFSUtils.delete(conf, path);
			System.out.println("hdfs delete file : " + path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return minSplitAttribute;
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
		DecisionTreeSprintMR.main(paths);
		
		AttributeGiniWritable bestAttr = chooseBestAttribute(output);
		String attribute = bestAttr.getAttribute();
		System.out.println("best attribute: " + attribute);
		System.out.println("isCategory: " + bestAttr.isCategory());
		if (bestAttr.isCategory()) {
			return attribute;
		}
		
		
//		String[] splitPoints = bestAttr.obtainSplitPoints();
//		System.out.print("splitPoints: ");
//		ShowUtils.print(splitPoints);
//		TreeNode treeNode = new TreeNode(attribute);
//		String[] subAttributes = new String[attributes.length - 1];
//		for (int i = 0, j = 0; i < attributes.length; i++) {
//			if (!attribute.equals(attributes[i])) {
//				subAttributes[j++] = attributes[i];
//			}
//		}
//		System.out.print("subAttributes: ");
//		ShowUtils.print(subAttributes);
//		String[] tmpPaths = DataHandler.splitDataSet(
//				data, subAttributes, splitPoints);
//		for (int i = 0, len = tmpPaths.length; i < len; i++) {
//			String name = tmpPaths[0].substring(tmpPaths[i].lastIndexOf(File.separator) + 1);
//			String hdfsPath = HDFSUtils.HDFS_URL + "dt/temp/" + name;
//			HDFSUtils.copyFromLocalFile(conf, tmpPaths[i], hdfsPath);
//			treeNode.setChild(splitPoints[i], build(hdfsPath, subAttributes));
//		}
//		return treeNode;
		return null;
	}
	
	private void classify(TreeNode treeNode, Path testSetPath, 
			String output) {
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			FileSystem fs = testSetPath.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, testSetPath);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			Data testData = DataLoader.load(fsInputStream, true);
			DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
			Object[] results = (Object[]) treeNode.classify(testData);
			String path = FileUtils.obtainRandomTxtPath();
			out = new FileOutputStream(new File(path));
			writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			for (int i = 0, len = results.length; i < len; i++) {
				sb = new StringBuilder();
				sb.append(i+1).append("\t").append(results[i]);
				writer.write(sb.toString());
				writer.newLine();
			}
			writer.flush();
			Path outputPath = new Path(output);
			fs = outputPath.getFileSystem(conf);
			if (!fs.exists(outputPath)) {
				fs.mkdirs(outputPath);
			}
			String name = path.substring(path.lastIndexOf(File.separator) + 1);
			HDFSUtils.copyFromLocalFile(conf, path, output + 
					File.separator + name);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
		}
	}
	
	public void run(String[] args) {
		try {
			if (null == conf) conf = new Configuration();
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error, please input three path.");
				System.out.println("1. trainset path.");
				System.out.println("2. testset path.");
				System.out.println("3. result output path.");
				System.exit(2);
			}
			String input = prepare(new Path(inputArgs[0]));
			TreeNode treeNode = (TreeNode) build(input, data.getAttributes());
			TreeNodeHelper.print(treeNode, 0, null);
			String testSetPath = inputArgs[1];
			String output = inputArgs[2];
			classify(treeNode, new Path(testSetPath), output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeSprintJob job = new DecisionTreeSprintJob();
		job.run(args);
	}

}
