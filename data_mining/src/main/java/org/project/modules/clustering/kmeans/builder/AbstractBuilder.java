package org.project.modules.clustering.kmeans.builder;

import java.util.List;

import org.project.modules.clustering.kmeans.data.KMeansCluster;
import org.project.modules.clustering.kmeans.data.Point;

public abstract class AbstractBuilder {
	
	//计算两点之间的曼哈顿距离
	protected double manhattanDistance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
	
	//计算两点之间的欧氏距离
	protected double euclideanDistance(Point a, Point b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	protected void printClusters(List<KMeansCluster> clusters) {
		for (KMeansCluster cluster : clusters) {
			System.out.println("center: " + cluster.getCenter());
			System.out.println("cluster size: " + cluster.getPoints().size());
			for (Point point : cluster.getPoints()) {
				System.out.println(point);
			}
		}
	}
}
