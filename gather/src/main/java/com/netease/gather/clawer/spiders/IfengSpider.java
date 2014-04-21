package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.RandomSN;
import com.netease.gather.common.util.ShortUrlGenerator;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: AzraelX
 * Date: 13-8-21
 * Time: 下午2:19
 */
public class IfengSpider {

    private static final Logger logger = Logger.getLogger(IfengSpider.class);

    public static List<Doc> clawNews() throws Exception{
        DateFormat dayf = new SimpleDateFormat("yyyyMMdd");
        String day = dayf.format(TimeControl.clawStartTime().getTime());
        return spiderCtrl("http://news.ifeng.com/rt-channel/rtlist_"+day+"/1.shtml","news");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("http://ent.ifeng.com/zz/list_0/0.shtml","ent");
    }

    public static List<Doc> clawSports() throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();
        newslist.addAll(spiderCtrl("http://sports.ifeng.com/lanqiu/list_0/0.shtml","sports"));
        newslist.addAll(spiderCtrl("http://sports.ifeng.com/gnzq/list_0/0.shtml","sports"));
        newslist.addAll(spiderCtrl("http://sports.ifeng.com/gjzq/list_0/0.shtml","sports"));
        newslist.addAll(spiderCtrl("http://sports.ifeng.com/zonghe/list_0/0.shtml","sports"));

        return newslist;
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("http://tech.ifeng.com/roll/rtlist_0/index.shtml","tech");
    }

    private static List<Doc> spiderCtrl(String url,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(url, channel);
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
    private static List<Doc> commonSpider(String url,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

        String htmlstr = HttpUtil.getURL(url, "UTF8", null);
        Document document = Jsoup.parse(htmlstr);
        Elements docs = document.select("div.newsList li");

        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Element doc:docs){
            String time = doc.select("h4").get(0).text();
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                final Element link = doc.select("a").get(0);

                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                tdoc.setTitle(link.text());
                tdoc.setUrl(link.attr("href"));
                tdoc.setSource("ifeng");
                tdoc.setChannel(channel);
                tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"/"+time, "yyyy/MM/dd HH:mm"));
                tdoc.setCreatetime(new Date());
                tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","ifeng"+ RandomSN.getRandomString(10,"ilu"));
//                    put("t",link.text());
//                    put("l",link.attr("href"));
//                }};
                newslist.add(tdoc);
            }
        }


        return newslist;
    }


    public static List<Doc> clawFinance() throws Exception{


        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    String htmlstr = HttpUtil.getURL("http://finance.ifeng.com/news/index.shtml", "UTF8", null);
                    Document document = Jsoup.parse(htmlstr);
                    Elements docs = document.select("div.contentL li");

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String start = df.format(TimeControl.clawStartTime().getTime());
                    String end = df.format(TimeControl.clawEndTime().getTime());

                    for(Element doc:docs){
                        String time = doc.select("span.time01").get(0).text();
                        if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                            final Element link = doc.select("a").get(0);
                            Doc tdoc = new Doc();
//                            tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                            tdoc.setTitle(link.text());
                            tdoc.setUrl(link.attr("href"));
                            tdoc.setSource("ifeng");
                            tdoc.setChannel("finance");
                            tdoc.setPtime(DateUtil.stringToDate(time, "yyyy-MM-dd HH:mm:ss"));
                            tdoc.setCreatetime(new Date());
                            tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","ifeng"+ RandomSN.getRandomString(10,"ilu"));
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
