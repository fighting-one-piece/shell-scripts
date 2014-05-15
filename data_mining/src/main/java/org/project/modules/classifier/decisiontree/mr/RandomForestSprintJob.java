package org.project.modules.classifier.decisiontree.mr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.utils.FileUtils;
import org.project.utils.HDFSUtils;
import org.project.utils.ShowUtils;

public class RandomForestSprintJob extends AbstractJob {
	
	private Data data = null;
	
	public void prepare(Path input) {
		try {
			FileSystem fs = input.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, input);
			FSDataInputStream fsInputStream = fs.open(hdfsPaths[0]);
			data = DataLoader.load(fsInputStream, true);
			DataHandler.fill(data, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String prepareRandom(int attributeNum) {
		Data randomData = DataLoader.loadRandom(data, attributeNum);
		String[] tmpPaths = DataHandler.splitMultiDataSet(
				randomData, randomData.getAttributes(), null);
		System.out.println(tmpPaths[0]);
		String name = tmpPaths[0].substring(tmpPaths[0].lastIndexOf(File.separator) + 1);
		String path = HDFSUtils.HDFS_URL + "dt/temp/" + name;
		HDFSUtils.copyFromLocalFile(conf, tmpPaths[0], path);
		return path;
	}
	
	private void vote(String output) {
		try {
			Path path = new Path(output);
			FileSystem fs = path.getFileSystem(conf);
			Path[] paths = HDFSUtils.getPathFiles(fs, path);
			ShowUtils.print(paths);
			Map<String, Map<String, Integer>> map = 
					new HashMap<String, Map<String, Integer>>();
			for (Path outPath : paths) {
				FSDataInputStream fsInputStream = fs.open(outPath);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fsInputStream));
				String line = reader.readLine();
				while (null != line) {
					String[] result = line.split("\t");
					String lineNum = result[0];
					Map<String, Integer> valueCount = map.get(lineNum);
					if (null == valueCount) {
						valueCount = new HashMap<String, Integer>();
						map.put(lineNum, valueCount);
					}
					Integer value = valueCount.get(result[1]);
					valueCount.put(result[1], null == value ? 1 : value + 1);
					line = reader.readLine();
				}
				IOUtils.closeQuietly(fsInputStream);
			}
			String fOut = FileUtils.obtainRandomTxtPath();
			OutputStream out = new FileOutputStream(new File(fOut));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
				sb = new StringBuilder();
				sb.append(entry.getKey()).append("\t");
				Map<String, Integer> valueCount = entry.getValue();
				int max = 0;
				String maxResult = null;
				for (Map.Entry<String, Integer> e : valueCount.entrySet()) {
					int v = e.getValue();
					if (v > max) {
						max = v;
						maxResult = e.getKey();
					}
				}
				sb.append(maxResult);
				writer.write(sb.toString());
				writer.newLine();
			}
			writer.flush();
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
			HDFSUtils.copyFromLocalFile(conf, fOut, output + 
					File.separator + "final_result.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(String[] args) {
		try {
			if (null == conf) conf = new Configuration();
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 5) {
				System.out.println("error, please input three path.");
				System.out.println("1. trainset path.");
				System.out.println("2. testset path.");
				System.out.println("3. result output path.");
				System.out.println("4. random tree number.");
				System.out.println("5. random attribute number.");
				System.exit(2);
			}
			int treeNum = Integer.parseInt(inputArgs[3]);
			int attributeNum = Integer.parseInt(inputArgs[4]);
			
			prepare(new Path(inputArgs[0]));
			String output = inputArgs[2];
			String[] dtArgs = new String[]{inputArgs[0], inputArgs[1], output};
			for (int i = 0; i < treeNum; i++) {
				String input = prepareRandom(attributeNum);
				dtArgs[0] = input;
				DecisionTreeSprintJob job = new DecisionTreeSprintJob();
				job.run(dtArgs);
			}
			vote(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		RandomForestSprintJob job = new RandomForestSprintJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}
}
