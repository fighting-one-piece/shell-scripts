package com.netease.gather.theard;

import com.netease.gather.cluster.dbscan.DataPoint;
import com.netease.gather.cluster.dbscan.distance.Distance;
import net.rubyeye.xmemcached.MemcachedClient;
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
public class DistanceThread implements Callable<Map> {

    private static final Logger logger = LoggerFactory.getLogger(DistanceThread.class);

//    private static MemcachedClient memcached;
//
//    static {
//        memcached = ScheduleContext.BF.getBean("memcachedClient", MemcachedClient.class);
//    }

    private DataPoint dataPoint;
    private DataPoint other;
    private Distance distance;
    private boolean useCahce;
    private MemcachedClient memcached;

    public DistanceThread(DataPoint dataPoint,DataPoint other,Distance distance,boolean useCahce,MemcachedClient memcached){
        this.dataPoint = dataPoint;
        this.other = other;
        this.distance = distance;
        this.useCahce = useCahce;
        this.memcached = memcached;
    }

    @Override
    public Map call() throws Exception {
        double distance= this.useCahce?getDistance(dataPoint,other):this.distance.getDistance(dataPoint.getDimensioin(),other.getDimensioin());
        Map rmap = new HashMap();
        rmap.put("distance",distance);
        rmap.put("other",other);
        return rmap;
    }


    private double getDistance(DataPoint dp1,DataPoint dp2){
        Double distance=null;
        String dp1name = dp1.getDataPointName();
        String dp2name = dp2.getDataPointName();
        String name = dp1name.compareTo(dp2name)<0?dp1name+","+dp2name:dp2name+","+dp1name;
        try {

            distance = memcached.get("gather.distance.between?dp1,dp2"+name,3000);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if(distance==null){
            distance = this.distance.getDistance(dp1.getDimensioin(),dp2.getDimensioin());
            try {
                memcached.set("gather.distance.between?dp1,dp2"+name,600,distance);
            }catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }

        return distance;

    }
}
