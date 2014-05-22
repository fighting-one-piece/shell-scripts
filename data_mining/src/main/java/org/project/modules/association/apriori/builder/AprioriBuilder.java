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
	
	private List<Set<String>> records = new ArrayList<Set<String>>();
	
	private List<List<ItemSet>> candidateItemSet = new ArrayList<List<ItemSet>>();
	
	private List<List<ItemSet>> frequencyItemSet = new ArrayList<List<ItemSet>>();
	
	static {
		data = new Data();
		data.getInstances().add(new Instance(
				new String[]{"豆奶", "莴苣"}));
		data.getInstances().add(new Instance(
				new String[]{"莴苣", "尿布", "葡萄酒", "甜菜"}));
		data.getInstances().add(new Instance(
				new String[]{"豆奶", "尿布", "葡萄酒", "橙汁"}));
		data.getInstances().add(new Instance(
				new String[]{"莴苣", "豆奶", "尿布", "葡萄酒"}));
		data.getInstances().add(new Instance(
				new String[]{"莴苣", "豆奶", "尿布", "橙汁"}));
	}
	
	private List<ItemSet> frequency_1_itemset() {
		List<ItemSet> frequencyItem = new ArrayList<ItemSet>();
		List<ItemSet> candidateItem = new ArrayList<ItemSet>();
		Map<String, Integer> candidates = new HashMap<String, Integer>();
		for (Instance instance : data.getInstances()) {
			Set<String> valueSet = new TreeSet<String>();
			for (String value : instance.getValues()) {
				Integer mValue = candidates.get(value);
				candidates.put(value, null == mValue ? 1 : mValue + 1);
				valueSet.add(value);
			}
			records.add(valueSet);
		}
		System.out.println("frequency_1_itemset: ");
		ShowUtils.print(candidates);
		for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
			candidateItem.add(new ItemSet(entry.getKey(), entry.getValue()));
			if (entry.getValue() > minSupport) {
				frequencyItem.add(new ItemSet(entry.getKey(), entry.getValue()));
			}
		}
		candidateItemSet.add(candidateItem);
		frequencyItemSet.add(frequencyItem);
		return frequencyItem;
	}
	
	private void frequency_k_itemset(int k) {
		Iterator<ItemSet> f1Iter = frequencyItemSet.get(k - 2).iterator();
		Iterator<ItemSet> f2Iter = frequencyItemSet.get(0).iterator();
		List<ItemSet> candidateItem = new ArrayList<ItemSet>();
		while (f1Iter.hasNext()) {
			ItemSet item1 = f1Iter.next();
//			System.out.println("%%");
//			ShowUtils.print(item1.getItem());
//			System.out.println("%%");
			while (f2Iter.hasNext()) {
				ItemSet item2 = f2Iter.next();
//				System.out.println("&&");
//				ShowUtils.print(item2.getItem());
//				System.out.println("&&");
				ItemSet temp = new ItemSet();
				temp.getItem().addAll(item1.getItem());
				if (!temp.getItem().containsAll(item2.getItem())) {
					temp.getItem().addAll(item2.getItem());
					boolean isContain = false;
					for (ItemSet itemSet : candidateItem) {
						if (itemSet.getItem().containsAll(temp.getItem())) {
							isContain = true;
						}
					}
					if (!isContain) {
						candidateItem.add(temp);
					}
				}
			}
			f2Iter = frequencyItemSet.get(0).iterator();
		}
		candidateItemSet.add(candidateItem);
		List<ItemSet> frequencyItem = new ArrayList<ItemSet>();
		for (ItemSet itemSet : candidateItem) {
			int support = calculateSupport(itemSet.getItem().toArray(new String[0]));
			if (support > minSupport) {
				frequencyItem.add(itemSet);
			}
		}
		frequencyItemSet.add(frequencyItem);
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
			List<List<String>> ss = subItemSet(now);
			// 判断是否在pre集中
			boolean flag = false;
			for (List<String> li : ss) {
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
			for (Set<String> sr : records) {
				if (sr.containsAll(now.getItem()))
					i++;
				now.setSupport(i);
			}
			if (now.getSupport() < minSupport)
				ir.remove();
		}
	}

	@SuppressWarnings("unused")
	private int calculateSupport(String... items) {
		int support = 0;
		for (Instance instance : data.getInstances()) {
			int temp = 0;
			for (String value : instance.getValues()) {
				for (String item : items) {
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
	
	private List<List<String>> subItemSet(ItemSet itemSet) {
		List<List<String>> results = new ArrayList<List<String>>();
		List<String> items = new ArrayList<String>(itemSet.getItem());
		for (int i = 0, len = items.size(); i < len; i++) {
			List<String> temp = new ArrayList<String>(items);
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
	
	private List<List<ItemSet>> getCandidateItemSet() {
		return candidateItemSet;
	}
	
	public static void print(List<List<ItemSet>> itemSetss) {
		System.out.println(itemSetss.size());
		for (List<ItemSet> itemSets : itemSetss) {
			System.out.println("------");
			for (ItemSet itemSet : itemSets) {
				System.out.println(itemSet.getItem());
			}
			System.out.println("------");
		}
	}
	
	public static void main(String[] args) {
		AprioriBuilder ab = new AprioriBuilder();
//		ab.build();
//		System.out.println(ab.calculateSupport("莴苣", "豆奶", "尿布"));
//		ab.frequency_itemset(ab.frequency_1_itemset());
//		List<List<ItemSet>> fiss = ab.getFrequencyItemSet();
//		System.out.println(fiss.size());
//		for (List<ItemSet> fis : fiss) {
//			System.out.println("------");
//			for (ItemSet is : fis) {
//				System.out.println(is.getItem());
//			}
//			System.out.println("------");
//		}
		ab.frequency_1_itemset();
		print(ab.getCandidateItemSet());
		print(ab.getFrequencyItemSet());
		ab.frequency_k_itemset(2);
		print(ab.getCandidateItemSet());
		print(ab.getFrequencyItemSet());
		ab.frequency_k_itemset(3);
		print(ab.getCandidateItemSet());
		print(ab.getFrequencyItemSet());
	}
}
