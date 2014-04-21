package com.netease.gather.classifier.naivebayes;

import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.FileUtil;
import com.netease.gather.common.util.HessianUtil;
import com.netease.gather.extapi.CMSUtil;
import com.netease.gather.nlp.TFIDF;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AzraelX
 * Date: 13-10-8
 * Time: 下午2:29
 */
public class Trainer {

    private static final Logger logger = LoggerFactory.getLogger(Trainer.class);

//    @Resource(name="docService")
//    private DocService docService;
//    @Resource(name="hotService")
//    private HotService hotService;

    public void train(String channel ,List<DefaultKeyValue> catetopicids)  throws Exception{
        Map<String,Map> mmap = genModel(catetopicids,channel);
        saveModel(channel,mmap);
    }

//    public Map<String,Model> genModel() throws Exception{
//
//        Map<String,Integer> catecounts = new HashMap<String,Integer>();
//        Map<String,Map<String,Double>> catewords = new HashMap<String,Map<String,Double>>();
//
//        Map hmap = new HashMap();
//        hmap.put("condition","  col is not null and col <>''  ");
//        List<Hot> hlist=hotService.getListByParameters(hmap);
//        for(Hot hot:hlist){
//            String cate = hot.getCol();
//            catecounts.put(cate,catecounts.containsKey(cate)?catecounts.get(cate)+1:1);
//
//            Map<String,Double> words = catewords.containsKey(cate)?catewords.get(cate):new HashMap<String, Double>();
//            Map dmap = new HashMap();
//            dmap.put("hotid",hot.getAutoid());
//            List<Doc> dlist = docService.getListByParameters(dmap);
//            for (Doc doc:dlist){
//                Map<String, Double> tfmap = TFIDF.getTFForOneDoc(doc);
//                for(Map.Entry<String,Double> entry:tfmap.entrySet()){
//                    String key = entry.getKey();
//                    words.put(key,words.containsKey(key)?words.get(key)+entry.getValue():entry.getValue());//按词频概率累加，可按词频累加
//                }
//            }
//            catewords.put(cate,words);
//        }
//
//        for(Map.Entry<String,Map<String,Double>> cwentry:catewords.entrySet()){
//            Map<String,Double> words = cwentry.getValue();
//            double wcount = 0;
//            for(Map.Entry<String,Double> entry:words.entrySet()){
//                wcount+=entry.getValue();
//            }
//
//            for(Map.Entry<String,Double> entry:words.entrySet()){
//                words.put(entry.getKey(),entry.getValue()/wcount);
//            }
//        }
//
//
//
//        int count = 0;
//        for(Map.Entry<String,Integer> entry:catecounts.entrySet()){
//            count+=entry.getValue();
//        }
//
//        Map<String,Model> mmap = new HashMap<String, Model>();
//        for(Map.Entry<String,Integer> entry:catecounts.entrySet()){
//            Model model=new Model();
//            model.setCategory(entry.getKey());
//            model.setPriori((double)entry.getValue()/count);
//            model.setWords(catewords.get(entry.getKey()));
//            mmap.put(entry.getKey(),model);
//        }
//
//        return mmap;
//    }

