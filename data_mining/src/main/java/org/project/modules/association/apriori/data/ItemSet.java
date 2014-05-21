package org.project.modules.association.apriori.data;

import java.util.Iterator;
import java.util.TreeSet;

public class ItemSet {

	private TreeSet<String> item = null;
	
	private int support = 0;
	
	public ItemSet() {
	}
	
	public ItemSet(TreeSet<String> item) {
		this.item = item;
	}
	
	public ItemSet(String item, int support) {
		getItem().add(item);
		this.support = support;
	}
	
	public ItemSet(TreeSet<String> item, int support) {
		this.item = item;
		this.support = support;
	}

	public TreeSet<String> getItem() {
		if (null == item) {
			item = new TreeSet<String>();
		}
		return item;
	}

	public void setItem(TreeSet<String> item) {
		this.item = item;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	
	public boolean isMerge(ItemSet other) {
		if (null == other || other.getItem().size() != getItem().size()) {
			return false;
		}
		Iterator<String> iIter = getItem().iterator();
		Iterator<String> oIter = other.getItem().iterator();
		int size = getItem().size();
		while (iIter.hasNext() && oIter.hasNext() && --size > 0) {
			if (!iIter.next().equals(oIter.next())) {
				return false;
			}
		}
		return !(getItem().last().equals(other.getItem().last()));
	}
	
	public void merge(String value) {
		getItem().add(value);
	}
	
	
}
