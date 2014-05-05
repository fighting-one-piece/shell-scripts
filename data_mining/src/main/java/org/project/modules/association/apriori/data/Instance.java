package org.project.modules.association.apriori.data;

public class Instance {

	private Object[] values = null;
	
	public Instance() {
	}
	
	public Instance(Object[] values) {
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
	
	
}
