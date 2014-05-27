package org.project.modules.classifier.decisiontree;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;
import org.project.modules.classifier.decisiontree.mr.writable.AttributeKVWritable;

public class Test {

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		String url = "hdfs://hadoop-namenode-1896:9000/user/hadoop_hudong/project/rf/output1/part-r-00000";
		SequenceFile.Reader reader = null;
		try {
			Set<String> attributes = new HashSet<String>();
			Map<String, Map<Object, Integer>> attributeValueStatistics
				= new HashMap<String, Map<Object, Integer>>();
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(url);
			reader = new SequenceFile.Reader(fs, path, conf);
			AttributeKVWritable key = (AttributeKVWritable) 
					ReflectionUtils.newInstance(reader.getKeyClass(), conf);
			IntWritable value = new IntWritable();
			while (reader.next(key, value)) {
				String attributeName = key.getAttributeName();
				attributes.add(attributeName);
				Map<Object, Integer> valueStatistics = 
						attributeValueStatistics.get(attributeName);
				if (null == valueStatistics) {
					valueStatistics = new HashMap<Object, Integer>();
					attributeValueStatistics.put(attributeName, valueStatistics);
				}
				valueStatistics.put(key.getAttributeValue(), value.get());
				value = new IntWritable();
			}
			System.out.println(attributes.size());
			System.out.println(attributeValueStatistics.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
