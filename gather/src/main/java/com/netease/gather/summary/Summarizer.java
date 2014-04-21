package com.netease.gather.summary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//LIXUSIGN
public class Summarizer {
    
	List<String> texts;

    public Summarizer(List<String> texts) {
    	this.texts = texts;
    }

    private List<String> pickDownSentence(Results<Sentence> results){
    	
    	List<String> finalResults = new ArrayList<String>();
    	if(results != null){
    		for (Sentence c: results.results) {
                boolean max = true;
                if(results.s != null && results.n.get(c) != null){
                	for (Sentence neighbor : results.n.get(c)) {
                        if (results.s.get(neighbor) > results.s.get(c)) {
                            max = false;
                        }
                    }
                    if (max) {
                        finalResults.add(c.text);
                    }
                }
            }
            return finalResults;
    	}
        return null;
    }
    
    public List<String> summar() {
        return pickDownSentence(new MRWalker().walk(pickUpSentence(), BasicMatrixCompute.SL));
    }
    
    
    private List<Sentence> pickUpSentence(){
    	
    	Set<String> wst = new HashSet<String>();
        for (String s: texts) {
        	if(s != null && !s.equals(BasicMatrixCompute.NIL_STR)){
        		for (String w : BasicMatrixCompute.getSegment(s)) {
        			wst.add(w);
                }
        	}
        }
        List<String> ws = new ArrayList<String>();
        for (String s: wst) {
        	ws.add(s);
        }
        Map<String, Double> map = BasicMatrixCompute.IDFUtil.calIDF(ws,texts);
        List<Sentence> sen = new ArrayList<Sentence>(texts.size());
        for (String s: texts) {
        	if(s != null && !s.equals(BasicMatrixCompute.NIL_STR)){
        		sen.add(new Sentence(s, map, ws));
        	}
        }
        return sen;
    }
}