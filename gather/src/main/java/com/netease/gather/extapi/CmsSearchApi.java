package com.netease.gather.extapi;

import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmsSearchApi {
    private static final Logger logger = LoggerFactory.getLogger(CmsSearchApi.class);

    /**
     * 报废，CMS接口更换
     * @param keyword
     * @param channelid
     * @param start
     * @param size
     * @return
     */
//    public static List<Map> searchArticle(List<String> keyword, String channelid, String source,String userid,String point,String startday,String endday,String order,int start, int size,boolean isand) {
//        List<Map> doclist = new ArrayList<Map>();
//        if (keyword == null || keyword.size() == 0 || size < 1) {
//            return doclist;
//        }
//
//        String split = " ";
//        if(isand){
//            split = ",";
//        }
//
//        String searchstr = "";
//        for(String word:keyword){
//            searchstr = searchstr + word + split;
//        }
//        searchstr = searchstr.substring(0,searchstr.length()-1);
//
//        startday = StringUtil.isEmpty(startday)? DateUtil.DateToString(new Date(),"yyyy-MM-dd"):startday;
//        try {
//            String url = "http://index.solr.cms.163.com/search/cgindex/"+channelid+"/"+start+"/"+size+"/"+ (StringUtil.isEmpty(source)?"000":source)+"/"+ (StringUtil.isEmpty(userid)?"000":userid) +"/" + (StringUtil.isEmpty(point)?"000":point)
//                    + "/"+startday+(StringUtil.isEmpty(endday)?"":" "+endday)+"/"+ (StringUtil.isEmpty(order)?"000":order) +"/" + URLEncoder.encode(searchstr, "utf-8");
//            String rs = HttpUtil.getURL(url, "utf-8", null);
//            if(rs!=null && rs.trim().length()>0){
//            	Map jsonMap = JsonUtil.fromJson(rs, Map.class);
//            	List jsonList = (List)jsonMap.get("result");
//                for (Object o : jsonList) {
//                    Map<String, String> map = (Map) o;
//                    doclist.add(map);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//        return doclist;
//    }

    /**
     *
     * @param keyword
     * @param channel
     * @param start
     * @param size
     * @return
     */
	public static List<Map> searchArticle(List<String> keyword, String channel, int start, int size,boolean isand) {
		List<Map> doclist = new ArrayList<Map>();
		if (keyword == null || keyword.size() == 0 || size < 1) {
			return doclist;
		}

        String split = " ";
        if(isand){
            split = ",";
        }

        String searchstr = "";
        for(String word:keyword){
            searchstr = searchstr + word + split;
        }
        searchstr = searchstr.substring(0,searchstr.length()-1);

		try {
			String url = "http://data.index.163.com/article/relation/channel/" + channel + "/keyword/" + URLEncoder.encode(searchstr, "utf-8") + "/start/" + start + "/size/" + size + "/maxonly/true";
			String rs = HttpUtil.getURL(url, "utf-8", null);
			if(rs!=null && rs.trim().length()>0){
				List jsonList = JsonUtil.fromJson(rs, List.class);
				for (Object o : jsonList) {
					Map<String, String> map = (Map) o;
                    doclist.add(map);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return doclist;
	}

    public static List<Map> searchPhotoset(List<String> keyword, String channel, int start, int size,boolean isand) {
        List<Map> doclist = new ArrayList<Map>();
        if (keyword == null || keyword.size() == 0 || size < 1) {
            return doclist;
        }

        String split = " ";
        if(isand){
            split = ",";
        }

        String searchstr = "";
        for(String word:keyword){
            searchstr = searchstr + word + split;
        }
        searchstr = searchstr.substring(0,searchstr.length()-1);

        try {
            String url = "http://data.index.163.com/photo/relation/channel/" + channel + "/keyword/" + URLEncoder.encode(searchstr, "utf-8") + "/start/" + start + "/size/" + size + "/";
            String rs = HttpUtil.getURL(url, "utf-8", null);
            if(rs!=null && !rs.equals("null") && rs.trim().length()>0){
                List jsonList = JsonUtil.fromJson(rs, List.class);
                for (Object o : jsonList) {
                    Map<String, String> map = (Map) o;
                    doclist.add(map);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return doclist;
    }


    public static void main(String[] arg) throws Exception {
        List<String> strs = new ArrayList<String>();
        strs.add("山西");
        searchArticle(strs,"0001",0,1,false);
    }

}
