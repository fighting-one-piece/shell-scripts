package com.netease.gather.nlp;

import com.netease.gather.cluster.dbscan.distance.CosineSimilarity;
import com.netease.gather.cluster.dbscan.distance.Distance;
import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.extapi.CMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: AzraelX
 * Date: 13-10-29
 * Time: 下午6:04
 */
public class SimilarityCalc {
    private static final Logger logger = LoggerFactory.getLogger(SimilarityCalc.class);
    public static double calcByCosWString(String text1,String text2) throws Exception {

        Map<String, Double> map1 = TFIDF.getTF(text1);
        Map<String, Double> map2 = TFIDF.getTF(text2);
        return calcByCos(map1,map2);
    }

    public static double calcByCosWStringByChar(String text1,String text2) throws Exception {

        Map<String, Double> map1 = TFIDF.getCF(text1);
        Map<String, Double> map2 = TFIDF.getCF(text2);
        return calcByCos(map1,map2);
    }


    public static double calcByCosWUrl(String url1,String url2) throws Exception {

        Map<String, Double> map1 = getTFUseCMS(url1);
        Map<String, Double> map2 = getTFUseCMS(url2);

        return calcByCos(map1,map2);
    }

    private static Map<String, Double> getTFUseCMS(String url) throws Exception {
        Map<String, Double> map = new HashMap<String, Double>();
        boolean get = false;
        if(url.contains(".163.com")){
            try {
                String docid = CommonUtil.get163DocidFromUrl(url);
                String context = CMSUtil.getArticleWCache(docid);
                map = TFIDF.getTF(context);
                get = true;
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        if(!get){
            map = TFIDF.getTFForUrl(url);
        }
        return map;
    }

    private static double calcByCos(Map<String, Double> map1,Map<String, Double> map2) throws Exception {
        List<Map<String,Double>> dim1 = new ArrayList<Map<String, Double>>(Arrays.asList(map1));
        List<Map<String,Double>> dim2 = new ArrayList<Map<String, Double>>(Arrays.asList(map2));
        Distance distance = new CosineSimilarity();
        return distance.getDistance(dim1,dim2);
    }

}
