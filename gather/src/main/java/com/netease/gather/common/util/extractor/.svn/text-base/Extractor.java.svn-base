package com.netease.gather.common.util.extractor;

import com.netease.gather.cluster.dbscan.Cluster;
import com.netease.gather.cluster.dbscan.ClusterAnalysis;
import com.netease.gather.cluster.dbscan.DataPoint;
import com.netease.gather.cluster.dbscan.distance.EuclideanDistance;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
* Author: ykxu
* Date: 13-4-24 下午5:39
*/
public class Extractor {

    private static final Logger logger = LoggerFactory.getLogger(Extractor.class);

    public static Map getContext(String url){

        Document doc = null;
        boolean sucess = false;
        int retry = 5;
        while (!sucess){
            try{
                if(retry>0){
                    doc = Jsoup.connect(url).timeout(5000).get();
                }
                sucess=true;
            }catch (Exception e){
                retry--;
                logger.error(e.getMessage()+","+url);
            }
        }


        Map rmap = new HashMap();


        String context="";
        String title="";
        List<Map<String,String>> imgList = new ArrayList<Map<String, String>>();
//        System.out.println(doc);
        if (doc != null) {
//            System.out.print(doc.title().split("_")[0].split("-")[0]);
            title = doc.title().split("_")[0].split("-")[0].split("\\|")[0];
            Element body = doc.body();
            if(body!=null){
                String[] abandon={"[style*=display:none]",".hidden","[class*=summary]","[class*=side]","[id*=side]","[class*=footer]","[id*=footer]","[class*=foot]","[id*=foot]","[class*=header]","[id*=header]","[class*=nav]","[id*=nav]","[class*=copyright]","[id*=copyright]","[class*=right]","[id*=right]","[class*=menu]","[id*=menu]","[class*=focus]","[id*=focus]","[class*=tie]","[id*=tie]","[class*=comment]","[id*=comment]","[class*=broadcast]","[id*=broadcast]","[class*=about]","[id*=about]","[class*=share]","[id*=share]","[class*=commend]","[id*=commend]"};
                for (String str:abandon){
                    Elements rmele = body.select(str);
                    rmele.remove();
                }
                Elements ilist = body.select("img[src~=(?i)\\.(png|jpe?g)]:not(img[src*=#]):not(img[onerror])");
                for (Element element:ilist){
                    String height = element.attr("height");
                    String width = element.attr("width");
                    try {
                        if(height.length()>0&&!width.contains("%")&&Double.valueOf(height.replace("px",""))<100){
                            continue;
                        }
                        if(width.length()>0&&!width.contains("%")&&Double.valueOf(width.replace("px",""))<120){
                            continue;
                        }
                    }catch (Exception e){
                        continue;
                    }

                    Map<String,String> imgMap = new HashMap<String, String>();
                    imgMap.put("src",element.attr("src"));
                    imgMap.put("alt",element.attr("alt"));
                    imgList.add(imgMap);
                }
//            Elements hs = body.select(".hidden");
//            hs.remove();
//            Elements sm = body.select("[class*=summary]");
//            sm.remove();
//            Elements slide = body.select("[class*=side]");
//            slide.remove();
//            Elements sid = body.select("[id*=side]");
//            sid.remove();
//            System.out.println(hs);
                String a = body.html().replace("&quot;"," ").replace("&nbsp;"," ");
                a = Jsoup.clean(a, Whitelist.basic());
//            System.out.println(a);
                String[] d = a.split("\\n");
                List<String> gList = new ArrayList<String>();
                for (String s: d){
                    String t =  Jsoup.clean(Jsoup.parse(s).text(), Whitelist.simpleText());
                    if(!s.equals(t)){
                        String[] h = t.split(" ");
                        for (String z: h){
                            gList.add(z);
                        }
                    }else {
                        gList.add(t);
                    }

                }
//            for (String g:gList){
//                System.out.println(g);
//            }
                List<String> textl = new ArrayList<String>();
                List<DataPoint> dList = new ArrayList<DataPoint>();
                for(int i=0;i<gList.size();i++){
                    String t =gList.get(i);
                    List<Double> dim = new ArrayList<Double>();
                    dim.add((double) dList.size());
                    dim.add((double) t.length());
//                    dim[0] = dList.size();
//                    dim[1] = t.length();
                    DataPoint tmp = new DataPoint(dim,String.valueOf(dList.size()),false);
                    dList.add(tmp);
                    textl.add(t);
                }

                ClusterAnalysis ca=new ClusterAnalysis(new EuclideanDistance(),true,false,false);
                List<Cluster> clusterList=ca.doDbscanAnalysis(dList, 12, 8); //12,6
                Collections.sort(clusterList, new Comparator<Cluster>() {
                    public int compare(Cluster d1, Cluster d2) {
                        int s1 = d1 == null ? 0 : d1.getDataPoints() == null ? 0 : d1.getDataPoints().size();
                        int s2 = d2 == null ? 0 : d2.getDataPoints() == null ? 0 : d2.getDataPoints().size();
                        return s2 - s1;

                    }
                });

                Set<String> ds = new HashSet<String>();
                for (DataPoint dp:dList){
                    ds.add(dp.getDataPointName());

                }


//            System.out.println(clusterList.size());
                int count = 0;
                for(int i=0;i<clusterList.size();i++){
                    Cluster tempCluster = clusterList.get(i);
                    if(tempCluster.getDataPoints()!=null&&tempCluster.getDataPoints().size()>0){
                        count++;
                        for(DataPoint dp:tempCluster.getDataPoints()){
                            ds.remove(dp.getDataPointName());
                        }
                    }
                }
//            System.out.println(count);

                List<String> sList = new ArrayList<String>(ds);
                Collections.sort(sList, new Comparator<String>() {
                    public int compare(String d1, String d2) {
                        int s1 = StringUtil.isBlank(d1) ? 0 : Integer.valueOf(d1);
                        int s2 = StringUtil.isBlank(d2) ? 0 : Integer.valueOf(d2);
                        return s1 - s2;

                    }
                });
                for(String dp:sList){
                    context = context + "\n" + textl.get(Integer.valueOf(dp));
//                System.out.println(textl.get(Integer.valueOf(dp)));
                }
            }
//            return result;
        }
        rmap.put("imgs",imgList);
        rmap.put("context",context);
        rmap.put("title",title);
        return rmap;
    }

