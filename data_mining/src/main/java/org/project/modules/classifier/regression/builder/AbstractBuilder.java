package org.project.modules.classifier.regression.builder;

public abstract class AbstractBuilder {

	protected double sigmoid(double value) {
		 return 1d / (1d + Math.exp(-value));  
	}
	
	protected double sigmoid(double[] data, double[] weights) {
		double z = 0.0;
		for (int i = 0, len = data.length; i < len; i++) {
			z += data[i] * weights[i];
		}
		return sigmoid(z);
	}
	
	public abstract void build();
}
