package com.netease.gather.classifier.naivebayes;

import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.FileUtil;
import com.netease.gather.domain.Doc;
import com.netease.gather.nlp.TFIDF;
import com.netease.gather.service.data.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-10-9
 * Time: 下午2:31
 */
public class Classifier {

    private static final Logger logger = LoggerFactory.getLogger(Classifier.class);

//    @Resource(name="docService")
    private static DocService docService;

    static {
        docService = ScheduleContext.BF.getBean("docService", DocService.class);
    }

//    private static Map<String,Model> ammap = new HashMap<String, Model>();
//    private static Map<String,Model> cmmap = new HashMap<String, Model>();
    private static Map<String,Map<String,Map>> colmmap = new HashMap<String,Map<String,Map>>();

    static {
        try{
//            ammap=loadModel("all");
//            cmmap=loadModel("chan");
            List<String> channels = new ArrayList<String>(Arrays.asList("news", "ent", "sports", "finance", "tech", "travel", "war", "air","stock"));
//            for(Map.Entry<String,Model> entry:cmmap.entrySet()){
            for(String channel:channels){
                Map<String,Map> tmap = loadModel(channel);
                colmmap.put(channel,tmap);
            }
        }catch (Exception e){
            logger.error("分类器初始化出错！");
            logger.error(e.getMessage(),e);
        }
    }

    public static Map<String,Map> loadModel(String name) throws Exception{
        Map<String,Map> mmap = new HashMap<String, Map>();
        try {
            ClassLoader cl = Classifier.class.getClassLoader();
//            String modelfile = new File(cl.getResource(Constants.CLASSIFIER).toURI()).getAbsolutePath();
            String modelfile = "/home/workspace/gather/classify_new/"+name+".m";
            if(FileUtil.exists(modelfile)){
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(modelfile));
                mmap = (Map<String,Map>) is.readObject();// 从流中读取User的数据
            }
        }catch (Exception e){
            logger.error("加载模型文件出错！");
            logger.error(e.getMessage(),e);
        }

        return mmap;
    }

    public String classifyForHot(List<String> docnos,String channel) throws Exception{

        Map<String,Integer> cmap = new HashMap<String, Integer>();
        for(String docno:docnos){
            Map<String,String> qmap = new HashMap<String, String>();
            qmap.put("docno",docno);
            Doc doc = docService.getOneByParameters(qmap);
            if(doc!=null){
                Map<String, Double> tfmap= TFIDF.getTFForUrl(doc.getUrl());
                String cate = classify(tfmap,colmmap.get(channel));
//                String cate = (String) JsonUtil.fromJson(HttpUtil.getURL("http://bling.163.com/api/classifyByUrl?url=" + URLEncoder.encode(doc.getUrl(), "UTF8") + "&channel=" + channel, "UTF8", null), Map.class).get("category");
                cmap.put(cate,cmap.containsKey(cate)?cmap.get(cate)+1:1);
            }
        }

        int maxcate=0;
        String category = "";
        for (Map.Entry<String,Integer> centry:cmap.entrySet()){
            String catename = centry.getKey();
            int count = centry.getValue();
            if(count>maxcate){
                category = catename;
                maxcate = count;
            }
        }

        return category;
    }

//    public String classifyForHot(List<String> docnos,String channel) throws Exception{
//
//        Map<String,Integer> cmap = new HashMap<String, Integer>();
//        for(String docno:docnos){
//            Map<String,String> qmap = new HashMap<String, String>();
//            qmap.put("docno",docno);
//            Doc doc = docService.getOneByParameters(qmap);
//            if(doc!=null){
//                Map<String, Double> tfmap= TFIDF.getWCForUrl(doc.getUrl());
//                String cate = classify(tfmap,colmmap.get(channel));
//                cmap.put(cate,cmap.containsKey(cate)?cmap.get(cate)+1:1);
//            }
//        }
//
//        int maxcate=0;
//        String category = "";
//        for (Map.Entry<String,Integer> centry:cmap.entrySet()){
//            String catename = centry.getKey();
//            int count = centry.getValue();
//            if(count>maxcate){
//                category = catename;
//                maxcate = count;
//            }
//        }
//
//        return category;
//    }


//    public static String classifyFlat(Doc doc) throws Exception{
//        Map<String, Double> tfmap= TFIDF.getWCForUrl(doc.getUrl());
////        logger.info(String.valueOf(tfmap));
//        return classify(tfmap,ammap);
//    }
//
//    public static String classifyLevel(Doc doc) throws Exception{
//        Map<String, Double> tfmap= TFIDF.getWCForUrl(doc.getUrl());
//
//        String chan = classify(tfmap,cmmap);
//        return classify(tfmap,colmmap.get(chan));
//    }

    public static String classifyForText(String docid,String text,String channel) throws Exception{
        Map<String, Double> tfmap= TFIDF.getTFForOneDoc(docid,text);
        String cate = classify(tfmap,colmmap.get(channel));
        return cate;
    }

    public static String classifyForOne(String url,String channel) throws Exception{
        Map<String, Double> tfmap= TFIDF.getTFForUrl(url);
        String cate = classify(tfmap,colmmap.get(channel));
        return cate;
    }

    public static String classify(Map<String, Double> tfmap,Map<String,Map> mmap) throws Exception{

//        Map<String,BigDecimal> pmap = new HashMap<String, BigDecimal>();
        Map<String,Model> models = mmap.get("models");
        Map<String,Double> wordsweight = mmap.get("wordsweight");
        Map<String,Double> pmap = new HashMap<String, Double>();
        for (Map.Entry<String,Model> modelEntry:models.entrySet()){
            String catename = modelEntry.getKey();
            Model model = modelEntry.getValue();
//            logger.info(catename);
            Map<String,Double> words = model.getWords();
//            BigDecimal posteriori = BigDecimal.valueOf(1.0);
            double posteriori = 0.0;
            for(Map.Entry<String,Double> tfentry:tfmap.entrySet()){
                String word = tfentry.getKey();
                Double wposteriori = words.containsKey(word)?words.get(word):(double)1/(words.size()+1);
//                logger.info(word+":"+String.valueOf(wposteriori));
//                posteriori = posteriori.multiply(BigDecimal.valueOf(wposteriori));
                Double wordweight = wordsweight.containsKey(word)?wordsweight.get(word):(double)1/(models.size()+1);
//                logger.info(word+":"+wposteriori+","+wordweight);
                posteriori = posteriori + wordweight*Math.log(wposteriori);
            }
//            logger.info(String.valueOf(posteriori));
//            logger.info(String.valueOf(model.getPriori()));
//            BigDecimal prob = posteriori.multiply(BigDecimal.valueOf(model.getPriori()));
            double prob = posteriori + Math.log(model.getPriori());
//            logger.info(String.valueOf(prob));
            pmap.put(catename,prob);
        }

//        BigDecimal maxprob=BigDecimal.valueOf(0.0);
        double maxprob= Double.NEGATIVE_INFINITY;
        String category = "";
        for (Map.Entry<String,Double> pentry:pmap.entrySet()){
            String catename = pentry.getKey();
            double prob = pentry.getValue();
//            logger.info(catename+":"+prob);
            if(prob>maxprob){
                category = catename;
                maxprob = prob;
            }
        }

        return category;
    }
}
