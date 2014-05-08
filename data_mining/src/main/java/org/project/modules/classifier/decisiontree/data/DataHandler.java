package org.project.modules.classifier.decisiontree.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/** 数据处理类*/
public class DataHandler {
	
	/**
	 * 抽取文本信息
	 * @param line 文本
	 * @return
	 */
	public static Instance extract(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			String[] entry = value.split(":");
			instance.setAttribute(entry[0], entry[1]);
		}
		return instance;
	}
	
	/**
	 * 抽取文本信息
	 * @param line 文本
	 * @param attributes 特征属性集
	 * @return
	 */
	public static Instance extract(String line, Set<String> attributes) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			String[] entry = value.split(":");
			instance.setAttribute(entry[0], entry[1]);
			if (!attributes.contains(entry[0])) {
				attributes.add(entry[0]);
			}
		}
		return instance;
	}
	
	/**
	 * 抽取带行号的文本信息
	 * @param line 文本
	 * @param attributes 特征属性集
	 * @return
	 */
	public static Instance extractWithId(String line, Set<String> attributes) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		Instance instance = new Instance();
		instance.setId(Long.parseLong(tokenizer.nextToken()));
		instance.setCategory(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextToken();
			String[] entry = value.split(":");
			instance.setAttribute(entry[0], entry[1]);
			if (!attributes.contains(entry[0])) {
				attributes.add(entry[0]);
			}
		}
		return instance;
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(Data data, Object fillValue) {
		fill(data.getInstances(), data.getAttributes(), fillValue);
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(Map<Object, List<Instance>> splits, 
			String[] attributes, Object fillValue) {
		for (List<Instance> instances : splits.values()) {
			fill(instances, attributes, fillValue);
		}
	}
	
	/** 缺失数据填充默认值*/
	public static void fill(List<Instance> instances, String[] attributes, Object fillValue) {
		fillValue = null == fillValue ? 0 : fillValue;
		for (Instance instance : instances) {
			Map<String, Object> instanceAttrs = instance.getAttributes();
			Object attrValue = null;
			for (int i = 0, attrLen = attributes.length; i < attrLen; i++) {
				attrValue = instanceAttrs.get(attributes[i]);
				instanceAttrs.put(attributes[i], 
						null == attrValue ? fillValue : attrValue);
			}
		}
	}
	
	/** 移除部分属性并填充缺失属性默认值*/
	public static void removeAndFill(Data data, int n, Object fillValue) {
		List<Instance> instances = data.getInstances();
		String[] attributes = data.getAttributes();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : instances) {
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String key = entry.getKey();
				Integer value = map.get(key);
				map.put(key, null == value ? 1 : value + 1);
			}
		}
		Set<String> a = new HashSet<String>();
		Set<String> b = new HashSet<String>();
		for (Instance instance : instances) {
			Map<String, Object> attrs = instance.getAttributes();
			Object attrValue = null;
			for (int i = 0, attrLen = attributes.length; i < attrLen; i++) {
				attrValue = attrs.get(attributes[i]);
				if (map.get(attributes[i]) < n) {
					a.add(attributes[i]);
					attrs.remove(attributes[i]);
				} else {
					attrs.put(attributes[i], 
							null == attrValue ? fillValue : attrValue);
					b.add(attributes[i]);
				}
			}
		}
		data.setPurningAttributes(b.toArray(new String[0]));
		System.out.println("all attribute size: " + attributes.length);
		System.out.println("remove attribute size: " + a.size());
		System.out.println("remain attribute size: " + b.size());
	}
	
	/** *
	 * 投票
	 * @param results
	 * @return
	 */
	public static Object[] vote(List<Object[]> results) {
		int columnNum = results.get(0).length;
		Object[] finalResult = new Object[columnNum];
		for (int i = 0; i < columnNum; i++) {
			Map<Object, Integer> resultCount = new HashMap<Object, Integer>();
			for (Object[] result : results) {
				if (null == result[i]) continue;
				Integer count = resultCount.get(result[i]);
				resultCount.put(result[i], null == count ? 1 : count + 1);
			}
			int max = 0;
			Object maxResult = null;
			for (Map.Entry<Object, Integer> entry : resultCount.entrySet()) {
				if (max < entry.getValue()) {
					max = entry.getValue();
					maxResult = entry.getKey();
				}
			}
			finalResult[i] = maxResult;
		}
		return finalResult;
	}
	
}
