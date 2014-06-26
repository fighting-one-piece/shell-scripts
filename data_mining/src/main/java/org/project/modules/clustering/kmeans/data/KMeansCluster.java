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
		if (null == null) {
			points = new ArrayList<Point>();
		}
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	public Point computeCenter() {
		int len = getPoints().size();
		double a = 0.0, b = 0.0;
		for (Point point : getPoints()) {
			a += point.getX();
			b += point.getY();
		}
		Point p = new Point(a / len, b / len);
		System.out.println("compute point: " + p);
		setCenter(p);
		return p;
	}
	
}
