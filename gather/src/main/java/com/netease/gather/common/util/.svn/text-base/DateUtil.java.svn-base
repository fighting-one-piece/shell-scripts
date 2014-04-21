package com.netease.gather.common.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 时间工具类
 * @author caojh
 *
 */
public class DateUtil {
	// 格式：年－月－日 小时：分钟：秒
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// 格式：年－月－日
	public static final String NO_TIME_DATE_FORMAT = "yyyy-MM-dd";
	// 格式 : 年/月/日
	public static final String NO_TIME_DATE_FORMAT2 = "yyyy/MM/dd";
    private static final Logger logger = Logger.getLogger(DateUtil.class);

	/**
	 * 按指定格式返回时间字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String DateToString(Date date, String format) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			// log.error(e);
		}
		return result;
	}
	
	/**
	 * 默认返回年－月－日 小时：分钟：秒格式的时间字符串
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			// log.error(e);
		}
		return result;
	}
	
	/**
	 * 返回年－月－日格式的日期字符串
	 * @param date
	 * @return
	 */
	public static String DateToStringWithoutTime(Date date){
		return DateToString(date, NO_TIME_DATE_FORMAT);
	}
	
	public static String DateToStringWithoutTime2(Date date){
		return DateToString(date, NO_TIME_DATE_FORMAT2);
	}

    /**
     * 根据字符串格式返回Date @ykxu
     * @param dateString
     * @param format
     * @return
     */
    public static Date stringToDate(String dateString, String format){
        if (dateString == null)
            return null;
        if (dateString.equalsIgnoreCase("")){
            logger.error("传入参数中的[时间串]为空");
            return null;
        }
        if ((format == null) || (format.equalsIgnoreCase(""))) {
            logger.error("传入参数中的[时间格式]为空");
            return null;
        }
        Hashtable<Integer,String> h = new Hashtable();
        String javaFormat = new String();
        if (format.indexOf("yyyy") != -1)
            h.put(new Integer(format.indexOf("yyyy")), "yyyy");
        else if (format.indexOf("yy") != -1)
            h.put(new Integer(format.indexOf("yy")), "yy");
        if (format.indexOf("MM") != -1)
            h.put(new Integer(format.indexOf("MM")), "MM");
        else if (format.indexOf("mm") != -1)
            h.put(new Integer(format.indexOf("mm")), "MM");
        if (format.indexOf("dd") != -1)
            h.put(new Integer(format.indexOf("dd")), "dd");
        if (format.indexOf("hh24") != -1)
            h.put(new Integer(format.indexOf("hh24")), "HH");
        else if (format.indexOf("hh") != -1)
            h.put(new Integer(format.indexOf("hh")), "HH");
        else if (format.indexOf("HH") != -1) {
            h.put(new Integer(format.indexOf("HH")), "HH");
        }
        if (format.indexOf("mi") != -1)
            h.put(new Integer(format.indexOf("mi")), "mm");
        else if ((format.indexOf("mm") != -1) && (h.containsValue("HH")))
            h.put(new Integer(format.lastIndexOf("mm")), "mm");
        if (format.indexOf("ss") != -1)
            h.put(new Integer(format.indexOf("ss")), "ss");
        if (format.indexOf("SSS") != -1) {
            h.put(new Integer(format.indexOf("SSS")), "SSS");
        }
        for (int intStart = 0; format.indexOf("-", intStart) != -1; intStart++) {
            intStart = format.indexOf("-", intStart);
            h.put(new Integer(intStart), "-");
        }
        for (int intStart = 0; format.indexOf(".", intStart) != -1; intStart++) {
            intStart = format.indexOf(".", intStart);
            h.put(new Integer(intStart), ".");
        }
        for (int intStart = 0; format.indexOf("/", intStart) != -1; intStart++) {
            intStart = format.indexOf("/", intStart);
            h.put(new Integer(intStart), "/");
        }

        for (int intStart = 0; format.indexOf(" ", intStart) != -1; intStart++) {
            intStart = format.indexOf(" ", intStart);
            h.put(new Integer(intStart), " ");
        }

        for (int intStart = 0; format.indexOf(":", intStart) != -1; intStart++) {
            intStart = format.indexOf(":", intStart);
            h.put(new Integer(intStart), ":");
        }

        if (format.indexOf("年") != -1)
            h.put(new Integer(format.indexOf("年")), "年");
        if (format.indexOf("月") != -1)
            h.put(new Integer(format.indexOf("月")), "月");
        if (format.indexOf("日") != -1)
            h.put(new Integer(format.indexOf("日")), "日");
        if (format.indexOf("时") != -1)
            h.put(new Integer(format.indexOf("时")), "时");
        if (format.indexOf("分") != -1)
            h.put(new Integer(format.indexOf("分")), "分");
        if (format.indexOf("秒") != -1)
            h.put(new Integer(format.indexOf("秒")), "秒");
        int i = 0;
        while (h.size() != 0) {
            Enumeration e = h.keys();
            int n = 0;
            while (e.hasMoreElements()) {
                i = ((Integer)e.nextElement()).intValue();
                if (i >= n)
                    n = i;
            }
            String temp = (String)h.get(new Integer(n));
            h.remove(new Integer(n));
            javaFormat = temp + javaFormat;
        }
        SimpleDateFormat df = new SimpleDateFormat(javaFormat);
        df.setLenient(false);
        Date myDate = new Date();
        try {
            myDate = df.parse(dateString);
        } catch (ParseException e) {
            logger.error("日期格式转换错误!将字符串转换成时间时出错");
        }
        return myDate;
    }
    
    /**
     * 获得当前年， 如2012
     * @return
     */
    public static int getToYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
	
    public static Date nextDay(Date date, int i)
    {
        Calendar calendar = Calendar.getInstance();
        if(date != null)
            calendar.setTime(date);
        calendar.add(6, i);
        return calendar.getTime();
    }

    public static long getDaysBetweenTwoDates(Date dateFrom, Date dateEnd) {
        long begin = dateFrom.getTime();
        long end = dateEnd.getTime();
        long inter = end - begin;
        if (inter < 0) {
            inter = inter * (-1);
        }
        long dateMillSec = 24 * 60 * 60 * 1000;

        long dateCnt =  inter / dateMillSec;

        long remainder = inter % dateMillSec;

        if (remainder != 0) {
            dateCnt++;
        }
        return dateCnt;
    }

    public static long getHoursBetweenTwoDates(Date dateFrom, Date dateEnd) {
        long begin = dateFrom.getTime();
        long end = dateEnd.getTime();
        long inter = end - begin;
        if (inter < 0) {
            inter = inter * (-1);
        }
        long dateMillSec = 60 * 60 * 1000;

        long dateCnt =  inter / dateMillSec;

        long remainder = inter % dateMillSec;

        if (remainder != 0) {
            dateCnt++;
        }
        return dateCnt;
    }

	public static void main(String args[]){
		
		System.out.println(DateToStringWithoutTime(new Date()));
	}
}
