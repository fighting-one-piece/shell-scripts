package org.project.modules.association.apriori.data;

import java.util.Iterator;
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
	
	public boolean isMerge(ItemSet other) {
		if (null == other || other.getItem().size() != getItem().size()) {
			return false;
		}
		Iterator<Object> iIter = getItem().iterator();
		Iterator<Object> oIter = other.getItem().iterator();
		int size = getItem().size();
		while (iIter.hasNext() && oIter.hasNext() && --size > 0) {
			if (!iIter.next().equals(oIter.next())) {
				return false;
			}
		}
		return !(getItem().last().equals(other.getItem().last()));
	}
	
	public void merge(Object value) {
		getItem().add(value);
	}
	
	
}
