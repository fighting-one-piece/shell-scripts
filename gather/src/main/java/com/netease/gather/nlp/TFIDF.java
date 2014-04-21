package com.netease.gather.nlp;

import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.common.util.ShortUrlGenerator;
import com.netease.gather.common.util.extractor.Extractor;
import com.netease.gather.domain.Doc;
import com.netease.gather.extapi.CMSUtil;
import com.netease.gather.theard.TFThread;
import net.rubyeye.xmemcached.MemcachedClient;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: AzraelX
 * Date: 13-8-27
 * Time: 下午2:47
 */
public class TFIDF {

    private static final Logger logger = LoggerFactory.getLogger(TFIDF.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final int EXPIRE_TIME = 1200;

    private static MemcachedClient memcached;

    static {
        memcached = ScheduleContext.BF.getBean("memcachedClientForNlp", MemcachedClient.class);
    }
//    private static POSTagger pos;
//
//    static {
//        try {
//            ClassLoader cl = TFIDF.class.getClassLoader();
//            String POSTAGGER = new File(cl.getResource(Constants.POSTAGGER).toURI()).getAbsolutePath();
//            String CWSTAGGER = new File(cl.getResource(Constants.CWSTAGGER).toURI()).getAbsolutePath();
//            CWSTagger cws = new CWSTagger(CWSTAGGER);
//            pos = new POSTagger(cws,POSTAGGER);
//        }  catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static Map<String,Map<String,Double>> calTFIDF(List<Doc> doclist) throws Exception{

        Map<String,Map<String,Double>> dtfmap = new HashMap<String, Map<String, Double>>();//(docno -> (词 -> tf))
        Map<String,Integer> dfmap = new HashMap<String, Integer>();//(词 -> df)

        //计算每篇文档中每个词的tf 词频
        List<Future<Map>> futureList = new ArrayList<Future<Map>>();
        for (Doc doc:doclist){
            try {
                futureList.add(executorService.submit(new TFThread(doc)));
//                futureList.add(ThreadManager.calTF(doc));
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
                Map<String,Double> tfmap = (Map<String, Double>) rmap.get("tfmap");;
                Doc doc = (Doc) rmap.get("doc");
                if(tfmap!=null&&tfmap.size()>0){
                    dtfmap.put(doc.getDocno(),tfmap);
                }
            }
        }

//        //计算每篇文档中每个词的tf 词频
//        for (Doc doc:doclist){
////            Map<String,Integer> wdmap = new HashMap<String, Integer>();
////            Map<String,Double> tfmap = new HashMap<String, Double>();
//
////            String[][] stag = pos.tag2Array(Extractor.getContext(map.get("l")).get("context").toString());
////            if(stag!=null){
////                String[] words = stag[0];
////                String[] pos = stag[1];
////                int count = 0;
////                //计算词频
////                for(int i=0;i<pos.length;i++){
////                    if(!Tags.isStopword(pos[i])){
////                        wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
////                        count +=1;
////                    }
////                }
////                //计算词频/词总数 tf
////                for(Map.Entry<String,Integer> wdcount:wdmap.entrySet()){
////                    tfmap.put(wdcount.getKey(),wdcount.getValue().doubleValue()/count);
////                }
////
////                dtfmap.put(map.get("docno"),tfmap);
////                logger.info(map.get("docno"));
////            }
//
//            Map<String,Double> tfmap = getTFForUrl(doc.getUrl());
//            if(tfmap!=null&&tfmap.size()>0){
//                dtfmap.put(doc.getDocno(),tfmap);
//            }
//        }

        logger.info("tf计算完成！");
        //计算每个词的df 包含词的文档数
        for(Map.Entry<String,Map<String,Double>> dtf:dtfmap.entrySet()){
            for(Map.Entry<String,Double> tf:dtf.getValue().entrySet()){
                dfmap.put(tf.getKey(),dfmap.containsKey(tf.getKey())?dfmap.get(tf.getKey())+1:1);
            }
        }
        logger.info("df计算完成！");

        //计算每个词的idf log(文档总数/(包含该词的文档数+1))
        Map<String,Double> idfmap = new HashMap<String, Double>();//(词->idf)
        double docnums = Integer.valueOf(dtfmap.size()).doubleValue();
        for(Map.Entry<String,Integer> df:dfmap.entrySet()){
            idfmap.put(df.getKey(),Math.log(docnums/(df.getValue()+1)));
        }
        logger.info("idf计算完成！");

        //针对每个词计算tf*idf
        Map<String,Map<String,Double>> dtfidfmap = new HashMap<String, Map<String, Double>>();//(docno -> (词,tfidf))
        for(Map.Entry<String,Map<String,Double>> dtf:dtfmap.entrySet()){
            Map<String,Double> tfidfmap = new HashMap<String, Double>();
            for (Map.Entry<String,Double> tf:dtf.getValue().entrySet()){
                tfidfmap.put(tf.getKey(),tf.getValue()*idfmap.get(tf.getKey()));
            }
            dtfidfmap.put(dtf.getKey(),tfidfmap);
        }
        logger.info("tfidf计算完成！");

        return dtfidfmap;
    }

