package org.project.modules.decisiontree.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.utils.ShowUtils;

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
		return vote(results);
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
		return vote(results);
	}
	
	/** *
	 * 投票
	 * @param results
	 * @return
	 */
	private Object[] vote(List<Object[]> results) {
		System.out.println("-----------results-------------");
		ShowUtils.print(results);
		System.out.println("-----------results-------------");
		int columnNum = results.get(0).length;
		Object[] finalResult = new Object[columnNum];
		for (int i = 0; i < columnNum; i++) {
			Map<Object, Integer> resultCount = new HashMap<Object, Integer>();
			for (Object[] result : results) {
				if (null == result[i]) continue;
				Integer count = resultCount.get(result[i]);
				resultCount.put(result[i], null == count ? 1 : count + 1);
			}
			int max = 0;
			Object maxResult = null;
			for (Map.Entry<Object, Integer> entry : resultCount.entrySet()) {
				if (max < entry.getValue()) {
					max = entry.getValue();
					maxResult = entry.getKey();
				}
			}
			finalResult[i] = maxResult;
		}
		return finalResult;
	}
}
