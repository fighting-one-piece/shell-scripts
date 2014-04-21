package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.Doc;
import com.netease.gather.service.logic.GenNewsForHao123Service;
import com.netease.gather.service.logic.NewsHotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("genNewsForHao123Service")
public class GenNewsForHao123ServiceImpl implements GenNewsForHao123Service {
	private static final Logger logger = LoggerFactory
			.getLogger(GenNewsForHao123ServiceImpl.class);

	private final static String TEST_ID = "00014R7I";

	private static final String CHANNAL_ID = "00014PGR";

	@Resource
	private NewsHotService newsHotService;
	private static Map<String, String> coltopic = new HashMap<String, String>() {
		{
			if ("demo".equals(Config.init().get("env"))) {
				// 测试
				put("CHANNAL_ID", TEST_ID);
			} else if ("prod".equals(Config.init().get("env"))) {
				put("CHANNAL_ID", CHANNAL_ID);
			}
		}
	};

	public void getSocialNewsForHao123(String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception {
		int hsize = newsHotService.genHots(channel, starttime, endtime, "",
				clustersize);
		if (hsize == 0) {
			return;
		}
		List<Map> showhots = newsHotService.choiceHot(channel, "shehui",
				starttime, endtime, 1, showlimit);
		showhots.addAll(newsHotService.choiceHot(channel, "guoji", starttime,
				endtime, 1, showlimit));
		showhots = newsHotService.choice163Art(showhots, channel);
		pickNewsWithPic(showhots);
	}

	private void pickNewsWithPic(List<Map> showhots) throws Exception {
		List<String> docIdList = new ArrayList<String>();
		if (showhots != null) {
			for (Map map : showhots) {
				Doc doc = (Doc) map.get("cdoc");
				
				// 取163来源的新闻
				if ("163".equals(doc.getSource())) {
					String docid = CommonUtil.get163DocidFromUrl(doc.getUrl());
					// 推送的新闻太少，暂时不判断正文是否包含图片
					// Map<String, String> article =
					// HessianUtil.getCmsWebService().getArticle(docid);
					// String html = article.get("body");
					// String picUrl = CommonUtil.getPicFromHtml(html);

					// if (!StringUtil.isEmpty(picUrl)) {
					logger.info("采纳docid:" + docid + ", url: " + doc.getUrl());
					docIdList.add(docid);
					// }
				}
			}
		}
		logger.info("把 新闻推送到cms相关栏目 {}", coltopic.get("CHANNAL_ID"));
		Map<String, List<String>> jsonMap = new HashMap<String, List<String>>();
		// 放到CMS相应的栏目中
		jsonMap.put(coltopic.get("CHANNAL_ID"), docIdList);
		String jsonstr = JsonUtil.toJsonStr(jsonMap);

		String ret = HttpUtil.getURL(
                Constants.ROBOT_PUSH_CMS_URL + URLEncoder.encode(jsonstr, "utf-8"), "GBK", null);
		logger.info("json:{},ret{}", jsonstr, ret);
	}
}
