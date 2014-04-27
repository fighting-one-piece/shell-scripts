package org.project.modules.decisiontree.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

public class DataLoader {
	
	public static Data load(String path) {
		Set<String> attributes = new HashSet<String>();
		List<Instance> instances = new ArrayList<Instance>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(path))));
			Instance instance = null;
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				instance = new Instance();
				instance.setCategory(tokenizer.nextToken());
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					instance.setAttribute(entry[0], entry[1]);
					if (!attributes.contains(entry[0])) {
						attributes.add(entry[0]);
					}
				}
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return new Data(attributes.toArray(new String[0]), instances);
	}
	
	public static Data LoadRandom(String path) {
		return loadRandom(load(path));
	}
	
	public static Data loadRandom(Data data) {
		String[] attributes = data.getAttributes();
		List<Instance> instances = data.getInstances();
		Random random = new Random();
		int minRandomAttributeCount = attributes.length / 30;
		int randomAttributeCount = minRandomAttributeCount + 
				random.nextInt(attributes.length - minRandomAttributeCount);
		while (randomAttributeCount <= 1) {
			randomAttributeCount = random.nextInt(attributes.length);
		}
		Set<String> randomAttributeSet = new HashSet<String>();
		while (randomAttributeSet.size() != randomAttributeCount) {
			randomAttributeSet.add(attributes[random.nextInt(attributes.length)]);
		}
		int instance_len = instances.size();
		int minRandomDataCount = instance_len / 20;
		int randomDataCount = minRandomDataCount + 
				random.nextInt(instance_len - minRandomDataCount);
		//不存在相同的数据，即便存在重复数据整体应该也没有什么影响
		Set<Instance> instanceSet = new HashSet<Instance>();
		int instanceSet_len = instanceSet.size();
		while (instanceSet_len < randomDataCount) {
			instanceSet.add(instances.get(random.nextInt(instanceSet_len)));
			instanceSet_len = instanceSet.size();
		}
		return new Data(randomAttributeSet.toArray(new String[0]), 
				new ArrayList<Instance>(instanceSet));
	}

}
