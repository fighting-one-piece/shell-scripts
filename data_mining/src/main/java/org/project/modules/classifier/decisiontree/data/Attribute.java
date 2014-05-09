package org.project.modules.classifier.decisiontree.data;

public class Attribute {

	private String name = null;
	
	private String value = null;

	private double gainRatio = 0.0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public double getGainRatio() {
		return gainRatio;
	}

	public void setGainRatio(double gainRatio) {
		this.gainRatio = gainRatio;
	}
	
	public String[] getSplitPoints() {
		return value.split(",");
	}
	
	
}
