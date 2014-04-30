package org.project.common.matrix;

public class DefaultMatrix extends AbstractMatrix {

	private Object[][] values = null;
	
	protected DefaultMatrix(int rows, int columns) {
		super(rows, columns);
	}

	public Object[][] getValues() {
		return values;
	}

	public void setValues(Object[][] values) {
		this.values = values;
	}
	
	

}
