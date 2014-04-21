package com.netease.gather.classifier.naivebayes;

import java.io.Serializable;
import java.util.Map;

/**
 * User: AzraelX
 * Date: 13-10-8
 * Time: 下午2:03
 */
public class Model implements Serializable{
    private String category;
    private Double priori;
    private Map<String,Double> words;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPriori() {
        return priori;
    }

    public void setPriori(Double priori) {
        this.priori = priori;
    }

    public Map<String, Double> getWords() {
        return words;
    }

    public void setWords(Map<String, Double> words) {
        this.words = words;
    }
}
