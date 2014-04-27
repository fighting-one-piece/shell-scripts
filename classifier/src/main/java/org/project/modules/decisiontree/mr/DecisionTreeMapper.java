package org.project.modules.decisiontree.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.project.modules.decisiontree.original.DecisionTree;
import org.project.modules.decisiontree.original.Sample;
import org.project.modules.decisiontree.original.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTreeMapper extends Mapper<LongWritable, Text, LongWritable, MapperOutput> {
	
	private static final Logger log = LoggerFactory.getLogger(DecisionTreeMapper.class);
	
	/** mapper's partition */
	private int partition;
	
	private List<Sample> sampleList = new ArrayList<Sample>();
	
	private Set<String> attributeSet = new HashSet<String>();
	
	private Map<Object, List<Sample>> sampleMap = new HashMap<Object, List<Sample>>();
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		partition = conf.getInt("mapred.task.partition", -1);
		log.info("partition : {}", partition);
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		Sample sample = new Sample();
		sample.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String[] entry = tokenizer.nextToken().split(":");
			sample.setAttribute(entry[0], entry[1]);
			if (!attributeSet.contains(entry[0])) {
				attributeSet.add(entry[0]);
			}
		}
		List<Sample> samples = sampleMap.get(sample.getCategory());
		if (null == samples) {
			samples = new ArrayList<Sample>();
			sampleMap.put(sample.getCategory(), samples);
		}
		samples.add(sample);
		sampleList.add(sample);
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		String[] attributes = attributeSet.toArray(new String[0]);
		Tree tree = (Tree) DecisionTree.buildWithC45(sampleMap, attributes);
		log.info("decision tree: {}" + tree);
		DecisionTree.outputDecisionTree(tree, 0, null);
		
		MapperOutput output = new MapperOutput(tree);
		context.write(new LongWritable(1), output);
		log.info("DecisionTreeMapper cleanup finish");
	}
}
