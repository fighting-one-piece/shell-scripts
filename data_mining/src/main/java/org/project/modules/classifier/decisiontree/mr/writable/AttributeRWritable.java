package org.project.modules.classifier.decisiontree.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AttributeRWritable implements Writable, Cloneable {
	
	private String attribute = null;
	
	private double gainRatio = 0.0;
	
	private String splitPoints = null;
	
	public AttributeRWritable() {
		
	}
	
	public AttributeRWritable(String attribute, double gainRatio, String splitPoints) {
		this.attribute = attribute;
		this.gainRatio = gainRatio;
		this.splitPoints = splitPoints;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.attribute = new String(buff);
		this.gainRatio = dataInput.readDouble();
		length = dataInput.readInt();
		buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		this.splitPoints = new String(buff);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(attribute.length());
		dataOutput.writeBytes(attribute);
		dataOutput.writeDouble(gainRatio);
		dataOutput.writeInt(splitPoints.length());
		dataOutput.writeBytes(splitPoints);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getSplitPoints() {
		return splitPoints;
	}

	public void setSplitPoints(String splitPoints) {
		this.splitPoints = splitPoints;
	}

	public double getGainRatio() {
		return gainRatio;
	}

	public void setGainRatio(double gainRatio) {
		this.gainRatio = gainRatio;
	}
	
	public String[] obtainSplitPoints() {
		if (null == splitPoints || splitPoints.length() == 0) {
			return null;
		}
		return splitPoints.contains(",") ? splitPoints.split(",") :
			new String[]{splitPoints};
	}

}
