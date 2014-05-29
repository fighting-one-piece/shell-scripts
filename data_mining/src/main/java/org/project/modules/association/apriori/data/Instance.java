package org.project.modules.association.apriori.data;

public class Instance {
	
	private Long id = null;

	private String[] values = null;
	
	public Instance() {
	}
	
	public Instance(String[] values) {
		this.values = values;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
	
}
