package org.project.modules.decisiontree.mr;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DecisionTreeReducer extends Reducer<Text, MapperOutput, Text, MapperOutput>  {

	@Override
	protected void reduce(Text key, Iterable<MapperOutput> values, Context context)
			throws IOException, InterruptedException {
		MapperOutput output = values.iterator().next();
		context.write(key, output);
	}
}
