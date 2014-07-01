package org.project.utils;

public class DistanceUtils {
	
	public static double euclidean(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.pow((p2[i] - p1[i]), 2);
		}
		return Math.sqrt(result);
	}

	public static double manhattan(double[] p1, double[] p2) {
		double result = 0.0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}
	
	public static double cosine(double[] p1, double[] p2) {
		double a = 0, b = 0, c = 0;
		for (int i = 0; i < p1.length; i++) {
			a += p1[i] * p2[i];
			b += Math.pow(p1[i], 2);
			c += Math.pow(p2[i], 2);
		}
		b = Math.sqrt(b);
		c = Math.sqrt(c);
		return a / (b * c);
	}
}
