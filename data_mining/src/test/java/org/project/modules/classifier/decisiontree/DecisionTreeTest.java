package org.project.modules.classifier.decisiontree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeSprintBuilder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;
import org.project.utils.ShowUtils;

public class DecisionTreeTest {
	
	@Test
	public void purning() {
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.load(trainFilePath);
		DataHandler.fill(data, 0);
		System.out.println("data attrs: " + data.getAttributes().length);
		TreeNode treeNode = (TreeNode) treeBuilder.build(data);
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		TreeNodeHelper.purningTreeNode(treeNode, 25, 0, treeNodes);
		System.out.println(treeNodes.size());
		List<Object[]> results = new ArrayList<Object[]>();
		Set<String> attributes = new HashSet<String>();
		for (TreeNode node : treeNodes) {
			obtainAttributes(node, attributes);
		}
		String testFilePath = "d:\\trainset_extract_1.txt";
		Data testData = DataLoader.load(testFilePath);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
		for (TreeNode node : treeNodes) {
			Object[] result = (Object[]) node.classify(testData);
			ShowUtils.print(result);
			results.add(result);
		}
		ShowUtils.print(DataHandler.vote(results));
		System.out.println("tree attrs: " + attributes.size());
	}
	
	private void obtainAttributes(TreeNode treeNode, Set<String> attributes) {
		attributes.add(treeNode.getAttribute());
		Map<Object, Object> children = treeNode.getChildren();
		for (Map.Entry<Object, Object> entry : children.entrySet()) {
			Object value = entry.getValue();
			attributes.add(entry.getKey().toString());
			if (value instanceof TreeNode) {
				obtainAttributes((TreeNode) value, attributes);
			} 
		}
	}
	
	@Test
	public void splitData() {
		String path = "d:\\trainset_5.txt";
		Data data = DataLoader.loadWithId(path);
		Data splitData = new Data(data.getInstances(), 
				"income", 
				new String[]{"high", "medium,low"});
		Map<String, List<Instance>> a = DataHandler.splitData(splitData);
		for (Map.Entry<String, List<Instance>> entry : a.entrySet()) {
			System.out.println(entry.getKey());
			ShowUtils.print(entry.getValue());
		}
	}
	
	@Test
	public void splitData1() {
		String path = "d:\\trainset_5.txt";
		Data data = DataLoader.loadWithId(path);
		Data splitData = new Data(data.getInstances(), 
				null, new String[]{"high", "medium,low"});
		List<List<Instance>> instancess = DataHandler.split(splitData);
		for (List<Instance> instances : instancess) {
			ShowUtils.print(instances);
			System.out.println("-------");
		}
	}
	
	@Test
	public void builderC45() {
		String path = "d:\\trainset_10_l.txt";
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 0);
		Builder builder = new DecisionTreeC45Builder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
		String p = "d:\\trainset_1_l.txt";
		Data testData = DataLoader.loadWithId(p);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 0);
		Object[] results = (Object[]) treeNode.classify(testData);
		ShowUtils.print(results);
		
	}
	
	@Test
	public void builderSprint() {
		String path = "d:\\trainset_extract_10_l.txt";
//		String path = "d:\\trains14_id.txt";
//		String path = "d:\\attribute_1000_r_100.txt";
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 1.0);
		Builder builder = new DecisionTreeSprintBuilder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
		String p = "d:\\trainset_extract_1_l.txt";
//		String p = "d:\\trains14_id.txt";
//		String p = "d:\\attribute_100_r_10.txt";
		Data testData = DataLoader.loadWithId(p);
		DataHandler.fill(testData.getInstances(), data.getAttributes(), 1.0);
		Object[] results = (Object[]) treeNode.classifySprint(testData);
		ShowUtils.print(results);
	}
	
}
