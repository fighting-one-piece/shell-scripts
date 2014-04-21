package com.netease.gather.common.util;

public class URLUtil {

	/**
	 * 去除url中的页面部分
	 * @param url
	 * @return
	 */
	public static String removePage(String url){
		
		url = removeParameter(url);
		return url.substring(0, url.lastIndexOf("/") + 1);
	}
	
	/**
	 * 获取url中的页面
	 * @param url
	 * @return
	 */
	public static String getPage(String url){
		url = removeParameter(url);
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}
	
	/**
	 * 去除url中的参数部分
	 * @param url
	 * @return
	 */
	public static String removeParameter(String url){
		if(url == null){
			return "";
		}
		return url.split("\\?")[0];
	}
	
	/**
	 * 获取URL中的后缀
	 * @param url
	 * @return
	 */
	public static String getSuffix(String url){
		url = removeParameter(url);
		return url.substring(url.lastIndexOf("."),  url.length());
	}
	
	public static String getDomain(String url){
		url = removeParameter(url);
		
		
		return url.substring(0, url.indexOf("/", url.indexOf("//") + 2) + 1);
		
		
	}
}
