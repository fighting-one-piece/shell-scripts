package org.project.modules.decisiontree.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataLoader;
import org.project.modules.decisiontree.node.TreeNode;

public class ForestBuilder implements Builder {
	
	private int random = 0;
	
	private Builder builder = null;
	
	public ForestBuilder(int random, Builder builder) {
		this.random = random;
		this.builder = builder;
	}

	@Override
	public Object build(Data data) {
		ExecutorService pools = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		List<Future<TreeNode>> futures = new ArrayList<Future<TreeNode>>();
		for (int i = 0; i < random; i++) {
			DecisionCallable callable = new DecisionCallable(data, builder);
			futures.add(pools.submit(callable));
		}
		System.out.println("futures size: " + futures.size());
		List<TreeNode> results = new ArrayList<TreeNode>();
		handleFuture(futures, results);
		int futureLen = futures.size();
		int resultsLen = results.size();
		while (resultsLen < futureLen) {
			handleFuture(futures, results);
			resultsLen = results.size();
		}
		pools.shutdown();
		return results;
	}
	
	private void handleFuture(List<Future<TreeNode>> futures, List<TreeNode> results) {
		Iterator<Future<TreeNode>> iterator = futures.iterator();
		while (iterator.hasNext()) {
			Future<TreeNode> future = iterator.next();
			if (future.isDone()) {
				try {
					results.add(future.get());
					iterator.remove();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
	}

}

class DecisionCallable implements Callable<TreeNode> {
	
	private Data data = null;
	
	private Builder builder = null;
	
	public DecisionCallable(Data data, Builder builder) {
		this.data = data;
		this.builder = builder;
	}

	@Override
	public TreeNode call() throws Exception {
		Data randomData = DataLoader.loadRandom(data);
		Object object = builder.build(randomData);
		return null != object ? (TreeNode) object : null;
	}
	
}
