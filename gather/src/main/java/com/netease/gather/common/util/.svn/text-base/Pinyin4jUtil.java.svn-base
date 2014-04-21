package com.netease.gather.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pinyin4jUtil {

	/*************************************************************************** 
	 * 获取中文汉字拼音 默认输出 
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 * @return 
	 */
	public static String getPinyin(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyinZh_CN(makeStringByStringSet(chinese, null)).toLowerCase();
	}
	
	/*************************************************************************** 
	 * 获取中文汉字拼音 默认输出 (忽略多单字情况)
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 * @return 
	 */
	public static String getPinyin0(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyin(chinese).split(",")[0];
	}

	/*************************************************************************** 
	 * 首字母大写输出 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 * @return 
	 */
	public static String getPinyinFirstToUpperCase(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyinZh_CN(makeStringByStringSet(chinese, null));
	}
	
	/*************************************************************************** 
	 * 拼音简拼输出 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 * @return 
	 */
	public static String getPinyinJianPin(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyinJianPin(makeStringByStringSet(chinese, null));
	}
	
	/*************************************************************************** 
	 * 拼音简拼输出 (忽略多单字情况)
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 * @return 
	 */
	public static String getPinyinJianPin0(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyinJianPin(chinese).split(",")[0];
	}
	
	/**
	 * 得到模糊处理后的拼音
	 * @param chinese
	 * @return
	 */
	public static String getFuzzyPinyin(String chinese) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getFuzzyPinyin(chinese,fuzzy);
	}
	
	/**
	 * 得到模糊处理后的拼音
	 * @param chinese
	 * @param fuzzy
	 * @return
	 */
	public static String getFuzzyPinyin(String chinese,Map<String,String> fuzzy) {
		if(StringUtils.isEmpty(chinese))
			return "";
		return getPinyinZh_CN(makeStringByStringSet(chinese, fuzzy)).toLowerCase();
	}
	
	public static String converWithFuzzy(String pinyin){
		if(StringUtils.isEmpty(pinyin))
			return "";
		return converWithFuzzy(pinyin,fuzzy);
	}
	
	public static String converWithFuzzy(String pinyin,Map<String,String> fuzzy){
		if(StringUtils.isEmpty(pinyin))
			return "";
		for (String key : fuzzy.keySet()) {
			if (pinyin.indexOf(key) >= 0) {
				pinyin = pinyin.replaceAll(key, fuzzy.get(key));
			}
		}
		return pinyin;
	}

	/*************************************************************************** 
	 * 字符集转换 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @param chinese 
	 *            中文汉字 
	 * @throws net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
	 */
	public static Set<String> makeStringByStringSet(String chinese, Map<String, String> fuzzy) {
		char[] chars = chinese.toCharArray();
		if (chinese != null && !chinese.trim().equalsIgnoreCase("")) {
			char[] srcChar = chinese.toCharArray();
			String[][] temp = new String[chinese.length()][];
			for (int i = 0; i < srcChar.length; i++) {
				char c = srcChar[i];

				// 是中文或者a-z或者A-Z转换拼音
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {

					try {
						temp[i] = PinyinHelper.toHanyuPinyinStringArray(chars[i], getDefaultOutputFormat());

						if (fuzzy != null)
							for (int j = 0; j < temp[i].length; j++) {
								temp[i][j]=converWithFuzzy(temp[i][j], fuzzy);
							}

					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
//                } else if (((int) c >= 65 && (int) c <= 90)  
//                        || ((int) c >= 97 && (int) c <= 122)) {  
				} else {
					temp[i] = new String[] { String.valueOf(srcChar[i]) };
//                    temp[i] = new String[] { "" };  
				}
			}
			String[] pingyinArray = Exchange(temp);
			Set<String> zhongWenPinYin = new HashSet<String>();
			for (int i = 0; i < pingyinArray.length; i++) {
				zhongWenPinYin.add(pingyinArray[i]);
			}
			return zhongWenPinYin;
		}
		return null;
	}

	/*************************************************************************** 
	 * Default Format 默认输出格式 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @return 
	 */
	public static HanyuPinyinOutputFormat getDefaultOutputFormat() {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);// u显示
		return format;
	}

	/*************************************************************************** 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @author: wang_chian@foxmail.com 
	 * @version: Jan 13, 2012 9:39:54 AM 
	 * @param strJaggedArray 
	 * @return 
	 */
	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	/*************************************************************************** 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @version: Jan 13, 2012 9:39:47 AM 
	 * @param strJaggedArray 
	 * @return 
	 */
	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index] = capitalize(strJaggedArray[0][i]) + capitalize(strJaggedArray[1][j]);
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			for (int i = 0; i < strJaggedArray[0].length; i++)
				strJaggedArray[0][i] = capitalize(strJaggedArray[0][i]);
			return strJaggedArray;
		}
	}

	/*************************************************************************** 
	 * 首字母大写 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @version: Jan 13, 2012 9:36:18 AM 
	 * @param s 
	 * @return 
	 */
	private static String capitalize(String s) {
		char ch[];
		ch = s.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		String newString = new String(ch);
		return newString;
	}

	/*************************************************************************** 
	 * 字符串集合转换字符串(逗号分隔) 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @version: Jan 13, 2012 9:37:57 AM 
	 * @param stringSet 
	 * @return 
	 */
	private static String getPinyinZh_CN(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append(s);
			} else {
				str.append(s + ",");
			}
			i++;
		}
		return str.toString();
	}
	
	/*************************************************************************** 
	 * 字符串集合转换字符串(逗号分隔) 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @version: Jan 13, 2012 9:37:57 AM 
	 * @param stringSet 
	 * @return 
	 */
	private static String getPinyinJianPin(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			String tmp="";
			char arr[] = s.toCharArray(); // 将字符串转化成char型数组
			for (char c:arr) {
				if (c >= 65 && c <= 90 ) { // 判断是否是大写字母
					tmp += c;
				} else if (c >= '0' && c <= '9') { // 判断是否是数字
					tmp += c;
				}
			}
			if (i == stringSet.size() - 1) {
				str.append(tmp);
			} else {
				str.append(tmp + ",");
			}
			i++;
		}
		return str.toString().toLowerCase();
	}

	private static Map<String, String> fuzzy = new HashMap<String, String>();
	static {
		fuzzy.put("zh", "z");
		fuzzy.put("ch", "c");
		fuzzy.put("sh", "s");
		fuzzy.put("ang", "an");
		fuzzy.put("eng", "en");
		fuzzy.put("ing", "in");
        fuzzy.put("agn", "an");
		fuzzy.put("egn", "en");
		fuzzy.put("ign", "in");
		fuzzy.put("l", "n");
		fuzzy.put("f", "h");
		fuzzy.put("r", "n");
	}

	/*************************************************************************** 
	 * Test 
	 *  
	 * @Name: Pinyin4jUtil.java 
	 * @Description: TODO 
	 * @version: Jan 13, 2012 9:49:27 AM 
	 * @param args 
	 */
	public static void main(String[] args) {
		String str = "女";
//        Set<String> s=makeStringByStringSet(str);
//        for(String s1:s){
//        	System.out.println(s1);
//        }
		System.out.println("默认输出：" + getPinyin(str));
		System.out.println("默认输出(忽略多单字)：" + getPinyin0(str));
		System.out.println("首字母大写输出：" + getPinyinFirstToUpperCase(str));
		System.out.println("简拼输出：" + getPinyinJianPin(str));
		System.out.println("简拼输出(忽略多单字)：" + getPinyinJianPin0(str));
		System.out.println("模糊输出：" + getFuzzyPinyin(str, fuzzy));
		System.out.println("模糊输出2：" + converWithFuzzy(str, fuzzy));

	}
}