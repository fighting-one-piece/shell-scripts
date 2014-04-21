package com.netease.gather.cluster.dbscan;

/**
 * Author: ykxu
 * Date: 13-4-24 下午5:32
 */


import com.netease.gather.cluster.dbscan.distance.Distance;
import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.theard.DistanceThread;
import net.rubyeye.xmemcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ClusterAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(ClusterAnalysis.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
    private Distance distance; //距离计算逻辑
    private boolean judgesmaller; //是否小于半径
    private boolean usecache; //是否缓存


    private boolean parallel; //是否并行

    private static MemcachedClient memcached;

    static {
        logger.info(String.valueOf(Runtime.getRuntime().availableProcessors()+1));
        memcached = ScheduleContext.BF.getBean("memcachedClientForNlp", MemcachedClient.class);
    }

    /**
     *
     * @param distance 距离计算逻辑
     * @param judgesmaller true-小于半径算一类，false-大于半径算一类
     */
    public ClusterAnalysis(Distance distance,boolean judgesmaller,boolean usecache,boolean parallel) {
        this.distance = distance;
        this.judgesmaller = judgesmaller;
        this.usecache = usecache;
        this.parallel = parallel;
    }

    public List<Cluster> doDbscanAnalysis(List<DataPoint> dataPoints,double radius, int ObjectNum) {

        long start = System.currentTimeMillis();
        List<Cluster> clusterList=new ArrayList<Cluster>();
        for(int i=0; i<dataPoints.size();i++){
            DataPoint dp=dataPoints.get(i);
            List<DataPoint> arrivableObjects=this.parallel?isKeyAndReturnObjectsParallel(dp, dataPoints, radius, ObjectNum):isKeyAndReturnObjects(dp,dataPoints,radius,ObjectNum);
            if(arrivableObjects!=null){
                Cluster tempCluster=new Cluster();
                tempCluster.setClusterName("Cluster "+i);
                tempCluster.setDataPoints(arrivableObjects);
                clusterList.add(tempCluster);
            }
        }

        if(this.parallel){
            long pend = System.currentTimeMillis();
            logger.info("初始聚点计算完成！"+(pend-start)/60000+"min");
        }

        for(int i=0;i<clusterList.size();i++){
            for(int j=0;j<clusterList.size();j++){
                if(i!=j){
                    Cluster clusterA=clusterList.get(i);
                    Cluster clusterB=clusterList.get(j);

                    List<DataPoint> dpsA=clusterA.getDataPoints();
                    List<DataPoint> dpsB=clusterB.getDataPoints();

                    boolean flag=mergeList(dpsA,dpsB);
                    if(flag){
                        clusterList.set(j, new Cluster());
                    }
                }
            }
        }

        if(this.parallel){
            long cend = System.currentTimeMillis();
            logger.info("聚类合并计算完成！"+(cend-start)/60000+"min");
        }
        return clusterList;
    }



    public void displayCluster(List<Cluster> clusterList){
        if(clusterList!=null){
            for(Cluster tempCluster:clusterList){
                if(tempCluster.getDataPoints()!=null&&tempCluster.getDataPoints().size()>0){
                    System.out.println("----------"+tempCluster.getClusterName()+"----------");
                    for(DataPoint dp:tempCluster.getDataPoints()){
                        System.out.println(dp.getDataPointName());
                    }
                }
            }
        }
    }


//    private double getDistance(DataPoint dp1,DataPoint dp2){
//        double distance=0.0;
//        double[] dim1=dp1.getDimensioin();
//        double[] dim2=dp2.getDimensioin();
//        if(dim1.length==dim2.length){
//            for(int i=0;i<dim1.length;i++){
//                double temp=Math.pow((dim1[i]-dim2[i]), 2);
//                distance=distance+temp;
//            }
//            distance=Math.pow(distance, 0.5);
//            return distance;
//        }
//        return distance;
//    }


    private List<DataPoint> isKeyAndReturnObjects(DataPoint dataPoint,List<DataPoint> dataPoints,double radius,int ObjectNum){
        List<DataPoint> arrivableObjects=new ArrayList<DataPoint>();

        int adjustment = 1;
        if(!this.judgesmaller){
            adjustment = -1;
        }
        for(DataPoint dp:dataPoints){
            double distance= this.usecache?getDistance(dataPoint,dp):this.distance.getDistance(dataPoint.getDimensioin(),dp.getDimensioin());
//            double distance= getDistance(dataPoint,dp);
            if(distance*adjustment<=radius*adjustment){
                arrivableObjects.add(dp);
            }
        }

        if(arrivableObjects.size()>=ObjectNum){
            dataPoint.setKey(true);
            return arrivableObjects;
        }

        return null;
    }

    private List<DataPoint> isKeyAndReturnObjectsParallel(DataPoint dataPoint,List<DataPoint> dataPoints,double radius,int ObjectNum){
        List<DataPoint> arrivableObjects=new ArrayList<DataPoint>();

        int adjustment = 1;
        if(!this.judgesmaller){
            adjustment = -1;
        }
        List<Future<Map>> futureList = new ArrayList<Future<Map>>();
        for(DataPoint dp:dataPoints){
            try {
                futureList.add(executorService.submit(new DistanceThread(dataPoint,dp,this.distance,this.usecache,memcached)));
//                futureList.add(ThreadManager.calDistance(dataPoint,dp,this.distance,this.usecache,memcached));
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Future<Map> future:futureList){
            Map rmap = null;
            try {
                rmap = future.get(600000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                future.cancel(true);
            }
            if(rmap!=null){
                double distance = Double.valueOf(rmap.get("distance").toString());
                DataPoint dp = (DataPoint) rmap.get("other");
                if(distance*adjustment<=radius*adjustment){
                    arrivableObjects.add(dp);
                }
            }
        }

        if(arrivableObjects.size()>=ObjectNum){
            dataPoint.setKey(true);
            return arrivableObjects;
        }

        return null;
    }

    private double getDistance(DataPoint dp1,DataPoint dp2){
        Double distance=null;
        String dp1name = dp1.getDataPointName();
        String dp2name = dp2.getDataPointName();
        String name = dp1name.compareTo(dp2name)<0?dp1name+","+dp2name:dp2name+","+dp1name;
        try {

            distance = memcached.get("gather.distance.between?dp1,dp2"+name,3000);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }

        if(distance==null){
            distance = this.distance.getDistance(dp1.getDimensioin(),dp2.getDimensioin());
            try {
                memcached.set("gather.distance.between?dp1,dp2"+name,600,distance);
            }catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return distance;

    }
    private boolean isContain(DataPoint dp,List<DataPoint> dps){
        boolean flag=false;
        String name=dp.getDataPointName().trim();
        for(DataPoint tempDp:dps){
            String tempName=tempDp.getDataPointName().trim();
            if(name.equals(tempName)){
                flag=true;
                break;
            }
        }

        return flag;
    }


    private boolean mergeList(List<DataPoint> dps1,List<DataPoint> dps2){
        boolean flag=false;

        if(dps1==null||dps2==null||dps1.size()==0||dps2.size()==0){
            return flag;
        }

        for(DataPoint dp:dps2){
            if(dp.isKey()&&isContain(dp,dps1)){
                flag=true;
                break;
            }
        }

        if(flag){
            for(DataPoint dp:dps2){
                if(!isContain(dp,dps1)){
                    DataPoint tempDp=new DataPoint(dp.getDimensioin(),dp.getDataPointName(),dp.isKey());
                    dps1.add(tempDp);
                }
            }
        }


        return flag;
    }

//    public static void main(String[] args){
//        ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();
//
//        double[] a={2,3};
//        double[] b={2,4};
//        double[] c={1,4};
//        double[] d={1,3};
//        double[] e={2,2};
//        double[] f={3,2};
//
//        double[] g={8,7};
//        double[] h={8,6};
//        double[] i={7,7};
//        double[] j={7,6};
//        double[] k={8,5};
//
//        double[] l={100,2};//??????
//
//
//        double[] m={8,20};
//        double[] n={8,19};
//        double[] o={7,18};
//        double[] p={7,17};
//        double[] q={8,21};
//
//        dpoints.add(new DataPoint(a,"a",false));
//        dpoints.add(new DataPoint(b,"b",false));
//        dpoints.add(new DataPoint(c,"c",false));
//        dpoints.add(new DataPoint(d,"d",false));
//        dpoints.add(new DataPoint(e,"e",false));
//        dpoints.add(new DataPoint(f,"f",false));
//
//        dpoints.add(new DataPoint(g,"g",false));
//        dpoints.add(new DataPoint(h,"h",false));
//        dpoints.add(new DataPoint(i,"i",false));
//        dpoints.add(new DataPoint(j,"j",false));
//        dpoints.add(new DataPoint(k,"k",false));
//
//        dpoints.add(new DataPoint(l,"l",false));
//
//        dpoints.add(new DataPoint(m,"m",false));
//        dpoints.add(new DataPoint(n,"n",false));
//        dpoints.add(new DataPoint(o,"o",false));
//        dpoints.add(new DataPoint(p,"p",false));
//        dpoints.add(new DataPoint(q,"q",false));
//
//        ClusterAnalysis ca=new ClusterAnalysis(new EuclideanDistance(),true);
//        List<Cluster> clusterList=ca.doDbscanAnalysis(dpoints, 2, 4);
//        ca.displayCluster(clusterList);
//
//    }
}

