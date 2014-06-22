package org.project.modules.classifier.regression.builder;

import org.project.modules.classifier.regression.data.DataSet;
import org.project.modules.classifier.regression.data.DataSetHandler;

public class RandomGradientAscentBuilder extends AbstractBuilder {
	
	private DataSet initialize() {
		return DataSetHandler.load("d:\\regression.txt");
	}
	
	private void randomGradientAscent(DataSet dataSet) {
		double[][] datas = dataSet.obtainDatas();
		double[] weights = new double[]{1.0, 1.0};
		double[] categories = dataSet.obtainCategories();
		double alpha = 0.01;
		for (int i = 0, len = datas.length; i < len; i++) {
			double h = sigmoid(datas[i], weights);
			double error = categories[i] - h;
			for (int j = 0, len1 = weights.length; j < len1; j++) {
				weights[j] += alpha * error * datas[i][j]; 
			}
		}
		for(double weight : weights) {
			System.out.println("weight: " + weight);
		}
	}
	
	@Override
	public void build() {
		DataSet dataSet = initialize();
		randomGradientAscent(dataSet);
	}
	
	public static void main(String[] args) {
		RandomGradientAscentBuilder builder = new RandomGradientAscentBuilder();
		builder.build();
	}

}
