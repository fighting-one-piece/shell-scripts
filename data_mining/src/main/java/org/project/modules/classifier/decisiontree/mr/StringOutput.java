package org.project.modules.classifier.decisiontree.mr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class StringOutput implements Writable, Cloneable {
	
	private String result = null;
	
	public StringOutput() {
		this.result = "result";
	}
	
	public StringOutput(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		boolean isRead = dataInput.readBoolean();
		if (isRead) {
			int length = dataInput.readInt();
			byte[] buff = new byte[length];
			dataInput.readFully(buff, 0, length);
			this.result = new String(buff);
		}
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeBoolean(null != result);
		if (null != result) {
			byte[] buff = result.getBytes();
			dataOutput.writeInt(buff.length);
			dataOutput.write(buff);
		}
	}

}
