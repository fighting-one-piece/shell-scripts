package org.project.modules.classifier.decisiontree.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 数据实体类*/
public class Data {

	/** 特征属性集合*/
	private String[] attributes = null;
	/** 剪枝特征属性集合，暂时没有用*/
	private String[] purningAttributes = null;
	/** 样本实例集合*/
	private List<Instance> instances = null;
	/** 数据集所要隐藏的属性*/
	private String[] hideAttributes = null;
	/** 数据集所要分割的属性*/
	private String splitAttribute = null;
	/** 数据集所要分割的值*/
	private String[] splitPoints = null;
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
	
	public Data(List<Instance> instances, String[] attributes,
			String[] splitPoints) {
		super();
		this.instances = instances;
		this.attributes = attributes;
		this.splitPoints = splitPoints;
	}
	
	public Data(List<Instance> instances, String[] attributes,
			String splitAttribute, String[] splitPoints) {
		super();
		this.instances = instances;
		this.attributes = attributes;
		this.splitAttribute = splitAttribute;
		this.splitPoints = splitPoints;
	}
	
	public String[] getAttributes() {
		return attributes;
	}
	
	public Set<String> getAttributeSet() {
		Set<String> attributeSet = new HashSet<String>();
		for (String attribute : attributes) {
			attributeSet.add(attribute);
		}
		return attributeSet;
	}
	
	public String[] getAttributesExcept(String... exceptAttributes) {
		String[] subAttributes = new String[attributes.length - 1];
		for (int i = 0, j = 0; i < attributes.length; i++) {
			boolean isExcept = false;
			for (String exceptAttribute : exceptAttributes) {
				if (exceptAttribute.equals(attributes[i])) {
					isExcept = true;
				}
			}
			if (!isExcept) subAttributes[j++] = attributes[i];
		}
		return subAttributes;
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

	public String[] getPurningAttributes() {
		return purningAttributes;
	}

	public void setPurningAttributes(String[] purningAttributes) {
		this.purningAttributes = purningAttributes;
	}
	
	public String[] getHideAttributes() {
		return hideAttributes;
	}

	public void setHideAttributes(String[] hideAttributes) {
		this.hideAttributes = hideAttributes;
	}

	public String getSplitAttribute() {
		return splitAttribute;
	}

	public void setSplitAttribute(String splitAttribute) {
		this.splitAttribute = splitAttribute;
	}

	public String[] getSplitPoints() {
		return splitPoints;
	}

	public void setSplitPoints(String[] splitPoints) {
		this.splitPoints = splitPoints;
	}
	
}