    public static String getImage(String url){
        Document doc = null;
        try{
            doc = Jsoup.connect(url).timeout(3000).get();
        }catch (IOException e){
            logger.error(e.getMessage(),e);
//            System.out.println(Arrays.toString(e.getStackTrace()));
        }

//        Map<String,String> rmap = new HashMap<String,String>();
//        rmap.put("context","");
//        rmap.put("title","");
        if (doc != null) {
            Element body = doc.body();
            String[] abandon={"[style*=display:none]","[class*=summary]","[class*=side]","[id*=side]","[class*=footer]","[id*=footer]","[class*=foot]","[id*=foot]",
                    "[class*=header]","[id*=header]","[class*=nav]","[id*=nav]","[class*=copyright]","[id*=copyright]"};
            for (String str:abandon){
                Elements rmele = body.select(str);
                rmele.remove();
            }
            Elements ilist = body.select("img[src~=(?i)\\.(png|jpe?g)]:not(img[src*=#]):not(img[onerror])");
            for (Element element:ilist){
                String height = element.attr("height");
                String width = element.attr("width");
                logger.info(height +"+"+width);
                if(height.length()>0&&Integer.valueOf(height)<100){
                    continue;
                }
                if(width.length()>0&&Integer.valueOf(width)<120){
                    continue;
                }
                String imagesrc = element.attr("src");
                logger.info(imagesrc);
            }
//            logger.info(String.valueOf(ilist));
            if(ilist!=null&&ilist.size()>0){
                return ilist.get(0).attr("src");
            }
        }

        return "";
    }

    public static void main(String[] args) throws Exception {
//        Extractor.getContext("http://news.jwb.com.cn/art/2013/4/24/art_186_2773249.html");
//        Extractor.getContext("http://www.gucheng.com/hot/2013/2386407.shtml");
//        Extractor.getContext("http://news.guhantai.com/2013/0425/231768.shtml");
//        Extractor.getContext("http://leaders.people.com.cn/GB/10722961.html");
//        Extractor.getContext("http://news.qq.com/a/20130426/000777.htm ");
//        Extractor.getContext("http://news.163.com/13/0425/03/8T9BKCE700014AED.html");
//            Extractor.getContext("http://news.163.com/13/0627/03/92BKP1P700014AED.html");
//            Extractor.getContext("http://news.163.com/13/0628/11/92F1T2DI0001124J.html");
//            Extractor.getContext("http://news.163.com/13/0701/12/92MTP7JF00014JB6.html");
////            Extractor.getContext("http://movies.yahoo.com/news/lone-ranger-review-hi-yawn-silver-awaaaay-040003766.html");
//            Extractor.getContext("http://news.163.com/13/0702/12/92PDEOM30001121M.html");
//            Extractor.getContext("http://news.163.com/13/0701/09/92MIFLIJ00014JB6.html");
            Extractor.getContext("http://news.163.com/13/0627/20/92DD9K6H00014JB5.html");
//            Extractor.getContext("http://news.xinhuanet.com/politics/2013-07/03/c_116391308.htm");
//            Extractor.getContext("http://news.qq.com/a/20130703/014520.htm");
//        Extractor.getContext("http://news.163.com/13/0627/03/92BL36NU00014AED.html");
//        Extractor.getContext("http://hn.ifeng.com/zixun/yaowen/detail_2013_04/26/747550_0.shtml ");
//        Extractor.getContext("http://tw.people.com.cn/n/2013/0725/c14657-22326312.html");
//            System.out.println(Jsoup.clean("<div><a href='#'>1215664</a></div>", Whitelist.none()));
//
//        Extractor.getImage("http://news.163.com/13/0705/16/931I3RC500011229.html");
//        Extractor.getImage("http://news.163.com/13/0705/16/931IMAQL0001124J.html");
//        Extractor.getImage("http://news.163.com/13/0705/00/92VT8GH000014JB6.html");
//        Extractor.getImage("http://news.qq.com/a/20130703/014520.htm");
//        Extractor.getImage("http://finance.sina.com.cn/china/20130705/165816032066.shtml");
//        Extractor.getImage("http://news.163.com/13/0705/18/931QRCP20001124J.html");
//        Extractor.getImage("http://news.163.com/13/0709/19/93C87UHM00011229.html");
//        System.out.println(Arrays.toString("李天一新律师身份遭疑受害人曾求助酒吧|李天一|律师|酒吧_新浪娱乐_新浪网".split("_")[0].split("-")[0].split("\\|")));
    }
}
