package com.netease.gather.clawer.spiders;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.common.util.ShortUrlGenerator;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AzraelX
 * Date: 13-8-20
 * Time: 下午3:16
 */
public class HexunSpider {

    private static final Logger logger = Logger.getLogger(HexunSpider.class);

    public static List<Doc> clawFinance() throws Exception{
        return spiderCtrl("100,108,122,121,119,107,114,115,182,170,177,200","finance");
    }

    private static List<Doc> spiderCtrl(String cols,String channel) throws Exception{

        boolean sucess = false;
        int retry = 5;
        List<Doc> newslist = new ArrayList<Doc>();
        while (!sucess){
            try {
                if(retry>0){
                    newslist = commonSpider(cols, channel);
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
    private static List<Doc> commonSpider(String cols,String channel) throws Exception{

        List<Doc> newslist = new ArrayList<Doc>();

//        Map<String,String> header = new HashMap<String, String>(){{
//            put("Referer","http://roll.news.sina.com.cn/s/channel.php?ch="+ch);
//            put("Host","roll.news.sina.com.cn");
//        }};
//        http://roll.hexun.com/roolNews_listRool.action?type=all&ids=&date=2014-01-17&page=1&tempTime=46331623
        DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
        String start = df.format(TimeControl.clawStartTime().getTime());
        String end = df.format(TimeControl.clawEndTime().getTime());

        String today = DateUtil.DateToString(new Date(),"yyyy-MM-dd");

        int i=1;
        boolean stop = false;
        while (!stop){
            String jsonstr = HttpUtil.getURL("http://roll.hexun.com/roolNews_listRool.action?type=all&ids="+cols+"&date="+today+"&page="+i, "UTF-8", null);
            Map jsonmap = JsonUtil.fromJson(jsonstr, Map.class);

            for(Map onenew : (List<Map>)jsonmap.get("list")){
                String time = (String) onenew.get("time");
                if(time.compareTo(start) >= 0&&time.compareTo(end) < 0){
                    if(onenew.get("title").toString().startsWith(DateUtil.DateToString(new Date(),"MM月dd日"))){
                        continue;
                    }
                    Doc tdoc = new Doc();
//                tdoc.setDocno(RandomSN.getRandomString(10, "ilu"));
                    tdoc.setTitle(onenew.get("title").toString());
                    tdoc.setUrl(onenew.get("titleLink").toString());
                    tdoc.setSource("hexun");
                    tdoc.setChannel(channel);
                    tdoc.setPtime(DateUtil.stringToDate(Calendar.getInstance().get(Calendar.YEAR)+"-"+time,"yyyy-MM-dd HH:mm"));
                    tdoc.setCreatetime(new Date());
                    tdoc.setDocno(ShortUrlGenerator.generatorAllStr(tdoc.getUrl()));
//                Map<String,String> tmap = new HashMap<String, String>(){{
//                    put("docno","sina"+ RandomSN.getRandomString(10, "ilu"));
//                    put("t",onenew.get("title").toString());
//                    put("l",onenew.get("url").toString());
//                }};
                    newslist.add(tdoc);
                }

                if(time.compareTo(start) < 0){
                    stop = true;
                }
            }

            i++;
        }


        return newslist;
    }
    public static void main(String[] args) throws Exception{
        System.out.println(clawFinance());
    }

}

