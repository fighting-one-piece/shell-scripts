package org.project.modules.decisiontree.original;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;

/**
 ** 决策树（非叶结点），决策树中的每个非叶结点都引导了一棵决策树 *
 *  每个非叶结点包含一个分支属性和多个分支，分支属性的每个值对应一个分支，该分支引导了一棵子决策树
 */
public class Tree implements Writable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String attribute;
	private Map<Object, Object> children = new HashMap<Object, Object>();

	public Tree(String attribute) {
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
	
	public static void tree2json(Tree tree, StringBuilder sb) {
		if (null == tree) return; 
		sb.append("{");
		sb.append("attribute:").append(tree.getAttribute());
		Map<Object, Object> children = tree.getChildren();
		if (children.size() > 0) {
			sb.append(",");
			sb.append("children: {");
			for (Map.Entry<Object, Object> entry : children.entrySet()) {
				sb.append(entry.getKey()).append(":");
				Object value = entry.getValue();
				if (value instanceof String) {
					sb.append(value);
				} else if (value instanceof Tree) {
					tree2json((Tree) value, sb);
				}
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");
		} 
		sb.append("}");
	}
	
	public static Tree read(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		return (Tree) JSONUtils.parseJsonData(jsonData, Tree.class);
	}
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		Tree temp = (Tree) JSONUtils.parseJsonData(jsonData, Tree.class);
		this.attribute = temp.getAttribute();
		this.children = temp.getChildren();
	}
	
	@Override
	public void write(DataOutput dataOutput) throws IOException {
		String jsonData = JSONUtils.object2json(this);
		System.out.println(jsonData.length() + " : " + jsonData);
		dataOutput.writeInt(jsonData.length());
		dataOutput.write(jsonData.getBytes());
	}
	
	public static void main(String[] args) throws Exception {
		Tree t_1 = new Tree("t_1");
		t_1.setChild("t_1_1", "t_1_1");
		Tree t_2 = new Tree("t_2");
		t_2.setChild("t_2_1", "t_2_1");
		Tree t_3 = new Tree("t_3");
		t_2.setChild("t_2_2", t_3);
		t_1.setChild("t_1_2", t_2);
		
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(new File("d:\\tree.txt")));
		out.writeInt(250);
		out.writeObject(t_1);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(new File("d:\\tree.txt")));
		int val = in.readInt();
		System.out.println(val);
		Tree t = (Tree) in.readObject();
		System.out.println(t.attribute);
		System.out.println(t.getChildren());
		in.close();
		
	}

	
}
