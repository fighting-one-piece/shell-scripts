package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.HtmlBuildUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.common.util.StringUtil;
import com.netease.gather.domain.Doc;
import com.netease.gather.service.logic.PublishHotService;
import com.netease.gather.service.logic.NewsHotService;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("publishHotService")
public class PublishHotServiceImpl implements PublishHotService {

    private static final Logger logger = LoggerFactory.getLogger(PublishHotServiceImpl.class);

    @Resource(name="newsHotService")
    private NewsHotService newsHotService;

    private static boolean demo = true;
    static {
            if ("demo".equals(Config.init().get("env"))) {
                // 测试
                demo = true;
            } else if ("prod".equals(Config.init().get("env"))) {
                demo = false;
            }
    }

    private static Map<String,Double> colweight = new HashMap<String, Double>(){{
        put("shehui",1.5);
        put("guoji",0.8);
        put("guonei",0.8);
    }};

    private Map<String,Integer> heardcolsize = new HashMap<String, Integer>(){{
        put("shehui",6);
        put("guoji",1);
        put("guonei",1);
    }};


    @Override
    public void showHot(String channel, String starttime, String endtime, int clustersize, int showlimit) throws Exception{
        int hsize = newsHotService.genHots(channel, starttime, endtime, "", clustersize);
        if(hsize==0){
            return;
        }
        List<Map> showhots = newsHotService.choiceHot(channel, "", starttime, endtime, 2, showlimit);
        showhots = newsHotService.choice163Art(showhots, channel);
        showhots = newsHotService.pick163Hots(showhots);
        newsHotService.sortHotCommen(showhots);

        logger.info("排序完成！");
        genHotPage(showhots, starttime, endtime, channel);
    }
    
    private void genHotPage(List<Map> rehots, String starttime, String endtime, String channel) throws Exception{
        if(rehots.size()==0){
            return;
        }

        newsHotService.calSimWithHeadlines(rehots, channel, endtime, 10);
        logger.info("头条去重完成！");
//            logger.info(String.valueOf(rehots));

        List<Map> hotlist = new ArrayList<Map>();
        for(Map remap:rehots){
            Doc cdoc = (Doc)remap.get("cdoc");
            Map map=new HashMap();
            map.put("title",cdoc.getTitle());
            map.put("url",cdoc.getUrl());
            map.put("col",remap.get("col"));
            hotlist.add(map);
        }

        Map paramap = new HashMap();
        paramap.put("hotlist",hotlist);
        if(demo){
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + "/smart/" + channel + "hotrank.html", "specialRightHots.vm", "GBK", paramap);
        }
        HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/"+channel+"hotrank.html", "specialRightHots.vm", "GBK",  paramap);

        if("news".equals(channel)){
            rehots= dealTitle(rehots,16.5,20.5);
        }


