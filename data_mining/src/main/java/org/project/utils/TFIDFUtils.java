package org.project.utils;

public class TFIDFUtils {

	public static void wordZH(String content) {
	}
	
	public static void wordEN(String content) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(content.toCharArray(), content.length());
		stemmer.stem();
		System.out.println(stemmer.toString());
	}
	
	public static void main(String[] args) {
		String content = "An organization is an official group of people, for example a political party, a business, a charity, or a club";
		wordEN(content);
		Stemmer stemmer = new Stemmer();
		stemmer.stem("d:\\a.txt", "d:\\a1.txt");
	}
}
