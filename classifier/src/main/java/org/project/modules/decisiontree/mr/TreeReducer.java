package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class TreeReducer extends Reducer<LongWritable, StringOutput, LongWritable, StringOutput>  {

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
