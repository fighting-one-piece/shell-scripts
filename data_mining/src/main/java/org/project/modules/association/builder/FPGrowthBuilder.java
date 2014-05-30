package org.project.modules.association.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.modules.association.data.Data;
import org.project.modules.association.data.DataLoader;
import org.project.modules.association.data.Instance;
import org.project.modules.association.node.FPTreeNode;
import org.project.modules.association.node.FPTreeNodeHelper;
import org.project.utils.ShowUtils;

public class FPGrowthBuilder {

	/** 最小支持度 */
	private int minSupport = 2;
	/** 数据集 */
	private Data data = null;
	/** 有序频繁一项集*/
	private List<Map.Entry<String, Integer>> entries = null;
	/** 头表*/
	private List<FPTreeNode> headTables = null;

	public void initialize() {
		data = DataLoader.load("d:\\apriori.txt");
		entries = new ArrayList<Map.Entry<String, Integer>>(); 
		headTables = new ArrayList<FPTreeNode>();
	}

	public void adjust() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Instance instance : data.getInstances()) {
			for (String value : instance.getValues()) {
				Integer mValue = map.get(value);
				map.put(value, null == mValue ? 1 : mValue + 1);
			}
		}
		ShowUtils.print(map);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() >= minSupport) {
				entries.add(entry);
			}
		}
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return ((Integer) o2.getValue()).compareTo((Integer) o1.getValue());
			}
		});
		System.out.println(entries);
		for (Instance instance : data.getInstances()) {
//			ShowUtils.print(instance.getValues());
			instance.replaceValues(entries);
			ShowUtils.print(instance.getValues());
		}
	}
	
	public void buildTree(FPTreeNode treeNode) {
		for (Instance instance : data.getInstances()) {
			for (String value : instance.getValues()) {
				if (value.equals(treeNode.getName())) {
					treeNode.setCount(treeNode.getCount() + 1);
				} else {
					boolean isExist = false;
					for (FPTreeNode child : treeNode.getChildren()) {
						if (value.equals(child.getName())) {
							treeNode.setCount(treeNode.getCount() + 1);
							isExist = true;
						}
					}
					if (!isExist) {
						FPTreeNode child = new FPTreeNode(value, 1);
						treeNode.getChildren().add(child);
						treeNode = child;
					}
				}
			}
		}
	}
	
	public void buildHeadTables() {
		for (Map.Entry<String, Integer> entry : entries) {
			headTables.add(new FPTreeNode(entry.getKey(), entry.getValue()));
		}
	}
	
	public void build() {
		initialize();
		adjust();
		FPTreeNode treeNode = new FPTreeNode();
		buildTree(treeNode);
		FPTreeNodeHelper.print(treeNode, 0);
	}

	public static void main(String[] args) {
		FPGrowthBuilder fpg = new FPGrowthBuilder();
		fpg.build();
	}

}
