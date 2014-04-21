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
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-20
 * Time: 下午3:16
 */
public class TmtpostSpider {

    private static final Logger logger = Logger.getLogger(TmtpostSpider.class);

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("http://www.tmtpost.com/","tech");
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

        Map<String,String> header = new HashMap<String, String>(){{
            put("Referer","http://www.tmtpost.com/");
            put("Host","www.tmtpost.com");
            put("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
        }};
        String htmlstr = HttpUtil.getURL(url, "UTF-8", header);
        Document document = Jsoup.parse(htmlstr);
        Elements docs = document.select("ul.post-list li");


        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Element doc:docs){
            String time = doc.select("span.post-time").get(0).text();
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                final Element link = doc.select("h2.fl a").get(0);
                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                tdoc.setTitle(link.text());
                tdoc.setUrl(link.attr("href"));
                tdoc.setSource("tmtpost");
                tdoc.setChannel(channel);
                tdoc.setPtime(DateUtil.stringToDate(time, "yyyy/MM/dd HH:mm"));
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

    public static void main(String[] args) throws Exception{
        System.out.println(clawTech());
    }

}

