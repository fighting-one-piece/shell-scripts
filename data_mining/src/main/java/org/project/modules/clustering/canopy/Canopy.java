package org.project.modules.clustering.canopy;

import java.util.ArrayList;
import java.util.List;

public class Canopy {

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
	
	
}
