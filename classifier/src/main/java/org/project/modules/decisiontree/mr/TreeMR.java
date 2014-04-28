package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.Iterator;

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

public class TreeMR {
	
	class TreeMapper extends Mapper<LongWritable, Text, LongWritable, StringOutput> {
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
		}

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			context.write(key, new StringOutput(String.valueOf(value.toString().length())));
		}
		
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			super.cleanup(context);
		}
	}
	
	class TreeReducer extends Reducer<LongWritable, StringOutput, LongWritable, StringOutput>  {

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
		}
		
		@Override
		protected void reduce(LongWritable key, Iterable<StringOutput> values, Context context)
				throws IOException, InterruptedException {
			Iterator<StringOutput> iterator = values.iterator();
			while (iterator.hasNext()) {
				context.write(key, iterator.next());
			}
		}
		
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			super.cleanup(context);
		}
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
			
			Job job = new Job(configuration, "Tree");
			job.setJarByClass(TreeMR.class);
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			job.setOutputKeyClass(LongWritable.class);
			job.setOutputValueClass(StringOutput.class);
			
			job.setMapperClass(TreeMapper.class);
			job.setNumReduceTasks(0); 
//			job.setReducerClass(TreeReducer.class);
			
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
