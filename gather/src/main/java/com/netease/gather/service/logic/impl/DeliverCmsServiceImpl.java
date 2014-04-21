package com.netease.gather.service.logic.impl;

import com.netease.gather.classifier.naivebayes.ClassifierWKeyWords;
import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.ArticlePushed;
import com.netease.gather.extapi.CMSUtil;
import com.netease.gather.nlp.SimilarityCalc;
import com.netease.gather.service.data.ArticleService;
import com.netease.gather.service.logic.DeliverCmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("deliverCmsService")
public class DeliverCmsServiceImpl implements DeliverCmsService {

    private static final Logger logger = LoggerFactory.getLogger(DeliverCmsServiceImpl.class);

    @Resource(name = "articleService")
    ArticleService articleService;

    private static boolean demo = true;
    static {
        if ("demo".equals(Config.init().get("env"))) {
            // 测试
            demo = true;
        } else if ("prod".equals(Config.init().get("env"))) {
            demo = false;
        }
    }

    private static final Map<String,Map<String,Double>> dropfilter = new HashMap<String, Map<String,Double>>(){{
        put("travel", new HashMap<String,Double>(){{
            put("sup",24.0);
            put("sub",0.0);
        }});
        put("war", new HashMap<String,Double>(){{
            put("sup",22.0);
            put("sub",16.0);
        }});
        put("air", new HashMap<String,Double>(){{
            put("sup",22.0);
            put("sub",16.0);
        }});
    }};

    private static final Map<String,Map<String,Double>> warnfilter = new HashMap<String, Map<String,Double>>(){{
        put("travel", new HashMap<String,Double>(){{
            put("sup",18.5);
            put("sub",5.0);
        }});
    }};

    private static final List<String> ldr = new ArrayList<String>(Arrays.asList("习近平","李克强","张德江","俞正声","刘云山","王岐山","张高丽"));

    private static final Map<String,String> sources = new HashMap<String, String>(){{
        put("travel","00064M1B");
        put("war","00014OMD");
        put("air","00014PHJ");
        put("gov","00234IG8");
        put("ldr","00234IGD");
        put("whzg","00234IKA");
        put("stock","00253B0H");
//            put("war","00014OVF");
    }};

    private static Map<String,String> targets = new HashMap<String, String>(){{
        if("demo".equals(Config.init().get("env"))){
            //测试
            put("gov","00234IG8");
            put("ldr","00234IGD");
//            put("meishi","00064M2C");
//            put("huwai","00064M2C");
        }else if("prod".equals(Config.init().get("env"))){
            put("meishi","00064LG8");
            put("huwai","00064M0U");
            put("jingnei","00064M2P");
            put("chujing","00064M2O");

            put("zhoubian","00014OL0");
            put("guoji","00013CP0");
            put("zhongguo","00013COV");

            put("air","00014RTL");
            put("minhang","00014RTM");
            put("tongyong","00014RTN");
            put("gongwu","00014RTO");
            put("fangwu","00014RTP");
            put("gongye","00014RU3");
            put("hangtian","00014RTQ");

            put("gov","00234IG8");
            put("ldr","00234IGD");
            put("whzg","00234IKA");

        }
    }};

    private static Map<String,List<String>> warnpopos = new HashMap<String, List<String>>(){{
        if("demo".equals(Config.init().get("env"))){
            //测试
//            put("meishi","00064M2C");
//            put("huwai","00064M2C");
        }else if("prod".equals(Config.init().get("env"))){
            put("00064LG8",Arrays.asList("youwang@corp.netease.com","lanyao@corp.netease.com","ygguo@corp.netease.com"));
            put("00064M0U",Arrays.asList("youwang@corp.netease.com","lanyao@corp.netease.com","bjlinnuo@corp.netease.com"));
            put("00064M2P",Arrays.asList("youwang@corp.netease.com","lanyao@corp.netease.com","bjlinnuo@corp.netease.com"));
            put("00064M2O",Arrays.asList("youwang@corp.netease.com","lanyao@corp.netease.com","bjlinnuo@corp.netease.com"));
        }
    }};


