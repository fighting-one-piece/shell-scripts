package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-20
 * Time: 下午3:16
 */
public class _163Spider {

    private static final Logger logger = Logger.getLogger(_163Spider.class);

    public static List<Doc> clawNews() throws Exception{
        return spiderCtrl("http://news.163.com/special/0001220O/news_json.js?" + Math.random(),"news","news");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("http://ent.163.com/special/00032IAD/ent_json.js?" + Math.random(),"ent","ent");
    }

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl("http://money.163.com/special/00251G8F/news_json.js?" + Math.random(),"news","finance");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("http://tech.163.com/special/00094IHV/news_json.js?" + Math.random(),"news","tech");
    }

    private static List<Doc> spiderCtrl(String url,String dataname,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(url, dataname, channel);
                }
                sucess = true;
            }catch (Exception e){
                retry--;
                logger.error(e);
            }
        }

        return newslist;
    }

    @SuppressWarnings("unchecked")
    private static List<Doc> commonSpider(String url,String dataname,String channel) throws Exception{
        List<Doc> newslist = new ArrayList<Doc>();

        String jsonstr = HttpUtil.getURL(url,"GBK",null).replace("var data=","");
        Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(List<Map<String,String>> onecate : (List<List<Map<String,String>>>)jsonmap.get(dataname)){
            for (Map<String,String> onenew : onecate){
                if(onenew.get("p").compareTo(start) >= 0&&onenew.get("p").compareTo(end) < 0){
                    Doc tdoc = new Doc();
//                    tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                    tdoc.setTitle(onenew.get("t"));
                    tdoc.setUrl(onenew.get("l"));
                    tdoc.setSource("163");
                    tdoc.setChannel(channel);
                    tdoc.setPtime(DateUtil.stringToDate(onenew.get("p"),"yyyy-MM-dd HH:mm:ss"));
                    tdoc.setCreatetime(new Date());
                    tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                    Map<String,String> tmap = new HashMap<String, String>(){{
//                        put("docno","163"+ RandomSN.getRandomString(10,"ilu"));
//                        put("t",onenew.get("t"));
//                        put("l",onenew.get("l"));
//                    }};
                    newslist.add(tdoc);
                }
            }
        }

        return newslist;
    }

    public static List<Doc> clawSports() {
        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    String htmlstr = HttpUtil.getURL("http://sports.163.com/special/s/0005rt/sportsgd.html","GBK",null);

                    Document document = Jsoup.parse(htmlstr);
                    Elements docs = document.select("ul.articleList li");

                    DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
                    String start = df.format(TimeControl.clawStartTime().getTime());
                    String end = df.format(TimeControl.clawEndTime().getTime());

                    for(Element doc:docs){
                        String time = doc.select("span.postTime").get(0).text();
                        if(time.compareTo(start)>=0&&time.compareTo(end) < 0){
                            final Element link = doc.select("a").get(0);
                            Doc tdoc = new Doc();
//                            tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                            tdoc.setTitle(link.text());
                            tdoc.setUrl(link.attr("href"));
                            tdoc.setSource("163");
                            tdoc.setChannel("sports");
                            tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR) + "-" + time, "yyyy-MM-dd HH:mm"));
                            tdoc.setCreatetime(new Date());
                            tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
        //                Map<String,String> tmap = new HashMap<String, String>(){{
        //                    put("docno","163"+ RandomSN.getRandomString(10,"ilu"));
        //                    put("t",link.text());
        //                    put("l",link.attr("href"));
        //                }};
                            newslist.add(tdoc);
                        }
                    }
                }
                sucess = true;
            }catch (Exception e){
                retry--;
                logger.error(e);
            }
        }


        return newslist;
    }
}

