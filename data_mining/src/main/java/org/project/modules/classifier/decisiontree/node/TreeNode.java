package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Writable;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

/**
 ** 决策树（非叶结点），决策树中的每个非叶结点都引导了一棵决策树 *
 *  每个非叶结点包含一个分支属性和多个分支，分支属性的每个值对应一个分支，该分支引导了一棵子决策树
 */
public class TreeNode extends Node implements Writable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String attribute = null;
	
	private Map<Object, Object> children = new HashMap<Object, Object>();

	public TreeNode() {
		
	}
	
	public TreeNode(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Object getChild(Object attrValue) {
		return children.get(attrValue);
	}

	public void setChild(Object attrValue, Object child) {
		children.put(attrValue, child);
	}

	public Map<Object, Object> getChildren() {
		return children;
	}
	
	public void setChildren(Map<Object, Object> children) {
		this.children = children;
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
		}
		return result;
	}
	
	public Object classify(Instance instance) {
		Object attributeValue = instance.getAttribute(attribute);
		if (null == attributeValue) return null;
		for (Map.Entry<Object, Object> entry : children.entrySet()) {
			if (attributeValue.equals(entry.getKey())) {
				Object value = entry.getValue();
				if (value instanceof TreeNode) {
					return ((TreeNode) value).classify(instance);
				} else {
					return value;
				}
			} 
		}
		return null;
	}
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		TreeNode temp = (TreeNode) TreeNodeHelper.json2TreeNode(jsonData);
		this.attribute = temp.getAttribute();
		this.children = temp.getChildren();
	}
	
	@Override
	public void write(DataOutput dataOutput) throws IOException {
		StringBuilder sb = new StringBuilder();
		TreeNodeHelper.treeNode2json(this, sb);
		System.out.println(sb.toString());
		dataOutput.writeInt(sb.length());
		dataOutput.write(sb.toString().getBytes());
	}
	
	
}
