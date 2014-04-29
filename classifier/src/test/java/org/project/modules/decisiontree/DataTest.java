package org.project.modules.decisiontree;

import org.junit.Test;
import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.DataLoader;

public class DataTest {

	@Test
	public void loadRandomData() {
		Data data = DataLoader.load("D:\\trains14.txt");
		System.out.println("data attribute len: " + data.getAttributes().length);
		System.out.println("data instances len: " + data.getInstances().size());
		for (int i = 0; i < 20; i++) {
			Data randomData = DataLoader.loadRandom(data, 3);
			System.out.println("random data attribute len: " + randomData.getAttributes().length);
			System.out.println("random data instances len: " + randomData.getInstances().size());
		}
	}
}
