package org.project.modules.decisiontree.builder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.modules.decisiontree.data.BestAttribute;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataHandler;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.decisiontree.node.TreeNode;

public abstract class AbstractBuilder implements Builder {

	@Override
	public Object build(Data data) {
		//数据预处理
		preHandle(data);
		//如果只有一个样本，将该样本所属分类作为新样本的分类
		Map<Object, List<Instance>> splits = data.getSplits();
		String[] attributes = data.getAttributes();
		if (splits.size() == 1) {
			return splits.keySet().iterator().next();
		}
		// 如果没有供决策的属性，则将样本集中具有最多样本的分类作为新样本的分类，即投票选举出分类
		if (attributes.length == 0) {
			return obtainMaxCategory(splits);
		}
		// 选取最优属性信息
		BestAttribute bestAttribute = chooseBestAttribute(data);
		// 决策树根结点，分支属性为选取的测试属性
		int bestAttrIndex = bestAttribute.getIndex();
		if (bestAttrIndex == -1) {
			return obtainMaxCategory(splits);
		}
		TreeNode tree = new TreeNode(attributes[bestAttrIndex]);
		// 已用过的测试属性不应再次被选为测试属性
		String[] subAttributes = new String[attributes.length - 1];
		for (int i = 0, j = 0; i < attributes.length; i++) {
			if (i != bestAttrIndex) {
				subAttributes[j++] = attributes[i];
			}
		}
		// 根据分支属性生成分支分裂信息
		Map<Object, Map<Object, List<Instance>>> subSplits = bestAttribute.getSplits();
		for (Entry<Object, Map<Object, List<Instance>>> entry : subSplits.entrySet()) {
			Object attrValue = entry.getKey();
			Data subData = new Data(subAttributes, entry.getValue());
			Object child = build(subData);
			tree.setChild(attrValue, child);
		}
		return tree;
	}
	
	/** 数据预处理*/
	protected void preHandle(Data data) {
		//这里只是赋予默认值处理
		DataHandler.fill(data, 0);
	}
	
	/**
	 * 获取数据集的最佳属性信息
	 * @param data
	 * @return
	 */
	public abstract BestAttribute chooseBestAttribute(Data data);
	
	/**
	 * 获取数量最多的类型
	 * @param splits
	 * @return
	 */
	protected Object obtainMaxCategory(Map<Object, List<Instance>> splits) {
		int max = 0;
		Object maxCategory = null;
		for (Entry<Object, List<Instance>> entry : splits.entrySet()) {
			int cur = entry.getValue().size();
			if (cur > max) {
				max = cur;
				maxCategory = entry.getKey();
			}
		}
		return maxCategory;
	}
	

}
