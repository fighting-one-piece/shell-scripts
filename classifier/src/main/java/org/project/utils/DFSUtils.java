package org.project.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DFSUtils {

	public static Path[] getPathFiles(FileSystem fs, Path path) throws IOException {
		if (!fs.exists(path)) {
			System.err.println("Path Not Exist");
		}
		List<Path> files = new ArrayList<Path>();
		for (FileStatus file : fs.listStatus(path)) {
			if (file.isDir() || file.getPath().getName().startsWith("_")) {
				continue;
			}
			files.add(file.getPath());
		}
		if (files.isEmpty()) {
			throw new IOException("No File Found !");
		}
		return files.toArray(new Path[files.size()]);
	}
}
