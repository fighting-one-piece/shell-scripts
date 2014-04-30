package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataHandler;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.decisiontree.node.ForestNode;
import org.project.modules.decisiontree.node.TreeNode;
import org.project.utils.DFSUtils;

public class RandomForestClassifyMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(RandomForestClassifyMR.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(RandomForestClassifyMapper.class);
		job.setNumReduceTasks(0);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error");
				System.exit(2);
			}
			
			DistributedCache.addCacheFile(
					new Path(inputArgs[2]).toUri(), configuration);
			
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

class RandomForestClassifyMapper extends Mapper<LongWritable, Text, IntWritable, Text> {

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
	
	@SuppressWarnings("resource")
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);
		URI[] uris = DistributedCache.getCacheFiles(conf);
		
		Path path = new Path(uris[0]);
		Path[] seqFilePaths = DFSUtils.getPathFiles(fs, path);
		
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, seqFilePaths[0], conf);
		IntWritable key = (IntWritable) ReflectionUtils.newInstance(reader.getKeyClass(), conf); 
		BuilderMapperOutput value = new BuilderMapperOutput();
		List<BuilderMapperOutput> outputs = new ArrayList<BuilderMapperOutput>();
		while (reader.next(key, value)) {
			outputs.add(value);
			value = new BuilderMapperOutput();
		}
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		for (BuilderMapperOutput output : outputs) {
			TreeNode treeNode = output.getTreeNode();
			treeNodes.add(treeNode);
		}
		Data data = new Data(attributes.toArray(new String[0]), instances);
		ForestNode forest = new ForestNode(treeNodes);
		Object[] results = (Object[]) forest.classify(data);
		for (int i = 0, len = results.length; i < len; i++) {
			context.write(new IntWritable(i), new Text(String.valueOf(results[i])));
		}
	}
	
}
