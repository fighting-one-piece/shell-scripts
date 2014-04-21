package com.netease.gather.extapi;

import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.common.util.HessianUtil;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */

public class ExtractPhotosetUtil {
	private static final Logger logger = Logger.getLogger(ExtractPhotosetUtil.class);

	//从发布器代码中获得图集
	public static List<Integer> extractPhotosetFromCmsCode(String docid) {
        List<Integer> phsid = new ArrayList<Integer>();
        try {
            Map<String,String> article = HessianUtil.getCmsWebService().getArticle(docid);
            Document doc = Jsoup.parse(article.get("body"));
            Elements ps = doc.select("p");
            Pattern pattern = Pattern.compile("/photosetssi/([0-9]+)\\.html");
            for(Element p:ps){
                Matcher matcher = pattern.matcher(p.html());
                while (matcher.find()) {
                    phsid.add(Integer.valueOf(matcher.group(1)));
                }
            }
        }catch (Exception e){
            logger.error(e);
        }
        return phsid;
	}
	
	//从页面源码中获得图集
    public static List<Integer> extractPhotosetFromSourceCode(String url) throws Exception {
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

        Element body = doc.body();
        Elements photosets = body.select("div.nph_photo_viewlarge");
        List<Integer> phsid = new ArrayList<Integer>();
        for(Element photoset:photosets){
            String photosetid = CommonUtil.get163DocidFromUrl(photoset.attr("data-link"));
            phsid.add(Integer.valueOf(photosetid));
        }
        return phsid;
	}

    public static void main(String[] arg) throws Exception {
//        List<Integer> phsid = extractPhotosetFromSourceCode("http://news.163.com/13/1125/13/9EHFTQA70001124J.html");
        List<Integer> phsid = extractPhotosetFromCmsCode("9NOCTDIT00014AED");
//        List<Integer> phsid = extractPhotosetFromCmsCode("9EJTB4IH00014JB6");
        for (Integer psid:phsid){
            Map<String,String> info = HessianUtil.getPhotoService240().getSetInfo("0001", psid);
            logger.info(info);
            List<Map<String,String>> phs = HessianUtil.getPhotoService240().getPhotoListBySetid("0001", psid);
            logger.info(phs);
        }
        logger.info(phsid);
    }

}
