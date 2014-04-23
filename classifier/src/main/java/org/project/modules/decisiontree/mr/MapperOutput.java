package org.project.modules.decisiontree.mr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.project.modules.decisiontree.Tree;

public class MapperOutput implements Writable, Cloneable {
	
	private Tree tree = null;
	
	public MapperOutput(Tree tree) {
		this.tree = tree;
	}
	
	public Tree getTree() {
		return tree;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		boolean isReadTree = dataInput.readBoolean();
		if (isReadTree) {
			
		}
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeBoolean(null != tree);
		if (null != tree) {
			
		}
	}

}