    @Override
    public void deliver(String channel) throws Exception {
        List<Map<String,String>> slist = CMSUtil.getListByTrans(sources.get(channel));
        List<Map<String,String>> tlist = CMSUtil.getListByTrans(targets.get(channel));
        filter(channel,slist,tlist);
    }

    @Override
    public void deliver4Stock(String channel) throws Exception {
        List<Map<String,String>> slist = CMSUtil.getListByTrans(sources.get(channel));
        List<Map<String,String>> slist_clsed = new ArrayList<Map<String, String>>();
        for(Map<String,String> smap:slist){
            String title = smap.get("title");
            String url = smap.get("url");
            String col = ClassifierWKeyWords.classify("finance", title, url, false, true);
            logger.info(col);
            if("zhengquan".equals(col)){
                slist_clsed.add(smap);
            }
        }
        List<Map<String,String>> tlist = CMSUtil.getListByTrans(targets.get(channel));
        filter(channel,slist_clsed,tlist);
    }

    private void filter(String channel,List<Map<String,String>> slist,List<Map<String,String>> tlist) throws Exception{
        List<Map<String,String>> rlist = new ArrayList<Map<String, String>>();
        Iterator<Map<String,String>> sit = slist.iterator();
        Pattern pattern = Pattern.compile("\\[\\d{4}-\\d{2}-\\d{2}\\]");

        while (sit.hasNext()){
            Map<String,String> smap = sit.next();
            String title = smap.get("title");
            Matcher matcher = pattern.matcher(title);
            if (matcher.find()) {
                logger.info(matcher.group());
                sit.remove();
                continue;
            }
            for(Map<String,String> tmap : tlist){
                if(smap.get("docid").equals(tmap.get("docid"))){
                    sit.remove();
                }
            }
        }

        Set<String> ext = new HashSet<String>();
        List<Map<String,String>> bakdocs = new ArrayList<Map<String,String>>();
        int docsize = slist.size();
        t1:for (int i=0;i<docsize;i++){
            Map<String,String> doc1 = slist.get(i);
            if(!ext.contains(doc1.get("url"))){
                for (int j=i+1;j<docsize;j++){
                    Map<String,String> doc2 = slist.get(j);
                    if(!ext.contains(doc2.get("url"))){
                        double titsim = SimilarityCalc.calcByCosWStringByChar(doc1.get("title"), doc2.get("title"));
                        if(titsim>=0.7){
                            ext.add(doc1.get("url"));
                            doc1.put("remark","去重");
                            continue t1;
                        }
                    }
                }
                bakdocs.add(doc1);
            }
        }

        for(Map<String,String> dmap:bakdocs){
            Map<String,String> rmap = new HashMap<String, String>();
            if(ext.contains(dmap.get("url"))){
                continue;
            }
            String title = dmap.get("title");
            String url = dmap.get("url");
            rmap.put("title",title);
            rmap.put("docid",dmap.get("docid"));
            rmap.put("url",url);

            boolean usecontxt = false;
            if("air".equals(channel)){
                usecontxt = true;
            }
            rmap.put("col", ClassifierWKeyWords.classify(channel, title, url, usecontxt, true));
            rlist.add(rmap);
            ext.add(dmap.get("url"));
        }


        Map<String,List<String>> topicdocids = new HashMap<String,List<String>>();
        Map<String,List<String>> topicdocidschan = new HashMap<String,List<String>>();
        Map<String,List<Map<String,String>>> limiteddocs = new HashMap<String,List<Map<String,String>>>();
        for(Map<String,String> remap:rlist){
            String col = remap.get("col");
            String topicid = targets.get(col);
            if(StringUtil.isEmpty(topicid)){
                continue;
            }
            List<String> docids = topicdocids.get(topicid);
            if(docids==null){
                docids = new ArrayList<String>();
            }
            String title = remap.get("title");
            double len = StringUtil.chineseLength(title)/2.0;
            Map<String,Double> dropmap = dropfilter.get(channel);
            if(dropmap!=null){
                double dsup = dropmap.get("sup");
                double dsub = dropmap.get("sub");

                if(len>dsup||len<dsub){
                    remap.put("remark","超长");
                    continue;
                }
            }


            Map<String,Double> warnmap = warnfilter.get(channel);

            if(warnmap!=null){
                double wsup = warnmap.get("sup");
                double wsub = warnmap.get("sub");
                if(len>wsup||len<wsub){
                    List<Map<String,String>> limiteddoc = limiteddocs.get(topicid);
                    if(limiteddoc==null){
                        limiteddoc = new ArrayList<Map<String,String>>();
                    }
                    limiteddoc.add(remap);
                    limiteddocs.put(topicid,limiteddoc);
                }
            }

            docids.add(remap.get("docid"));
            topicdocids.put(topicid, docids);

            String ctopicid = targets.get(channel);
            if(!StringUtil.isEmpty(ctopicid)){
                List<String> cdocids = topicdocidschan.get(ctopicid);
                if(cdocids==null){
                    cdocids = new ArrayList<String>();
                }
                cdocids.add(remap.get("docid"));
                topicdocidschan.put(ctopicid,cdocids);
            }
        }

        String res = CMSUtil.pushToCMS(JsonUtil.toJsonStr(topicdocids));
        logger.info(res);
        if(topicdocidschan.size()>0){
            res = CMSUtil.pushToCMS(JsonUtil.toJsonStr(topicdocidschan));
            logger.info(res);
        }

        Map<String,String> warns = new HashMap<String, String>();
        for(Map.Entry<String,List<Map<String,String>>> entity:limiteddocs.entrySet()){
            String warning = "";
            String topicid = entity.getKey();
            List<Map<String,String>> limiteddoc = entity.getValue();
            if(limiteddoc!=null&&limiteddoc.size()>0){
                String tocwarn = "栏目id:" + topicid + "\r\n";
                for(Map<String,String> remap:limiteddoc){
                    tocwarn = tocwarn + "标题["+ remap.get("title") + "]字数" + (StringUtil.chineseLength(remap.get("title"))/2.0) +"\r\n";
                }

                warning = warning + tocwarn;
            }
            if(!StringUtil.isEmpty(warning)){
                warns.put(topicid,warning);
            }
        }

        for(Map.Entry<String,String> warn:warns.entrySet()){
            String topicid = warn.getKey();
            String warnstr = warn.getValue();
            logger.info(warnstr);
            List<String> popos = warnpopos.get(topicid);
            if(popos!=null&&popos.size()>0){
                for(String popo:popos){
                    PoPo.send(popo, "机器推送标题字数异常，请及时修改!\r\n" + warnstr);
                }
            }

        }

        if(demo){

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            int min = cal.get(Calendar.MINUTE);
            int initmin = min >= 30 ? 30 : 0;
            cal.set(Calendar.MINUTE, initmin);

            Map pagePara = new HashMap();
            pagePara.put("hotlist",rlist);
            File dir = FileUtil.createFile(Constants.HTMLROOT + channel + "/" + DateUtil.DateToString(cal.getTime(), "yyyyMMdd"));
            List<String> fname = new ArrayList<String>();
            if(dir!=null&&dir.listFiles()!=null){

                for(File file:dir.listFiles()){
                    fname.add(file.getName().split("\\.")[0]);
                }
            }

            Collections.sort(fname);
            pagePara.put("fname",fname);
            pagePara.put("channel",channel);


            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/" + DateUtil.DateToString(cal.getTime(), "yyyyMMdd") + "/" + DateUtil.DateToString(cal.getTime(), "yyyyMMddHHmmss") + ".html", "ranktmp.vm", "GBK", pagePara);

            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/rank.html", "ranktmp.vm", "GBK", pagePara);
        }
    }

