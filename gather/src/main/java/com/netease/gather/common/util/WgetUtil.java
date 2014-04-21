/**
 * 
 */
package com.netease.gather.common.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

/**
 * @author tangh 页面抓取
 */
public class WgetUtil {

  protected static Log logger = LogFactory.getLog(WgetUtil.class);

  /**
   * 抓取网页内容(包含换行) 此方法兼容HTTP和FTP协议
   */
  public static String wgetDocument(String urlString) {
    return wgetDocument(urlString, "GBK");
  }

  /**
   * 抓取网页内容(包含换行) 此方法兼容HTTP和FTP协议
   */
  public static String wgetDocument(String urlString, String charset) {
    StringBuffer document = new StringBuffer();
    try {
      URL url = new URL(urlString);
      URLConnection conn = url.openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn
          .getInputStream(), charset));
      String line = null;
      while ((line = reader.readLine()) != null) {
        document.append(line).append("\n");
      }
      reader.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return document.toString();
  }

  /**
   * 抓取网页内容(纯文本，不包含换行) 此方法兼容HTTP和FTP协议
   */
  public static String wgetTxt(String urlString) {
    StringBuffer document = new StringBuffer();
    try {
      URL url = new URL(urlString);
      URLConnection conn = url.openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn
          .getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        document.append(line);
      }
      reader.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return document.toString();
  }

  /**
   * 抓取文件(图片等)保存到本地 此方法只能用于HTTP协议
   */
	public static boolean wgetFile(String fileUrl, String fileName) {
		return wgetFile(fileUrl, fileName, null);
	}
	
	/**
	 * 抓取文件(图片等)保存到本地 此方法只能用于HTTP协议
	 */
	public static boolean wgetFile(String fileUrl, String fileName,Map<String,String> headers) {
		try {
			java.io.File file = new java.io.File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// if (!file.exists()) {
			URL url = new URL(fileUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (headers != null && headers.size() > 0) {
				for (Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			DataInputStream in = new DataInputStream(connection.getInputStream());
			DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
			byte[] buffer = new byte[4096];
			int count = 0;
			
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			// }
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage() + " when fileUrl is:" + fileUrl, e);
			return false;
		}
	}

  /**
   * 抓取文件(图片等)保存到本地 此方法只能用于HTTP协议
   * 
   * @param source
   *          源文件
   * @param 目标文件
   */
  public static void buildHTML(String source, String dist) {
    if (StringUtil.isEmpty(source) || StringUtil.isEmpty(dist)) {
      return;
    }
    File target;
    String data;
    PrintWriter targetWriter = null;
    BufferedReader reader = null;
    String path = dist.substring(0,dist.lastIndexOf("/")+1);
    String name = dist.substring(dist.lastIndexOf("/")+1,dist.lastIndexOf("."));
    
    String bak = path + name + "_bak." + FileUtil.getTypePart(dist);
    try {
      target = new File(bak);
      if (!target.getParentFile().exists()) {
        target.getParentFile().mkdirs();
      }
      target.createNewFile();
      targetWriter = new PrintWriter(target, "GBK");
      reader = new BufferedReader(new InputStreamReader(new DataHandler(
          new URLDataSource(new URL(source))).getInputStream(), "GBK"));
      while ((data = reader.readLine()) != null) {
        targetWriter.println(data);
      }
      targetWriter.flush();
      try {
        File file = new File(bak);
//      if (file.exists() && file.length() > 1000) {
        if (file.exists()) {
          FileUtil.copy(bak, dist, true);
          FileUtil.deleteFile(bak);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      logger.error("createHTML error:" + e.toString());
    } finally {
      try {
        targetWriter.close();
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        logger.error("createHTML error: close reader error!");
        targetWriter = null;
        reader = null;
      }
      target = null;
      targetWriter = null;
      reader = null;
    }
  }
  
  /**
   * 抓取文件(图片等)保存到本地 此方法只能用于HTTP协议
   * 
   * @param source
   *          源文件
   * @param 目标文件
   */
  public static void buildHTML(String source, String dist,String charset) {
	if(StringUtil.isEmpty(charset)){
		charset = "GBK";
	}
    if (StringUtil.isEmpty(source) || StringUtil.isEmpty(dist)) {
      return;
    }
    File target;
    String data;
    PrintWriter targetWriter = null;
    BufferedReader reader = null;
    String path = dist.substring(0,dist.lastIndexOf("/")+1);
    String name = dist.substring(dist.lastIndexOf("/")+1,dist.lastIndexOf("."));
    
    String bak = path + name + "_bak." + FileUtil.getTypePart(dist);
    try {
      target = new File(bak);
      if (!target.getParentFile().exists()) {
        target.getParentFile().mkdirs();
      }
      target.createNewFile();
      targetWriter = new PrintWriter(target, charset);
      reader = new BufferedReader(new InputStreamReader(new DataHandler(
          new URLDataSource(new URL(source))).getInputStream(), charset));
      while ((data = reader.readLine()) != null) {
        targetWriter.println(data);
      }
      targetWriter.flush();
      try {
        File file = new File(bak);
        if (file.exists() && file.length() > 1000) {
          FileUtil.copy(bak, dist, true);
          FileUtil.deleteFile(bak);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      logger.error("createHTML error:" + e.toString());
    } finally {
      try {
        targetWriter.close();
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        logger.error("createHTML error: close reader error!");
        targetWriter = null;
        reader = null;
      }
      target = null;
      targetWriter = null;
      reader = null;
    }
  }
  
  public static int getUrlStatus(String url){
      HttpClient httpclient = new DefaultHttpClient();

      try{
          HttpGet httpget = new HttpGet(url);
          httpget.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
          httpget.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

          HttpResponse response = httpclient.execute(httpget);
          StatusLine sl=response.getStatusLine();
          return  sl.getStatusCode();
      }catch (Exception e){
          logger.error(e.getMessage(),e);
      }finally{
          //释放连接
          httpclient.getConnectionManager().shutdown();
      }

      return -1;
  }
  
  
  public static void main (String args[]){
	  
	  WgetUtil.wgetFile("http://www.lvmama.com/info/uploadfile/20120315/900x600_7686MMP719BR0001.jpg", "C://sdfs.jpg");
  }
  
  
  
}
