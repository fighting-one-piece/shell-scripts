package org.project.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DFSUtils {

	public static final String HDFS_URL = "";

	public static Path[] getPathFiles(FileSystem fs, Path path)
			throws IOException {
		if (!fs.exists(path)) {
			throw new IOException("path not exists : " + path);
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

	public static void delete(Configuration conf, Iterable<Path> paths)
			throws IOException {
		if (conf == null) {
			conf = new Configuration();
		}
		for (Path path : paths) {
			FileSystem fs = path.getFileSystem(conf);
			if (fs.exists(path)) {
				fs.delete(path, true);
			}
		}
	}

	public static void delete(Configuration conf, Path... paths)
			throws IOException {
		delete(conf, Arrays.asList(paths));
	}
}
