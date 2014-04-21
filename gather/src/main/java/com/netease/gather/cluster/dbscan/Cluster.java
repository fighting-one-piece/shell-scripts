package com.netease.gather.cluster.dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: ykxu
 * Date: 13-4-24 下午5:31
 */
public class Cluster {
    private List<DataPoint> dataPoints = new ArrayList<DataPoint>(); // 类簇中的样本点
    private String clusterName; //簇名

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}