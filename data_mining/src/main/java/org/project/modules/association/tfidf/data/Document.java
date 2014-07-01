package org.project.modules.association.tfidf.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Document {

	private String name = null;
	
	private String category = null;
	
	private String[] words = null;
	
	private Map<String, Double> tfidfWords = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}
	
	public Map<String, Double> getTfidfWords() {
		if (null == tfidfWords) {
			tfidfWords = new HashMap<String, Double>();
		}
		return tfidfWords;
	}

	public void setTfidfWords(Map<String, Double> tfidfWords) {
		this.tfidfWords = tfidfWords;
	}

	public Map<String, Integer> wordStatistics() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String word : words) {
			Integer count = map.get(word);
			map.put(word, null == count ? 1 : count + 1);
		}
		return map;
	}
	
	public boolean containWord(String word) {
		for (String temp : words) {
			if (temp.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}
	
	public String[] topWords(int n) {
		String[] topWords = new String[n];
		List<Map.Entry<String, Double>> list = 
				new ArrayList<Map.Entry<String, Double>>(tfidfWords.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		
		});
		int index = 0;
		for (Map.Entry<String, Double> entry : list) {
			if (index == n) {
				break;
			}
			topWords[index++] = entry.getKey();
			System.out.print(name + " : " + entry.getKey() + " : ");
			DecimalFormat df4  = new DecimalFormat("##.0000");
			System.out.println(df4.format(entry.getValue()));
		}
		return topWords;
	}
	
	public double[] vector(String[] words) {
		double[] vector = new double[words.length];
		Map<String, Integer> map = wordStatistics();
		int index = 0;
		for (String word : words) {
			Integer count = map.get(word);
			vector[index++] = null == count ? 0 : count;
		}
		return vector;
	}
	
}
