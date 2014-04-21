package com.netease.gather.extapi;

import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.*;
import net.rubyeye.xmemcached.MemcachedClient;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by AzraelX on 14-1-8.
 */
public class CMSUtil {

    private static final Logger logger = LoggerFactory.getLogger(CMSUtil.class);

    private static MemcachedClient memcached;

    private final static Map<String, String> cmsListTrans = new HashMap<String, String>() {
        {
            //put("00014R7I", "http://news.163.com/special/000120FU/suetest05.js");
            put("00014R7I", "http://news.163.com/special/00014OTP/suetest05.js");
            put("0001122A", "http://news.163.com/special/00014OTP/gather_news_shehui.js");
            put("00064M1B", "http://travel.163.com/special/00064M30/article_api_00064m1b.js");
            put("00064M28", "http://travel.163.com/special/00064M30/article_api_00064m28.js");
            put("00014OMD", "http://news.163.com/special/00014OTP/gather_news_war.js");
            put("00014PHJ", "http://news.163.com/special/00014OTP/gather_news_air.js");
            put("00234IG8", "http://news.163.com/special/00014OTP/gather_00234ig8.js");
            put("00234IGD", "http://news.163.com/special/00014OTP/gather_00234igd.js");
            put("00234IKA", "http://gov.163.com/special/00234IHC/article_api_00234ika.js");
        }
    };

    static {
        memcached = ScheduleContext.BF.getBean("memcachedClientForNlp", MemcachedClient.class);
    }


    public static String getArticleWCache(String docid) throws Exception{
        String context = memcached.get("gather.doctext?docid="+docid,30000);
        if(context==null){
            try{
                Map<String,String> doc = HessianUtil.getCmsWebService().getArticle(docid);
                context = Jsoup.clean(doc.get("body"), Whitelist.none()).replace("&nbsp;"," ").replace("&middot;", "·");
            }catch (Exception e) {
                logger.error(e.getMessage());
            }
            if(context==null) context="";
            memcached.set("gather.doctext?docid="+docid,604800,context);
        }

        return context;

    }

    public static List<Map<String, String>> getListByTrans(String topicId) throws Exception {
        String url = "";
        if (cmsListTrans.containsKey(topicId)) {
            url = cmsListTrans.get(topicId);
        }

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        if (!StringUtil.isBlank(url)) {
            String jsonstr = HttpUtil.getURL(url, "GBK", null);
            Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);
            List<Map<String, String>> dlist = (List<Map<String, String>>) jsonmap.get("doclist");
            results = dlist;
        }else {
            String today = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
            results.addAll(Arrays.asList(HessianUtil.getCmsWebService().getList("topicid=" + topicId + ";liststart=" + 0 + ";listnum=" + 200)));
//            results.addAll(Arrays.asList(HessianUtil.getCmsWebService().getList("topicid=" + topicId + ";startday=" + today + ";endday=" + today + ";liststart=" + 0 + ";listnum=" + 200)));
        }

        return results;
    }

    public static String pushToCMS(String data) throws Exception{
        return HttpUtil.getURL(Constants.ROBOT_PUSH_CMS_URL + URLEncoder.encode(data, "utf-8"), "GBK", null);
    }

    public static String modiLspriCMS(String topicid,String docid,int lspri) throws Exception{
        return HttpUtil.getURL(Constants.ROBOT_MODLSPRI_CMS_URL + "&topicid="+topicid+"&docid="+docid+"&lspri="+lspri, "GBK", null);
    }
}