    public static Map<String,Map<String,Double>> calTF(List<Doc> doclist) throws Exception{

        Map<String,Map<String,Double>> dtfmap = new HashMap<String, Map<String, Double>>();//(docno -> (词 -> tf))

        //计算每篇文档中每个词的tf 词频
        List<Future<Map>> futureList = new ArrayList<Future<Map>>();
        for (Doc doc:doclist){
            try {
                futureList.add(executorService.submit(new TFThread(doc)));
//                futureList.add(ThreadManager.calTF(doc));
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
                Map<String,Double> tfmap = (Map<String, Double>) rmap.get("tfmap");;
                Doc doc = (Doc) rmap.get("doc");
                if(tfmap!=null&&tfmap.size()>0){
                    dtfmap.put(doc.getDocno(),tfmap);
                }
            }
        }

        return dtfmap;
    }

//    public String getDoctext(Map<String,String> doc) throws Exception{
//
//        String doctext = memcached.get("gather.doctext?docid="+doc.get("docno"),3000);
//        if(doctext==null||doctext.trim().length()==0){
//            doctext=Extractor.getContext(doc.get("l")).get("context").toString();
//            memcached.set("gather.doctext?docid="+doc.get("docno"),72000,doctext);
//        }
//
//        return doctext;
//
//    }

//    public static Map<String, Double> getTFForOneDoc(Doc doc) throws Exception{
//
////        Map<String,Double> tfmap = memcached.get("gather.doctf?docid="+doc.getDocno(),30000);
////        if(tfmap==null){
//        Map<String,Double> tfmap = getTF(Extractor.getContext(doc.getUrl()).get("context").toString());
////            try {
////                memcached.set("gather.doctf?docid=" + doc.getDocno(), 30, tfmap);
////            }catch (Exception e){
////                logger.error(e.getMessage());
////            }
//
////        }
////        logger.info(doc.getDocno());
//
//
//        return tfmap;
//    }

