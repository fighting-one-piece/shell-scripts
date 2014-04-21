package com.netease.gather.common.util;

import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.exception.ApplicationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

/**
 * User: AzraelX
 * Date: 13-11-15
 * Time: 下午4:08
 */
public class CommonUtil {

    public static String getChannelById(String channelid) throws Exception{
        String channel = "";
        if("0001".equals(channelid)){
            channel = "news";
        }else if("0003".equals(channelid)){
            channel = "ent";
        }else if("0005".equals(channelid)){
            channel = "sports";
        }else if("0025".equals(channelid)){
            channel = "finance";
        }else if("0009".equals(channelid)){
            channel = "tech";
        }else {
            throw new ApplicationException("频道错误！");
        }
        return channel;
    }

    public static String getChannelid(String channel) throws Exception{
        String channelid = "";
        switch (Constants.CHANNEL.getChannel(channel)){
            case news: channelid="0001"; break;
            case ent: channelid = "0003"; break;
            case sports: channelid = "0005"; break;
            case finance: channelid = "0025"; break;
            case tech: channelid = "0009"; break;
            default: throw new ApplicationException("频道错误！");
        }
        return channelid;
    }

    public static String getChannel_163name(String channel) throws Exception{
        String channelname = "";
        switch (Constants.CHANNEL.getChannel(channel)){
            case news: channelname="news"; break;
            case ent: channelname = "ent"; break;
            case sports: channelname = "sports"; break;
            case finance: channelname = "money"; break;
            case tech: channelname = "tech"; break;
            default: throw new ApplicationException("频道错误！");
        }
        return channelname;
    }

    public static String get163DocidFromUrl(String url){
        if(url!=null && url.contains(".html") && url.contains("/")){
            return url.substring(url.lastIndexOf("/")+1,url.indexOf(".html"));
        }
        return "";
    }

    public static String getKeywords(String url) {
        String rs = HttpUtil.getURL(url, "gbk", null);
        String charset = "gbk";
        Document doc = Jsoup.parse(rs);
        Elements elements = doc.getElementsByTag("meta");
        String key = "";
        for (Element e : elements) {
            if (e.attr("content") != null && e.attr("content").contains("charset")) {
                String charsetString = e.attr("content").substring(e.attr("content").indexOf("charset"));
                String[] cs = charsetString.split("=");
                if (cs.length > 1) {
                    charset = cs[1].trim();
                    break;
                }
            }
            if (e.attr("charset") != null && e.attr("charset").trim().length() > 0) {
                charset = e.attr("charset");
                break;
            }
        }
        String rs1 = HttpUtil.getURL(url, charset, null);
        Document doc1 = Jsoup.parse(rs1);
        Elements elements1 = doc1.getElementsByTag("meta");
        for (Element e : elements1) {
            if ("keywords".equals(e.attr("name"))) {
                key = e.attr("content");
            }
        }
        return key;
    }

    public static String getPicFromHtml(String html) {
        String picurl = "";
        Document d = Jsoup.parse(html);
        Elements picli = d.getElementsByTag("img");
        for (Element pic : picli) {
            String attr = pic.attr("src");
            if(attr != null && !attr.toLowerCase().endsWith("gif") && !attr.toLowerCase().endsWith("png") && StringUtil.isURL(attr)){
                picurl = attr;
                break;
            }
        }
        return picurl;
    }


    //依次传入投票数、投票时间、压缩因子、重力因子、转移因子、整体扩大系数
    private static double hacknews(long voteSum, Date time, double gravityPower, int transferPower, double pressPower, int enlargePower){
        //一个小时的毫秒数
        Long ONE_HOUR = 3600000L;
        long nowTime = new Date().getTime();
        int denominator = (int) (( nowTime - time.getTime() ) / ONE_HOUR + transferPower);
        if(denominator <= 0) denominator = transferPower;
        //System.out.println("tienum is: " + voteSum +",  time："+gravityPower+ ", time is: " + denominator+",  gravityPower："+gravityPower+",  transferPower："+transferPower+",  pressPower："+pressPower+",  enlargePower："+enlargePower);
        double hackerScore =  Math.pow(voteSum, pressPower) / Math.pow(denominator, gravityPower);
        return hackerScore*enlargePower;
    }

    //文章使用hacknews计算得分
    public static double hacknews4Doc(long voteSum, Date time){
        return hacknews(voteSum*10+1,time,2D,2,1.5D,1000);
    }
}
