package org.project.modules.clustering.kmeans.builder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.project.modules.clustering.kmeans.data.KMeansCluster;
import org.project.modules.clustering.kmeans.data.Point;

public class KMeansBuilder extends AbstractBuilder {
	
	public List<Point> initData() {
		List<Point> points = new ArrayList<Point>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = KMeansBuilder.class.getClassLoader().getResourceAsStream("kmeans1.txt");
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
	
	//计算两点之间的曼哈顿距离
	public double manhattanDistance(Point a, Point b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
	
	//计算两点之间的欧氏距离
	public double euclideanDistance(Point a, Point b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	//随机生成中心点，并生成K个聚类
	public List<KMeansCluster> genCenterAndCluster(List<Point> points, int k) {
		List<KMeansCluster> clusters = new ArrayList<KMeansCluster>();
		Random random = new Random();
		for (int i = 0, len = points.size(); i < k; i++) {
			KMeansCluster cluster = new KMeansCluster();
			Point center = points.get(random.nextInt(len));
			cluster.setCenter(center);
			cluster.getPoints().add(center);
			clusters.add(cluster);
		}
		return clusters;
	}
	
	public void genCluster(List<Point> points, List<KMeansCluster> clusters) {
		for (Point point : points) {
			KMeansCluster minCluster = null;
			double minDistance = Integer.MAX_VALUE;
			for (KMeansCluster cluster : clusters) {
				Point center = cluster.getCenter();
				System.out.println("p: " + point + " c:" + center);
				double distance = euclideanDistance(point, center);
				System.out.println(distance);
				if (distance < minDistance) {
					minDistance = distance;
					minCluster = cluster;
				}
			}
			System.out.println(minCluster);
			minCluster.getPoints().add(point);
		}
		boolean flag = true;
		for (KMeansCluster cluster : clusters) {
			Point center = cluster.getCenter();
			System.out.println("center: " + center.getX() + ":" + center.getY());
			Point newCenter = cluster.computeCenter();
			System.out.println("new center: " + center.getX() + ":" + center.getY());
			if (!center.equals(newCenter)) {
				flag = false;
			}
		}
		if (!flag) {
			genCluster(points, clusters);
		}
	}
	
	public void build() {
		List<Point> points = initData();
		List<KMeansCluster> clusters = genCenterAndCluster(points, 4);
		genCluster(points, clusters);
	}

	public static void main(String[] args) {
		KMeansBuilder builder = new KMeansBuilder();
		builder.build();
	}
	
}
