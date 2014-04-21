/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netease.gather.common.web.sna;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public interface CookieHandler {

	public void setSessionId(String sessionId, HttpServletRequest request, HttpServletResponse response) throws ServletException;

	public void removeSessionId(HttpServletRequest request, HttpServletResponse response) throws ServletException;

	public String getSessionId(HttpServletRequest request, HttpServletResponse response) throws ServletException;

	public String getCookieName();

	public void setCookieName(String cookieName);

	public String getCookieDomain();

	public void setCookieDomain(String cookieDomain);

	public int getCookieMaxAge();

	public void setCookieMaxAge(int cookieMaxAge);

	public String getCookiePath();

	public void setCookiePath(String cookiePath);

	public boolean isCookieSecure();

	public void setCookieSecure(boolean cookieSecure);
}
