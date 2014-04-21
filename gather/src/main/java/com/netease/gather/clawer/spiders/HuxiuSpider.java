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

import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-20
 * Time: 下午3:16
 */
public class HuxiuSpider {

    private static final Logger logger = Logger.getLogger(HuxiuSpider.class);

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("http://www.huxiu.com/","tech");
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
            put("Referer","http://www.huxiu.com/");
            put("Host","www.huxiu.com");
            put("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
        }};
        String htmlstr = HttpUtil.getURL(url, "UTF-8", header);
        Document document = Jsoup.parse(htmlstr);
        Elements docs = document.select("div.article-box:has(div.article-box-ctt:has(h4))");


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Element doc:docs){
            String time = doc.select("div.box-other time").get(0).text();
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                final Element link = doc.select("div.article-box-ctt h4:has(a) a").get(0);
                String docurl = link.attr("href");
                if(!"".equals(docurl) && !docurl.toLowerCase().startsWith("http://")){
                    URI base= new URI("http://www.huxiu.com/");//基本网页URI
                    URI abs=base.resolve(docurl);//解析于上述网页的相对URL，得到绝对URI
                    URL absURL=abs.toURL();//转成URL
                    docurl = absURL.toString();
                }
                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                tdoc.setTitle(link.text());
                tdoc.setUrl(docurl);
                tdoc.setSource("huxiu");
                tdoc.setChannel(channel);
                tdoc.setPtime(DateUtil.stringToDate(time, "yyyy-MM-dd HH:mm"));
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

