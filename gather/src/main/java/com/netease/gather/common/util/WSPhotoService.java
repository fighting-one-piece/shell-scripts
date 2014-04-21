package com.netease.gather.common.util;

import com.netease.photo.service.PhotoService;
import com.netease.photo.service.PhotoService240;
import com.netease.photo.upload.PhotoUpload;
import com.netease.photo.upload.Utils;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ykxu
 * Date: 12-9-10
 * Time: 上午11:32
 */
public class WSPhotoService {

    private static PhotoService photoservice;
    private static PhotoService photoservice2;
//    private static PhotoUpload photoUpload;
    private static Map<String,PhotoUpload> photoUploadMap=new HashMap<String,PhotoUpload>();
    private static PhotoService240 photoservice240;
//    private static final String photoservice240url = "http://oracle.service.240.photo.163.com/pic";

    private static final Logger logger = Logger.getLogger(PhotoService.class);
    static {
        try {
            photoservice = Utils.getPhotoService();
            //创建上传类
//            photoUpload = new PhotoUpload(photoservice,"0006");
            photoservice240  = HessianUtil.getPhotoService240();
            photoservice2  = HessianUtil.getPhotoService();
        } catch (MalformedURLException e) {
            logger.error("获取webservcie错误，请检查您的配置",e);
        }
    }
    
	private static PhotoUpload getPhotoUploadByChannelId(String channelId) {
		PhotoUpload pu = photoUploadMap.get(channelId);
		if (pu == null) {
			pu = new PhotoUpload(photoservice, channelId);
			photoUploadMap.put(channelId, pu);
		}
		return pu;
	}

    /**
     * 创建图集
     */
    public static int createPhotoset(String setname, String topicid){
        //创建图集   第一个参数代表图集名字，第二个参数代表栏目id，可根据实际栏目id修改         第三个代表图集跟帖是否需要审核，默认可传false
        int setid = getPhotoUploadByChannelId(topicid.substring(4)).createphotosetWithBoard(setname, topicid, false);
        //如果需要图集可以用下面方法
//        photoUpload.updatePrevue(setid, "图集描述");
        return setid;
    }
    
    public static boolean setPhotosetName(String channelId,int setid, String value){
    	boolean ret = getPhotoUploadByChannelId(channelId).updateSetInfo(setid, "setname", value);
    	return ret;
    }
    
    public static boolean setPhotosetSource(String channelId,int setid, String value){
    	boolean ret = getPhotoUploadByChannelId(channelId).updateSetInfo(setid, "source", value);
    	return ret;
    }
    
    public static boolean setPhotosetSummary(String channelId,int setid, String value){
		if (value.length() > 220) {
			value = value.substring(0, 220);
			int p = value.lastIndexOf("。");
			if (p < 0) {
				p = value.lastIndexOf("！");
			}
			if (p < 0) {
				p = value.lastIndexOf("？");
			}
			if (p < 0) {
				p = value.lastIndexOf("，");
				if (p > 0) {
					p--;
				}
			}
			if (p < 0) {
				p = value.lastIndexOf(" ");
			}
			if (p > 0) {
				value = value.substring(0, p + 1);
			}
		}
    	boolean ret = getPhotoUploadByChannelId(channelId).updateSetInfo(setid, "prevue", value);
    	return ret;
    }
    
	public static boolean setPhotosetCannotSearch(String channelId,int setid) {
		boolean ret = getPhotoUploadByChannelId(channelId).updateSetInfo(setid, "type", "3");
		return ret;
	}

    public static String uploadPhoto(int setid, String topicid, String filepath,String description){
        //上传图片，第一个参数是栏目id，第二个图集id，第三个图片路径
        String photoid = getPhotoUploadByChannelId(topicid.substring(4)).uploadPhoto(topicid, setid, filepath);
        
        if(description!=null){
	        if (description.length() > 220) {
	        	description = description.substring(0, 220);
				int p = description.lastIndexOf("。");
				if (p < 0) {
					p = description.lastIndexOf("！");
				}
				if (p < 0) {
					p = description.lastIndexOf("？");
				}
				if (p < 0) {
					p = description.lastIndexOf("，");
					if (p > 0) {
						p--;
					}
				}
				if (p < 0) {
					p = description.lastIndexOf(" ");
				}
				if (p > 0) {
					description = description.substring(0, p + 1);
				}
			}
	        getPhotoUploadByChannelId(topicid.substring(4)).addOrUpdatePhotoDesc(photoid,description);
        }
        return photoid;
    }

    public static void addOrUpdatePhotoDesc(String channelId,String photoid, String description){
    	getPhotoUploadByChannelId(channelId).addOrUpdatePhotoDesc(photoid,description);
    }

    public static boolean setPhotoTitle(String channelId,String photoid,String value){
        return getPhotoUploadByChannelId(channelId).setPhotoProperty("title",photoid,value);
    }

    public static boolean setOrUpdateTag(String channelId,String photoid,String value){
        //设置tag
        return getPhotoUploadByChannelId(channelId).setOrUpdateTag(photoid, value);
    }

    public static boolean pushPhotoset(String channelId,int setid){
        //发布图集
        return getPhotoUploadByChannelId(channelId).pushPhotoset(setid);
    }


    public static List<Map<String,String>> getPhotoListBySetid(String channelid,int setid)throws Exception{
        return photoservice240.getPhotoListBySetid(channelid, setid);
    }

    public static List<Map<String,String>> getPhotoSetsByTopicId(String topicid,int start,int end)throws Exception {
        return photoservice240.getPhotoSetsByTopicId(topicid,start,end,false);
    }

    public static List<Map<String,String>> getPhotoSetList(String channelid, String fromDate,String toDate,int start, int length)throws Exception {
        return photoservice240.getPhotoSetList(channelid,fromDate,toDate,start,length);
    }

    public static boolean deletePhoto(String photoid, int setid, String channelid)throws Exception{
        return photoservice2.deletePhoto(photoid,setid,channelid);
    }
    
    /**
     * 函数名称：根据图集ID获取该图集信息包括封面img
     * @param channelid
     * @param setid
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> getSetInfo(String channelid, int setid){
    	Map map = new HashMap<String, String>();
    	try{
    		map = photoservice240.getSetInfo(channelid, setid);
    	}catch (Exception e) {
    		logger.error(e.getMessage());
		}
  	  return map;
    }

    
	public static List<Map<String, String>> getSetListByTopic(String topicid, int start, int length) {
		List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
		try {
			lst = photoservice240.getSetListByTopic(topicid, start, length);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return lst;
	}
    
    public static void main(String arg[]) throws Exception {
        boolean  a = WSPhotoService.deletePhoto("8C2AA96U4P060011",26005,"0011");
        System.out.print(a);
//        WSPhotoService.addOrUpdatePhotoDesc("8C2AA96U4P060011","12131231");
//        List<Map<String, String>> photos = WSPhotoService.getPhotoListBySetid("0011",26010);
//        List<Map<String, String>> lastchg = WSPhotoService.getPhotoSetList("0011","20121030","20121031",0,100);
//        for(Map<String, String> pmap:lastchg){
//            System.out.println(pmap);
////            System.out.println(pmap.get("photoid"));
////            System.out.println(pmap.get("img"));
////            System.out.println(pmap.get("oimg"));
////            System.out.println(pmap.get("timg"));
////            System.out.println(pmap.get("simg"));
//        }
    }
}
