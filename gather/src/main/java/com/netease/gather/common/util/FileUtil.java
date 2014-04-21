/*
 * Created on 2004-8-29
 */
package com.netease.gather.common.util;

import java.io.*;

/**
 * @author tangh
 * 对文件进行操作
 */
public class FileUtil {

	/**
	 * 拷贝文件
	 * @param oldname
	 * @param newname
	 * @param overwrite
	 * @return
	 */
	public static boolean copy(String oldname, String newname, boolean overwrite)throws IOException {
		if (!overwrite && new File(newname).exists()) return false;
    
		FileInputStream input = new FileInputStream(oldname);
        String newDire = newname.substring(0, newname.lastIndexOf(File.separator));
        mkdirs(newDire);
		FileOutputStream output = new FileOutputStream(newname);
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = input.read(b)) != -1)
		{
			output.write(b, 0, len);
		}
		output.flush();
		output.close();
		input.close();
		return true;
	}
	
	/**
	 *	删除一个指定的文件
	 */
	public static boolean deleteFile(String FileName) {
		boolean bRet = false;
		if (FileName!=null && FileName.length() > 0) {
			File filename = new File(FileName);
			if (filename.delete()) {
				bRet = true;
			}
		}
		return bRet;
	}
	/**
	 *	删除一个指定的目录并且包括该目录中的子目录和文件
	 */
	public static boolean deleteTree(String PathName) {
		File path = new File(PathName);
		String list[] = path.list();
		boolean isok = true;
		if (list != null) {
			int length = list.length;
			for (int x = 0; x < length; x++) {
				String temp =PathName+"/"+list[x];
				File f = new File(temp);
				if (f.isFile()) {
					isok = f.delete();
					continue;
				}
				if (f.isDirectory()) {
					if (isEmpty(f)) {
						isok = f.delete();
					} else {
						isok = deleteTree(temp);
					}
				}
				if (!isok) {
					return false;
				}
			}
			path.delete();
		}
		return isok;
	}
	/**
	 *	获得一个文件或一个目录中的全部文件的尺寸
	 */
	public static long getFilesSize(String FileName) {
		long lRet = 0L;
		File file = new File(FileName);
		if (file.isDirectory()) {
			String list[] = file.list();
			if (list != null) {
				for (int x = 0; x < list.length; x++) {
					String temp =FileName+"/"+list[x];
					File f = new File(temp);
					if (f.isFile()) {
						lRet += f.length();
					} else if (f.isDirectory()) {
						lRet += getFilesSize(temp);
					}
				}

			}
		} else {
			lRet = file.length();
		}
		return lRet;
	}
	/**
	 *	文件或者目录重命名
	 */
	public static boolean renameFile(String SourceFileName, String TargetFileName) {
		boolean bRet = false;
		File fileS = new File(SourceFileName);
		File fileT = new File(TargetFileName);
		if (fileS.exists() && fileS.renameTo(fileT)) {
			bRet = true;
		}
		return bRet;
	}
	/**
	 *	创建一个目录
	 */
	public static boolean mkDir(String DirectoryName) {
		boolean bRet = false;
		File file = new File(DirectoryName);
		if (!file.exists() && file.mkdirs()) {
			bRet = true;
		}
		return bRet;
	}

	/**
	 *	返回指定名称是否为空
	 */
	private static boolean isEmpty(File tempdir) {
		if (tempdir.exists() && tempdir.isDirectory()) {
			String list[] = tempdir.list();
			if (list.length == 0) {
				return true;
			}
		}else{
			return true;
		}
		return false;
	}

	/**
	 * GBK编码写入文件
	 * @param filename
	 * @param str
	 * @throws java.io.IOException
	 */
	public static void writIn(String filename, String str) throws IOException{
		writIn(filename,str,"GBK");
	}
	
	/**
	 *  往一个指定文件里全新写入指定编码字符串
	 * @param filename
	 * @param str
	 * @param strCode 编码，如GBK
	 * @throws java.io.IOException
	 */
	public static void writIn(String filename, String str, String strCode) throws IOException {
		FileWriter fw = null;
		try {
			mkDir(filename.substring(0, filename.lastIndexOf("/")));
			fw = new FileWriter(filename); //建立FileWrite变量,并设定由fw变量变数引用
			BufferedWriter bw = new BufferedWriter(fw);
			//建立BufferedWriter变量,并设定由bw变量变数引用
			//将字串写入文件
			bw.write(new String(str.getBytes(strCode)));
			bw.flush(); //将资料更新至文件
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(fw!=null) fw.close(); //关闭档案
		}
	}
	
 	/**将信息记录追加到一个文件
 	 * 常用于记日志
 	 * @param msg
 	 * @param file
 	 * @throws java.io.IOException
 	 */
 	public static void log(String msg,String file) throws IOException {
		 FileWriter logFile = new FileWriter(file,true);
		 PrintWriter logPrintWriter = new PrintWriter(logFile);
	   logPrintWriter.println(msg);
	   logPrintWriter.flush();
 	} 
	
	/**
	 * 读取文件
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static String read(String filename) throws IOException{
			File file = new File(filename);
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
			BufferedReader reader = new BufferedReader(read);
			String line = "";
			StringBuffer readfile = new StringBuffer();
			while ((line = reader.readLine()) != null){
				//readfile.append(line + "\r\n");
				readfile.append(line);
			}
			read.close();
			return readfile.toString().trim();
	}
	/**
	 * 读取文件
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readNoNewLine(String filename) throws IOException{
	  File file = new File(filename);
	  InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
	  BufferedReader reader = new BufferedReader(read);
	  String line = "";
	  StringBuffer readfile = new StringBuffer();
	  while ((line = reader.readLine()) != null){
	    readfile.append(line + "\r");
	  }
	  read.close();
	  return readfile.toString().trim();
	}
 	
	/**
	 * 从文件路径得到文件名。
	 * @param filePath 文件的路径，可以是相对路径也可以是绝对路径
	 * @return 对应的文件名
	 * @since  0.4
	 */
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	/**
	 * 从文件名得到文件绝对路径。
	 * @param fileName 文件名
	 * @return 对应的文件路径
	 * @since  0.4
	 */
	public static String getFilePath(String fileName) {
		File file = new File(fileName);
		return file.getAbsolutePath();
	}

	/**
	 * 得到文件的类型。
	 * 实际上就是得到文件名中最后一个“.”后面的部分。
	 * @param fileName 文件名
	 * @return 文件名中的类型部分
	 * @since  0.5
	 */
	public static String getTypePart(String fileName) {
		int point = fileName.lastIndexOf('.');
		int length = fileName.length();
		if (point == -1 || point == length - 1) {
			return "";
		} else {
			return fileName.substring(point + 1, length);
		}
	}

	/**
	 * 得到文件的类型。
	 * 实际上就是得到文件名中最后一个“.”后面的部分。
	 * @param file 文件
	 * @return 文件名中的类型部分
	 * @since  0.5
	 */
	public static String getFileType(File file) {
		return getTypePart(file.getName());
	}

	/**列出目录下的文件或子目录*/
	public static String[] dirfile(String dirname) throws IOException {
		File dir = new File(dirname);
		return  dir.list();
	}
	/**
	 * 读文件。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean canRead(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).canRead();
	}
	/**
	 * 写文件。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean canWrite(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).canWrite();
	}
	/**
	 * 删除文件。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean delete(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).delete();
	}

	/**
	 * @param remoteAbsPath
	 * @throws Exception
	 */
	public static void deleteOnExit(String remoteAbsPath) throws Exception {
		getFile(remoteAbsPath).deleteOnExit();
	}
	/**
	 * 判断文件是否存在。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean exists(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).exists();
	}

	/**
	 * 此处插入方法描述。
	 * @return java.lang.String
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static String getCanonicalPath(String remoteAbsPath)
		throws Exception, IOException {
		return getFile(remoteAbsPath).getCanonicalPath();
	}

	/**
	 * 获得某个文件。
	 * @return java.io.File
	 * @param remoteAbsPath java.lang.String
	 */
	private static File getFile(String remoteAbsPath) {
		return new File(remoteAbsPath);
	}
	/**
	 * 获取服务器工作目录。
	 * @return java.lang.String
	 * @exception java.io.IOException 异常说明。
	 * @exception Exception 异常说明。
	 */
	public static String getServerWorkDir() throws IOException, Exception {
		return getFile(".").getCanonicalPath();
	}
	/**
	 * 判断是否为目录。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean isDirectory(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).isDirectory();
	}
	/**
	 * 判断是否为文件。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean isFile(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).isFile();
	}
	/**
	 * 判断是否为隐藏文件。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean isHidden(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).isHidden();
	}
	/**
	 * 获得最后修改时间。
	 * @return long
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static long lastModified(String remoteAbsPath) throws Exception {
		return getFile(remoteAbsPath).lastModified();
	}
	/**
	 * 获得文件长度。
	 * @return long
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static long length(String remoteAbsPath) throws IOException {
		return getFile(remoteAbsPath).length();
	}
	/**
	 * 获取目录列表。
	 * @return java.lang.String[]
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static String[] list(String remoteAbsPath) throws IOException {
		return getFile(remoteAbsPath).list();
	}
	/**
	 * 创建目录。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean mkdir(String remoteAbsPath) throws IOException {
		return getFile(remoteAbsPath).mkdir();
	}
	/**
	 * 创建目录。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean mkdirs(String remoteAbsPath) throws IOException{
		return getFile(remoteAbsPath).mkdirs();
	}
	/**
	 * 修改服务器文件名称。
	 * @return boolean
	 * @param origRemoteAbsPath java.lang.String
	 * @param renamedRemoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean renameTo(String origRemoteAbsPath,String renamedRemoteAbsPath)
		throws Exception {
		return getFile(origRemoteAbsPath).renameTo(getFile(renamedRemoteAbsPath));
	}
	/**
	 * 设置最后修改时间。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean setLastModified(String remoteAbsPath, long time)
		throws Exception {
		return getFile(remoteAbsPath).setLastModified(time);
	}
	/**
	 * 设置文件只读。
	 * @return boolean
	 * @param remoteAbsPath java.lang.String
	 * @exception Exception 异常说明。
	 */
	public static boolean setReadOnly(String remoteAbsPath) throws IOException {
		return getFile(remoteAbsPath).setReadOnly();
	}

    /**
     * 创建任意深度的文件所在文件夹,可以用来替代直接new File(path)。
     *
     * @param path
     * @return File对象
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static File createFile(String path) {
        File file = new File(path);
        //寻找父目录是否存在
        File parent = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
        //如果父目录不存在，则递归寻找更上一层目录
        if (!parent.exists()) {
            createFile(parent.getPath());
            //创建父目录
            parent.mkdirs();
        }
        return file;
    }
}
