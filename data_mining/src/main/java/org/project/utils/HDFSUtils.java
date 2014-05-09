package org.project.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtils {

	public static final String HDFS_URL = "hdfs://centos.host1:9000/user/hadoop/data/";
	
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
	
	public static void copyFromLocalFile(Configuration conf, 
			String localPath, String hdfsPath) {
		Path path = new Path(hdfsPath);
		copyFromLocalFile(conf, localPath, path);
	}
	
	public static void copyFromLocalFile(Configuration conf, 
			String localPath, Path path) {
		if (conf == null) {
			conf = new Configuration();
		}
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(localPath));
			FileSystem fs = path.getFileSystem(conf);
			output = fs.create(path);
			IOUtils.copy(input, output);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
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
