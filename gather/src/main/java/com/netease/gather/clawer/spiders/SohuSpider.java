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
 * Date: 13-8-21
 * Time: 下午4:49
 */
public class SohuSpider {

    private static final Logger logger = Logger.getLogger(SohuSpider.class);

    public static List<Doc> clawNews() throws Exception{
        return spiderCtrl("news",new ArrayList<Integer>(Arrays.asList(0,1,2,4)),"news"); //4是军事
//        return commonSpider("news",new ArrayList<Integer>(Arrays.asList(0,1,2)),"news");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("yule",new ArrayList<Integer>(),"ent");
    }

    public static List<Doc> clawSports() throws Exception{
        return spiderCtrl("sports",new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,6,7,8)),"sports");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("it",new ArrayList<Integer>(),"tech");
    }

    private static List<Doc> spiderCtrl(String site,List<Integer> filter,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(site, filter, channel);
                }
                sucess = true;
            }catch (Exception e){
                retry--;
                logger.error(site+filter+channel);
                logger.error(e);
            }
        }

        return newslist;
    }

    @SuppressWarnings("unchecked")
    private static List<Doc> commonSpider(String site,List<Integer> filter,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

        DateFormat dayf = new SimpleDateFormat("yyyyMMdd");
        String day = dayf.format(TimeControl.clawStartTime().getTime());

        String jsonstr = HttpUtil.getURL("http://"+site+".sohu.com/_scroll_newslist/"+day+"/news.inc", "UTF8", null).replace("var newsJason = ","");
        Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);

        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(final List onenew : (List<List>)jsonmap.get("item")){
            int no = Integer.parseInt(onenew.get(0).toString());
            boolean vaild = filter == null || filter.size() == 0 || filter.contains(no);
            if(vaild){
                String time = onenew.get(3).toString();
                if(time.compareTo(start) >= 0&&time.compareTo(end) < 0){

                    Doc tdoc = new Doc();
//                    tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                    tdoc.setTitle(onenew.get(1).toString());
                    tdoc.setUrl(onenew.get(2).toString());
                    tdoc.setSource("sohu");
                    tdoc.setChannel(channel);
                    tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"/"+time,"yyyy/MM/dd HH:mm"));
                    tdoc.setCreatetime(new Date());
                    tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                    Map<String,String> tmap = new HashMap<String, String>(){{
//                        put("docno","sohu"+ RandomSN.getRandomString(10, "ilu"));
//                        put("t", onenew.get(1).toString());
//                        put("l", onenew.get(2).toString());
//                    }};
                    newslist.add(tdoc);
                }
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
                    String htmlstr = HttpUtil.getURL("http://business.sohu.com/business_scrollnews.shtml","GBK",null);

                    Document document = Jsoup.parse(htmlstr);
                    Elements docs = document.select("div.f14list li:has(a)");

                    DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
                    String start = df.format(TimeControl.clawStartTime().getTime());
                    String end = df.format(TimeControl.clawEndTime().getTime());

                    for(Element doc:docs){
                        String time = doc.select("span").get(0).text().trim().replace("(","").replace(")","");
                        if(time.compareTo(start)>=0&&time.compareTo(end)<0){
                            final Element link = doc.select("a").get(0);
                            Doc tdoc = new Doc();
//                            tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                            tdoc.setTitle(link.text());
                            tdoc.setUrl(link.attr("href"));
                            tdoc.setSource("sohu");
                            tdoc.setChannel("finance");
                            tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"/"+time,"yyyy/MM/dd HH:mm"));
                            tdoc.setCreatetime(new Date());
                            tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","sohu"+ RandomSN.getRandomString(10,"ilu"));
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
