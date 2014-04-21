package com.netease.gather.clawerpic.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianRuntimeException;
import com.netease.gather.common.constants.Config;
import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.FileUtil;
import com.netease.gather.common.util.ImageUtil;
import com.netease.gather.common.util.WSPhotoService;
import com.netease.gather.common.util.WgetUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class UploadPicUtil {

	private static final Logger logger = LoggerFactory.getLogger(UploadPicUtil.class);
	
	private static boolean isTest(){
		if ("demo".equals(Config.init().get("env"))) {
			return true;
		}else{
			return false;
		}
	}

	private static String getTopicId(String source,String groupid) {
		if("tour".equals(groupid)){
			if (isTest()) {
				return "5M4L0006";
			} else {
				if (source.indexOf("网易") == 0) {
					return "5LIG0006";
				} else
					return "5LIF0006";
			}
		}else if("war".equals(groupid)){
			if (isTest()) {
				return "5M4F0001";
			} else {
				return "54TD0001";
			}
		}else if("aviation".equals(groupid)){
			if (isTest()) {
				return "5M6D0001";
			} else {
				return "5MC30001";
			}
		}
		return null;
	}
	
	private static int getNewSetid(PictureSet ps) throws Exception {
		int setid = 0;
		if (setid == 0) {
			while (setid == 0) {
				try {
					String topicId = getTopicId(ps.getSource(),ps.getGroupid());
					setid = WSPhotoService.createPhotoset(ps.getTitle(), topicId);
					logger.info("setid is {},topicid is {},jobid is {}", setid, topicId, ps.getJobid());
				} catch (HessianRuntimeException e) {
					logger.error(ps.getJobid() + " " + ps.getTitle() + " 新建图集失败！" + e.getMessage());
				}
			}
		}
		return setid;
	}

	private static String uploadPhoto(int setid, String topicId, String upfile, String des, String title) throws Exception {
		String photoid = "";
		try {
			photoid = WSPhotoService.uploadPhoto(setid, topicId, upfile, des);
		} catch (Exception e) {
			logger.error(e.getMessage() + " 上传文件失败", e);
			return "";
		}
		if (photoid == null || "".equals(photoid)) {
			logger.error("photoid is null " + setid);
			return "";
		}

		//        boolean uptag = false;
		//        while (!uptag){
		//            try{
		//                uptag = WSPhotoService.setOrUpdateTag(photoid, tag);
		//            }catch (HessianRuntimeException e){
		//                logger.error(photoid+" 打tag失败 "+e.getMessage());
		//            }
		//        }
		
		boolean uptitle = false;
		while (!uptitle) {
			try {
				uptitle = WSPhotoService.setPhotoTitle(topicId.substring(4),photoid, title);
			} catch (Exception e) {
				logger.error(photoid + " 打title失败 " + e.getMessage());
			}
		}

		return photoid;
	}

	private static Map<String, String> getClawerPicHeaders(String jobid) {
		if (jobid != null && jobid.indexOf("lvmama_") == 0) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Referer", "www.lvmama.com");
			return headers;
		}
		if (jobid != null && jobid.indexOf("daqi") == 0) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Referer", "www.daqi.com");
			return headers;
		}
		return null;
	}
	
	private static boolean isImage(String filename) {
		try {
			BufferedImage bi = ImageIO.read(new File(filename));
			if (bi == null) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	private static String wgetPicFile(String picUrl,String jobid) throws Exception {
		Map<String, String> headers = getClawerPicHeaders(jobid);
		
		if (StringUtils.isBlank(picUrl))
			return "";
		String name = picUrl.substring(picUrl.lastIndexOf("/"));
		if (name.indexOf("?") >= 0) {
			name = name.substring(0, name.indexOf("?"));
		}
		if (StringUtils.isBlank(name)) {
			logger.info("url异常:picUrl=" + picUrl);
			return "";
		}
		final String filename = "/tmp/" + name;
		String upfile = filename;
		boolean get = WgetUtil.wgetFile(picUrl, filename, headers);
		if (!get) {
			logger.info("下载异常");
			return "";
		}
		boolean isPic=isImage(filename);
		if(!isPic){
			logger.info("下载的文件不是图片");
			return "";
		}

		if (!".jpg".equals(picUrl.substring(picUrl.lastIndexOf("."))) && !".gif".equals(picUrl.substring(picUrl.lastIndexOf(".")))) {
			String newfile = null;
			if(filename.contains(".")){
				newfile=filename.substring(0, filename.lastIndexOf(".")) + ".jpg";
			}else{
				newfile=filename+".jpg";
			}
			upfile = newfile;
			logger.info("后缀异常：" + picUrl.substring(picUrl.lastIndexOf("/")));
			
			try {
				boolean convert = ImageIO.write(ImageIO.read(new File(filename)), "JPEG", new File(newfile));
				logger.info(newfile);
				if (!convert) {
					logger.info("转换异常");
					return "";
				}
			} catch (Exception e) {
				logger.error("转换异常:" + e.getMessage(), e);
				return "";
			} finally {
				FileUtil.deleteFile(filename);
			}
		}

		return upfile;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int getSimilarTitleSetid(String jobid, String author, String title) {
		if(StringUtils.isBlank(author)){
			return 0;
		}
		Map map = new HashMap();
		map.put("jobid", jobid);
		map.put("likeTitle", title);
		map.put("author", author);
		map.put("order", "autoid");
		map.put("start", 0);
		map.put("size", 1);
		try {
			List<PictureSet> list = ScheduleContext.FACADE.getPictureSetService().getListByParameters(map);
			if (list.size() > 0) {
				return list.get(0).getSetid();
			} else {
				return 0;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	private static int getSimilarTitleSetid(PictureSet ps, PictureSet returnTitle) {
		if (ps.getSource().indexOf("网易") != 0) {//只有站内才需要合并
			return 0;
		}
		final String title = ps.getTitle();
		int setid = 0;
		String similar = null;

		if (title.matches("([^\\d]+?)([\\(\\[【（]?\\d+).*")) {
			String s1 = title.replaceAll("([^\\d]+?)([\\(\\[【（]?\\d+).*", "$1");
			int sid = getSimilarTitleSetid(ps.getJobid(), ps.getAuthor(), s1);
			if (sid > 0) {
				similar = s1;
				setid = sid;
			}
		}
		if (title.matches("(.+?)([\\(\\[【（]?之?[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾]+).*")) {
			String s1 = title.replaceAll("(.+?)([\\(\\[【（]?之?[一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾]+).*", "$1");
			int sid = getSimilarTitleSetid(ps.getJobid(), ps.getAuthor(), s1);
			if (sid > 0) {
				if (similar == null || s1.length() < similar.length()) {
					similar = s1;
					setid = sid;
				}
			}
		}
		if (title.matches("(.+?)([\\(\\[【（]?[上中下]+).*")) {
			String s1 = title.replaceAll("(.+?)([\\(\\[【（]?[上中下]+).*", "$1");
			int sid = getSimilarTitleSetid(ps.getJobid(), ps.getAuthor(), s1);
			if (sid > 0) {
				if (similar == null || s1.length() < similar.length()) {
					similar = s1;
					setid = sid;
				}
			}
		}
		if (title.matches("(.+?)([\\(\\[【（]?[IVXⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ]+).*")) {
			String s1 = title.replaceAll("(.+?)([\\(\\[【（]?[IVXⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ]+).*", "$1");
			int sid = getSimilarTitleSetid(ps.getJobid(), ps.getAuthor(), s1);
			if (sid > 0) {
				if (similar == null || s1.length() < similar.length()) {
					similar = s1;
					setid = sid;
				}
			}
		}

		returnTitle.setTitle(similar);
		return setid;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean isExistsSameTitle(String title,String groupId) {
		Map map = new HashMap();
		map.put("title", title);
		map.put("groupid", groupId);
		try {
			int count = ScheduleContext.FACADE.getPictureSetService().getCountByParameters(map);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean isExistsSameUrl(String url) {
		Map map = new HashMap();
		map.put("url", url);
		try {
			int count = ScheduleContext.FACADE.getPictureSetService().getCountByParameters(map);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	public static void uploadPhoto(PictureSet ps, List<Picture> pictures) {
		uploadPhoto(ps, pictures, 0);
	}

	public static void uploadPhoto(PictureSet ps, List<Picture> pictures, int atLeast) {
		try {
			if (ps == null || pictures == null) {
				return;
			}

			if(StringUtils.isBlank(ps.getTitle())){
				logger.warn("标题为空!{}", ps.toString());
				return;
			}
			if (isExistsSameTitle(ps.getTitle(), ps.getGroupid())) {
				logger.info("已存在相同标题({},{})的图集！", ps.getTitle(),ps.getGroupid());
				return;
			}
			if (isExistsSameUrl(ps.getUrl())) {
				logger.info("已存在相同URL({})的图集！", ps.getUrl());
				return;
			}
			
			String topicId = getTopicId(ps.getSource(), ps.getGroupid());
			String channelId = topicId.substring(4);
			
			PictureSet returnTitle = new PictureSet();
			int setid = getSimilarTitleSetid(ps, returnTitle);
			if (setid == 0) {
				setid = getNewSetid(ps);
				WSPhotoService.setPhotosetSource(channelId,setid, ps.getSource() + "&&" + ps.getUrl());
				StringBuilder sb = new StringBuilder();
				if (StringUtils.isNotBlank(ps.getAuthor())) {
					sb.append("作者:").append(ps.getAuthor()).append("<br/>");
				}
				if (StringUtils.isNotBlank(ps.getSummary())) {
					sb.append(ps.getSummary());
				}
				WSPhotoService.setPhotosetSummary(channelId,setid, sb.toString());
			} else {
				String similar = returnTitle.getTitle();
				WSPhotoService.setPhotosetName(channelId,setid, similar);
			}

			ps.setSetid(setid);
			if (ps.getSummary() != null && ps.getSummary().length() > 1300) {
				ps.setSummary(ps.getSummary().substring(0, 1300));
			}

			int uploadNum = 0;
			for (Picture p : pictures) {
				String upfile = null;
				for (int i = 0; i < 5; i++) {
					upfile = wgetPicFile(p.getUrl(),ps.getJobid());
					if (StringUtils.isNotEmpty(upfile)) {
						break;
					}
				}
				if (StringUtils.isEmpty(upfile)) {
					continue;
				}
				
				if (ps.getSource().indexOf("网易") == 0) {//站内加宽高限制
					Map<String, Integer> map = ImageUtil.getPicHeightAWidth(new FileInputStream(upfile));
					if (!(map.get("width") >= 500 && map.get("height") >= 400 || map.get("width") >= 400 && map.get("height") >= 500)) {
						FileUtil.deleteFile(upfile);
						continue;
					}
				}

				String photoid = "";
				int limit = 10;
				while (StringUtils.isEmpty(photoid) && limit > 0) {
					photoid = uploadPhoto(setid, getTopicId(ps.getSource(),ps.getGroupid()), upfile, p.getDescription(), "");
					limit--;
				}
				if (limit == 0 && StringUtils.isEmpty(photoid)) {
					logger.warn("上传图片超过重试次数：setid:{},topicid:{},upfile:{},des:{},title:{}", new Object[] { setid, getTopicId(ps.getSource(),ps.getGroupid()), upfile,
							p.getDescription(), ps.getTitle() });
					FileUtil.deleteFile(upfile);
					continue;
				}

				uploadNum++;
				FileUtil.deleteFile(upfile);
				p.setPhotoid(photoid);
				p.setSetid(setid);
				//				ScheduleContext.FACADE.getPictureService().saveOne(p);
			}
			ps.setUploadnum(uploadNum);

			if(isTest()){
//				boolean push = false;
//				while (!push) {
//					try {
//						push = WSPhotoService.pushPhotoset(channelId,setid);
//					} catch (HessianRuntimeException e) {
//						logger.error(" 发布图集" + setid + "失败 " + e.getMessage());
//					}
//				}
//				logger.info("jobid:{},setid:{},picNum:{} published.", ps.getJobid(), ps.getSetid(),ps.getUploadnum());
				WSPhotoService.setPhotosetCannotSearch(channelId,setid);
			}
			logger.info("jobid:{},setid:{},picNum:{} uploaded.", ps.getJobid(), ps.getSetid(),ps.getUploadnum());
			
			ScheduleContext.FACADE.getPictureSetService().saveOne(ps);

			if (uploadNum >= 10) {
				Thread.sleep(500);
			} else if (uploadNum == 0) {
//				PoPo.send("PictureSet:" + ps.toString() + " upload 0 pic!");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage() + " when ps.url is " + ps.getUrl(), e);
		}
	}

}
