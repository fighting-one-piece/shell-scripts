package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-21
 * Time: 下午4:25
 */
public class QQSpider {

    private static final Logger logger = Logger.getLogger(QQSpider.class);

    public static List<Doc> clawNews() throws Exception{
        return spiderCtrl("news","news","");
//        return commonSpider("news","news","newsgn,newsgj,newssh");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("ent","ent","");
    }

    public static List<Doc> clawSports() throws Exception{
        return spiderCtrl("sports","sports","");
    }

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl("finance","finance","");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("tech","tech","");
    }

    private static List<Doc> spiderCtrl(String site,String channel,String cata) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(site, channel, cata);
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
    private static List<Doc> commonSpider(final String site,String channel,String cata) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

        Map<String,String> header = new HashMap<String, String>(){{
            put("Referer","http://roll."+site+".qq.com/");
            put("Host","roll."+site+".qq.com");
        }};
        String jsonstr = HttpUtil.getURL("http://roll." + site + ".qq.com/interface/roll.php?" + Math.random() + "&cata="+cata+"&site=" + site + "&date=&page=1&mode=1&of=json", "GBK", header);
        Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);
        Elements docs = Jsoup.parse(((Map<String, String>) jsonmap.get("data")).get("article_info")).select("li");

        DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());



        for(Element doc:docs){
            String time = doc.select("span.t-time").get(0).text();
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                String ttit = doc.select("span.t-tit").get(0).text();
                if(!ttit.contains("彩票")){
                    final Element link = doc.select("a").get(0);

                    Doc tdoc = new Doc();
//                    tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                    tdoc.setTitle(link.text());
                    tdoc.setUrl(link.attr("href"));
                    tdoc.setSource("qq");
                    tdoc.setChannel(channel);
                    tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"-"+time,"yyyy-MM-dd HH:mm"));
                    tdoc.setCreatetime(new Date());
                    tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                    Map<String,String> tmap = new HashMap<String, String>(){{
//                        put("docno","qq"+ RandomSN.getRandomString(10,"ilu"));
//                        put("t",link.text());
//                        put("l",link.attr("href"));
//                    }};
                    newslist.add(tdoc);
                }
            }
        }

        return newslist;
    }
}
