package org.project.modules.clustering.kmeans.data;

import java.util.ArrayList;
import java.util.List;

public class KMeansCluster {

	private Point center = null;
	
	private List<Point> points = null;

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public List<Point> getPoints() {
		if (null == points) {
			points = new ArrayList<Point>();
		}
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	public Point computeMeansCenter() {
		int len = getPoints().size();
		double a = 0.0, b = 0.0;
		for (Point point : getPoints()) {
			a += point.getX();
			b += point.getY();
		}
		return new Point(a / len, b / len);
	}
	
	public Point computeMediodsCenter() {
		Point targetPoint = null;
		double distance = Integer.MAX_VALUE;
		for (Point point : getPoints()) {
			double d = 0.0;
			for (Point temp : getPoints()) {
				d += distance(point, temp);
			}
			if (d < distance) {
				distance = d;
				targetPoint = point;
			}
		}
		return targetPoint;
	}
	
	public double distance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
	
}
