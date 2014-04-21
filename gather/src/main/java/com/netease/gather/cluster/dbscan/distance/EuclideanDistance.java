package com.netease.gather.cluster.dbscan.distance;

import java.util.List;

/**
 * User: AzraelX
 * Date: 13-7-24
 * Time: 下午6:19
 */
public class EuclideanDistance implements Distance {

    @Override
    public double getDistance(List v1,List v2){
        double distance=0.0;
        if(v1.size()==v2.size()){
            for(int i=0;i<v1.size();i++){
                double temp=Math.pow(Double.valueOf(v1.get(i).toString())-Double.valueOf(v2.get(i).toString()), 2);
                distance=distance+temp;
            }
            distance=Math.pow(distance, 0.5);
            return distance;
        }
        return distance;
    }
}
