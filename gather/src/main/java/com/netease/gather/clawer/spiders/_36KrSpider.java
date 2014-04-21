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
public class _36KrSpider {

    private static final Logger logger = Logger.getLogger(_36KrSpider.class);

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("http://www.36kr.com/","tech");
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

//        Map<String,String> header = new HashMap<String, String>(){{
//            put("Referer","http://www.huxiu.com/");
//            put("Host","www.huxiu.com");
//            put("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
//        }};
        String htmlstr = HttpUtil.getURL(url, "UTF-8", null);
        Document document = Jsoup.parse(htmlstr);
        Elements docs = document.select("div.articles article.post-1:has(div.right-col)");


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Element doc:docs){
            String time = "";
            Elements times = doc.select("abbr.timeago");
            if(times.size()>0){
//                time = times.get(0).attr("title");
                time = doc.select("abbr.timeago").get(0).attr("title").split("\\+")[0].replace("T"," ");
            }else {
                time = doc.select("div.postmeta").get(0).text().split("•")[1];
                String month = DateUtil.DateToString(new Date(),"yyyy-MM");
                time = month+"-"+time.split("/")[1].trim()+" "+time.split("/")[0].trim()+":00";
            }
            if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                final Element link = doc.select("h1:has(a) a").get(0);
                String docurl = link.attr("href");
                if(!"".equals(docurl) && !docurl.toLowerCase().startsWith("http://")){
                    URI base= new URI("http://www.36kr.com/");//基本网页URI
                    URI abs=base.resolve(docurl);//解析于上述网页的相对URL，得到绝对URI
                    URL absURL=abs.toURL();//转成URL
                    docurl = absURL.toString();
                }
                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                tdoc.setTitle(link.text());
                tdoc.setUrl(docurl);
                tdoc.setSource("36kr");
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

