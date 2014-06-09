package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public abstract class Node implements Writable, Serializable {

	private static final long serialVersionUID = 1L;

	protected enum Type {
		LEAF, BRANCH
	}

	public abstract Type getType();

	public abstract Object classify(Data data);

	public abstract Object classify(Instance... instances);

	public static Node read(DataInput in) throws IOException {
		Type type = Type.values()[in.readInt()];
		Node node = null;
		switch (type) {
			case LEAF:
				node = new LeafNode();
				break;
			case BRANCH:
				node = new BranchNode();
				break;
			default:
				throw new IllegalStateException(
						"This implementation is not currently supported");
		}
		node.readFields(in);
		return node;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		int ordinal = getType().ordinal();
		System.out.println("write: " + ordinal);
		dataOutput.writeInt(ordinal);
		writeNode(dataOutput);
	}

	protected abstract void writeNode(DataOutput out) throws IOException;

}
