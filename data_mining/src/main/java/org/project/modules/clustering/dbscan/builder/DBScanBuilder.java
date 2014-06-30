package org.project.modules.clustering.dbscan.builder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.clustering.dbscan.data.Point;

public class DBScanBuilder {
	
	//半径
	public static double Epislon = 20;
	//密度
	public static int MinPts = 20;
	
	public List<Point> initData() {
		List<Point> points = new ArrayList<Point>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = DBScanBuilder.class.getClassLoader().getResourceAsStream("dbscan.txt");
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				double x = Double.parseDouble(tokenizer.nextToken());
				double y = Double.parseDouble(tokenizer.nextToken());
				points.add(new Point(x , y));
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		return points;
	}
	
	//计算两点之间的欧氏距离
	public double euclideanDistance(Point a, Point b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	public List<Point> obtainNeighbors(Point current, List<Point> points) {
		List<Point> neighbors = new ArrayList<Point>();
		for (Point point : points) {
			double distance = euclideanDistance(current, point);
			if (distance < Epislon) {
				neighbors.add(point);
			}
		}
		return neighbors;
	}
	
	public void mergeCluster(Point point, List<Point> neighbors,
			int clusterId, List<Point> points) {
		point.setClusterId(clusterId);
		for (Point neighbor : neighbors) {
			if (!neighbor.isAccessed()) {
				neighbor.setAccessed(true);
				List<Point> nneighbors = obtainNeighbors(neighbor, points);
				if (nneighbors.size() > MinPts) {
					for (Point nneighbor : nneighbors) {
						if (nneighbor.getClusterId() <= 0) {
							nneighbor.setClusterId(clusterId);
						}
					}
				}
			}
			if (neighbor.getClusterId() <= 0) {
				neighbor.setClusterId(clusterId);
			}
		}
	}
	
	public void cluster(List<Point> points) {
		int clusterId = 0;
		boolean flag = true;
		while (flag) {
			for (Point point : points) {
				if (point.isAccessed()) {
					continue;
				}
				point.setAccessed(true);
				List<Point> neighbors = obtainNeighbors(point, points);
				if (neighbors.size() < MinPts) {
					//clusterId初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声 
					if(point.getClusterId() <= 0) {
						 point.setClusterId(-1);
					}
				} else {
					if(point.getClusterId() <= 0) {
						clusterId++;
					} else {
						clusterId = point.getClusterId();
					}
					mergeCluster(point, neighbors, clusterId, points);
				}
			}
		}
	}
	
	public void print(List<Point> points) {
		for (Point point : points) {
			System.out.println(point.getClusterId() + " - " + point);
		}
	}

	public void build() {
		List<Point> points = initData();
		cluster(points);
		print(points);
	}
	
	public static void main(String[] args) {
		DBScanBuilder builder = new DBScanBuilder();
		builder.build();
	}
}
