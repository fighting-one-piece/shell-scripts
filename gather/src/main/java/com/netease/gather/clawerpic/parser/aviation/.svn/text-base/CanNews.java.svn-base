package com.netease.gather.clawerpic.parser.aviation;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class CanNews extends AbstractParser {
	// http://cannews.com.cn/piccenter/hkzt/
	// http://cannews.com.cn/piccenter/mhzt/
	// http://cannews.com.cn/piccenter/fwzt/
	// http://cannews.com.cn/piccenter/fxzt/

	public static final String CHARSET = "utf-8";

	private String getUrl() {
		if ("can_news_hkzt".equals(jobid)) {
			return "http://cannews.com.cn/piccenter/hkzt/";
		} else if ("can_news_mhzt".equals(jobid)) {
			return "http://cannews.com.cn/piccenter/mhzt/";
		} else if ("can_news_fwzt".equals(jobid)) {
			return "http://cannews.com.cn/piccenter/fwzt/";
		} else if ("can_news_fxzt".equals(jobid)) {
			return "http://cannews.com.cn/piccenter/fxzt/";
		}
		return "";
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> picSetList = new LinkedList<PictureSet>();
		String html = ClawerPicUtil.getHtmlByUrl(getUrl(), CHARSET);
		// 取首页的html
		Document doc = Jsoup.parse(html);
		// 筛选出<div class="pages">
		Elements elems = doc.getElementsByClass("pages");
		// logger.debug(elems);
		// 筛选出类似<a href="http://www.cannews.com.cn/piccenter/hkzt/15.shtml">...
		// 15</a>，找到最大页数
		Elements lastPageElems = elems.select("a:contains(...)");
		// logger.debug(lastPageElems);
		String text = lastPageElems.text();
		text = text.substring(text.lastIndexOf(".") + 1);
		// 筛选出<a href="http://www.cannews.com.cn/2014/0225/77225.shtml"
		// class="title">
		elems = doc.select("a.title");
		// logger.debug(elems);
		int pageNum = 1;
		try {
			pageNum = Integer.valueOf(text.trim());
		} catch (Exception e) {
			logger.warn(e.getMessage() + " --- 没有...,说明只有1页");
			pageNum = 1;
		}
		// logger.debug(pageNum);
		for (int i = 1; i <= pageNum; i++) {
			if (i > 1) {
				logger.debug(getUrl() + i + ".shtml");
				html = ClawerPicUtil.getHtmlByUrl(getUrl() + i + ".shtml",
						CHARSET);
				doc = Jsoup.parse(html);
				logger.debug("start to get html from " + getUrl() + i
						+ ".shtml");
				// 筛选出<span class="right">
				elems = doc.select("a.title");
				break;
			}

			Iterator<Element> it = elems.iterator();
			while (it.hasNext()) {
				Element elem = it.next();
				String picSetUrl = elem.attr("href");
				logger.debug(picSetUrl);

				if (lastPictureSet != null) {
					String url = lastPictureSet.getUrl();
					if (url != null && url.equals(picSetUrl)) {
						// 图集已经处理过，返回
						return picSetList;
					}
				}
				String pichtml = ClawerPicUtil.getHtmlByUrl(picSetUrl, CHARSET);
				Document picDoc = Jsoup.parse(pichtml);
				// <p class="summary">
				Elements summary = picDoc.select("p.summary");
				logger.debug(summary.text());
				// logger.debug(elem);

				PictureSet picSet = new PictureSet();
				picSet.setUrl(picSetUrl);
				picSet.setTitle(ClawerPicUtil.removeBlankText(elem.text()));
				picSet.setSummary(ClawerPicUtil.removeBlankText(summary.text()));
				picSetList.add(picSet);
			}

		}

		return picSetList;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		if (ps == null)
			return null;
		List<Picture> picList = new LinkedList<Picture>();

		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHARSET);
		Document doc = Jsoup.parse(html);

		Elements elems = doc.select("script:not([src])");
		Iterator<Element> it = elems.iterator();
		while (it.hasNext()) {
			String data = it.next().data();
			// 取出photos.push中的内容
			if (data.contains("photos.push")) {
				String picUrl = "", note = "";
				String[] dataArr = data.split("\n");
				for (int i = 0; i < dataArr.length; i++) {

					if (dataArr[i].contains("photos.push")) {
						String json = dataArr[i].substring(
								dataArr[i].indexOf("(") + 1,
								dataArr[i].lastIndexOf(")"));

						Map map = null;
						try {
							map = JsonUtil.fromJson(json, Map.class);
							picUrl = (String) map.get("big");
							note = ClawerPicUtil.removeBlankText((String) map.get("note"));
							picList.add(getPic(picUrl, note));
							logger.debug(picUrl + " -[" + note + "]");
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}

					}
				}
			}
		}
		return picList;
	}

	private Picture getPic(String url, String title) {
		Picture pic = new Picture();
		pic.setUrl(url);
		pic.setDescription(ClawerPicUtil.removeBlankText(title));
		return pic;
	}

	public static void main(String[] args) {
		CanNews cn = new CanNews();
		cn.setJobid("can_news_fxzt");
		PictureSet ps = new PictureSet();
		ps.setUrl("http://www.cannews.com.cn/2014/0228/78229.shtml");
		cn.getPictureList(ps);
	}
}
