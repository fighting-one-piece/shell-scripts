package org.project.common.distance;

import org.project.common.vector.Vector;

public class ManhattanDistanceMeasure implements DistanceMeasure {

	@Override
	public double distance(Vector<Double> v1, Vector<Double> v2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static double distance(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

}
