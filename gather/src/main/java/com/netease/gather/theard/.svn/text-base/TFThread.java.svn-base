package com.netease.gather.theard;

import com.netease.gather.domain.Doc;
import com.netease.gather.nlp.TFIDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * User: AzraelX
 * Date: 13-9-9
 * Time: 下午3:16
 */
public class TFThread implements Callable<Map> {

    private static final Logger logger = LoggerFactory.getLogger(TFThread.class);

    private Doc doc;

    public TFThread(Doc doc){
        this.doc = doc;
    }

    @Override
    public Map call() throws Exception {
        Map<String,Double> tfmap = TFIDF.getTFForUrl(doc.getUrl());
        Map rmap = new HashMap();
        rmap.put("tfmap",tfmap);
        rmap.put("doc",doc);
        return rmap;
    }
}
