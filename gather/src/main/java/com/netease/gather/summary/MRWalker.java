package com.netease.gather.summary;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

//LIXUSIGN
public class MRWalker implements RandomWalker{

    
    private static <T extends Sim<T>> Results<T> tidyData(List<T> data,Results<T> results,double[] r){
    	
    	List<BasicMatrixCompute.Pair<T>> temp = new ArrayList<BasicMatrixCompute.Pair<T>>();
        for (int i = 0; i < data.size(); ++i) {
            results.s.put(data.get(i), r[i]);
            temp.add(new BasicMatrixCompute.Pair<T>(data.get(i), r[i]));
        }
        Collections.sort(temp);
        Collections.reverse(temp);
        for (BasicMatrixCompute.Pair<T> pair: temp) {
            results.results.add(pair.data);
        }
        return results;
    }

    private static <T extends Sim<T>> double[][] similarity(List<T> data) {
       
    	double[][] results = new double[data.size()][data.size()];
        for (int i = 0; i < data.size(); ++i) {
            for (int j = 0; j <= i; ++j) {
                results[i][j] = results[j][i] = data.get(i).similarity(data.get(j));
            }
        }
        return results;
    }
    
    public <T extends Sim<T>> Results<T> walk(List<T> data,double h){
        
    	Results<T> results = new Results<T>();
        if (data.size() == 0) {
            return results;
        }
        double[][] pro = BasicMatrixCompute.MCal.trans(similarity(data),h);
        for (int i = 0; i < data.size(); ++i) {
            for (int j = 0; j < data.size(); ++j) {
                if (pro[i][j] > 0) {
                    List<T> list = results.n.get(data.get(i));
                    if (list == null) {
                    	list = new ArrayList<T>();
                    }
                    list.add(data.get(j));
                    results.n.put(data.get(i), list);
                }
            }
        }
        double[] r = BasicMatrixCompute.MCal.power(pro,data.size());
        return tidyData(data,results,r);
    }
    
}