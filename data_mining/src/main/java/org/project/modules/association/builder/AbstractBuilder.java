package org.project.modules.association.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.project.modules.association.data.ItemSet;
import org.project.modules.association.node.AssociationRule;
import org.project.modules.association.node.AssociationRuleHelper;

public class AbstractBuilder implements Builder {
	
	/** 最小支持度*/
//	private int minSupport = 2;
	/** 最小置信度*/
	private double minConfidence = 0.6;
	/** 频繁集集合*/
	private List<List<ItemSet>> frequencies = null;
	/** 关联规则集合*/
	private Set<AssociationRule> associationRules = null;
	
	protected void initialize() {
		frequencies = new ArrayList<List<ItemSet>>();
		associationRules = new HashSet<AssociationRule>();
	}

	@Override
	public void buildFrequencyItemSet() {
		
	}
	
	@Override
	public void buildAssociationRules() {
		List<ItemSet> lastFrequency = getLastFrequency();
		association_rule_gen(lastFrequency);
	}
	
	/** 计算项集支持度*/
	private int calculateSupport(String... items) {
		if (null == items || items.length == 0) return 0; 
		int support = 0;
//		for (Instance instance : data.getInstances()) {
//			int temp = 0;
//			for (String value : instance.getValues()) {
//				for (String item : items) {
//					if (item.equals(value)) {
//						temp++;
//					}
//				}
//			}
//			if (temp == items.length) {
//				support++;
//			}
//		}
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

}
