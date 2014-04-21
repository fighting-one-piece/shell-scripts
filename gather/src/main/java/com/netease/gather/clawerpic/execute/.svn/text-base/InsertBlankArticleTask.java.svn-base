package com.netease.gather.clawerpic.execute;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.util.HessianUtil;
import com.netease.gather.common.util.WSPhotoService;
import com.netease.gather.extapi.CMSUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class InsertBlankArticleTask {

	private static final Logger logger = LoggerFactory.getLogger(ClawerPicExecutor.class);

	private static String getParamValue(String[] args, String name) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && args[i].equals(name)) {
				if (i + 1 < args.length)
					return args[i + 1];
				else
					return null;
			}
		}
		return null;
	}

	private static boolean isTest() {
		if ("demo".equals(Config.init().get("env"))) {
			return true;
		} else {
			return false;
		}
	}

	private static List<String> getPicTopicidList(String groupid) {
		List<String> lst = new ArrayList<String>();
		if ("tour".equals(groupid)) {
			lst.add("5LIG0006");
			lst.add("5LIF0006");
		} else if ("war".equals(groupid)) {
			lst.add("54TD0001");
		}
		return lst;
	}

	private static List<String> getCmsTopicid(String groupid) {
		List<String> ret=new ArrayList<String>();
		if ("tour".equals(groupid)) {
			if (isTest()) {
				return ret;
			} else {
				ret.add("00064M28");
				ret.add("00064M33");
				return ret;
			}
		} else if ("war".equals(groupid)) {
			if (isTest()) {
				return ret;
			} else {
				ret.add("00014RRL");
				return ret;
			}
		} else if ("aviation".equals(groupid)) {
			if (isTest()) {
				return ret;
			} else {
				ret.add("00014RTU");
				return ret;
			}
		}
		return ret;
	}

	private static void sendEmptyArtile(String url, String docTitle, String topicid, String source, String imgsrc,String summary) throws Exception {
		// 放到CMS相应的栏目中

		// args are
		// docid,topicid,title,stitle,imgsrc,digest,daynum,lspri,ptime,modelmode,userid,nickname,source,url,search
		// 必填项 topicid, title, lspri, userid, nickname, source, url
		// 推送的栏目id，要拼的字符串，权重，用户id，昵称，新闻来源，分类对应的url
		// 机器推送userid=jiqiren, nickname="机器人"
		// title: [财经]</a> <a target="_blank"
		// href="http://money.163.com/13/1210/10/9FNQBQ2800252G50.html">中央经济工作会议在京开幕
		// 部署明年工作
		// url: http://money.163.com
		// <a href='url'>title</a>
		// StringBuilder title = new StringBuilder("[");
		// title.append(colNameMap.get(key)).append("]")
		// .append("</a> <a target=\"_blank\" href=\"").append(url)
		// .append("\">").append(docTitle).append("</a>");

		int i = 5;
		while (i-- >= 0) {
			boolean suc = HessianUtil.getCmsWebService().insertBlankArticle(null, topicid, docTitle, null, imgsrc, summary, 0, 60, null, null,
					"jiqiren", "机器人", source, url, null);
			logger.info("[title: " + docTitle + ", suc:" + suc + ", url:" + url + ", source: " + source);
			if (suc)
				break;
			Thread.sleep(1000);
		}
	}

	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(t1);
			cal.add(Calendar.MINUTE, -30);
			Date limit = cal.getTime();

			String groupid = getParamValue(args, "-g");
			String isAll = getParamValue(args, "-a");
//						String groupid = "tour";
//						String isAll = "y";

			assert StringUtils.isNotBlank(groupid) : "groupid is blank!";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String[] gArr = groupid.split(",");
			for (String gid : gArr) {
				List<String> topicLst = getPicTopicidList(gid);
				List<String> cmsTopicLst = getCmsTopicid(gid);
				for (String cmsTopicid : cmsTopicLst) {
					if (StringUtils.isNotBlank(cmsTopicid)) {
						List<Map<String, String>> dlist = CMSUtil.getListByTrans(cmsTopicid);
						for (String topicid : topicLst) {
							List<Map<String, String>> psList = WSPhotoService.getSetListByTopic(topicid, 0, 200);
							t1: for (Map<String, String> m : psList) {
								//					System.out.println(m.get("setname") + " " + m.get("seturl") + " " + m.get("timg") + " " + m.get("createdate") + " "
								//							+ m.get("firstpublish") + " " + m.get("lmodify"));
								Date firstpublish = sdf.parse(m.get("firstpublish"));
								if ("y".equals(isAll) || firstpublish.compareTo(limit) >= 0) {
									String seturl = m.get("seturl");
									for (Map<String, String> dmap : dlist) {
										if (seturl.equals(dmap.get("url"))) {
											continue t1;
										}
									}
									String source = m.get("source");
									if (source.contains("&&")) {
										source = source.substring(0, source.indexOf("&&"));
									}
									sendEmptyArtile(seturl, m.get("setname"), cmsTopicid, source, null, m.get("prevue"));
								}
							}
						}
					}
				}
			}

			long t2 = System.currentTimeMillis();
			logger.info("InsertBlankArticleTask groupid:{} finish cost time:{} s", groupid, (t2 - t1) / 1000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			System.exit(0);
		}
	}
}
