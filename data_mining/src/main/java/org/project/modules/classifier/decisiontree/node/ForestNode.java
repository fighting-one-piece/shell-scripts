package org.project.modules.classifier.decisiontree.node;

import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.Instance;

public class ForestNode extends Node {

	private static final long serialVersionUID = 1L;
	
	private List<TreeNode> treeNodes = null;
	
	public ForestNode(List<TreeNode> treeNodes) {
		this.treeNodes = treeNodes;
	}
	
	@Override
	public Object classify(Data data) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (TreeNode treeNode : treeNodes) {
			Object result = treeNode.classify(data);
			if (null != result) {
				results.add((Object[]) treeNode.classify(data));
			}
		}
		return DataHandler.vote(results);
	}
	
	@Override
	public Object classify(Instance... instances) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (TreeNode treeNode : treeNodes) {
			Object result = treeNode.classify(instances);
			if (null != result) {
				results.add((Object[]) treeNode.classify(instances));
			}
		}
		return DataHandler.vote(results);
	}
	
}