package org.project.modules.classifier.decisiontree.mr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeMWritable;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeRWritable;

public class DecisionTreeMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(DecisionTreeMR.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(AttributeMWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(AttributeRWritable.class);
		
		job.setMapperClass(DecisionTreeMapper.class);
		job.setReducerClass(DecisionTreeReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			Job job = new Job(configuration, "Decision Tree");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
//			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class DecisionTreeMapper extends Mapper<LongWritable, Text, 
	Text, AttributeMWritable> {
	
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
		while (tokenizer.hasMoreTokens()) {
			String attribute = tokenizer.nextToken();
			String[] entry = attribute.split(":");
			context.write(new Text(entry[0]), new AttributeMWritable(id, category, entry[1]));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class DecisionTreeReducer extends Reducer<Text, AttributeMWritable, Text, AttributeRWritable> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
	protected void reduce(Text key, Iterable<AttributeMWritable> values,
			Context context) throws IOException, InterruptedException {
		double totalNum = 0.0;
		Map<String, Map<String, Integer>> attrValueSplits = 
				new HashMap<String, Map<String, Integer>>();
		Iterator<AttributeMWritable> iterator = values.iterator();
		while (iterator.hasNext()) {
			AttributeMWritable attribute = iterator.next();
			String attributeValue = attribute.getAttributeValue();
			Map<String, Integer> attrValueSplit = attrValueSplits.get(attributeValue);
			if (null == attrValueSplit) {
				attrValueSplit = new HashMap<String, Integer>();
				attrValueSplits.put(attributeValue, attrValueSplit);
			}
			String category = attribute.getCategory();
			Integer categoryNum = attrValueSplit.get(category);
			attrValueSplit.put(category, null == categoryNum ? 1 : categoryNum + 1);
			totalNum++;
		}
		double gainInfo = 0.0;
		double splitInfo = 0.0;
		for (Map<String, Integer> attrValueSplit : attrValueSplits.values()) {
			double totalCategoryNum = 0;
			for (Integer categoryNum : attrValueSplit.values()) {
				totalCategoryNum += categoryNum;
			}
			double entropy = 0.0;
			for (Integer categoryNum : attrValueSplit.values()) {
				double p = categoryNum / totalCategoryNum;
				entropy -= p * (Math.log(p) / Math.log(2));
			}
			double dj = totalCategoryNum / totalNum;
			gainInfo += dj * entropy;
			splitInfo -= dj * (Math.log(dj) / Math.log(2));
		}
		double gainRatio = gainInfo / splitInfo;
		StringBuilder splitPoints = new StringBuilder();
		for (String attrValue : attrValueSplits.keySet()) {
			splitPoints.append(attrValue).append(",");
		}
		splitPoints.deleteCharAt(splitPoints.length() - 1);
		System.out.println("attribute: " + key.toString());
		System.out.println("gainRatio: " + gainRatio);
		System.out.println("splitPoints: " + splitPoints.toString());
		context.write(key, new AttributeRWritable(
				key.toString(), gainRatio, splitPoints.toString()));
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
}