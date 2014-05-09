package org.project.modules.classifier.decisiontree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.Instance;

public class DataTest {

	@Test
	public void a() {
		String path = "d:\\trainset_100.txt";
		Data data = DataLoader.load(path);
		Map<String, Map<Object, Integer>> a = 
				new HashMap<String, Map<Object, Integer>>();
		for (Instance instance : data.getInstances()) {
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				Map<Object, Integer> b = a.get(key);
				if (null == b) {
					b = new HashMap<Object, Integer>();
					a.put(key, b);
				}
				Integer c = b.get(value);
				b.put(value, null == c ? 1 : c + 1);
			}
		}
		for (Map.Entry<String, Map<Object, Integer>> e : a.entrySet()) {
			System.out.print(e.getKey() + "-->");
			for (Map.Entry<Object, Integer> f : e.getValue().entrySet()) {
				System.out.print(f.getKey() + "--" + f.getValue() + ":");
			}
			System.out.println();
		}
		System.out.println(a.size());
		System.out.println(data.getAttributes().length);
	}
	
	@Test
	public void b() throws Exception {
		OutputStream out = new FileOutputStream(new File("D:\\a.txt"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		StringBuilder sb = new StringBuilder();
		sb.append("1").append("\t").append("1").append("\t").append("a:b");
		writer.write(sb.toString());
		writer.flush();
		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(writer);
	}
	
	@Test
	public void c() {
		
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.io.tmpdir"));
		System.out.println(System.getProperty("user.dir"));
		System.out.println(System.getProperty("user.home"));
	}
}