    public static Map<String, Double> getTFForUrl(String url) throws Exception{

        String docno = ShortUrlGenerator.generatorAllStr(url);
        Map<String,Double> tfmap = memcached.get("gather.doctf?docno="+ docno,30000);
        if(tfmap==null){
            String context = "";
            boolean get163Art = false;
            if(url.contains(".163.com")){
                try {
                    String docid = CommonUtil.get163DocidFromUrl(url);
                    context = CMSUtil.getArticleWCache(docid);
                    get163Art = true;
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
            }

            if(!get163Art){
                context = (String) Extractor.getContext(url).get("context");
            }

            tfmap = getTF(context);
            try {
//                tfmap = (Map<String, Double>) JsonUtil.fromJson(HttpUtil.getURL("http://bling.163.com/api/getTFByUrl?url=" + URLEncoder.encode(url, "UTF8"), "UTF8", null), Map.class).get("tfmap");
                memcached.set("gather.doctf?docno=" + docno, EXPIRE_TIME, tfmap);
            }catch (Exception e){
                logger.error(e.getMessage());
            }

        }
        return tfmap;
    }

    public static Map<String, Double> getTFForOneDoc(String docno,String conext) throws Exception{

        Map<String,Double> tfmap = memcached.get("gather.doctf?docno="+docno,30000);
        if(tfmap==null){
            tfmap = getTF(conext);
            try {
                memcached.set("gather.doctf?docno=" + docno, EXPIRE_TIME, tfmap);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
//        logger.info(doc.getDocno());


        return tfmap;
    }

    public static Map<String, Double> getWCForUrl(String url) throws Exception{

        String docno = ShortUrlGenerator.generatorAllStr(url);
        Map<String,Double> tfmap = memcached.get("gather.docwc?docno="+ docno,30000);
        if(tfmap==null){
            tfmap = getWC((String)Extractor.getContext(url).get("context"));
            try {
//                tfmap = (Map<String, Double>) JsonUtil.fromJson(HttpUtil.getURL("http://bling.163.com/api/getWCByUrl?url=" + URLEncoder.encode(url, "UTF8"), "UTF8", null), Map.class).get("wcmap");
                memcached.set("gather.docwc?docno=" + docno, EXPIRE_TIME, tfmap);
            }catch (Exception e){
                logger.error(e.getMessage());
            }

        }
        return tfmap;
    }

    public static Map<String, Double> getWCForOneDoc(String docno,String conext) throws Exception{

        Map<String,Double> tfmap = memcached.get("gather.docwc?docno="+docno,30000);
        if(tfmap==null){
            tfmap = getWC(conext);
            try {
                memcached.set("gather.docwc?docno=" + docno, EXPIRE_TIME, tfmap);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return tfmap;
    }

    public static Map<String, Double> getTF(String conext) throws Exception{

//        Map<String,Integer> wdmap = new HashMap<String, Integer>();
//        Map<String, Double> tfmap = new HashMap<String, Double>();
//        String[][] stag = pos.tag2Array(conext);
//
//        if(stag!=null){
//            String[] words = stag[0];
//            String[] pos = stag[1];
//            int count = 0;
//            //计算词频
//            for(int i=0;i<pos.length;i++){
////                logger.info(pos[i]);
////                if(!Tags.isStopword(pos[i])){
////                    wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
////                    count +=1;
////                }
//                //2013.10.12改完只取名词动词实体词
//                if(Tags.isEntiry(pos[i])||"名词".equals(pos[i])||"动词".equals(pos[i])){
//                    wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
//                    count +=1;
//                }
//            }
//            //计算词频/词总数 tf
//            for(Map.Entry<String,Integer> wdcount:wdmap.entrySet()){
//                tfmap.put(wdcount.getKey(),wdcount.getValue().doubleValue()/count);
//            }
//        }


        Map<String, Double> tfmap = new HashMap<String, Double>();
        Map<String, Double> wdmap = getWC(conext);
//        logger.info(String.valueOf(wdmap));
        if(wdmap.size()>0){
            double count = 0;
            //计算词总数
            for(Map.Entry<String,Double> wdcount:wdmap.entrySet()){
                count = count + wdcount.getValue();
            }

            //计算词频/词总数 tf
            for(Map.Entry<String,Double> wdcount:wdmap.entrySet()){
                tfmap.put(wdcount.getKey(),wdcount.getValue()/count);
            }
        }

        return tfmap;
    }

    public static Map<String, Double> getWC(String conext) throws Exception{

        Map<String,Double> wdmap = new HashMap<String, Double>();
//        Map<String, Double> tfmap = new HashMap<String, Double>();


        //2013-10-14更换分词器
        List<Term> terms = NlpAnalysis.parse(conext);
        for(Term term:terms){
            String word = term.getName();
            if(word!=null&&word.length()>0){
                String nature = term.getNatrue().natureStr;
                if(nature==null||nature.equals("null")) continue;
                if(word.length()<2) continue;
                if(nature.startsWith("n")||nature.startsWith("v")||nature.startsWith("i")||nature.startsWith("j")){
                    wdmap.put(word,wdmap.containsKey(word)?wdmap.get(word)+1:1);
                }
            }
        }


//        String[][] stag = pos.tag2Array(conext);
//
//        if(stag!=null){
//            String[] words = stag[0];
//            String[] pos = stag[1];
//            //计算词频
//            for(int i=0;i<pos.length;i++){
//                //2013.10.12改完只取名词动词实体词
//                if(Tags.isEntiry(pos[i])||"名词".equals(pos[i])||"动词".equals(pos[i])){
//                    wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
////                    count +=1;
//                }
//            }
//        }


        return wdmap;
    }

    public static Map<String, Double> getCF(String conext) throws Exception{

//        Map<String,Integer> wdmap = new HashMap<String, Integer>();
//        Map<String, Double> tfmap = new HashMap<String, Double>();
//        String[][] stag = pos.tag2Array(conext);
//
//        if(stag!=null){
//            String[] words = stag[0];
//            String[] pos = stag[1];
//            int count = 0;
//            //计算词频
//            for(int i=0;i<pos.length;i++){
////                logger.info(pos[i]);
////                if(!Tags.isStopword(pos[i])){
////                    wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
////                    count +=1;
////                }
//                //2013.10.12改完只取名词动词实体词
//                if(Tags.isEntiry(pos[i])||"名词".equals(pos[i])||"动词".equals(pos[i])){
//                    wdmap.put(words[i],wdmap.containsKey(words[i])?wdmap.get(words[i])+1:1);
//                    count +=1;
//                }
//            }
//            //计算词频/词总数 tf
//            for(Map.Entry<String,Integer> wdcount:wdmap.entrySet()){
//                tfmap.put(wdcount.getKey(),wdcount.getValue().doubleValue()/count);
//            }
//        }


        Map<String, Double> cfmap = new HashMap<String, Double>();

        Map<String, Double> ccmap = getWC(conext);
        for(Character character:conext.toCharArray()){
            ccmap.put(String.valueOf(character),ccmap.containsKey(character)?ccmap.get(character)+1:1);
        }
//        logger.info(String.valueOf(wdmap));
        if(ccmap.size()>0){
            double count = 0;
            //计算字总数
            for(Map.Entry<String,Double> wdcount:ccmap.entrySet()){
                count = count + wdcount.getValue();
            }

            //计算字频/字总数 cf
            for(Map.Entry<String,Double> wdcount:ccmap.entrySet()){
                cfmap.put(wdcount.getKey(),wdcount.getValue()/count);
            }
        }

        return cfmap;
    }
}
