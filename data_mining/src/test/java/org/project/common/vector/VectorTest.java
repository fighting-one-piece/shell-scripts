package org.project.common.vector;

import org.junit.Test;

public class VectorTest {

	@Test
	public void test() {
		Double[] values = new Double[]{1.0, 2.0};
		Vector<Double> vector = new DoubleVector(values);
		System.out.println(vector.getElement(0).get());
		System.out.println(vector.getElement(1).get());
		Vector<Double> newVector = vector.plus(1.0);
		System.out.println(newVector.getElement(0).get());
		System.out.println(newVector.getElement(1).get());
	}
}
