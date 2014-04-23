package org.project.modules.decisiontree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 ** 决策树（非叶结点），决策树中的每个非叶结点都引导了一棵决策树 *
 *  每个非叶结点包含一个分支属性和多个分支，分支属性的每个值对应一个分支，该分支引导了一棵子决策树
 */
public class Tree {
	
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
	
	public void read(DataInput dataInput) {
		
	}
	
	public void readFields(DataInput dataInput) throws IOException {
		int attribute_len = dataInput.readInt();
		byte[] buff = new byte[4098];
		dataInput.readFully(buff, 0, attribute_len);
		
	}
	
	public void write(DataOutput dataOutput) {
		
	}
	
	public void writeFields(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(attribute.length());
		dataOutput.writeBytes(attribute);
		for (Map.Entry<Object, Object> entry : children.entrySet()) {
			String key = String.valueOf(entry.getKey());
			dataOutput.writeInt(key.length());
			dataOutput.writeBytes(key);
			Object value = entry.getValue();
			if (!(value instanceof Tree)) {
				String v = String.valueOf(value);
				dataOutput.writeInt(v.length());
				dataOutput.writeBytes(v);
			} else {
				((Tree) value).writeFields(dataOutput);
			}
		}
	}
}
