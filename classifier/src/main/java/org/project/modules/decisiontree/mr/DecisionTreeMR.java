package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.decisiontree.original.DecisionTree;
import org.project.modules.decisiontree.original.Sample;
import org.project.modules.decisiontree.original.Tree;

public class DecisionTreeMR {
	
	class DecisionTreeMapper extends Mapper<LongWritable, Text, LongWritable, MapperOutput> {
		
		/** mapper's partition */
		private int partition;
		
		private List<Sample> sampleList = new ArrayList<Sample>();
		
		private Set<String> attributeSet = new HashSet<String>();
		
		private Map<Object, List<Sample>> sampleMap = new HashMap<Object, List<Sample>>();
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
			Configuration conf = context.getConfiguration();
			partition = conf.getInt("mapred.task.partition", -1);
			System.out.println(partition);
		}

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			Sample sample = new Sample();
			sample.setCategory(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				String[] entry = tokenizer.nextToken().split(":");
				sample.setAttribute(entry[0], entry[1]);
				if (!attributeSet.contains(entry[0])) {
					attributeSet.add(entry[0]);
				}
			}
			List<Sample> samples = sampleMap.get(sample.getCategory());
			if (null == samples) {
				samples = new ArrayList<Sample>();
				sampleMap.put(sample.getCategory(), samples);
			}
			samples.add(sample);
			sampleList.add(sample);
		}
		
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			super.cleanup(context);
			String[] attributes = attributeSet.toArray(new String[0]);
			Tree tree = (Tree) DecisionTree.buildWithC45(sampleMap, attributes);
			
			MapperOutput output = new MapperOutput(tree);
			context.write(new LongWritable(1), output);
		}
	}
	
	private static void configureJob(Job job) {
		job.setJarByClass(DecisionTreeMR.class);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(MapperOutput.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(MapperOutput.class);
		
		job.setMapperClass(DecisionTreeMapper.class);
		job.setNumReduceTasks(0); // No Reducers
//		job.setReducerClass(DecisionTreeReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error");
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
