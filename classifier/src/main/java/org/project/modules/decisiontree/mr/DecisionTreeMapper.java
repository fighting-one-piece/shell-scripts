package org.project.modules.decisiontree.mr;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DecisionTreeMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
	
	
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}
