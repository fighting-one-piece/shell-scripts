package com.netease.gather.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取用户ip
 */
public class IPUtil {
    private static Log logger = LogFactory.getLog(IPUtil.class);

    public static String getRequestIP(final HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if(ip!=null&&!"".equals(ip)){
                ip = ip.split(",")[0];
            }
            return ip;
        } catch (Exception ex) {
            logger.error(ex);
            return "";
        }
    }

}
