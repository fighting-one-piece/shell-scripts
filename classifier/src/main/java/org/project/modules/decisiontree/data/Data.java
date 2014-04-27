package org.project.modules.decisiontree.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 数据实体类*/
public class Data {

	/** 特征属性集合*/
	private String[] attributes = null;
	/** 样本实例集合*/
	private List<Instance> instances = null;
	/** 样本实例集合的分裂信息*/
	private Map<Object, List<Instance>> splits = null;
	
	public Data() {
		
	}
	
	public Data(String[] attributes, List<Instance> instances) {
		this.attributes = attributes;
		this.instances = instances;
	}
	
	public Data(String[] attributes, Map<Object, List<Instance>> splits) {
		this.attributes = attributes;
		this.splits = splits;
	}
	
	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public List<Instance> getInstances() {
		if (null == instances) {
			instances = new ArrayList<Instance>();
		}
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
	
	public Map<Object, List<Instance>> getSplits() {
		if (null == splits) {
			splits = new HashMap<Object, List<Instance>>();
			List<Instance> split = null;
			for (Instance instance : instances) {
				Object category = instance.getCategory();
				split = splits.get(category);
				if (null == split) {
					split = new ArrayList<Instance>();
					splits.put(category, split);
				}
				split.add(instance);
			}
		}
		return splits;
	}

	public void setSplits(Map<Object, List<Instance>> splits) {
		this.splits = splits;
	}

	
}
