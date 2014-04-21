package com.netease.gather.cluster.dbscan.distance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AzraelX
 * Date: 13-7-24
 * Time: 下午6:40
 */
public class CosineSimilarity implements Distance{

    @Override
    public double getDistance(List v1, List v2) {
        double distance=0.0;
        double muli = 0.0;
        double v1f = 0.0;
        double v2f = 0.0;
        Map m1 = new HashMap();
        for(Object o:v1){
            m1.putAll((Map)o);
        }
        Map m2 = new HashMap();
        for(Object o:v2){
            m2.putAll((Map)o);
        }
//        if(v1.length==v2.length){
        for(Object o:m1.entrySet()){
            Map.Entry entry = (Map.Entry)o;
            if(m2.containsKey(entry.getKey())){
                muli += (Double)entry.getValue()*(Double)m2.get(entry.getKey());
            }
            v1f +=  Math.pow((Double)entry.getValue(), 2);

        }

        for(Object o:m2.entrySet()){
            Map.Entry entry = (Map.Entry)o;
            v2f +=  Math.pow((Double)entry.getValue(), 2);
        }

//            for(int i=0;i<v1.length;i++){
//                double temp=v1[i]*v2[i];
//                muli=muli+temp;
//                v1f = v1f + Math.pow(v1[i], 2);
//                v2f = v2f + Math.pow(v2[i], 2);
//            }
        distance=muli/(Math.pow(v1f, 0.5)*Math.pow(v2f, 0.5));
//            return distance;
//        }
        return distance;
    }

}