        Map pagePara = new HashMap();
        pagePara.put("hotlist",rehots);
        pagePara.put("start",DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"));
        pagePara.put("end",DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"));
        pagePara.put("channel",channel);

        if(demo){
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT+channel+"/rank.html", "rank.vm", "GBK", pagePara);
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT+channel+"/"+starttime.substring(0,8)+"/"+DateUtil.DateToString(DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+"-"+DateUtil.DateToString(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+".html", "rank.vm", "GBK", pagePara);
        }

//        jsonmap.put("hotlist",hotlist);
//        printmap.put("printstr", JsonUtil.toJsonStr(jsonmap));
//        HtmlBuildUtil.buildHTML(Constants.HTMLROOT+channel+"/rank.json", "print.vm", "GBK", printmap);

        if("news".equals(channel)){
            genHeader(rehots,channel);
        }

    }

    private static List<Map> dealTitle(List<Map> showhots,double sublimit,double suplimit) throws Exception{

        List<Map> hotslist = new ArrayList<Map>();
        for(Map remap:showhots){
            Doc cdoc = (Doc) remap.get("cdoc");

            List<Doc> dlist = (List<Doc>) remap.get("dlist");

            double titlength = StringUtil.chineseLength(cdoc.getTitle())/2.0;
            if(titlength<sublimit||titlength>suplimit){
                for(Doc doc:dlist){
                    titlength = StringUtil.chineseLength(doc.getTitle())/2.0;
                    if(titlength>=sublimit&&titlength<=suplimit){
                        logger.info("旧标题：{},新标题：{}",cdoc.getTitle(),doc.getTitle());
                        cdoc.setTitle(doc.getTitle());
                        remap.put("cdoc", cdoc);
                        break;
                    }
                }
            }

            titlength = StringUtil.chineseLength(cdoc.getTitle())/2.0;
            if(titlength<16||titlength>19){
                for(Doc doc:dlist){
                    titlength = StringUtil.chineseLength(doc.getTitle())/2.0;
                    if(titlength>=16&&titlength<=19){
                        logger.info("bold标题：{}",doc.getTitle());
                        remap.put("boldtit",doc.getTitle());
                        break;
                    }
                }
            }

            //todo 判断标题
//            Doc fcdoc = (Doc) remap.get("cdoc");
//            double titlast = StringUtil.chineseLength(fcdoc.getTitle())/2.0;
//            if(titlast>=sublimit&&titlast<=suplimit){
//                hotslist.add(remap);
//            }
            Doc fcdoc = (Doc) remap.get("cdoc");
            String ftit = fcdoc.getTitle();
            double titlast = StringUtil.chineseLength(ftit)/2.0;

            if(titlast<sublimit||titlast>suplimit){
                List<Term> terms = NlpAnalysis.parse(ftit);
                String tit = ftit;
                for(Term term:terms){
                    String word = term.getName();
                    if(word!=null&&word.length()>0){
                        String nature = term.getNatrue().natureStr;
                        if(nature==null||nature.equals("null")) continue;
                        if(!(nature.startsWith("n")||nature.startsWith("v")||nature.startsWith("i")||nature.startsWith("j"))){
                            if(tit.indexOf(word)>0){
                                tit = tit.substring(0,tit.indexOf(word))+tit.substring(tit.indexOf(word)+word.length(),tit.length());
                            }
                            double ctitlen = StringUtil.chineseLength(tit)/2.0;
                            if(ctitlen>=sublimit&&ctitlen<=suplimit){
//                                fcdoc.setTitle(tit);
                                remap.put("cuttit",tit);
                                break;
                            }
                        }
                    }
                }
            }


            remap.put("titlength",StringUtil.chineseLength(fcdoc.getTitle())/2.0);
            hotslist.add(remap);



        }

        return hotslist;

    }

    private void genHeader(List<Map> rehots,String channel) throws Exception{

        List<Map> nowarhots = new ArrayList<Map>();

        for(Map remap:rehots){
            if(remap.containsKey("simtit")){
                continue;
            }
            String col = (String) remap.get("col");

            if(!col.equals("junshi")){
                nowarhots.add(remap);
            }
        }

        logger.info("去掉军事热点！");

        //todo 判断标题
        Map first = nowarhots.get(0);
        Doc fcdoc = (Doc) first.get("cdoc");
        Map fmap = null;
        double ftitl = StringUtil.chineseLength(fcdoc.getTitle())/2.0;
        logger.info(String.valueOf(ftitl));
        if(ftitl<16||ftitl>19){
            for(Map remap:nowarhots){
                if(remap.containsKey("boldtit")){
                    fmap = remap;
                    Doc cdoc = (Doc) fmap.get("cdoc");
                    cdoc.setTitle((String) fmap.get("boldtit"));
                    logger.info(String.valueOf(cdoc));
                    fmap.put("cdoc",cdoc);
                    break;
                }
            }
        }else {
            fmap = first;
        }

        logger.info(String.valueOf(fmap));
        List<Map> hots = new ArrayList<Map>();
        if(fmap!=null){
//            hots.add(fmap);
            long fhotid = Long.valueOf(fmap.get("hotid").toString());
            for(Map remap:nowarhots){
                long rhotid = Long.valueOf(remap.get("hotid").toString());
                if(rhotid!=fhotid){
                    Doc cdoc = (Doc) remap.get("cdoc");
                    double titlast = StringUtil.chineseLength(cdoc.getTitle())/2.0;
                    if(titlast>=16.5&&titlast<=20.5){
                        hots.add(remap);
                    }
                }
            }
        }else {
//            logger.info("没有符合标题条件的第一条新闻，不生成页面！");
//            return;
            for(Map remap:nowarhots){
                Doc cdoc = (Doc) remap.get("cdoc");
                double titlast = StringUtil.chineseLength(cdoc.getTitle())/2.0;
                if(titlast>=16.5&&titlast<=20.5){
                    hots.add(remap);
                }
            }
//            hots.addAll(tithots);
        }


        List<Map> headhots = new ArrayList<Map>();
        List<Map> remainhots = new ArrayList<Map>();
        List<Map> hshehui = new ArrayList<Map>();
        List<Map> hguonei = new ArrayList<Map>();
        List<Map> hguoji = new ArrayList<Map>();
        if(fmap!=null){
            String fcol = (String) fmap.get("col");
            heardcolsize.put(fcol,heardcolsize.get(fcol)-1);
            headhots.add(fmap);
        }
        for(Map remap:hots){
            String col = (String) remap.get("col");
            //2013.12.18 网首头条按条数固定分离 从上到下 2条国内，1条国际，5条社会
            if(hguonei.size()<heardcolsize.get("guonei")&&col.equals("guonei")){
                hguonei.add(remap);
                continue;
            }
            if(hguoji.size()<heardcolsize.get("guoji")&&col.equals("guoji")){
                hguoji.add(remap);
                continue;
            }
            if(hshehui.size()<heardcolsize.get("shehui")&&col.equals("shehui")){
                hshehui.add(remap);
                continue;
            }
            remainhots.add(remap);
        }

        headhots.addAll(hguonei);
        headhots.addAll(hguoji);
        headhots.addAll(hshehui);
        headhots.addAll(remainhots);

        List<Map> headhotlist = new ArrayList<Map>();
        for(Map remap:headhots){
            Doc cdoc = (Doc)remap.get("cdoc");
            Map map=new HashMap();
            map.put("title",cdoc.getTitle());
            map.put("url",cdoc.getUrl());
            map.put("col", remap.get("col"));

            String col = (String) remap.get("col");
            if(col.equals("shehui")||col.equals("guoji")||col.equals("guonei")){
                if(!remap.containsKey("simtit")){
                    headhotlist.add(map);
                }
            }
        }

//        //调出两条社会放在，7，8条
//        if(headhotlist.size()>=6){
//            int i=6;
//            for(Map remap:lastshehui){
//                Doc cdoc = (Doc)remap.get("cdoc");
//                Map map=new HashMap();
//                map.put("title",cdoc.getTitle());
//                map.put("url",cdoc.getUrl());
//                map.put("col", remap.get("col"));
//                headhotlist.add(i,map);
//                i++;
//            }
//        }



        if(headhotlist.size()>=8){
            Map jsonmap = new HashMap();
            Map printmap = new HashMap();
//            jsonmap = new HashMap();
//            printmap = new HashMap();
            jsonmap.put("hotlist",headhotlist);
            printmap.put("printstr", "smartheader("+JsonUtil.toJsonStr(jsonmap)+")");
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/header.json", "print.vm", "GBK", printmap);
        }else {
            logger.info("符合条件的热点不足8条，不生成页面！");
        }
    }
}
