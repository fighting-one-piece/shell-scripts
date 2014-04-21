package com.netease.gather.nlp;


import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

//自tagcloud工程
public class ShingleAlg {
	
	private static IKSegmenter segmenter = null;
	private static String UTF_8 = "UTF-8";
	
	public static double calShingleDistance(String str1, String str2) {
		
		int strSize1 = str1.length();
		int strSize2 = str2.length();
		int MAXSIZE = strSize1 - strSize2 > 0 ? strSize1 : strSize2;
		int[][] maxArray = new int[strSize1 + 1][strSize2 + 1];
		buildMaxArray(maxArray, str1, str2);
		String[] array1 = seg(str1);
		String[] array2=  seg(str2);
		int jiaoSize = jiaoji(array1, array2).length;
		int bingSize = union(array1, array2).length;
		double d1 = getMaxMaxSeq(maxArray, str1, str2, MAXSIZE);
		double d2 = scoreCompute(jiaoSize,bingSize);
		return (d1+d2)/2.0d;
	}
	
	private static String[] seg(String sourceStr){
		
		StringReader reader_ = new StringReader(sourceStr);
		segmenter = new IKSegmenter(reader_,true);
		Lexeme lexeme = null;
		Set<String> temp = new HashSet<String>();
		try {
			lexeme = segmenter.next();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (lexeme != null) {
				temp.add(new String(lexeme.getLexemeText().getBytes(),Charset.forName(UTF_8)));
				lexeme = segmenter.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return list2Array(temp);
	}
	
	private static String[] jiaoji(String[] array1,String[] array2){
    	
		Set<String> list = new HashSet<String>();
		for (int i = 0; i < array1.length; i++) {
			for (int j = 0; j < array2.length; j++) {
				if (array1[i].equalsIgnoreCase(array2[j])) {
					list.add(array1[i]);
				}
			}
		}
		return list2Array(list);
    }

	private static String[] union(String[] array1, String[] array2) {
		
		Set<String> list = new HashSet<String>();
		for (int i = 0; i < array1.length; i++) {
			list.add(array1[i]);
		}
		for (int i = 0; i < array2.length; i++) {
			boolean flag = true;
			for (int j = 0; j < array1.length; j++) {
				if (array2[i].equalsIgnoreCase(array1[j])) {
					flag = false;
				}
			}
			if (flag) {
				list.add(array2[i]);
			}
		}
		return list2Array(list);
	}
	
	private static String[] list2Array(Set<String> list){
		
		Iterator<String> iterator = list.iterator();
		String[] reArray = new String[list.size()];
		int i = 0;
		for(;iterator.hasNext();i++){
			String c = iterator.next();
			reArray[i] = c;
		}
		return reArray;
	}
	
	private static void buildMaxArray(int[][] maxArray, String str1, String str2) {

		char[] array = str1.toCharArray();
		char[] array2 = str2.toCharArray();
		int strSize1 = array.length;
		int strSize2 = array2.length;
		for (int i = 1; i < strSize1; i++) {
			for (int j = 1; j < strSize2; j++) {
				if (array[i - 1] == array2[j - 1]) {
					maxArray[i][j] = maxArray[i - 1][j - 1] + 1;
				} else
					maxArray[i][j] = Math.max(maxArray[i - 1][j],
							maxArray[i][j - 1]);
			}
		}
	}
	
	private static double getMaxMaxSeq(int[][] maxArray, String str1, String str2,int MAXSIZE) {

		char[] array = str1.toCharArray();
		char[] array2 = str2.toCharArray();
		int i = array.length;
		int j = array2.length;
		StringBuffer resultList = new StringBuffer();
		while (i > 0 && j > 0) {
			if (array2[j - 1] == array[i - 1]) {
				resultList.append(array2[j - 1]);
				i--;
				j--;
			} else {
				if (maxArray[i][j - 1] >= maxArray[i - 1][j]) {
					j--;
				} else {
					i--;
				}
			}
		}
		return (double) resultList.toString().length() / (double) MAXSIZE;
	}
	
	private static double scoreCompute(int length1,int length2){
		return (double) length1 / (double) length2;
	}
}
