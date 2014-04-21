package com.netease.gather.extapi;

import com.netease.gather.common.util.*;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 跟帖接口封装类
 * @author jyli1
 */

public class TieApiUtil {
	private static final Logger logger = Logger.getLogger(TieApiUtil.class);

	//普通文章和视频获得跟帖数
	public static long getArticleTieTotal(String boardid, String channel, String docid) throws Exception{
		String tiePreUrl = "http://"+ CommonUtil.getChannel_163name(channel)+".163.com/comment/";
		String url = tiePreUrl+docid.substring(8,12)+"/"+boardid+"/"+docid.substring(6, 8)+"/"+docid+".js";
        return parseTieRs(url);
	}

    public static String getArticleTieUrl(String boardid, String channel, String docid) throws Exception{
        return "http://comment."+ CommonUtil.getChannel_163name(channel)+".163.com/"+boardid+"/"+docid+".html";
    }

    public static String getPhotoSetTieUrl(String channel, int setid,String topicid) throws Exception{
        return "http://comment."+ CommonUtil.getChannel_163name(channel)+".163.com/photoview_bbs/"+generatePostid(setid, topicid)+".html";
    }

    //生成图集的docid
    public  static String  generatePostid(int setid,String topicid){
        String postid = IntUtil.c10to32(setid);
        while (postid.length() < 4) {
            postid = "0" + postid;
        }
        postid = "PHOT" + postid + topicid.substring(4, 8) + topicid.substring(0, 4); //生成属于图片库的 ID
        return postid;
    }

	//var commentData={"joincount":"27","count":"12","diggCount":"15","tcount":"10"}
    public static long parseTieRs(String url) {
        String rs = "";
        long count = 0;
        try {
            rs = HttpUtil.getURL(url, "utf-8", null);
            if(rs!=null && rs.trim().length()>0){
                String json = rs.substring(rs.indexOf("{"),rs.indexOf("}")+1);
                @SuppressWarnings("rawtypes")
                Map jsonMap = JsonUtil.fromJson(json, Map.class);
                if(jsonMap.get("joincount")!=null && StringUtil.isNum((String)jsonMap.get("joincount"))){
                    count = Long.valueOf((String)jsonMap.get("joincount"));
                }
            }
        }catch(Exception e){
            logger.error(e);
        }
        return count;
	}

}
