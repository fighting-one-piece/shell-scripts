package com.netease.gather.summary;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

//LIXUSIGN
public class Sentence implements Sim<Sentence> {
    
	String text;
    double[] tfidf;  
    
    public Sentence(String text, Map<String, Double> idf, List<String> terms) {
    	
    	this.text = text;
    	tfidf = new double[terms.size()];
        String nor = tokens(text);
        for (int i = 0; i < terms.size(); ++i) {
        	tfidf[i] = 0;
        }
        for (String term: nor.split(BasicMatrixCompute.SPACE_ZZ)) {
            for (int i = 0; i < terms.size(); ++i) {
            	if(term == null || terms.get(i) == null || idf.get(term)==null)
            		System.out.println(i);
            	tfidf[i] += similarity(term, terms.get(i)) * idf.get(term);
            }
        }
    }
    
    public static String tokens(String sentence) {
    	
    	IKSegmenter segmenter = null;
    	StringReader reader_ = new StringReader(sentence);
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
				temp.add(new String(lexeme.getLexemeText()));
				lexeme = segmenter.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return list2String(temp);
    }
    
    private static String list2String(Set<String> temp){
    	
    	StringBuffer buffer = new StringBuffer();
    	Iterator<String> iterator = temp.iterator();
    	for(;iterator.hasNext();){
    		String one = iterator.next();
    		buffer.append(one).append(BasicMatrixCompute.SPACE_LA);
    	}
    	return buffer.toString();
    }

    protected static double similarity(String word1, String word2) {
        
    	return (word1.equals(word2) == true)?1:0;
    }

    public double similarity(Sentence other) {
       
    	double a = 0;
        double b = 0;
        double c = 0;
        for (int i = 0; i < tfidf.length; ++i) {
            a += tfidf[i] * other.tfidf[i];
            b += tfidf[i] * tfidf[i];
            c += other.tfidf[i] * other.tfidf[i];
        }
        return a / Math.sqrt(b * c);
    }
}