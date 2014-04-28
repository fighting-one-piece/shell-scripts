package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.project.modules.decisiontree.builder.Builder;
import org.project.modules.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataHandler;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.decisiontree.node.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTreeBuilderMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(DecisionTreeBuilderMR.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(BuilderMapperOutput.class);
		
		job.setMapperClass(DecisionTreeBuilderMapper.class);
		job.setNumReduceTasks(0); 
		
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

class DecisionTreeBuilderMapper extends Mapper<LongWritable, Text, 
	LongWritable, BuilderMapperOutput> {
	
	private static final Logger log = LoggerFactory.getLogger(DecisionTreeBuilderMapper.class);
	
	private List<Instance> instances = new ArrayList<Instance>();
	
	private Set<String> attributes = new HashSet<String>();
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
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
		Builder builder = new DecisionTreeC45Builder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		
		BuilderMapperOutput output = new BuilderMapperOutput(treeNode);
		context.write(new LongWritable(1), output);
		log.info("DecisionTreeMapper cleanup finish");
	}
}