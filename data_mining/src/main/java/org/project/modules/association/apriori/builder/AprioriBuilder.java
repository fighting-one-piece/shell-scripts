package org.project.modules.association.apriori.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.project.modules.association.apriori.data.Data;
import org.project.modules.association.apriori.data.DataLoader;
import org.project.modules.association.apriori.data.Instance;
import org.project.modules.association.apriori.data.ItemSet;
import org.project.modules.association.apriori.node.AssociationRule;
import org.project.modules.association.apriori.node.AssociationRuleHelper;
import org.project.utils.ShowUtils;

public class AprioriBuilder {
	/** 最小支持度*/
	private int minSupport = 2;
	/** 最小置信度*/
	private double minConfidence = 0.6;
	/** 数据集*/
	private Data data = null;
	/** 候选集集合*/
	private List<List<ItemSet>> candidates = null;
	/** 频繁集集合*/
	private List<List<ItemSet>> frequencies = null;
	/** 关联规则集合*/
	private Set<AssociationRule> associationRules = null;
	
	public void initialize() {
		data = DataLoader.load("d:\\apriori.txt");
		candidates = new ArrayList<List<ItemSet>>();
		frequencies = new ArrayList<List<ItemSet>>();
		associationRules = new HashSet<AssociationRule>();
	}
	
	/** 生成频繁一项集*/
	private void frequency_1_itemset_gen() {
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		List<ItemSet> candidate = new ArrayList<ItemSet>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : data.getInstances()) {
			Set<String> valueSet = new TreeSet<String>();
			for (String value : instance.getValues()) {
				Integer mValue = map.get(value);
				map.put(value, null == mValue ? 1 : mValue + 1);
				valueSet.add(value);
			}
		}
		ShowUtils.print(map);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			candidate.add(new ItemSet(entry.getKey(), entry.getValue()));
			if (entry.getValue() >= minSupport) {
				frequency.add(new ItemSet(entry.getKey(), entry.getValue()));
			}
		}
		candidates.add(candidate);
		frequencies.add(frequency);
	}
	
	/** 生成频繁K项集*/
	private void frequency_k_itemset_gen(int k) {
		Iterator<ItemSet> f1Iter = frequencies.get(k - 2).iterator();
		Iterator<ItemSet> f2Iter = frequencies.get(0).iterator();
		List<ItemSet> candidate = new ArrayList<ItemSet>();
		while (f1Iter.hasNext()) {
			ItemSet item1 = f1Iter.next();
			while (f2Iter.hasNext()) {
				ItemSet item2 = f2Iter.next();
				ItemSet temp = new ItemSet();
				temp.getItems().addAll(item1.getItems());
				if (!temp.getItems().containsAll(item2.getItems())) {
					temp.getItems().addAll(item2.getItems());
					boolean isContain = false;
					for (ItemSet itemSet : candidate) {
						if (itemSet.getItems().containsAll(temp.getItems())) {
							isContain = true;
						}
					}
					if (!isContain) {
						candidate.add(temp);
					}
				}
			}
			f2Iter = frequencies.get(0).iterator();
		}
		candidates.add(candidate);
		List<ItemSet> frequency = new ArrayList<ItemSet>();
		for (ItemSet itemSet : candidate) {
			int support = calculateSupport(itemSet.getItemsArray());
			if (support >= minSupport) {
				frequency.add(itemSet);
			}
		}
		frequencies.add(frequency);
	}
	
	/** 计算项集支持度*/
	private int calculateSupport(String... items) {
		if (null == items || items.length == 0) return 0; 
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
	
	/** 计算关联规则置信度*/
	private void calculateConfidence(AssociationRule associationRule) {
		String[] arLeft = associationRule.getLeft().getItemsArray();
		String[] arRight = associationRule.getRight().getItemsArray();
		int leftLength = arLeft.length;
		int rightLength = arRight.length;
		String[] left = new String[leftLength + rightLength];
		String[] right = new String[rightLength];
		System.arraycopy(arLeft, 0, left, 0, leftLength);
		System.arraycopy(arRight, 0, left, leftLength, rightLength);
		System.arraycopy(arRight, 0, right, 0, rightLength);
		double leftSup = calculateSupport(left);
		double rightSup = calculateSupport(right);
		System.out.print(AssociationRuleHelper.convert(left) + ": " + leftSup + " ");
		System.out.println(AssociationRuleHelper.convert(right) + ": " + rightSup + " ");
		if (rightSup != 0) {
			double confidence = leftSup / rightSup;
			associationRule.setConfidence(confidence);
			if (confidence >= minConfidence && !AssociationRuleHelper.isContain(
					associationRules, associationRule)) {
				associationRules.add(associationRule);
			}
		}
		for (AssociationRule child : associationRule.getChildren()) {
			calculateConfidence(child);
		}
	}
	
	/** 获取最新频繁项集*/
	private List<ItemSet> getLastFrequency() {
		int index = frequencies.size() - 1;
		List<ItemSet> frequency = frequencies.get(index);
		while (0 == frequency.size()) {
			frequency = frequencies.get((index--));
		}
		return frequency;
	}
	
	/** 生成关联规则并且计算置信度*/
	private void association_rule_gen(List<ItemSet> frequency) {
		for (ItemSet itemSet : frequency) {
			AssociationRule ar = new AssociationRule(itemSet, null);
			child_association_rule_gen(ar);
			calculateConfidence(ar);
			AssociationRuleHelper.print(ar, 0);
		}
	}
	
	/** 生成子关联规则*/
	private void child_association_rule_gen(AssociationRule associationRule) {
		ItemSet left = associationRule.getLeft();
		TreeSet<String> items = left.getItems();
		int length = items.size();
		if (length == 1) return;
		List<String> temp = new ArrayList<String>(items);
		for (int i = 0; i < length; i++) {
			AssociationRule child = new AssociationRule();
			associationRule.getChildren().add(child);
			child.getRight().addAll(associationRule.getRight().getItems());
			child.getRight().add(temp.get(i));
			for (int j = 0; j < length; j++) {
				if (j != i) {
					child.getLeft().add(temp.get(j));
				}
			}
			child_association_rule_gen(child);
		}
	}
	
	public void build() {
		initialize();
		frequency_1_itemset_gen();
		print(candidates, true);
		print(frequencies, false);
		for (int k = 2; frequencies.get(k - 2).size() > 0; k++) {
			frequency_k_itemset_gen(k);
			print(candidates, true);
			print(frequencies, false);
		}
		List<ItemSet> lastFrequency = getLastFrequency();
		print(lastFrequency);
		association_rule_gen(lastFrequency);
		System.out.println("associationRules size: " + associationRules.size());
		for (AssociationRule associationRule : associationRules) {
			AssociationRuleHelper.print(associationRule);
		}
	}
	
	public void print(List<List<ItemSet>> itemSetss, boolean isCandidate) {
		System.out.println((isCandidate ?  "Candidate" : "Frequency") + " Item Set");
		System.out.println(itemSetss.size());
		for (List<ItemSet> itemSets : itemSetss) {
			print(itemSets);
		}
	}
	
	public void print(List<ItemSet> itemSets) {
		System.out.println("----------");
		for (ItemSet itemSet : itemSets) {
			System.out.println(itemSet.getItems());
		}
		System.out.println("----------");
	}
	
	public static void main(String[] args) {
		AprioriBuilder ab = new AprioriBuilder();
		ab.build();
	}
}
