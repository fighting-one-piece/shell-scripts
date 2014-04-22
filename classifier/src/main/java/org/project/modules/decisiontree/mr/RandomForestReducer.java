package org.project.modules.decisiontree.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RandomForestReducer extends Reducer<Text, IntWritable, Text, IntWritable>  {

	@Override
	protected void reduce(Text arg0, Iterable<IntWritable> arg1, Context context)
			throws IOException, InterruptedException {
		super.reduce(arg0, arg1, context);
	}
}
