package org.project.modules.association.apriori.data;

import java.util.TreeSet;

public class ItemSet {

	private TreeSet<Object> item = null;
	
	private int support = 0;
	
	public ItemSet() {
	}
	
	public ItemSet(TreeSet<Object> item) {
		this.item = item;
	}
	
	public ItemSet(Object item, int support) {
		getItem().add(item);
		this.support = support;
	}
	
	public ItemSet(TreeSet<Object> item, int support) {
		this.item = item;
		this.support = support;
	}

	public TreeSet<Object> getItem() {
		if (null == item) {
			item = new TreeSet<Object>();
		}
		return item;
	}

	public void setItem(TreeSet<Object> item) {
		this.item = item;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	
	
}
