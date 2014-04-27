package org.project.modules.decisiontree;

import java.util.List;

import org.project.modules.decisiontree.builder.Builder;
import org.project.modules.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.decisiontree.builder.ForestBuilder;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataLoader;
import org.project.modules.decisiontree.node.ForestNode;
import org.project.modules.decisiontree.node.TreeNode;

public class RandomForest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		int random = 10;
		Builder treeBuilder = new DecisionTreeC45Builder();
		Builder forestBuilder = new ForestBuilder(random, treeBuilder);
		Data data = DataLoader.load("");
		List<TreeNode> treeNodes = (List<TreeNode>) forestBuilder.build(data);
		Data testData = DataLoader.load("");
		ForestNode forestNode = new ForestNode(treeNodes);
		Object[] results = (Object[]) forestNode.classify(testData);
		System.out.println(results);
	}
}
