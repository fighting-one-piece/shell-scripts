package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.modules.decisiontree.builder.Builder;
import org.project.modules.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataHandler;
import org.project.modules.decisiontree.data.DataLoader;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.decisiontree.node.TreeNode;

public class RandomForestBuilderMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(RandomForestBuilderMR.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(BuilderMapperOutput.class);
		
		job.setMapperClass(RandomForestBuilderMapper.class);
		job.setNumReduceTasks(0);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 4) {
				System.out.println("error");
				System.exit(2);
			}
			configuration.set("forest.tree.number", inputArgs[2]);
			configuration.set("random.attribute.number", inputArgs[3]);
			
			Job job = new Job(configuration, "Random Forest");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));

			configureJob(job);
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class RandomForestBuilderMapper extends Mapper<LongWritable, Text, IntWritable, BuilderMapperOutput> {

	private int treeNum = 0;
	
	private int attributeNum = 0;
	
	private List<Instance> instances = new ArrayList<Instance>();
	
	private Set<String> attributes = new HashSet<String>();
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		treeNum = Integer.parseInt(conf.get("forest.tree.number", "10"));
		attributeNum = Integer.parseInt(conf.get("random.attribute.number", "1"));
		System.out.println("treeNum: " + treeNum);
		System.out.println("attributeNum: " + attributeNum);
	}
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		instances.add(DataHandler.extract(line, attributes));
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Data data = new Data(attributes.toArray(new String[0]), instances);
		System.out.println("data attribute len: " + data.getAttributes().length);
		System.out.println("data instances len: " + data.getInstances().size());
		for (int i = 0; i < treeNum; i++) {
			Data randomData = DataLoader.loadRandom(data, attributeNum);
			Builder builder = new DecisionTreeC45Builder();
			TreeNode treeNode = (TreeNode) builder.build(randomData);
			BuilderMapperOutput output = new BuilderMapperOutput(treeNode);
			context.write(new IntWritable(i), output);
			System.out.println(i + " tree build success");
		}
	}
}

