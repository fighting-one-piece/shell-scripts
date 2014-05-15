package org.project.modules.classifier.decisiontree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.utils.FileUtils;

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
	public void addLineNum() throws Exception {
		FileUtils.addLineNum("D:\\trainset_extract_1.txt", "D:\\trainset_extract_1_l.txt");
	}
	
	private Set<String> getAttribute(String input, int n) {
		Set<String> attributes = new HashSet<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				line = reader.readLine();
				tokenizer.nextToken();
				tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.size() != 100) {
						attributes.add(entry[0]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return attributes;
	}
	
	@Test
	public void generateDataFile() {
		String input = "D:\\trainset_extract_10_l.txt";
		String output = "D:\\attribute_1000_r_100.txt";
		Set<String> attributes = getAttribute(input, 1000);
		System.out.println(attributes.size());
		List<Instance> instances = new ArrayList<Instance>();
		InputStream in = null;
		BufferedReader reader = null;
//		OutputStream out = null;
//		BufferedWriter writer = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
//			out = new FileOutputStream(new File(output));
//			writer = new BufferedWriter(new OutputStreamWriter(out));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				Instance instance = new Instance();
				instance.setId(Long.parseLong(tokenizer.nextToken()));
				instance.setCategory(tokenizer.nextToken());
				int index = 0;
				while (tokenizer.hasMoreTokens() && index < 100) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.contains(entry[0])) {
						instance.setAttribute(entry[0], entry[1]);
						System.out.println("token: " + index);
						index++;
					}
				}
				while (index < 100) {
					for (String attribute : attributes) {
						Object value = instance.getAttribute(attribute);
						if (null == value) {
							instance.setAttribute(attribute, "1.0");
							System.out.println("add: " + index);
							index++;
						}
					}
				}
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
//			IOUtils.closeQuietly(out);
//			IOUtils.closeQuietly(writer);
		}
		DataHandler.writeData(output, new Data(
				attributes.toArray(new String[0]), instances));
	}
	
	
}
