package com.netease.gather.service.logic.impl;

import com.netease.gather.classifier.naivebayes.Classifier;
import com.netease.gather.cluster.dbscan.Cluster;
import com.netease.gather.cluster.dbscan.ClusterAnalysis;
import com.netease.gather.cluster.dbscan.DataPoint;
import com.netease.gather.cluster.dbscan.distance.CosineSimilarity;
import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.*;
import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.domain.Doc;
import com.netease.gather.domain.Hot;
import com.netease.gather.domain.HotDoc;
import com.netease.gather.nlp.SimilarityCalc;
import com.netease.gather.nlp.TFIDF;
import com.netease.gather.service.data.DocService;
import com.netease.gather.service.data.HotDocService;
import com.netease.gather.service.data.HotService;
import com.netease.gather.service.logic.NewsHotService;
import com.netease.gather.theard.SimilarityThread;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Service("newsHotService")
public class NewsHotServiceImpl implements NewsHotService {

    private static final Logger logger = LoggerFactory.getLogger(NewsHotServiceImpl.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);


    @Resource(name="docService")
    private DocService docService;
    @Resource(name="hotService")
    private HotService hotService;
    @Resource(name="hotDocService")
    private HotDocService hotDocService;
    @Resource(name="memcachedClient")
    private MemcachedClient memcached;
//    @Resource(name="commonTopicService")
//    private CommonTopicService commonTopicService;

    private static double clusterthreshold = 0.58;
    private static double mergerthreshold = 0.5;
    private static double titlesimweight = 0.9;

    static {
        if("demo".equals(Config.init().get("env"))){
            clusterthreshold = 0.58;
            mergerthreshold = 0.5;
            titlesimweight = 0.9;
//            clusterthreshold = 0.6;
//            mergerthreshold = 0.48;
        }
    }
    private static Map<String,Integer> colshowlimit = new HashMap<String, Integer>(){{
        put("shehui",2);
        put("hongguan",2);
        put("guonei",3);
    }};