    /**
     * 政务频道过滤领导人以及标题长度
     * @throws Exception
     */
    @Override
    public void filter4Gov(String channel) throws Exception {

        String sourceid = sources.get(channel);
        String targetid = targets.get(channel);

        List<Map<String,String>> tlist = CMSUtil.getListByTrans(sourceid);

        List<String> ldrnews = new ArrayList<String>();
        Iterator<Map<String,String>> sit = tlist.iterator();
        while (sit.hasNext()){
            Map<String,String> smap = sit.next();
            if(demo){
                int lspri = Integer.valueOf(smap.get("lspri"));
                if(lspri>=70){
                    sit.remove();
                    continue;
                }
            }
            String uid = smap.get("userid");
            if(!"jiqiren".equals(uid)&&!"netease".equals(uid)){
                sit.remove();
            }else {
                String docid = smap.get("docid");
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("topicid", targetid);
                param.put("docno", docid);
                ArticlePushed pushed = articleService.getOneByParameters(param);
                if(pushed!=null){
                    smap.put("pushed",String.valueOf(pushed.getAutoid()));
                    int plspri = pushed.getPriority();
                    int lspri = Integer.valueOf(smap.get("lspri"));
                    if(plspri!=lspri){
                        sit.remove();
                        continue;
                    }
                }
                String title = smap.get("title");
                for(String ldrstr : ldr){
                    if(title.contains(ldrstr)){
                        smap.put("setlspri","50");
                        ldrnews.add(smap.get("docid"));
                    }
                }
            }
        }

        if(ldrnews.size()>0){
            Map<String,List<String>> jsonmap = new HashMap<String, List<String>>();
            jsonmap.put(targets.get("ldr"),ldrnews);
            String jsonstr = JsonUtil.toJsonStr(jsonmap);
            CMSUtil.pushToCMS(jsonstr);
        }

//        Set<String> ext = new HashSet<String>();
        for (Map<String,String> map:tlist){
            String tit = map.get("title");
            double len = StringUtil.chineseLength(tit)/2.0;
            if(len<16||len>22){
                map.put("setlspri","50");
//                ext.add(map.get("url"));
            }
        }


        List<Map<String,String>> bakdocs = new ArrayList<Map<String,String>>();
        int docsize = tlist.size();
        for (int i=0;i<docsize;i++){
            Map<String,String> doc1 = tlist.get(i);
            if(!doc1.containsKey("setlspri")){
                bakdocs.add(doc1);
                for (int j=i+1;j<docsize;j++){
                    Map<String,String> doc2 = tlist.get(j);
                    if(!doc2.containsKey("setlspri")){
                        double titsim = SimilarityCalc.calcByCosWStringByChar(doc1.get("title"), doc2.get("title"));
                        double textsim = SimilarityCalc.calcByCosWUrl(doc1.get("url"), doc2.get("url"));
                        double sim = Math.max(textsim,titsim);
                        if(sim>=0.75){
//                            ext.add(doc2.get("url"));
                            doc2.put("setlspri","50");
                            logger.info(doc1.get("title")+":"+doc2.get("title")+":"+sim);
                        }
                    }
                }
            }
        }

        Collections.sort(bakdocs,new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                String ptime1 = o1.get("ptime");
                String ptime2 = o2.get("ptime");
                Date p1 = DateUtil.stringToDate(ptime1, "yyyy-MM-dd hh:mm:ss");
                Date p2 = DateUtil.stringToDate(ptime2,"yyyy-MM-dd hh:mm:ss");
                return p2.compareTo(p1);
            }
        });


        int pcount = 0;
        for(Map<String,String> doc:bakdocs){
            int point = 60;
            if(pcount<11){
                point = 70;
                if(demo){
                    point = 65;
                }
            }
            if(!doc.containsKey("setlspri")){
                doc.put("setlspri",String.valueOf(point));
                pcount++;
            }
        }


        for (Map<String,String> map:tlist){
            String docid = map.get("docid");
            if(map.containsKey("setlspri")){
                int setlspri = Integer.valueOf(map.get("setlspri"));
                int lspri = Integer.valueOf(map.get("lspri"));

                if(!map.containsKey("pushed")){
                    ArticlePushed pushed = new ArticlePushed();
                    pushed.setTopicid(targetid);
                    pushed.setDocno(docid);
                    pushed.setTitle(map.get("title"));
                    pushed.setPriority(setlspri);
                    pushed.setPushtime(new Date());
                    articleService.saveOne(pushed);
                }

                if(lspri!=setlspri){
                    String res = CMSUtil.modiLspriCMS(targetid,docid,setlspri);
                    logger.info(docid+":"+res);
                    if(map.containsKey("pushed")){
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("autoid", Long.valueOf(map.get("setlspri")));
                        param.put("priority", setlspri);
                        articleService.updateOne(param);
                    }
                }
            }
        }
    }

    /**
     * 高层动态设置领导人权重以及标题长度
     * @throws Exception
     */
    @Override
    public void filter4Ldr(String channel) throws Exception {

        String sourceid = sources.get(channel);
        String targetid = targets.get(channel);


        List<Map<String,String>> tlist = CMSUtil.getListByTrans(sourceid);

        Iterator<Map<String,String>> sit = tlist.iterator();
        while (sit.hasNext()){
            Map<String,String> smap = sit.next();
            if(demo){
                int lspri = Integer.valueOf(smap.get("lspri"));
                if(lspri>=70){
                    sit.remove();
                    continue;
                }
            }
            String uid = smap.get("userid");
            if(!"jiqiren".equals(uid)&&!"netease".equals(uid)){
                sit.remove();
            }else {
                String docid = smap.get("docid");
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("topicid", targetid);
                param.put("docno", docid);
                ArticlePushed pushed = articleService.getOneByParameters(param);
                if(pushed!=null){
                    smap.put("pushed",String.valueOf(pushed.getAutoid()));
                    int plspri = pushed.getPriority();
                    int lspri = Integer.valueOf(smap.get("lspri"));
                    if(plspri!=lspri){
                        sit.remove();
                    }
                }
            }
        }

        for (Map<String,String> map:tlist){
            String tit = map.get("title");
            double len = StringUtil.chineseLength(tit)/2.0;
            if(len<16||len>22){
                map.put("setlspri","50");
            }
        }

        int startpoint = 77;
        if(demo){
            startpoint = 67;
        }
        for(String ldrstr : ldr){
            for (Map<String,String> map:tlist){
                String tit = map.get("title");
                if(tit.contains(ldrstr)&&!map.containsKey("setlspri")){
                    map.put("setlspri", String.valueOf(startpoint));
                }
            }
            startpoint--;
        }

        for (Map<String,String> map:tlist){
            String docid = map.get("docid");
            if(map.containsKey("setlspri")){
                int setlspri = Integer.valueOf(map.get("setlspri"));
                int lspri = Integer.valueOf(map.get("lspri"));

                if(!map.containsKey("pushed")){
                    ArticlePushed pushed = new ArticlePushed();
                    pushed.setTopicid(targetid);
                    pushed.setDocno(docid);
                    pushed.setTitle(map.get("title"));
                    pushed.setPriority(setlspri);
                    pushed.setPushtime(new Date());
                    articleService.saveOne(pushed);
                }

                if(lspri!=setlspri){
                    String res = CMSUtil.modiLspriCMS(targetid,docid,setlspri);
                    logger.info(docid+":"+res);
                    if(map.containsKey("pushed")){
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("autoid", Long.valueOf(map.get("pushed")));
                        param.put("priority", setlspri);
                        articleService.updateOne(param);
                    }
                }
            }

        }
        
    }
}
