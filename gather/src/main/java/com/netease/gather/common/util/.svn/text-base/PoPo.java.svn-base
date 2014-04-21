package com.netease.gather.common.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 发送popo信息
 * @author heqingfei
 *
 */
public class PoPo {
	
	//管理员POPO
	private static String[] EXCEPTION_POPO  = new String[]{"ykxu@corp.netease.com","huangbin@corp.netease.com"};
	//private static String[] EXCEPTION_POPO  = new String[]{"fengxiao@corp.netease.com"};
	private static final Logger logger = Logger.getLogger(PoPo.class);
	/**
	 * 给企业版泡泡发消息 post方式提交
	 * 
	 *
	 * @param to String 接收人
	 * @param msg String 信息内容
	 * @return boolean 返回值
	 */
	private static boolean sendEEPopo(String to, String msg){
		boolean isSendOk=false;
		if (!isFineString(msg) || !isFineString(to)) {
			return isSendOk;
		}

		try {
			msg=URLEncoder.encode(msg, "UTF-8");
			String url = "http://220.181.29.178:5820/popo?account="+to+"&msg="+msg;

			URL postURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
			// Set properties for the HTTP connection
			con.setUseCaches(false); // do not use cache
			con.setDoOutput(false); // use for output
			con.setDoInput(true); // use for Input
			con.setRequestMethod("POST"); // use the POST method to submit the form
			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			String inputLine = ""; // Stores the line of text returned by the server
			String resultsPage = ""; // Stores the complete HTML results page

			while ((inputLine = in.readLine()) != null) {
				resultsPage += inputLine;
			}
			in.close();
			if("200".equals(resultsPage)){
				isSendOk=true;
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		return isSendOk;
	}
	
	
	/**
	 * 发送popo信息（默认发送给管理员）
	 * @param msg 信息
	 */
	public static void send(String msg){
		for(String email:EXCEPTION_POPO){
			sendEEPopo(email,msg);
		}
	}
	
	/**
	 * 发送popo信息
	 * @param to 接收人
	 * @param msg 信息
	 */
	public static void send(String to,String msg){
		sendEEPopo(to,msg);
	}
	
	public static boolean isFineString(String paramString){
	    return (paramString != null) && (paramString.length() > 0);
	}
	
	
	
	public static void main(String[] args) {
		long start=System.currentTimeMillis();
		System.out.println();
		PoPo.send("popo信息");
		long end=System.currentTimeMillis();
		System.out.println(end-start);
		
	}
}
