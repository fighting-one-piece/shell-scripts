package org.project.modules.classifier.decisiontree.mr.dt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.mr.AbstractJob;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeGiniWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeWritable;
import org.project.utils.HDFSUtils;
import org.project.utils.IdentityUtils;

public class DecisionTreeSprintJob extends AbstractJob {
	
	private List<String> split(String input) {
		List<String> paths = new ArrayList<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			Path inputPath = new Path(input);
			FileSystem fs = inputPath.getFileSystem(conf);
			Path[] hdfsPaths = HDFSUtils.getPathFiles(fs, inputPath);
			in = fs.open(hdfsPaths[0]);
			reader = new BufferedReader(new InputStreamReader(in));
			List<String> lines = new ArrayList<String>();
			int index = 0;
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				if ((index++) == 10000) {
					paths.add(writeToHDFS(lines));
					lines = new ArrayList<String>();
					index = 0;
				}
				lines.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return paths;
	}
	
	private String writeToHDFS(List<String> lines) {
		List<Instance> instances = new ArrayList<Instance>();
		Set<String> attributes = new HashSet<String>();
		for (String line : lines) {
			Instance instance = DataHandler.extractWithId(line, attributes);
			instances.add(instance);
		}
		Data data = new Data(attributes.toArray(new String[0]), instances);
		DataHandler.computeFill(data, 1.0);
		OutputStream out = null;
		BufferedWriter writer = null;
		String output = HDFSUtils.HDFS_TEMP_DATA_URL + IdentityUtils.generateUUID();
		try {
			Path outputPath = new Path(output);
			FileSystem fs = outputPath.getFileSystem(conf);
			out = fs.create(outputPath);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			StringBuilder sb = null;
			for (Instance instance : data.getInstances()) {
				sb = new StringBuilder();
				sb.append(instance.getId()).append("\t");
				sb.append(instance.getCategory()).append("\t");
				Map<String, Object> attrs = instance.getAttributes();
				for (Map.Entry<String, Object> entry : attrs.entrySet()) {
					sb.append(entry.getKey()).append(":");
					sb.append(entry.getValue()).append("\t");
				}
				writer.write(sb.toString());
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
		}
		return output;
	}
	
	private Job createJob(String jobName, String input, String output) {
		Configuration conf = new Configuration();
		Job job = null;
		try {
			job = new Job(conf, jobName);
			
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			
			job.setJarByClass(DecisionTreeSprintJob.class);
			
			job.setMapperClass(ComputeGiniMapper.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(AttributeWritable.class);
			
			job.setReducerClass(ComputeGiniReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(AttributeGiniWritable.class);
			
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return job;
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
			List<String> paths = split(inputArgs[0]);
			JobControl jobControl = new JobControl("ComputeGini");
			for (String path : paths) {
				System.out.println("split path: " + path);
				String output = null;
				ControlledJob controlledJob = new ControlledJob(conf);
				controlledJob.setJob(createJob(path, path, output));
				jobControl.addJob(controlledJob);
			}
			Thread jcThread = new Thread(jobControl);  
	        jcThread.start();  
	        while(true){  
	            if(jobControl.allFinished()){  
	                System.out.println(jobControl.getSuccessfulJobList());  
	                jobControl.stop();  
	            }  
	            if(jobControl.getFailedJobList().size() > 0){  
	                System.out.println(jobControl.getFailedJobList());  
	                jobControl.stop();  
	            }  
	        }  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DecisionTreeSprintJob job = new DecisionTreeSprintJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}

}

class ComputeGiniMapper extends Mapper<LongWritable, Text, Text, AttributeWritable> {

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		Long id = Long.parseLong(tokenizer.nextToken());
		String category = tokenizer.nextToken();
		boolean isCategory = true;
		while (tokenizer.hasMoreTokens()) {
			isCategory = false;
			String attribute = tokenizer.nextToken();
			String[] entry = attribute.split(":");
			context.write(new Text(entry[0]), new AttributeWritable(id,
					category, entry[1]));
		}
		if (isCategory) {
			context.write(new Text(category), new AttributeWritable(id,
					category, category));
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}
}

class ComputeGiniReducer extends Reducer<Text, AttributeWritable, Text, AttributeGiniWritable> {

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
	}

	@Override
	protected void reduce(Text key, Iterable<AttributeWritable> values,
			Context context) throws IOException, InterruptedException {
		String attributeName = key.toString();
		double totalNum = 0.0;
		Map<String, Map<String, Integer>> attrValueSplits = new HashMap<String, Map<String, Integer>>();
		Set<String> splitPoints = new HashSet<String>();
		Iterator<AttributeWritable> iterator = values.iterator();
		boolean isCategory = false;
		while (iterator.hasNext()) {
			AttributeWritable attribute = iterator.next();
			String attributeValue = attribute.getAttributeValue();
			if (attributeName.equals(attributeValue)) {
				isCategory = true;
				break;
			}
			splitPoints.add(attributeValue);
			Map<String, Integer> attrValueSplit = attrValueSplits
					.get(attributeValue);
			if (null == attrValueSplit) {
				attrValueSplit = new HashMap<String, Integer>();
				attrValueSplits.put(attributeValue, attrValueSplit);
			}
			String category = attribute.getCategory();
			Integer categoryNum = attrValueSplit.get(category);
			attrValueSplit.put(category, null == categoryNum ? 1
					: categoryNum + 1);
			totalNum++;
		}
		if (isCategory) {
			System.out.println("is Category");
			double initValue = 1.0;
			iterator = values.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				initValue = initValue / 2;
			}
			System.out.println("initValue: " + initValue);
			context.write(key, new AttributeGiniWritable(attributeName,
					initValue, true, null));
		} else {
			String minSplitPoint = null;
			double minSplitPointGini = 1.0;
			for (String splitPoint : splitPoints) {
				double splitPointGini = 0.0;
				double splitAboveNum = 0.0;
				double splitBelowNum = 0.0;
				Map<String, Integer> attrBelowSplit = new HashMap<String, Integer>();
				for (Map.Entry<String, Map<String, Integer>> entry : attrValueSplits
						.entrySet()) {
					String attrValue = entry.getKey();
					Map<String, Integer> attrValueSplit = entry.getValue();
					if (splitPoint.equals(attrValue)) {
						for (Integer v : attrValueSplit.values()) {
							splitAboveNum += v;
						}
						double aboveGini = 1.0;
						for (Integer v : attrValueSplit.values()) {
							aboveGini -= Math.pow((v / splitAboveNum), 2);
						}
						splitPointGini += (splitAboveNum / totalNum)
								* aboveGini;
					} else {
						for (Map.Entry<String, Integer> e : attrValueSplit
								.entrySet()) {
							String k = e.getKey();
							Integer v = e.getValue();
							Integer count = attrBelowSplit.get(k);
							attrBelowSplit
									.put(k, null == count ? v : v + count);
							splitBelowNum += e.getValue();
						}
					}
				}
				double belowGini = 1.0;
				for (Integer v : attrBelowSplit.values()) {
					belowGini -= Math.pow((v / splitBelowNum), 2);
				}
				splitPointGini += (splitBelowNum / totalNum) * belowGini;
				if (minSplitPointGini > splitPointGini) {
					minSplitPointGini = splitPointGini;
					minSplitPoint = splitPoint;
				}
			}
			context.write(key, new AttributeGiniWritable(key.toString(),
					minSplitPointGini, false, minSplitPoint));
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}

}
