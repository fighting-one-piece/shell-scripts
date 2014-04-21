package com.netease.gather.classifier.naivebayes;

import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.common.util.StringUtil;
import com.netease.gather.extapi.CMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*
*/
public class ClassifierWKeyWords {

	private static final Logger logger = LoggerFactory.getLogger(ClassifierWKeyWords.class);
    private static final Map<String,Map<String,List<String>>> kwmap = new HashMap<String, Map<String,List<String>>>(){{
        put("travel",TravelKeyWords.classwords);
        put("war", WarKeyWords.classwords);
        put("air", AirKeyWords.classwords);
        put("stock", StockKeyWords.classwords);
//            put("war","00014OVF");
    }};


	public static String classify(String channel,String title,String url,boolean usecontext,boolean usebayes) throws Exception{

        String category  ="";
        try {
            Map<String,List<String>> kws = kwmap.get(channel);

            if(kws!=null){
                if(usecontext){
                    String docid = CommonUtil.get163DocidFromUrl(url);
                    title = title + "\n" +CMSUtil.getArticleWCache(docid);
                }
                t1 :for(Map.Entry<String,List<String>> entry:kws.entrySet()){
                    String cate = entry.getKey();
                    List<String> ws = entry.getValue();
                    for(String word:ws){
                        if(title.contains(word)){
                            category = cate;
                            break t1;
                        }
                    }
                }
            }

            if(StringUtil.isEmpty(category)){
                if(usebayes){
                    String docid = CommonUtil.get163DocidFromUrl(url);
                    category = Classifier.classifyForText(docid,CMSUtil.getArticleWCache(docid), channel);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return category;
    }
}
