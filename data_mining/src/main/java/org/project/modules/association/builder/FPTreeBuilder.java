package org.project.modules.association.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.project.modules.association.data.Data;
import org.project.modules.association.data.DataLoader;
import org.project.modules.association.data.Instance;
import org.project.modules.association.data.ItemSet;
import org.project.modules.association.node.FPTreeNode;
import org.project.modules.association.node.FPTreeNodeHelper;
import org.project.utils.ShowUtils;

public class FPTreeBuilder {

	// 创建FPGrowthTree
	public FPTreeNode buildFPGrowthTree(Data data, List<FPTreeNode> leafs) {
		FPTreeNode rootNode = new FPTreeNode();
		for (Instance instance : data.getInstances()) {
			LinkedList<String> items = instance.getValuesList();
			FPTreeNode tempNode = rootNode;
			// 如果节点已经存在则加1
			FPTreeNode childNode = tempNode.findChild(items.peek());
			while (!items.isEmpty() && null != childNode) {
				childNode.incrementCount();
				tempNode = childNode;
				items.poll();
				childNode = tempNode.findChild(items.peek());
			}
			// 如果节点不存在则新增
			addNewTreeNode(tempNode, items, leafs);
		}
		return rootNode;
	}

	// 新增树节点
	private void addNewTreeNode(FPTreeNode parent, LinkedList<String> items,
			List<FPTreeNode> leafs) {
		while (items.size() > 0) {
			String item = items.poll();
			FPTreeNode child = new FPTreeNode(item, 1);
			child.setParent(parent);
			parent.addChild(child);
			if (items.size() == 0) {
				leafs.add(child);
			}
			addNewTreeNode(child, items, leafs);
		}
	}
	
	public static void main(String[] args) {
		FPTreeBuilder builder = new FPTreeBuilder();
		Data all = DataLoader.load("d:\\apriori2.txt");
		Map<String, Data> m = new HashMap<String, Data>();
		for (Instance instance : all.getInstances()) {
			LinkedList<String> temp = new LinkedList<String>();
			for (String value : instance.getValues()) {
				Data d = m.get(value);
				if (null == d) {
					d = new Data();
					m.put(value, d);
				}
				d.getInstances().add(new Instance(temp.toArray(new String[0])));
				temp.add(value);
			}
		}
		for (Map.Entry<String, Data> entry : m.entrySet()) {
			System.out.println("item: " + entry.getKey());
			Data data = entry.getValue();
			List<FPTreeNode> leafs = new LinkedList<FPTreeNode>();
			FPTreeNode treeNode = builder.buildFPGrowthTree(data, leafs);
			FPTreeNodeHelper.print(treeNode, 0);
			Map<String, Integer> map = new HashMap<String, Integer>();
//			b(treeNode, map);
			for (FPTreeNode leaf : leafs) {
				c(leaf, map);
			}
			ShowUtils.print(map);
		}
	}
	
	public static List<List<ItemSet>> frequencies = new ArrayList<List<ItemSet>>();
	
	public static void a(FPTreeNode node, Map<String, Integer> map, 
			String prefix, int min) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getName());
		if (null != prefix) {
			sb.append("_").append(prefix);
		}
		String item = sb.toString();
		Integer support = map.get(item);
		if (null == support) {
			map.put(item, min);
		}
	}
	
	public static void b(FPTreeNode node, Map<String, Integer> map) {
		for (FPTreeNode child : node.getChildren()) {
			String name = child.getName();
			Integer count = map.get(name);
			map.put(name, null == count ? child.getCount() : count + child.getCount());
			b(child, map);
		}
	}
	
	public static void c(FPTreeNode node, Map<String, Integer> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getName());
		String name = sb.toString();
		Integer count = map.get(name);
		map.put(name, null == count ? node.getCount() : count + node.getCount());
		FPTreeNode parent = node.getParent();
		while (null != parent && null != parent.getName()) {
			sb.append(",").append(parent.getName());
			String name1 = sb.toString();
			Integer count1 = map.get(name1);
			map.put(name1, null == count1 ? node.getCount() : count1 + node.getCount());
			parent = parent.getParent();
		}
		parent = node.getParent();
		if (null != parent && null != parent.getName())
			c(parent, map);
	}
}
