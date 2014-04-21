package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.HttpUtil;
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
 * Time: 下午5:10
 */
public class XinhuaSpider {

    private static final Logger logger = Logger.getLogger(XinhuaSpider.class);

    public static List<Doc> clawNews() throws Exception{
        List<Doc> rlist = new ArrayList<Doc>();
        rlist.addAll(spiderCtrl("1154670","news"));
        rlist.addAll(spiderCtrl("113322","news"));
        return rlist;
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("116713","ent");
    }

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl("115033","finance");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("115355","tech");
    }

    private static List<Doc> spiderCtrl(String node,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(node, channel);
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
    private static List<Doc> commonSpider(String node,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

        String htmlstr = HttpUtil.getURL("http://search.news.cn/mb/xinhuanet/search/?pno=1&namespace=%2Fmb%2Fxinhuanet&styleurl=http%3A%2F%2Fwww.xinhuanet.com%2Foverseas%2Fstatic%2Fstyle%2Fcss_erji.css&nodetype=3&nodeid="+node,"UTF8",null);

        Document document = Jsoup.parse(htmlstr);
        Elements docs = document.select("td:has(a.hei14)");

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Element doc:docs){
            String time = doc.select("span.showTimeOrNot").get(0).text();
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                final Element link = doc.select("a").get(0);

                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                tdoc.setTitle(link.text());
                tdoc.setUrl(link.attr("href"));
                tdoc.setSource("xinhua");
                tdoc.setChannel(channel);
                tdoc.setPtime(DateUtil.stringToDate(time,"yyyy.MM.dd HH:mm:ss"));
                tdoc.setCreatetime(new Date());
                tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","xinhua"+ RandomSN.getRandomString(10, "ilu"));
//                    put("t",link.text());
//                    put("l",link.attr("href"));
//                }};
                newslist.add(tdoc);
            }
        }

        return newslist;
    }

    public static List<Doc> clawSports() throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    String htmlstr = HttpUtil.getURL("http://www.xinhuanet.com/sports/gdxw.htm","UTF8",null);

                    Document document = Jsoup.parse(htmlstr);
                    Elements docs = document.select("table.hei14 td:has(a)");

                    DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
                    String start = df.format(TimeControl.clawStartTime().getTime());
                    String end = df.format(TimeControl.clawEndTime().getTime());

                    for(Element doc:docs){
                        String time = doc.select("span.day").get(0).text();
                        if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                            final Element link = doc.select("a").get(0);

                            Doc tdoc = new Doc();
//                            tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                            tdoc.setTitle(link.text());
                            tdoc.setUrl(link.attr("href"));
                            tdoc.setSource("xinhua");
                            tdoc.setChannel("sports");
                            tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"/"+time,"yyyy/MM-dd HH:mm"));
                            tdoc.setCreatetime(new Date());
                            tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","xinhua"+ RandomSN.getRandomString(10,"ilu"));
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
