package org.project.modules.decisiontree;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.project.modules.decisiontree.mr.BuilderMapperOutput;
import org.project.modules.utils.DFSUtils;
import org.project.modules.utils.ShowUtils;

public class DecisionTreeMRTest {
	
	public static final String DFS_URL = "hdfs://centos.host1:9000/user/hadoop/data/example/";
	
	private Configuration conf = new Configuration();
	
	@Before
	public void before() {
		conf.addResource(new Path("D:\\develop\\data\\hadoop\\hadoop-1.0.4\\conf\\core-site.xml"));
	}
	
	@SuppressWarnings("resource")
	@Test
	public void readSequenceFile() {
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "002/output3/part-m-00000");
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
			LongWritable key = (LongWritable) ReflectionUtils.newInstance(reader.getKeyClass(), conf); 
			BuilderMapperOutput value = new BuilderMapperOutput();
			while (reader.next(key, value)) {
				System.out.println(value.getTreeNode().getAttribute());
				value = new BuilderMapperOutput();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void listFile() {
		try {
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(DFS_URL + "002/output3");
			ShowUtils.print(DFSUtils.getPathFiles(fs, path));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
