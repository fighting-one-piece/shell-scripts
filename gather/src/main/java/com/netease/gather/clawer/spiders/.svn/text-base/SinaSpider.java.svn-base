package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.common.util.ShortUrlGenerator;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-21
 * Time: 下午4:35
 */
public class SinaSpider {

    private static final Logger logger = Logger.getLogger(SinaSpider.class);

    public static List<Doc> clawNews() throws Exception{
        return spiderCtrl("01","90,91,92,93","news"); //93是军事
//        return commonSpider("01","90,91,92","news");
    }

    public static List<Doc> clawEnt() throws Exception{
        return spiderCtrl("04","63","ent");
    }

    public static List<Doc> clawSports() throws Exception{
        return spiderCtrl("02","65,66,67,68,69,70,71,73,74","sports");
    }

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl("03","43","finance");
    }

    public static List<Doc> clawTech() throws Exception{
        return spiderCtrl("05","30","tech");
    }

    private static List<Doc> spiderCtrl(String ch,String cols,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(ch, cols, channel);
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
    private static List<Doc> commonSpider(final String ch,String cols,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();
        long start = TimeControl.clawStartTime().getTimeInMillis()/1000;
        long end = TimeControl.clawEndTime().getTimeInMillis()/1000;

        Map<String,String> header = new HashMap<String, String>(){{
            put("Referer","http://roll.news.sina.com.cn/s/channel.php?ch="+ch);
            put("Host","roll.news.sina.com.cn");
        }};
        String jsonstr = HttpUtil.getURL("http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col="+cols+"&spec=&type=&ch="+ch+"&k=&offset_page=0&offset_num=0&num=80&asc=&page=1&r="+Math.random(), "GBK", header).replace("var jsonData = ","");
        Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);

        for(Map onenew : (List<Map>)jsonmap.get("list")){
            long time = Long.parseLong(onenew.get("time").toString());
            if(time>=start&&time<end){
                Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10, "ilu"));
                tdoc.setTitle(onenew.get("title").toString());
                tdoc.setUrl(onenew.get("url").toString());
                tdoc.setSource("sina");
                tdoc.setChannel(channel);
                tdoc.setPtime(new Date(time*1000));
                tdoc.setCreatetime(new Date());
                tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","sina"+ RandomSN.getRandomString(10, "ilu"));
//                    put("t",onenew.get("title").toString());
//                    put("l",onenew.get("url").toString());
//                }};
                newslist.add(tdoc);
            }
        }


        return newslist;
    }
}
