package org.project.modules.association.mr;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.association.data.Data;
import org.project.modules.association.data.Instance;
import org.project.modules.association.node.FPTreeNode;
import org.project.modules.association.node.FPTreeNodeHelper;

public class FPGrowthMR {
	
	private static void configureJob(Job job) {
		job.setJarByClass(FPGrowthMR.class);
		
		job.setMapperClass(FPGrowthMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(FPGrowthReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error");
				System.out.println("error, please input two path. input and output");
				System.out.println("1. input path.");
				System.out.println("2. output path.");
				System.out.println("3. sort input path.");
				System.exit(2);
			}
			configuration.set("mapred.job.queue.name", "q_hudong");
			configuration.set("sort.input.path", inputArgs[2]);
			
			Path sortPath = new Path(inputArgs[2]);
			DistributedCache.addCacheFile(sortPath.toUri(), configuration);
			
			Job job = new Job(configuration, "FPGrowth Algorithm");
			
			FileInputFormat.setInputPaths(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class FPGrowthMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	private List<Map.Entry<String, Integer>> entries = null;
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		URI[] uris = DistributedCache.getCacheFiles(conf);
		Map<String, Integer> map = new HashMap<String, Integer>();
		SequenceFile.Reader reader = null;
		try {
			Path path = new Path(uris[0]);
			FileSystem fs = FileSystem.get(conf);
			reader = new SequenceFile.Reader(fs, path, conf);
			Text key = (Text) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			IntWritable value = new IntWritable();
			while (reader.next(key, value)) {
				map.put(key.toString(), value.get());
				key = new Text();
				value = new IntWritable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		entries = new ArrayList<Map.Entry<String, Integer>>(); 
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			entries.add(entry);
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer tokenizer = new StringTokenizer(value.toString());
		tokenizer.nextToken();
		List<String> results = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] items = token.split(",");
			for (Map.Entry<String, Integer> entry : entries) {
				String eKey = entry.getKey();
				for (String item : items) {
					if (eKey.equals(item)) {
						results.add(eKey);
						break;
					}
				}
			}
		}
		String[] values = results.toArray(new String[0]);
		StringBuilder sb = new StringBuilder();
		for (String v : values) {
			sb.append(v).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		for (String v : values) {
			context.write(new Text(v), new Text(sb.toString()));
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
}

class FPGrowthReducer extends Reducer<Text, Text, Text, IntWritable> {
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
//		Configuration conf = context.getConfiguration();
	}
	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {
		Data data = new Data();
		for (Text value : values) {
			Instance instance = new Instance();
			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			instance.setId(Long.parseLong(tokenizer.nextToken()));
			String token = tokenizer.nextToken();
			instance.setValues(token.split(","));
			data.getInstances().add(instance);
		}
		FPTreeNode treeNode = buildFPGrowthTree(data);
		FPTreeNodeHelper.print(treeNode, 0);
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
	//创建FPGrowthTree
	private FPTreeNode buildFPGrowthTree(Data data) {
		FPTreeNode rootNode = new FPTreeNode();
		for (Instance instance : data.getInstances()) {
			LinkedList<String> items = instance.getValuesList();
			FPTreeNode tempNode = rootNode;
			//如果节点已经存在则加1
			FPTreeNode childNode = tempNode.findChild(items.peek());
			while (!items.isEmpty() && null != childNode) {
				childNode.incrementCount();
				tempNode = childNode;
				items.poll();
				childNode = tempNode.findChild(items.peek());
			}
			//如果节点不存在则新增
			addNewTreeNode(tempNode, items);
		}
		return rootNode;
	}
	
	//新增树节点
	private void addNewTreeNode(FPTreeNode parent, LinkedList<String> items) {
		while (items.size() > 0) {
			String item = items.poll();
			FPTreeNode child = new FPTreeNode(item, 1);
			child.setParent(parent);
			parent.addChild(child);
			addNewTreeNode(child, items);
		}
	}
	
}