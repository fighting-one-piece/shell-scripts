package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.CommonUtil;
import com.netease.gather.common.util.HessianUtil;
import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.ArticlePushed;
import com.netease.gather.domain.Doc;
import com.netease.gather.service.data.ArticleService;
import com.netease.gather.service.logic.NewsHotService;
import com.netease.gather.service.logic.PushCmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.*;


@Service("pushCmsService")
public class PushCmsServiceImpl implements PushCmsService {

    private static final Logger logger = LoggerFactory.getLogger(PushCmsServiceImpl.class);

    @Resource(name="newsHotService")
    private NewsHotService newsHotService;
    @Resource(name = "articleService")
    private ArticleService articleService;

    private static Map<String,String> coltopic = new HashMap<String, String>(){{
        if("demo".equals(Config.init().get("env"))){
            //测试
           //put("guonei","00014R7I");
           put("guoji","00014R7I");
           //put("shehui","00014R7I");
           //put("hongguan","002550IG");
           put("tongxin","00094OEU");
           put("hulianwang","00094OEU");
           put("ityejie","00094OEU");
        }else if("prod".equals(Config.init().get("env"))){
            put("guonei","00014OIM");
            put("guoji","00014OIK");
            put("shehui","00014OIO");
            put("hongguan","00252G50");
            put("hulianwang","000915BF");
            put("tongxin","000915BE");
            put("ityejie","000915BD");
        }
    }};

    private static Map<String,String> focustopic = new HashMap<String, String>(){{
        put("guonei","00014OIN");
        put("guoji","00014OIL");
        put("shehui","00014OIP");
    }};


    @Override
    public void showCms(String channel, String starttime, String endtime, int clustersize, int showlimit) throws Exception{
        int hsize = newsHotService.genHots(channel, starttime, endtime, "",clustersize);
        if(hsize==0){
            return;
        }
        List<Map> showhots = newsHotService.choiceHot(channel, "", starttime, endtime, 2, showlimit);
        showhots = newsHotService.choice163Art(showhots,channel);
        pushCMS(showhots);
    }

    private void pushCMS(List<Map> rehots) throws Exception{
        Map<String,Set<String>> coldocids = new HashMap<String, Set<String>>();
        for(Map.Entry<String,String> entry:coltopic.entrySet()){
            Set<String> docids = new HashSet<String>();
            coldocids.put(entry.getKey(),docids);
        }

        Map<String,String> focusdocid = new HashMap<String, String>();
        for(Map.Entry<String,String> entry:focustopic.entrySet()){
            try{
                Map<String,String>[] docindexs = HessianUtil.getCmsWebService().getList("topicid="+entry.getValue()+";liststart=0;listnum=1");
                if(docindexs!=null&&docindexs.length>0){
                    for(Map<String,String> docindex:docindexs){
                        String url = docindex.get("url");
                        focusdocid.put(entry.getKey(),CommonUtil.get163DocidFromUrl(url));
                    }
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Map remap:rehots){
            Doc cdoc = (Doc) remap.get("cdoc");
            if("163".equals(cdoc.getSource())){
                String col = (String)remap.get("col");
                Set<String> docids = coldocids.get(col);
                if(docids!=null){
                    String docid = CommonUtil.get163DocidFromUrl(cdoc.getUrl());
                    if(!docid.equals(focusdocid.get(col))){
                        docids.add(docid);
                    }
                }
            }
        }
        Map<String,List<String>> topicdocids = new HashMap<String,List<String>>();
        for(Map.Entry<String,Set<String>> entry:coldocids.entrySet()){
//                    topicdocids.put("topicid",coltopic.get(entry.getKey()));
            List<String> docids = new ArrayList<String>(entry.getValue());
//                    topicdocids.put("docids",docids);
            if(docids.size() > 0){
            	topicdocids.put(coltopic.get(entry.getKey()),docids);
            }
        }
        String jsonstr = JsonUtil.toJsonStr(topicdocids);
//        PoPo.send("jjqi@corp.netease.com",jsonstr);
//        PoPo.send("ykxu@corp.netease.com",jsonstr);
        logger.info(jsonstr);
        String ret = HttpUtil.getURL(Constants.ROBOT_PUSH_CMS_URL+URLEncoder.encode(jsonstr, "utf-8"),"GBK",null);
        logger.info(ret);
        for(Map.Entry<String,List<String>> entry:topicdocids.entrySet()){
            List<String> docids = entry.getValue();
            for(String docid:docids){
                saveArt(docid,entry.getKey());
            }
        }

    }

    //暂存docid,与插空文章方式不同
    private void saveArt(String docid,String topicid) throws Exception {
        ArticlePushed art = new ArticlePushed();
        art.setPushtime(new Date());
        art.setDocno(docid);
        art.setTopicid(topicid);
        articleService.saveOne(art);
        logger.info("保存文章:{}, date:{}", docid, new Date());
    }

}
