package org.project.modules.association.apriori.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;
import org.project.utils.ShowUtils;

public class AprioriBuilder {
	
	private static int minSupport = 2;

	private static Data data = null;
	
	static {
		data = new Data();
		data.getInstances().add(new Instance(
				new Object[]{"豆奶", "莴苣"}));
		data.getInstances().add(new Instance(
				new Object[]{"莴苣", "尿布", "葡萄酒", "甜菜"}));
		data.getInstances().add(new Instance(
				new Object[]{"豆奶", "尿布", "葡萄酒", "橙汁"}));
		data.getInstances().add(new Instance(
				new Object[]{"莴苣", "豆奶", "尿布", "葡萄酒"}));
		data.getInstances().add(new Instance(
				new Object[]{"莴苣", "豆奶", "尿布", "橙汁"}));
	}
	
	private List<ItemSet> frequency_1_itemset() {
		List<ItemSet> results = new ArrayList<ItemSet>();
		Map<Object, Integer> candidates = new HashMap<Object, Integer>();
		for (Instance instance : data.getInstances()) {
			for (Object value : instance.getValues()) {
				Integer mValue = candidates.get(value);
				candidates.put(value, null == mValue ? 1 : mValue + 1);
			}
		}
		System.out.println("frequency_1_itemset: ");
		ShowUtils.print(candidates);
		for (Map.Entry<Object, Integer> entry : candidates.entrySet()) {
			if (entry.getValue() > minSupport) {
				results.add(new ItemSet(entry.getKey(), entry.getValue()));
			}
		}
		return results;
	}
	
	private int calculate_support(Object... items) {
		int support = 0;
		for (Instance instance : data.getInstances()) {
			int temp = 0;
			for (Object value : instance.getValues()) {
				for (Object item : items) {
					if (item.equals(value)) {
						temp++;
					}
				}
			}
			if (temp == items.length) {
				support++;
			}
		}
		return support;
	}
	
	private List<List<Object>> subItemSet(ItemSet itemSet) {
		List<List<Object>> results = new ArrayList<List<Object>>();
		List<Object> items = new ArrayList<Object>(itemSet.getItem());
		for (int i = 0, len = items.size(); i < len; i++) {
			List<Object> temp = new ArrayList<Object>(items);
			temp.remove(i);
			results.add(temp);
		}
		return results;
	}
	
	public void build() {
		frequency_1_itemset();
	}
	
	public static void main(String[] args) {
		AprioriBuilder ab = new AprioriBuilder();
		ab.build();
		System.out.println(ab.calculate_support("莴苣", "豆奶", "尿布"));
		
	}
}
