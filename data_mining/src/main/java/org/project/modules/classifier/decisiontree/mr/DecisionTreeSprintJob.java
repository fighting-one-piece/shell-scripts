package org.project.modules.classifier.decisiontree.mr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
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
		String hdfsPath = null;
		try {
			FileSystem fs = input.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, input);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			data = DataLoader.load(fsInputStream, true);
			DataHandler.fill(data, 0);
			Map<String, List<Instance>> path2Instances = DataHandler.splitData(data);
			for (String tmpPath : path2Instances.keySet()) {
				System.out.println(tmpPath);
				String name = tmpPath.substring(tmpPath.lastIndexOf(File.separator) + 1);
				hdfsPath = HDFSUtils.HDFS_URL + "dt/temp/" + name;
				HDFSUtils.copyFromLocalFile(conf, tmpPath, hdfsPath);
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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hdfsPath;
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
					if (value.isCategory()) {
						System.out.println("attr: " + value.getAttribute());
						System.out.println("gini: " + gini);
					}
					if (gini <= minSplitPointGini) {
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
	public Object build(String input, Data data) {
		List<Instance> instances = data.getInstances();
		if (instances.size() == 1) {
			return instances.get(0).getCategory();
		} else if (instances.size() > 1) {
			boolean isEqual = true;
			Object category = instances.get(0).getCategory();
			for (Instance instance : instances) {
				if (!category.equals(instance.getCategory())) {
					isEqual = false;
				}
			}
			if (isEqual) return category;
		}
		String output = HDFSUtils.HDFS_URL + "dt/temp/output";
		try {
			HDFSUtils.delete(conf, new Path(output));
			System.out.println("delete path : " + output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] args = new String[]{input, output};
		DecisionTreeSprintMR.main(args);
		
		AttributeGiniWritable bestAttr = chooseBestAttribute(output);
		String attribute = bestAttr.getAttribute();
		System.out.println("best attribute: " + attribute);
		System.out.println("isCategory: " + bestAttr.isCategory());
		if (bestAttr.isCategory()) {
			return attribute;
		}
		TreeNode treeNode = new TreeNode(attribute);
		String splitPoint = bestAttr.getSplitPoint();
		String[] attributes = data.getAttributesExcept(attribute);
		Set<String> attributeValues = attrName2Values.get(attribute);
		attributeValues.remove(splitPoint);
		StringBuilder sb = new StringBuilder();
		for (String attributeValue : attributeValues) {
			sb.append(attributeValue).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		String[] names = new String[]{splitPoint, sb.toString()};
		Map<String, List<Instance>> path2Instances = DataHandler.splitData(
				new Data(data.getInstances(), attribute, names));
		int index = 0;
		for (Map.Entry<String, List<Instance>> entry : path2Instances.entrySet()) {
			List<Instance> splitInstances = entry.getValue();
			if (splitInstances.size() == 0) {
				continue;
			}
			ShowUtils.print(instances);
			String path = entry.getKey();
			String name = path.substring(path.lastIndexOf(File.separator) + 1);
			String hdfsPath = HDFSUtils.HDFS_URL + "dt/temp/" + name;
			HDFSUtils.copyFromLocalFile(conf, path, hdfsPath);
			treeNode.setChild(names[index++], build(hdfsPath, 
					new Data(attributes, splitInstances)));
		}
		return treeNode;
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
			Object[] results = (Object[]) treeNode.classifySprint(testData);
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
			TreeNode treeNode = (TreeNode) build(input, data);
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