    public Map<String,Map> genModel(List<DefaultKeyValue> catetopicids,String channel) throws Exception{

        Map<String,Integer> catecounts = new HashMap<String,Integer>();
        Map<String,Map<String,Double>> catewords = new HashMap<String,Map<String,Double>>();
        Map<String,Integer> wordsdoccnt = new HashMap<String,Integer>();



        for(DefaultKeyValue catetopicid:catetopicids){

            String catename = (String) catetopicid.getKey();
            String topicid = (String) catetopicid.getValue();
            logger.info(catename);

            Map<String,Double> words = catewords.containsKey(catename)?catewords.get(catename):new HashMap<String, Double>();

            int catecount = 0;
//            Date startday = DateUtil.stringToDate("2013-10-10","yyyy-MM-dd");
            Date endday = new Date();
//            int daysize = 365;
            String startday = "2010-01-01";
            if("travel".equals(channel)){
                startday = "2000-01-01";
            }
            long daysize = DateUtil.getDaysBetweenTwoDates(DateUtil.stringToDate(startday,"yyyy-MM-dd"),endday);
            for(int i = 0;i < daysize; i++){
                String cday = DateUtil.DateToString(DateUtils.addDays(endday,-i),"yyyy-MM-dd");
                logger.info(cday);
                boolean stop = false;
                int start = 0;
                int querysize = 20;
                while (!stop){
//                    logger.info(String.valueOf(start));
                    try {
                        Map<String,String>[] docindexs =  HessianUtil.getCmsWebService().getList("topicid="+topicid+";startday="+cday+";endday="+cday+";liststart="+start+";listnum="+querysize+";");
                        if(docindexs!=null&&docindexs.length>0){
                            for(Map<String,String> docindex:docindexs){
//                                String docid = docindex.get("docid");
//                            if(docid.length()>8) docid = docid.substring(0,8)+topicid;
                                try{
                                    String docid = CommonUtil.get163DocidFromUrl(docindex.get("url"));
                                    Map<String,String> doc = HessianUtil.getCmsWebService().getArticle(docid);
                                    logger.info(docid);
                                    String source = doc.get("source")==null?"":doc.get("source");
                                    if(source.contains("证券")&&catename.equals("hongguan")){
                                        continue;
                                    }
//                                    String context = Jsoup.clean(doc.get("body"), Whitelist.none()).replace("&nbsp;"," ").replace("&middot;", "·");

                                    String context = CMSUtil.getArticleWCache(docid);
//                                    Map<String, Double> tfmap = TFIDF.getTFForOneDoc(docid,context);//按词频累加
                                    Map<String, Double> tfmap = TFIDF.getWCForOneDoc(docid, context);//按词数累加

                                    for(Map.Entry<String,Double> entry:tfmap.entrySet()){
                                        String key = entry.getKey();
                                        words.put(key,words.containsKey(key)?words.get(key)+entry.getValue():entry.getValue());
                                        wordsdoccnt.put(key,wordsdoccnt.containsKey(key)?wordsdoccnt.get(key)+1:1);
                                    }
                                }catch (Exception e){
                                    logger.error(e.getMessage()+docindex.get("url"));
                                }

                            }
                            catecount = catecount+docindexs.length;
                            if(docindexs.length==querysize){
                                start = start + querysize;
                            }else {
                                stop = true;
                            }
                        }else {
                            stop = true;
                        }
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                }
            }

            catewords.put(catename,words);
            catecounts.put(catename,catecounts.containsKey(catename)?catecounts.get(catename)+catecount:catecount);
        }


        int alldocsum = 0;
        for(Map.Entry<String,Integer> entry:catecounts.entrySet()){
            alldocsum+=entry.getValue();
        }

        for(Map.Entry<String,Map<String,Double>> wentry:catewords.entrySet()){
            Map<String,Double> words = wentry.getValue();
            double wcount = 0.0;
            for(Map.Entry<String,Double> entry:words.entrySet()){
                wcount+=entry.getValue();
            }

            for(Map.Entry<String,Double> entry:words.entrySet()){
                double tf = entry.getValue()/wcount;
                int docnum = wordsdoccnt.get(entry.getKey())==null?0:wordsdoccnt.get(entry.getKey());
                double idf = Math.log((double)alldocsum/(docnum+1));
                words.put(entry.getKey(),tf*idf);
            }
            catewords.put(wentry.getKey(),words);
        }



        Map<String,Model> mmap = new HashMap<String, Model>();
        for(Map.Entry<String,Integer> entry:catecounts.entrySet()){
            Model model=new Model();
            model.setCategory(entry.getKey());
            model.setPriori((double)entry.getValue()/alldocsum);
            model.setWords(catewords.get(entry.getKey()));
            mmap.put(entry.getKey(),model);
        }

        Map<String,Double> wordsweight = new HashMap<String, Double>();
        for(Map.Entry<String,Model> entry:mmap.entrySet()){
            Model model=entry.getValue();
            Map<String,Double> words = model.getWords();
            for(Map.Entry<String,Double> word:words.entrySet()){
                wordsweight.put(word.getKey(),wordsweight.containsKey(word.getKey())?wordsweight.get(word.getKey())+1:1);
            }
        }

        for(Map.Entry<String,Double> word:wordsweight.entrySet()){
//            wordsweight.put(word.getKey(),wordsweight.get(word.getKey())/Integer.valueOf(mmap.size()).doubleValue());
            wordsweight.put(word.getKey(),(wordsweight.get(word.getKey())+1)/(mmap.size()-wordsweight.get(word.getKey())+1));
        }

        Map<String,Map> modelmap = new HashMap<String,Map>();
        modelmap.put("models",mmap);
        modelmap.put("wordsweight",wordsweight);

        return modelmap;
    }

    public void saveModel(String modelname,Map<String,Map> modelmap) throws Exception{
        try{
            ClassLoader cl = Trainer.class.getClassLoader();
//            String modelfile = new File(cl.getResource(Constants.CLASSIFIER).toURI()).getAbsolutePath();
            String modelfile = "/home/workspace/gather/classify_new/"+modelname+".m";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FileUtil.createFile(modelfile)));
            os.writeObject(modelmap);// 将模型对象写进文件
            os.close();
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }
}
