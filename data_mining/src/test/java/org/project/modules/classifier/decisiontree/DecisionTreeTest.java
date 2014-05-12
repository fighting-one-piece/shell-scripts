package org.project.modules.classifier.decisiontree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
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
//		TreeSet<TreeNode> treeNodes = new TreeSet<TreeNode>();
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		TreeNodeHelper.purningTreeNode(treeNode, 25, 0, treeNodes);
		System.out.println(treeNodes.size());
		List<Object[]> results = new ArrayList<Object[]>();
		Set<String> attributes = new HashSet<String>();
		for (TreeNode node : treeNodes) {
//			System.out.println("------");
//			TreeNodeHelper.print(node, 0, null);
//			System.out.println("------");
//			StringBuilder sb = new StringBuilder();
//			TreeNodeHelper.treeNode2json(data, sb);
//			System.out.println(sb.toString());
			obtainAttributes(node, attributes);
//			Object[] result = (Object[]) node.classify(testData);
//			ShowUtils.print(result);
//			results.add(result);
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
	public void builder() {
		String path = "d:\\trains_5.txt";
		Data data = DataLoader.loadWithId(path);
		DataHandler.fill(data, 0);
		Builder builder = new DecisionTreeC45Builder();
		TreeNode treeNode = (TreeNode) builder.build(data);
		TreeNodeHelper.print(treeNode, 0, null);
	}
	
	public static void main(String[] args) {
		double p = 1;
		double a = p * (Math.log(p) / Math.log(2));
		System.out.println(a);
	}
	
}
