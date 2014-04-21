package com.netease.gather.theard;

import com.netease.gather.nlp.SimilarityCalc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * User: AzraelX
 * Date: 13-9-9
 * Time: 下午3:16
 */
public class SimilarityThread implements Callable<Double> {

    private static final Logger logger = LoggerFactory.getLogger(SimilarityThread.class);

    private String url1;
    private String url2;

    public SimilarityThread(String url1,String url2){
        this.url1 = url1;
        this.url2 = url2;
    }

    @Override
    public Double call() throws Exception {
        return SimilarityCalc.calcByCosWUrl(this.url1, this.url2);
    }
}
