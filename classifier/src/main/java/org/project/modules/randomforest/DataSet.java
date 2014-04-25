package org.project.modules.randomforest;

public class DataSet {

	private Object[] attributes = null;
	
	private Object[][] values = null;
	
	public DataSet() {
		
	}
	
	public DataSet(Object[] attributes, Object[][] values) {
		this.attributes = attributes;
		this.values = values;
	}

	public Object[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Object[] attributes) {
		this.attributes = attributes;
	}

	public Object[][] getValues() {
		return values;
	}

	public void setValues(Object[][] values) {
		this.values = values;
	}
	
	
}
