package org.project.modules.classifier.decisiontree.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;

public class DecisionTreeJob {
	
//	private Path input = null;
//	
//	private Path output = null;
	
	public void run() {
		
	}
	
	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			Path input = new Path(inputArgs[0]);
			Path output = new Path(inputArgs[1]);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
