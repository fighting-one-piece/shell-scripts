package org.project.modules.decisiontree.data;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/** 数据处理类*/
public class DataHandler {

	/**
	 * 文本转化为样本实例
	 * @param line 文本
	 * @return
	 */
	public static Instance convert(String line) {
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
	
	/** 数据填充默认值*/
	public static void fill(Data data, Object fillValue) {
		fill(data.getInstances(), data.getAttributes(), fillValue);
	}
	
	/** 数据填充默认值*/
	public static void fill(Map<Object, List<Instance>> splits, 
			String[] attributes, Object fillValue) {
		for (List<Instance> instances : splits.values()) {
			fill(instances, attributes, fillValue);
		}
	}
	
	/** 数据填充默认值*/
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
	
}