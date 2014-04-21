package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-21
 * Time: 下午3:29
 */
public class PeopleSpider {

    private static final Logger logger = Logger.getLogger(PeopleSpider.class);

    public static List<Doc> clawNews() throws Exception{
        return spiderCtrl(new ArrayList<Integer>(Arrays.asList(210802,1001,14576,34948,210804,1008,42510,210805,1011,1002,210807,14657,42272,210808,40531,1003)),"news");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl(new ArrayList<Integer>(Arrays.asList(1012,210809,14677,209043)),"ent");
    }

    public static List<Doc> clawSports() throws Exception{
        return spiderCtrl(new ArrayList<Integer>(Arrays.asList(22176,210810,14820)),"sports");
    }

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl(new ArrayList<Integer>(Arrays.asList(210803,1004,1009,71661)),"finance");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl(new ArrayList<Integer>(Arrays.asList(1010,1009)),"tech");
    }

    private static List<Doc> spiderCtrl(List<Integer> filter,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(filter, channel);
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
    private static List<Doc> commonSpider(List<Integer> filter,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

        String jsonstr = HttpUtil.getURL("http://news.people.com.cn/210801/211150/index.js?_=" + new Date().getTime(), "UTF8", null);
        Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        for(Map<String,String> onenew : (List<Map<String,String>>)jsonmap.get("items")){
            int nodeId = Integer.parseInt(onenew.get("nodeId"));
            boolean vaild = filter == null || filter.size() == 0 || filter.contains(nodeId);
            if(vaild){
                if(onenew.get("date").compareTo(start) >= 0&&onenew.get("date").compareTo(end) < 0){
                    String docurl = onenew.get("url");
                    if(!"".equals(docurl) && !docurl.toLowerCase().startsWith("http://")){
                        URI base= new URI("http://www.people.com.cn/");//基本网页URI
                        URI abs=base.resolve(docurl);//解析于上述网页的相对URL，得到绝对URI
                        URL absURL=abs.toURL();//转成URL
                        docurl = absURL.toString();
                    }
//                    final String finalDocurl = docurl;
                    Doc tdoc = new Doc();
//                    tdoc.setDocno(RandomSN.getRandomString(10,"ilu"));
                    tdoc.setTitle(onenew.get("title").replace("&nbsp;", " ").replace("&quot;", "\""));
                    tdoc.setUrl(docurl);
                    tdoc.setSource("people");
                    tdoc.setChannel(channel);
                    tdoc.setPtime(DateUtil.stringToDate(onenew.get("date"),"yyyy-MM-dd HH:mm:ss"));
                    tdoc.setCreatetime(new Date());
                    tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                    Map<String,String> tmap = new HashMap<String, String>(){{
//                        put("docno","people"+ RandomSN.getRandomString(10,"ilu"));
//                        put("t",onenew.get("title").replace("&nbsp;"," ").replace("&quot;","\""));
//                        put("l", finalDocurl);
//                    }};
                    newslist.add(tdoc);
                }
            }

        }


        return newslist;
    }

}
