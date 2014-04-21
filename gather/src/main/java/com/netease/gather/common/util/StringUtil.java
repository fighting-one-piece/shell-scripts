package com.netease.gather.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: ykxu
 * Date: 13-1-22 下午6:44
 */
public class StringUtil {

    /**
     * 使HTML的标签失去作用*
     *
     * @param input
     *            被操作的字符串
     * @return String
     */
    public static String escapeHTMLTag(String input) {
        if (input == null)
            return "";
//        input = input.trim().replaceAll("&", "&amp;");
        input = input.replaceAll("<", "&lt;");
        input = input.replaceAll(">", "&gt;");
        input = input.replaceAll("\n", "<br>");
        input = input.replaceAll("'", "&#39;");
        input = input.replaceAll("\"", "&quot;");
        input = input.replaceAll("\\\\", "&#92;");
        return input;
    }

    /**
     * 判断一个字符串是否空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /** 判断指定的字符串是否是空串 */
    public static boolean isBlank(String str) {
        if (isEmpty(str))
            return true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        return str != null && str.matches("\\d+(\\.\\d+)?");
    }

    /**
     * 判断是否是url
     * @param url
     * @return
     */
    public static boolean isURL(String url) {
        // judge url
        Pattern pattern = Pattern.compile("^[a-zA-z]+://[^\\s]*");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    //去掉s中的前n个r
    public static String replaceStart(String s, String r){
        if(s.startsWith(r)){
            s = s.trim().replaceFirst(r, "").trim();
            return replaceStart(s, r);
        }else {
            return s;
        }
    }
    
	private static float countCoverRate(String src, String target){
		int count = 0;
		for(char a : src.toCharArray()){
			if(target.indexOf(a)>-1){
				count++;
			}
		}
		return (float)count/(float)src.length();
	}
	
	//俩词之间的覆盖度
	public static float countDuplicate(String str1, String str2){
		float rate1 = countCoverRate(str1, str2);
		float rate2 = countCoverRate(str2, str1);
		return rate1>rate2 ? rate1 : rate2;
	}

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value
     *            指定的字符串
     * @return 字符串的长度
     */
    public static int chineseLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
         /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
             /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
             /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                 /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    //去掉字符串中带有(*)[*]（*）【*】等括弧及括弧中的内容
    public static String removeBracket(String str){
        str = str.replaceAll("\\(.*?\\)|\\{.*?}|\\[.*?]|（.*?）|【.*?】", "");
        return str;
    }
    
    public static String trim(String str){
    	if(str == null){
    		return null;
    	}
    	return str.trim();
    }
}
