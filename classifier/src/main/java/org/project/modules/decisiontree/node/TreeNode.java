package org.project.modules.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.Instance;
import org.project.modules.decisiontree.original.JSONUtils;

/**
 ** 决策树（非叶结点），决策树中的每个非叶结点都引导了一棵决策树 *
 *  每个非叶结点包含一个分支属性和多个分支，分支属性的每个值对应一个分支，该分支引导了一棵子决策树
 */
public class TreeNode extends Node implements Writable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String attribute;
	private Map<Object, Object> children = new HashMap<Object, Object>();

	public TreeNode() {
		
	}
	
	public TreeNode(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	public Object getChild(Object attrValue) {
		return children.get(attrValue);
	}

	public void setChild(Object attrValue, Object child) {
		children.put(attrValue, child);
	}

	public Set<Object> getAttributeValues() {
		return children.keySet();
	}
	
	public Map<Object, Object> getChildren() {
		return children;
	}
	
	@Override
	public Object classify(Data data) {
		List<Instance> instances = data.getInstances();
		return classify(instances.toArray(new Instance[0]));
	}
	
	@Override
	public Object classify(Instance... instances) {
		int length = instances.length;
		if (length == 0) return null;
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = classify(instances[i]);
			System.out.println(i + "--->"+ result[i]);
		}
		return result;
	}
	
	public Object classify(Instance instance) {
		Object attributeValue = instance.getAttribute(attribute);
		System.out.println("attributeValue: " + attributeValue);
		if (null == attributeValue) return null;
		for (Map.Entry<Object, Object> entry : children.entrySet()) {
			if (attributeValue.equals(entry.getKey())) {
				Object value = entry.getValue();
				System.out.println("value: " + value);
				if (value instanceof TreeNode) {
					return ((TreeNode) value).classify(instance);
				} else {
					return value;
				}
			} 
		}
		return null;
	}
	
	public static TreeNode read(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		System.out.println(jsonData);
		return (TreeNode) JSONUtils.parseJsonData(jsonData, TreeNode.class);
	}
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		TreeNode temp = (TreeNode) JSONUtils.parseJsonData(jsonData, TreeNode.class);
		this.attribute = temp.getAttribute();
		this.children = temp.getChildren();
	}
	
	@Override
	public void write(DataOutput dataOutput) throws IOException {
		String jsonData = JSONUtils.object2json(this, new String[]{"attributeValues"});
		dataOutput.writeInt(jsonData.length());
		dataOutput.write(jsonData.getBytes());
	}
	
	/** 
	 ** 将决策树输出到标准输出 
	 **/
	public void print(Object obj, int level, Object from) {
		for (int i = 0; i < level; i++)
			System.out.print("|-----");
		if (from != null)
			System.out.printf("(%s):", from);
		if (obj instanceof TreeNode) {
			TreeNode tree = (TreeNode) obj;
			String attrName = tree.getAttribute();
			System.out.printf("[%s = ?]\n", attrName);
			for (Object attrValue : tree.getAttributeValues()) {
				Object child = tree.getChild(attrValue);
				print(child, level + 1, attrName + " = "
						+ attrValue);
			}
		} else {
			System.out.printf("[CATEGORY = %s]\n", obj);
		}
	}
	
	public static void main(String[] args) {
		TreeNode root = new TreeNode("aaa");
		root.setChild("a1", "a1");
		TreeNode lev1 = new TreeNode("bbb");
		lev1.setChild("b1", "b1");
		TreeNode lev2 = new TreeNode("ccc");
		lev2.setChild("c1", "c1");
		lev1.setChild("b2", lev2);
		root.setChild("a2", lev1);
		String s = JSONUtils.object2json(root, new String[]{"attributeValues"});
		System.out.println(s);
		
		TreeNode treeNode = (TreeNode) JSONUtils.parseJsonTreeData(s, TreeNode.class);
		treeNode.print(treeNode, 0, null);
	}
	
	
}
