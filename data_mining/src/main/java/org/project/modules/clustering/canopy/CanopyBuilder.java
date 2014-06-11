package org.project.modules.clustering.canopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CanopyBuilder {

	private double T1 = 8;

	private double T2 = 4;

	private List<Point> points = null;
	
	private List<Canopy> canopies = null;
	
	public CanopyBuilder() {
		init();
	}

	public void init() {
		points = new ArrayList<Point>();
		points.add(new Point(8.1, 8.1));
		points.add(new Point(7.1, 7.1));
		points.add(new Point(6.2, 6.2));
		points.add(new Point(7.1, 7.1));
		points.add(new Point(2.1, 2.1));
		points.add(new Point(1.1, 1.1));
		points.add(new Point(0.1, 0.1));
		points.add(new Point(3.0, 3.0));
		canopies = new ArrayList<Canopy>();
	}
	
	public double distance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	public void run() {
		while (points.size() > 0) {
			Iterator<Point> iterator = points.iterator();
			while (iterator.hasNext()) {
				Point temp = iterator.next();
				System.out.println(temp);
				if (canopies.size() == 0) {
					Canopy canopy = new Canopy();
					canopy.setCenter(temp);
					canopy.getPoints().add(temp);
					canopies.add(canopy);
					iterator.remove();
					continue;
				}
				List<Canopy> newCanopies = new ArrayList<Canopy>();
				for (Canopy canopy : canopies) {
					Point center = canopy.getCenter();
					System.out.println("center: " + center);
					double d = distance(temp, center);
					System.out.println("d: " + d);
					if (d < T2) {
						canopy.getPoints().add(temp);
						iterator.remove();
						break;
					} else if (d < T1) {
						canopy.getPoints().add(temp);
					} else if (d > T1) {
						Canopy newCanopy = new Canopy();
						newCanopy.setCenter(temp);
						newCanopy.getPoints().add(temp);
						newCanopies.add(newCanopy);
						iterator.remove();
						break;
					}
				}
				if (newCanopies.size() > 0) {
					canopies.addAll(newCanopies);
				}
			}
		}
		for (Canopy c : canopies) {
			System.out.println(c.getCenter());
			System.out.println(c.getPoints().size());
		}
	}

	public static void main(String[] args) {
		CanopyBuilder builder = new CanopyBuilder();
		builder.run();
	}

}
