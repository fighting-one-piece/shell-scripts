package org.project.utils;

import java.io.File;

public class FileUtils {
	
	private FileUtils() {
		
	}

	public static String obtainOSTmpPath() {
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("os : " + os);
		String tmpPath = null;
		if (os.contains("windows")) {
			tmpPath = System.getProperty("java.io.tmpdir");
		} else if (os.contains("linux")) {
			tmpPath = System.getProperty("user.home") + File.separator 
					+ "temp" + File.separator;
		}
		System.out.println("tmpPath: " + tmpPath);
		return tmpPath;
	}
	
	public static String obtainRandomTxtPath() {
		return obtainOSTmpPath() + IdentityUtils.generateUUID() + ".txt";
	}
}
