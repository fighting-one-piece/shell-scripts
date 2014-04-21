//package com.netease.gather.theard;
//
//import com.netease.gather.cluster.dbscan.DataPoint;
//import com.netease.gather.cluster.dbscan.distance.Distance;
//import com.netease.gather.domain.Doc;
//import net.rubyeye.xmemcached.MemcachedClient;
//
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
///**
//* User: ykxu
//* Date: 13-1-11
//* Time: 下午3:28
//*/
//public class ThreadManager {
//    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
//
//    public static Future<Map> calTF(Doc doc){
//        return executorService.submit(new TFThread(doc));
//    }
//
//    public static Future<Map> calDistance(DataPoint dataPoint,DataPoint other,Distance distance,boolean useCahce,MemcachedClient memcached){
//        return executorService.submit(new DistanceThread(dataPoint,other,distance,useCahce,memcached));
//    }
//
//    public static Future<Double> calSimilarity(String url1,String url2){
//        return executorService.submit(new SimilarityThread(url1, url2));
//    }
//
//}
