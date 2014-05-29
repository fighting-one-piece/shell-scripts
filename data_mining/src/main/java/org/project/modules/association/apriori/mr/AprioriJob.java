package org.project.modules.association.apriori.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

public class AprioriJob {
	
	private Configuration conf = null;
	
	public void run(String[] args) {
		if (null == conf) conf = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error");
				System.out.println("error, please input two path. input and output");
				System.out.println("1. input path.");
				System.out.println("2. output path.");
				System.out.println("3. min support.");
				System.exit(2);
			}
			Frequency1ItemSetMR.main(inputArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AprioriJob job = new AprioriJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}
}
