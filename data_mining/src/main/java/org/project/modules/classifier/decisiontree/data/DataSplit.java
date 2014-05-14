package org.project.modules.classifier.decisiontree.data;

public class DataSplit {

	/** 数据集*/
	private Data data = null;
	/** 数据集所要分割的属性*/
	private String splitAttribute = null;
	/** 数据集所要分割的值*/
	private String[] splitPoints = null;
	
	public DataSplit() {
		
	}
	
	public DataSplit(Data data, String splitAttribute, String[] splitPoints) {
		super();
		this.data = data;
		this.splitAttribute = splitAttribute;
		this.splitPoints = splitPoints;
	}

	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
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
