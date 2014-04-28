package org.project.modules.decisiontree;

import java.util.List;

import org.project.modules.decisiontree.builder.Builder;
import org.project.modules.decisiontree.builder.DecisionTreeC45Builder;
import org.project.modules.decisiontree.builder.ForestBuilder;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataHandler;
import org.project.modules.decisiontree.data.DataLoader;
import org.project.modules.decisiontree.node.ForestNode;
import org.project.modules.decisiontree.node.TreeNode;
import org.project.modules.utils.ShowUtils;

public class RandomForest {
	
	private int randomNum = 1;
	
	private String trainFilePath = null;
	
	private String testFilePath = null;
	
	private Builder treeBuilder = null;
	
	public RandomForest() {
		
	}
	
	public RandomForest(int randomNum, String trainFilePath, 
			String testFilePath, Builder treeBuilder) {
		this.randomNum = randomNum;
		this.trainFilePath = trainFilePath;
		this.testFilePath = testFilePath;
		this.treeBuilder = treeBuilder;
	}
	
	public int getRandomNum() {
		return randomNum;
	}

	public void setRandomNum(int randomNum) {
		this.randomNum = randomNum;
	}

	public String getTrainFilePath() {
		return trainFilePath;
	}

	public void setTrainFilePath(String trainFilePath) {
		this.trainFilePath = trainFilePath;
	}

	public String getTestFilePath() {
		return testFilePath;
	}

	public void setTestFilePath(String testFilePath) {
		this.testFilePath = testFilePath;
	}

	public Builder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(Builder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Builder forestBuilder = new ForestBuilder(randomNum, treeBuilder);
		Data data = DataLoader.load(trainFilePath);
		System.out.println("data attributes len: " + data.getAttributes().length);
		List<TreeNode> treeNodes = (List<TreeNode>) forestBuilder.build(data);
		Data testData = DataLoader.load(testFilePath);
		System.out.println("testData attributes len: " + testData.getAttributes().length);
		DataHandler.fill(testData.getInstances(), data.getAttributes() , 0);
		ForestNode forestNode = new ForestNode(treeNodes);
		Object[] results = (Object[]) forestNode.classify(testData);
		ShowUtils.print(results);
	}

	public static void main(String[] args) {
		int randomNum = 10;
		Builder treeBuilder = new DecisionTreeC45Builder();
		String trainFilePath = "d:\\trainset_extract_10.txt";
		String testFilePath = "d:\\trainset_extract_1.txt";
		RandomForest randomForest = new RandomForest(randomNum, 
				trainFilePath, testFilePath, treeBuilder);
		randomForest.run();
	}
}
