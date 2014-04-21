package com.netease.gather.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class CookieUtil {

	/**
	 * 生成cookie
	 */
	public static void setCookie(HttpServletResponse response,String domain,String path,
		String name, String value,int time, boolean secure) {
		if(value==null) value="";
		Cookie cookie = new Cookie(name, value);
		cookie.setSecure(secure);           //表示是否Cookie只能通过加密的连接（即SSL）发送。 
		cookie.setPath(path);                //设置Cookie适用的路径
		cookie.setDomain(domain);           //设置Cookie适用的域
		cookie.setMaxAge(time);             //设置Cookie有效时间
		response.addCookie(cookie);
        response.setHeader("P3P","CP=.");  //跨域
	}

	/**
	 * 获取Cookie
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
            for (Cookie cooky : cookies) {
                if(cooky != null){
                    if (cooky.getName().equals(name)) {
                        return cooky;
                    }
                }
            }
		}
		return null;
	}
	/**
	 * 获取Cookie的值
	 */
	public static String getValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie == null) return null;
		String value = cookie.getValue();
		if ("null".equals(value)) return null;
		return value;
	}
	/**
	 * 删除cookie
	 */
	public static HttpServletResponse deleteCookie(HttpServletResponse response, Cookie cookie) {
		if (cookie == null) {
			return response;
		}
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return response;
	}
}
