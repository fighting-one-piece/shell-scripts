package org.project.modules.decisiontree.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class DecisionTreeMR {

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error");
				System.exit(2);
			}
			Job job = new Job(configuration, "Random Forest");
			job.setJarByClass(DecisionTreeMR.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			
			job.setMapperClass(RandomForestMapper.class);
			job.setReducerClass(RandomForestReducer.class);
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