    @Override
    @SuppressWarnings({"unchecked"})
    public int genHots(String channel, String starttime, String endtime,String source,int clustersize) throws Exception{

        String hotsizekey = "gather.hotsize?channel="+channel+",starttime="+starttime+",endtime="+endtime+",source="+source+",clustersize="+clustersize;
        Integer hotsize = memcached.get(hotsizekey);
        if(hotsize==null){
            String lockkey = "gather.lock.gethot?channel="+channel+",starttime="+starttime+",endtime="+endtime+",source="+source+",clustersize="+clustersize;
            logger.info(lockkey);
            if(memcached.add(lockkey,1800,"gethot_lock")){
                try {
                    logger.info(channel+":"+starttime+"--"+endtime);
                    Map map = new HashMap();
                    map.put("channel",channel);
                    map.put("starttime",DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"));
                    map.put("endtime",DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"));
                    map.put("clustersize",clustersize);
                    List<Hot> hots = hotService.getListByParameters(map);
                    if(hots.size()==0){
                        map.put("source",source);
                        List<Doc> docs = docService.getListByParameters(map);

                        logger.info(String.valueOf(docs.size()));

                        List<DefaultKeyValue> hotlist = findHot(docs,clustersize);
                        if(hotlist.size()>0){
                            hotService.deleteSomeByParameters(map);
                        }
                        Classifier classifier=new Classifier();
                        for (DefaultKeyValue kv : hotlist){
                            List<String> docnos = (List<String>) kv.getValue();
                            String col = classifier.classifyForHot(docnos,channel);
                            Hot hot = new Hot();
                            hot.setDocno(kv.getKey().toString());
                            hot.setChannel(channel);
                            hot.setCol(col);
                            hot.setStarttime(DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"));
                            hot.setEndtime(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"));
                            hot.setClustersize(clustersize);
                            hot.setCreatetime(new Date());
                            long hotid = hotService.saveOne(hot);
                            for(String docno:docnos){
                                HotDoc hotDoc = new HotDoc();
                                hotDoc.setHotid(hotid);
                                hotDoc.setDocno(docno);
                                hotDoc.setCreatetime(new Date());
                                hotDocService.saveOne(hotDoc);
                            }
                        }
                        hotsize = hotlist.size();
                        memcached.set(hotsizekey, 600, hotsize);
                    }else {
                        hotsize = hots.size();
                    }
//                    Map map = new HashMap();
//                    map.put("channel",channel);
//                    map.put("starttime",DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"));
//                    map.put("endtime",DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"));
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                    PoPo.send("ykxu@corp.netease.com","热点计算出错！\n" + e.getMessage());
                    hotsize = 0;
                }
                memcached.delete(lockkey);
            }else {
                logger.info("等待channel=" + channel + ",starttime=" + starttime + ",endtime=" + endtime + ",source="+source+",clustersize="+clustersize + "计算任务！");
                Thread.sleep(30000);
                hotsize = genHots(channel, starttime, endtime, source, clustersize);
            }
        }

        logger.info("热点数"+hotsize);
        return hotsize;
    }

    @Override
    public List<Map> choiceHot(String channel,String col, String starttime, String endtime,int repeat,int showlimit) throws Exception{
        Date start = DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss");
        Date end = DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss");
        int plus = (int) DateUtil.getHoursBetweenTwoDates(start, end);

        List<Hot> hots = new ArrayList<Hot>();
        for(int i=0;i<repeat;i++){
            Map hmap = new HashMap();
            hmap.put("channel",channel);
            hmap.put("col",col);
            hmap.put("starttime",DateUtils.addHours(start, -plus*i));
            hmap.put("endtime",DateUtils.addHours(end,-plus*i));
            List<Hot> nhots = hotService.getListByParameters(hmap);
            hots.addAll(nhots);
        }

        List<Map> allhots = new ArrayList<Map>();
        for(Hot hot:hots){
            Map remap = new HashMap();
            Map hdmap = new HashMap();
            hdmap.put("hotid",hot.getAutoid());
            remap.put("channel",channel);
            remap.put("hotid",hot.getAutoid());
            remap.put("col",hot.getCol());
            List<HotDoc> hotDocs = hotDocService.getListByParameters(hdmap);
            List<Doc> dlist = new ArrayList<Doc>();
            Set<String> soset = new HashSet<String>();
            for(HotDoc hotDoc:hotDocs){
                Map dmap = new HashMap();
                dmap.put("docno",hotDoc.getDocno());
                Doc doc = docService.getOneByParameters(dmap);

                if(doc!=null){
                    dlist.add(doc);
                    soset.add(doc.getSource());
                    if(hot.getDocno().equals(doc.getDocno())){
                        Doc cdoc = new Doc();
                        BeanUtils.copyProperties(doc,cdoc);
                        remap.put("cdoc",cdoc);//最好复制一下
                    }
                }
            }

            remap.put("dlist", dlist);
            remap.put("soset",soset);

            allhots.add(remap);
        }

        //所有情况都要合并去重，存在快讯的相似度不足，但是标题一样的情况。
        List<Map> norepeathots = rmRepeatHots(allhots);
        logger.info("热点去重完成！去重前:"+allhots.size()+";去重后:"+norepeathots.size());
//        if(repeat>1){
//            norepeathots = rmRepeatHots(allhots);
//            logger.info("热点去重完成！去重前:"+allhots.size()+";去重后:"+norepeathots.size());
//        }else {
//            norepeathots.addAll(allhots);
//        }

        List<Map> showhots = new ArrayList<Map>();
        for(Map remap:norepeathots){
            int slimit = colshowlimit.containsKey((String)remap.get("col"))?colshowlimit.get((String)remap.get("col")):showlimit;
            if(((List<Doc>)remap.get("dlist")).size()>=slimit){
                showhots.add(remap);
            }
        }
        logger.info("剔除文章数量不足的聚点完成！");

        return showhots;
    }

    @SuppressWarnings({"unchecked"})
    public List<DefaultKeyValue> findHot(List<Doc> doclist,int clustersize) throws Exception{
        Map<String,Map<String,Double>> dtfidf = TFIDF.calTFIDF(doclist);
//        Map<String,Map<String,Double>> dtf = TFIDF.calTF(doclist);

        Map<String,Doc> docmap = new HashMap<String, Doc>();
        for (Doc doc : doclist){
            docmap.put(doc.getDocno(),doc);
        }


        List<DataPoint> dplist = new ArrayList<DataPoint>();
        for (Map.Entry<String,Map<String,Double>> tfidf:dtfidf.entrySet()){
//        for (Map.Entry<String,Map<String,Double>> tfidf:dtf.entrySet()){
            List<Map<String,Double>> dim = new ArrayList<Map<String, Double>>();
            dim.add(tfidf.getValue());
            dplist.add(new DataPoint(dim,tfidf.getKey(),false));
        }
        logger.info("聚类初始化完成！clusterthreshold:"+clusterthreshold);

        ClusterAnalysis clusterAnalysis=new ClusterAnalysis(new CosineSimilarity(),false,true,true);
        List<Cluster> clusterList=clusterAnalysis.doDbscanAnalysis(dplist, clusterthreshold, clustersize);

        List<DefaultKeyValue> clist = new ArrayList<DefaultKeyValue>();

        if(clusterList!=null){
            Collections.sort(clusterList, new Comparator<Cluster>() {
                @Override
                public int compare(Cluster o1, Cluster o2) {
                    return o2.getDataPoints().size()-o1.getDataPoints().size();
                }
            });

            for (Cluster cluster:clusterList){
                if(cluster!=null&&cluster.getDataPoints()!=null&&cluster.getDataPoints().size()>0){
                    Set<String> sources = new HashSet<String>();
                    List<String> docnolist = new ArrayList<String>();
                    List<Doc> caldoclist = new ArrayList<Doc>();
                    for(DataPoint dp:cluster.getDataPoints()){
                        String docno = docmap.get(dp.getDataPointName()).getDocno();
                        docnolist.add(docno);
                        String source = docmap.get(dp.getDataPointName()).getSource();
                        sources.add(source);
                        caldoclist.add(docmap.get(dp.getDataPointName()));
                    }

                    //小于两个媒体报道，且非163热点
                    if(sources.size()<2&&!sources.contains("163")){
                        continue;
                    }

//                    if(sources.size()>1){
                    Doc picked = pickCoreDocByTitle(caldoclist);
                    String pickdocno = picked.getDocno();

                    DefaultKeyValue kv = new DefaultKeyValue();
                    kv.setKey(pickdocno);
                    kv.setValue(docnolist);
                    clist.add(kv);
//                    }

                }
            }
        }

        logger.info("聚点处理完成！");

        return clist;
    }

    public List<Doc> choiceCoreDocOption(final List<Doc> docList, boolean find163, List<Doc> _163docs) throws Exception{
        Set<String> sources = new HashSet<String>();
        List<Doc> _163doclist = new ArrayList<Doc>();
        for(Doc doc:docList){
            String source = doc.getSource();
            sources.add(source);
            if("163".equals(source)){
                _163doclist.add(doc);
            }
        }

        if(_163doclist.size()==0){
            if(find163){
                Set<String> exdoc = new HashSet<String>();
//                List<Doc> finddocs = getToday163Artcle(docList.get(0).getChannel());
//                logger.info("163文章数"+finddocs.size());
                logger.info("163文章数"+_163docs.size());
                List<Doc> simdocs = new ArrayList<Doc>();
                for(Doc fdoc:_163docs){
                    List<Future<Double>> futureList = new ArrayList<Future<Double>>();
                    for (Doc cdoc:docList){

                        try {
                            futureList.add(executorService.submit(new SimilarityThread(fdoc.getUrl(),cdoc.getUrl())));
                        }catch (Exception e){
                            logger.error(e.getMessage());
                        }

//                        double sim = SimilarityCalc.calcByCosWUrl(fdoc.getUrl(),cdoc.getUrl());
//                        if(sim>=clusterthreshold){
//                            if(!exdoc.contains(fdoc.getDocno())){
//                                exdoc.add(fdoc.getDocno());
//                                simdocs.add(fdoc);
//                                break;
//                            }
//                        }
                    }

                    for(Future<Double> future:futureList){
                        Double sim = 0.0;
                        try {
                            sim = future.get(600000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally{
                            future.cancel(true);
                        }
                        if(sim>=clusterthreshold){
                            if(!exdoc.contains(fdoc.getDocno())){
                                exdoc.add(fdoc.getDocno());
                                simdocs.add(fdoc);
                            }
                        }
                    }


                }
                _163doclist = simdocs;
            }
        }


        List<Doc> caldoclist = new ArrayList<Doc>();
        if(_163doclist.size()>0){
            caldoclist.addAll(_163doclist);
        }else {
            caldoclist.addAll(docList);
        }

        return caldoclist;
    }


//    public Doc pickCoreDocByTFCountLimitTitleLength(final List<Doc> caldoclist,double sublimit,double suplimit) throws Exception{
//        List<List> tits = new ArrayList<List>();
//
//        //按正文词数取聚点
//        for(Doc doc:caldoclist){
//            List tit = new ArrayList(Arrays.asList(doc,TFIDF.getTFForUrl(doc.getUrl()).size()));
//            tits.add(tit);
//        }
//
//        Collections.sort(tits,new Comparator<List>() {
//            @Override
//            public int compare(List o1, List o2) {
//                return Integer.valueOf(o2.get(1).toString()).compareTo(Integer.valueOf(o1.get(1).toString()));
//            }
//        });
//
//        List picked = tits.get(0);
//
//        Doc pickdoc = (Doc) picked.get(0);
//        double titlength = StringUtil.chineseLength(pickdoc.getTitle())/2.0;
//        if(titlength<sublimit||titlength>suplimit){
//            for(List list:tits){
//                Doc doc = (Doc) list.get(0);
//                titlength = StringUtil.chineseLength(doc.getTitle())/2.0;
//                if(titlength>=sublimit&&titlength<=suplimit){
//                    pickdoc.setTitle(doc.getTitle());
//                    break;
//                }
//            }
//        }
//
//        return pickdoc;
//
//    }


    public Doc pickCoreDocByTFCount(final List<Doc> caldoclist) throws Exception{
        List<List> tits = new ArrayList<List>();

        //按正文词数取聚点
        for(Doc doc:caldoclist){
            List tit = new ArrayList(Arrays.asList(doc,TFIDF.getTFForUrl(doc.getUrl()).size()));
            tits.add(tit);
        }

        Collections.sort(tits,new Comparator<List>() {
            @Override
            public int compare(List o1, List o2) {
                return Integer.valueOf(o2.get(1).toString()).compareTo(Integer.valueOf(o1.get(1).toString()));
            }
        });

        List picked = tits.get(0);

//        Doc pickdoc = (Doc) picked.get(0);

        Doc pickdoc = new Doc();
        BeanUtils.copyProperties((Doc) picked.get(0),pickdoc);

        return pickdoc;

    }


    public Doc pickCoreDocByTitle(final List<Doc> caldoclist) throws Exception{
        List<List> tits = new ArrayList<List>();

        //按标题权重去聚点
        for(Doc doc:caldoclist){
            List tit = new ArrayList(Arrays.asList(doc,0.0));
            tits.add(tit);
        }

        Map<Character,Integer> chars = new HashMap<Character, Integer>();
        for(List tit:tits){
            Doc doc = (Doc)tit.get(0);
            for(Character character:doc.getTitle().toCharArray()){
                chars.put(character,chars.containsKey(character)?chars.get(character)+1:1);
            }
        }

        for(List tit:tits){
            double score = 0.0;
            Doc doc = (Doc)tit.get(0);
            for(Character character:doc.getTitle().toCharArray()){
                score += chars.get(character);
            }
            tit.set(1,score);
        }

        Collections.sort(tits,new Comparator<List>() {
            @Override
            public int compare(List o1, List o2) {
                return Double.valueOf(o2.get(1).toString()).compareTo(Double.valueOf(o1.get(1).toString()));
            }
        });

        List picked = tits.get(0);

        Doc pickdoc = (Doc) picked.get(0);

        return pickdoc;

    }

    @Override
    public void calSimWithHeadlines(List<Map> rehots,String channel, String endtime,int calsize) throws Exception{
        List<List<Doc>> headlines = getHeadlines(channel,endtime);
        for(int i=0;i<headlines.size();i++){
            if(i>=calsize){
                break;
            }
            List<Doc> docline = headlines.get(i);
            for(Doc hdoc:docline){
                String htitle = hdoc.getTitle();
                String hurl = hdoc.getUrl();
                String hdocno = ShortUrlGenerator.generatorAllStr(hurl);

                logger.info("头条：{}",htitle);

                for(Map remap:rehots){
                    if(remap.containsKey("simtit")){
                        continue;
                    }
                    double sim = 0.0;
                    boolean issim = false;
                    List<Doc> dlist = (List<Doc>) remap.get("dlist");
                    for(Doc doc:dlist){
                        if(hdocno.equals(doc.getDocno())){
                            sim = 1.0;
                        }else {
                            sim = SimilarityCalc.calcByCosWStringByChar(htitle,doc.getTitle())*titlesimweight;
                        }
                        if(sim>=mergerthreshold){
                            issim=true;
                            remap.put("simtit",htitle);
                            break;
                        }
                    }

                    if(issim){
                        continue;
                    }

                    Doc cdoc = (Doc)remap.get("cdoc");
                    if(cdoc!=null){
                        String rtitle = cdoc.getTitle();
                        String rurl = cdoc.getUrl();
                        if(hurl.endsWith("html")){
                            sim = SimilarityCalc.calcByCosWUrl(hurl,rurl);
                        }else {
                            sim = SimilarityCalc.calcByCosWStringByChar(htitle,rtitle)*titlesimweight;
                        }
                    }
                    if(sim>=mergerthreshold){
                        remap.put("simtit",htitle);
                    }
                }
            }
        }
    }



    public List<List<Doc>> getHeadlines(String channel, String endtime) throws Exception{
        String filename = Constants.HTMLROOT+channel+"/headlines/"+DateUtil.DateToString(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+".html";

        File file = new File(filename);
        String rs = "";
        if(!file.exists()){
            rs =catchHeadlines(channel,endtime);
        }

        if(StringUtil.isBlank(rs)){
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                String data = "";
                while((data = read.readLine())!=null){
                    rs+=data;
                }
                read.close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }




        List<List<Doc>> dlist = new ArrayList<List<Doc>>();
        if(rs!=null&&!StringUtil.isEmpty(rs)){
            Document document = Jsoup.parse(rs);
            Elements eles = document.select("li:has(a)");
            for (Element ele:eles){
                Elements aeles = ele.select("a");
                List<Doc> alist = new ArrayList<Doc>();
                for (Element aele:aeles){
                    Doc doc  = new Doc();
                    doc.setTitle(aele.text());
                    doc.setUrl(aele.attr("href"));
                    doc.setChannel(channel);
                    alist.add(doc);
                }
                dlist.add(alist);
            }
        }

        return dlist;
    }

    public String catchHeadlines(String channel, String endtime) throws Exception{
        String url = "http://news.163.com/special/realtime_hot_tag_";

        switch (Constants.CHANNEL.getChannel(channel)){
            case news: url = url+"news/"; break;
            case ent: url = url+"ent/"; break;
            case sports: url = url+"sports/"; break;
            case finance: url = url+"money/";  break;
            case tech: url = url+"tech/"; break;
            default: throw new ApplicationException("频道错误！");
        }

        String filename = Constants.HTMLROOT+channel+"/headlines/"+DateUtil.DateToString(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+".html";

        String rs = HttpUtil.getURL(url, "GBK", null);
        if(rs!=null&&!StringUtil.isEmpty(rs)){
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.createFile(filename)),"GBK"));
                output.write(rs);
                output.close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }

        }

        return rs;
    }

    @Override
    public List<Map> rmRepeatHots(List<Map> allhots) throws Exception{
        List<Map> norepeathots = new ArrayList<Map>();
        Set<Long> exhotid = new HashSet<Long>();

        int hotsize = allhots.size();
        for (int i=0;i<hotsize;i++){
            Map remap = allhots.get(i);
            if(!exhotid.contains((Long)remap.get("hotid"))){

                for (int j=i+1;j<hotsize;j++){
                    Map anomap = allhots.get(j);
                    if(!exhotid.contains((Long)anomap.get("hotid"))){
                        boolean issim = isSim(remap,anomap);
                        if(issim){
                            exhotid.add((Long)anomap.get("hotid"));
                            List<Doc> docs1 = (List<Doc>)remap.get("dlist");
                            List<Doc> docs2 = (List<Doc>)anomap.get("dlist");
                            docs1.addAll(docs2);
                            remap.put("dlist",docs1);
                            Set<String> soset1 = (Set<String>)remap.get("soset");
                            Set<String> soset2 = (Set<String>)anomap.get("soset");
                            soset1.addAll(soset2);
                            remap.put("soset",soset1);
                        }
                    }
                }
                norepeathots.add(remap);
            }
        }

        return norepeathots;
    }

    @Override
    public boolean isSim(Map remap,Map anomap) throws Exception{
        boolean issim = false;

        double docsim = SimilarityCalc.calcByCosWUrl(((Doc)remap.get("cdoc")).getUrl(),((Doc)anomap.get("cdoc")).getUrl());
        double titsim = SimilarityCalc.calcByCosWStringByChar(((Doc)remap.get("cdoc")).getTitle(),((Doc)anomap.get("cdoc")).getTitle())*titlesimweight;
//                        double titsim = StringUtil.countDuplicate(((Doc)remap.get("cdoc")).getTitle(),((Doc)anomap.get("cdoc")).getTitle());
        double sim = Math.max(docsim,titsim);
        if(sim>=mergerthreshold){
            issim=true;
        }

        List<Doc> docs1 = (List<Doc>)remap.get("dlist");
        List<Doc> docs2 = (List<Doc>)anomap.get("dlist");
        t1:for(Doc doc1:docs1){
            for(Doc doc2:docs2){
                sim = SimilarityCalc.calcByCosWStringByChar(doc1.getTitle(), doc2.getTitle())*titlesimweight;
                if(sim>=mergerthreshold){
                    issim=true;
                    break t1;
                }
            }
        }

        return issim;
    }

    @Override
    public boolean hasSame(Map remap,Map anomap) throws Exception{
        boolean issamme = false;

        String url1 = ((Doc)remap.get("cdoc")).getUrl();
        String url2 = ((Doc)anomap.get("cdoc")).getUrl();
        if(url1.equals(url2)){
            issamme=true;
        }

        List<Doc> docs1 = (List<Doc>)remap.get("dlist");
        List<Doc> docs2 = (List<Doc>)anomap.get("dlist");
        t1:for(Doc doc1:docs1){
            for(Doc doc2:docs2){
                url1 = doc1.getUrl();
                url2 = doc2.getUrl();
                if(url1.equals(url2)){
                    issamme=true;
                    break t1;
                }
            }
        }

        return issamme;
    }

//    public List<Map> rmRepeatHots(List<Map> allhots) throws Exception{
//        List<Map> norepeathots = new ArrayList<Map>();
//        Set<Long> exhotid = new HashSet<Long>();
//
//        int hotsize = allhots.size();
//        for (int i=0;i<hotsize;i++){
//            Map remap = allhots.get(i);
//            if(!exhotid.contains((Long)remap.get("hotid"))){
//                List<Future<Double>> futureList = new ArrayList<Future<Double>>();
//                List<Doc> docs1 = (List<Doc>)remap.get("dlist");
//                for (int j=i+1;j<hotsize;j++){
//                    Map anomap = allhots.get(j);
//                    boolean issim = false;
//                    if(!exhotid.contains((Long)anomap.get("hotid"))){
//                        List<Doc> docs2 = (List<Doc>)remap.get("dlist");
//                        for(Doc doc1:docs1){
//                            for(Doc doc2:docs2){
////                                double sim = SimilarityCalc.calcByCosWUrl(doc1.getUrl(),doc2.getUrl());
////                                futureList.add(ThreadManager.calSimilarity(doc1.getUrl(),doc2.getUrl()));
//                                futureList.add(executorService.submit(new SimilarityThread(doc1.getUrl(),doc2.getUrl())));
//                            }
//                        }
//
//                        for (Future<Double> future:futureList){
//                            Double sim = 0.0;
//                            try {
//                                if(!issim){
//                                    sim = future.get(600000, TimeUnit.MILLISECONDS);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }finally{
//                                future.cancel(true);
//                            }
//                            if(sim!=null&&sim>=mergerthreshold){
//                                issim = true;
//                            }
//                        }
//
//                        if(issim){
//                            exhotid.add((Long)anomap.get("hotid"));
//                            docs1.addAll(docs2);
//                            remap.put("dlist",docs1);
//                            Set<String> soset1 = (Set<String>)remap.get("soset");
//                            Set<String> soset2 = (Set<String>)anomap.get("soset");
//                            soset1.addAll(soset2);
//                            remap.put("soset",soset1);
//                        }
//
//                    }
//                }
//                norepeathots.add(remap);
//            }
//        }
//
//        return norepeathots;
//    }

    @Override
    public List<Map> choice163Art(List<Map> allhots, String channel) throws Exception{
        List<Map> choicedhots = new ArrayList<Map>();
        List<Doc> _163docs = getToday163Artcle(channel);
        for(Map remap:allhots){
            Doc cdoc = (Doc)remap.get("cdoc");
            if(!"163".equals(cdoc.getSource())){
                List<Doc> dlist = (List<Doc>)remap.get("dlist");
                List<Doc> caldoclist = choiceCoreDocOption(dlist,true,_163docs);
                Doc ncdoc = pickCoreDocByTFCount(caldoclist);
//                Doc ncdoc = pickCoreDocByTFCountLimitTitleLength(caldoclist, 16, 20);
                remap.put("cdoc",ncdoc);
                logger.info("原聚点" + cdoc.getTitle() + ":" + cdoc.getUrl());
                logger.info("新聚点"+ncdoc.getTitle()+":"+ncdoc.getUrl());
            }
            choicedhots.add(remap);
        }
        logger.info("增补163文章为聚点完成！");

        return choicedhots;
    }

    @Override
    public List<Map> pick163Hots(List<Map> allhots) throws Exception{
        List<Map> choicedhots = new ArrayList<Map>();
        for(Map remap:allhots){
            Doc cdoc = (Doc)remap.get("cdoc");
            if(!"163".equals(cdoc.getSource())){
                continue;
            }
            choicedhots.add(remap);
        }
        logger.info("剔除163聚点完成！");

        return choicedhots;
    }


    @Override
    public void sortHotCommen(List<Map> rehots) throws Exception{

        if(rehots.size()==0){
            return;
        }

        Collections.sort(rehots,new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {

                List<Doc> o1doc = (List<Doc>)o1.get("dlist");
                List<Doc> o2doc = (List<Doc>)o2.get("dlist");
//                String col1= (String) o1.get("col");
//                String col2= (String) o2.get("col");
//                double weight1 = colweight.containsKey(col1)?colweight.get(col1):1.0;
//                double weight2 = colweight.containsKey(col2)?colweight.get(col2):1.0;
//                int rint = Double.valueOf(o2doc.size()*weight2).compareTo(o1doc.size() * weight1);
                int rint = Integer.valueOf(o2doc.size()).compareTo(o1doc.size());
                if(rint==0){
                    rint = ((Set)o2.get("soset")).size()-((Set)o1.get("soset")).size();
                }
                if(rint==0){
                    Date p1 = new Date();
                    Date p2 = new Date();
                    for(Doc doc:o1doc){
                        Date ptime = doc.getPtime();
                        if(ptime.after(p1)){
                            p1 = ptime;
                        }
                    }

                    for(Doc doc:o2doc){
                        Date ptime = doc.getPtime();
                        if(ptime.after(p2)){
                            p2 = ptime;
                        }
                    }
                    rint = p2.compareTo(p1);
                }

                return rint;
            }
        });

    }

    public List<Doc> getToday163Artcle(String channel) throws Exception{
        Map para = new HashMap();
        para.put("channel",channel);
        Date today = new Date();
        String todaystr = DateUtil.DateToString(today,"yyyyMMdd");
        String nextdaystr = DateUtil.DateToString(DateUtils.addDays(today,1),"yyyyMMdd");

        para.put("starttime",DateUtil.stringToDate(todaystr+"00:00:00", "yyyyMMddHH:mm:ss"));
        para.put("endtime",DateUtil.stringToDate(nextdaystr+"00:00:00", "yyyyMMddHH:mm:ss"));
        para.put("source","163");
        logger.info(String.valueOf(para));
        List<Doc> docs = docService.getListByParameters(para);
        return docs;
    }
    
}
