package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class TreeNodeHelper {
	
	/** 读取*/
	public static TreeNode readTreeNode(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		return (TreeNode) json2TreeNode(jsonData);
	}
	
	/** 截开树**/
	public static void purningTreeNode(TreeNode treeNode, int n, 
			int level, Set<TreeNode> treeNodes) {
		level++;
		if (level == 1) treeNodes.add(treeNode); 
		Map<Object, Object> children = treeNode.getChildren();
		for (Map.Entry<Object, Object> entry : children.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof TreeNode) {
				if (level > n) {
					children.put(entry.getKey(), null);
				} 
				purningTreeNode((TreeNode) value, n, 
						level > n ? 0 : level, treeNodes);
			}
		}
	}

	/** 
	 ** 将决策树输出到标准输出 
	 **/
	public static void print(Object obj, int level, Object from) {
//		if (level > 20) return;
		for (int i = 0; i < level; i++)
			System.out.print("|-----");
		if (from != null)
			System.out.printf("(%s):", from);
		if (obj instanceof TreeNode) {
			TreeNode tree = (TreeNode) obj;
			String attrName = tree.getAttribute();
			System.out.printf("[%s = ?]\n", attrName);
			for (Object attrValue : tree.getChildren().keySet()) {
				Object child = tree.getChild(attrValue);
				print(child, level + 1, attrName + " = "
						+ attrValue);
			}
		} else {
			System.out.printf("[CATEGORY = %s]\n", obj);
		}
	}
	
	public static Object json2TreeNode(String jsonData) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);  
		jsonConfig.setIgnoreDefaultExcludes(false);  
		jsonConfig.setAllowNonStringKeys(true); 
		JSONObject jsonObject = JSONObject.fromObject(jsonData, jsonConfig);
		return object2TreeNode(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	public static Object object2TreeNode(JSONObject jsonObject) {
		try {
			if (jsonObject.containsKey("children")) {
				TreeNode treeNode = new TreeNode();
				treeNode.setAttribute((String) jsonObject.get("attribute"));
				JSONObject v = (JSONObject) jsonObject.get("children");
				Map<Object, Object> children = new HashMap<Object, Object>();
				Set<Map.Entry<Object, Object>> entries = v.entrySet();
				for (Map.Entry<Object, Object> entry : entries) {
					Object entry_value = entry.getValue();
					if (entry_value instanceof JSONObject) {
						children.put(entry.getKey(), object2TreeNode(
								(JSONObject) entry_value));
					} else {
						children.put(entry.getKey(), entry.getValue());
					}
				}
				treeNode.setChildren(children);
				return treeNode;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static void treeNode2json(TreeNode treeNode, StringBuilder sb) {
		sb.append("{");
		sb.append("\"attribute\":");
		sb.append("\"" + treeNode.getAttribute()).append("\",");
		Map<Object, Object> children = treeNode.getChildren();
		if (children.size() != 0) {
			sb.append("\"children\":");
			sb.append("{");
			int i = 0;
			for (Map.Entry<Object, Object> entry : children.entrySet()) {
				i++;
				Object value = entry.getValue();
				sb.append("\"" + entry.getKey() + "\":");
				if (value instanceof TreeNode) {
					treeNode2json((TreeNode) value, sb);
				} else {
					sb.append("\"" + value + "\"");
				}
				if (i != children.size()) sb.append(",");
			}
			sb.append("}");
		}
		sb.append("}");
	}
	
	public static void obtainAttributes(TreeNode treeNode, Set<String> attributes) {
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
}
