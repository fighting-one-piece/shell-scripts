package org.project.modules.association.apriori.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;
import org.project.utils.ShowUtils;

public class AprioriBuilder {
	
	private static int minSupport = 2;

	private static Data data = null;
	
	private List<Set<Object>> records = new ArrayList<Set<Object>>();
	
	private List<List<ItemSet>> frequencyItemSet = new ArrayList<List<ItemSet>>();
	
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
			Set<Object> valueSet = new TreeSet<Object>();
			for (Object value : instance.getValues()) {
				Integer mValue = candidates.get(value);
				candidates.put(value, null == mValue ? 1 : mValue + 1);
				valueSet.add(value);
			}
			records.add(valueSet);
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
	
	private void frequency_itemset(List<ItemSet> items) {
		List<ItemSet> temp = new ArrayList<ItemSet>(items);
		List<ItemSet> results = new ArrayList<ItemSet>();
		int size = items.size();
		//连接
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (temp.get(i).isMerge(temp.get(j))) {
					ItemSet item = new ItemSet(temp.get(i).getItem());
					item.merge(temp.get(j).getItem().last());
					results.add(item);
				}
			}
		}
		//剪枝
		pruning(temp, results);
		
		if (results.size() != 0) {
			frequencyItemSet.add(results);
			frequency_itemset(results);
		}
	}
	
	private void pruning(List<ItemSet> pre, List<ItemSet> res) {
		// step 1 k项集的子集属于k-1项集
		Iterator<ItemSet> ir = res.iterator();
		while (ir.hasNext()) {
			// 获取所有k-1项子集
			ItemSet now = ir.next();
			List<List<Object>> ss = subItemSet(now);
			// 判断是否在pre集中
			boolean flag = false;
			for (List<Object> li : ss) {
				if (flag)
					break;
				for (ItemSet pis : pre) {
					if (pis.getItem().containsAll(li)) {
						flag = false;
						break;
					}
					flag = true;
				}
			}
			if (flag) {
				ir.remove();
				continue;
			}
			// step 2 支持度
			int i = 0;
			for (Set<Object> sr : records) {
				if (sr.containsAll(now.getItem()))
					i++;
				now.setSupport(i);
			}
			if (now.getSupport() < minSupport)
				ir.remove();
		}
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
	
	private List<List<ItemSet>> getFrequencyItemSet() {
		return frequencyItemSet;
	}
	
	public static void main(String[] args) {
		AprioriBuilder ab = new AprioriBuilder();
//		ab.build();
//		System.out.println(ab.calculate_support("莴苣", "豆奶", "尿布"));
		ab.frequency_itemset(ab.frequency_1_itemset());
		List<List<ItemSet>> fiss = ab.getFrequencyItemSet();
		System.out.println(fiss.size());
		for (List<ItemSet> fis : fiss) {
			System.out.println("------");
			for (ItemSet is : fis) {
				System.out.println(is.getItem());
			}
			System.out.println("------");
		}
//		TreeSet<Object> item = new TreeSet<Object>();
//		item.add("A");
//		item.add("B");
//		item.add("C");		
//		item.add("D");		
//		ItemSet itemSet = new ItemSet(item);
//		List<List<Object>> subItem = ab.subItemSet(itemSet);
//		for (List<Object> sis : subItem) {
//			System.out.println("----");
//			for (Object si : sis) {
//				System.out.println(si);
//			}
//			System.out.println("----");
//		}
	}
}
