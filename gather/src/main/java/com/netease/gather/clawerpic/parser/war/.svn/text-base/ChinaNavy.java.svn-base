package com.netease.gather.clawerpic.parser.war;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class ChinaNavy extends AbstractParser {
	private static Logger logger = Logger.getLogger(ChinaNavy.class);

	// 中国海军-军事酷图
	public static final String BASE_URL = "http://navy.81.cn/";
	public static final String URL = "http://navy.81.cn/jskt.htm";
	public static final String JSKT_URL = "http://navy.81.cn/jskt_";
	public static final String CHARSET = "utf-8";
	public static final String INDEX_CLASS = "content";

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> picSetList = new LinkedList<PictureSet>();
		String html = ClawerPicUtil.getHtmlByUrl(URL, CHARSET);
		// 取首页的html
		Document doc = Jsoup.parse(html);
		// 筛选出<div class="content">
		Elements elems = doc.getElementsByClass(INDEX_CLASS);
		// logger.debug(elems);
		// 筛选出类似<a href=jskt_9.htm class = "change">尾页</a>，找到最大页数
		Elements lastPageElems = elems.select("a:contains(尾页)");
		// logger.debug(lastPageElems);
		String href = lastPageElems.attr("href");
		logger.debug(href);
		// logger.debug(elems);
		// 取出jskt_9.htm,数字9
		try {
			int pageNum = Integer.valueOf(href.substring(href.indexOf("_") + 1,
					href.lastIndexOf(".htm")));

			for (int i = 1; i <= pageNum; i++) {
				if (i > 1) {
					logger.debug(JSKT_URL + i + ".htm");
					html = ClawerPicUtil.getHtmlByUrl(JSKT_URL + i + ".htm",
							CHARSET);
					doc = Jsoup.parse(html);
					logger.debug("start to get html from " + JSKT_URL + i
							+ ".htm");
					// 筛选出<div class="content">
					elems = doc.getElementsByClass(INDEX_CLASS);
				}

				Elements liElems = elems.select("li").select("a");
				Iterator<Element> it = liElems.iterator();
				while (it.hasNext()) {
					Element elem = it.next();
					String picSetUrl = BASE_URL + elem.attr("href");
					if (lastPictureSet != null
							&& lastPictureSet.getUrl().equals(picSetUrl)) {
						// 图集已经处理过，返回
						return picSetList;
					}
					String pichtml = ClawerPicUtil.getHtmlByUrl(picSetUrl,
							CHARSET);
					Document picDoc = Jsoup.parse(pichtml);
					// <div class="summary">
					Elements summary = picDoc.getElementsByClass("summary");
					// logger.debug(summary.text());
					// logger.debug(elem);

					PictureSet picSet = new PictureSet();
					picSet.setUrl(picSetUrl);
					picSet.setTitle(ClawerPicUtil.removeBlankText(elem.text()));
					picSet.setSummary(ClawerPicUtil.removeBlankText(summary
							.text()));
					picSetList.add(picSet);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
		// 筛选出<img class="pic">
		Elements elems = doc.getElementsByClass("pic");
		String src = elems.attr("src");
		String title = elems.attr("title");
		String picUrl = BASE_URL + src.substring(src.indexOf("img/"));

		picList.add(getPic(picUrl, title));

		Element picElems = doc.getElementById("displaypagenum");
		Elements picsElems = picElems.select("a").select(".num");
		// logger.debug(picsElems);
		Iterator<Element> it = picsElems.iterator();
		while (it.hasNext()) {
			Element elem = it.next();
			String href = elem.attr("href");
			href = href.substring(href.lastIndexOf("_"));
			String newUrl = ps.getUrl().replace(".htm", href);

			html = ClawerPicUtil.getHtmlByUrl(newUrl, CHARSET);
			doc = Jsoup.parse(html);
			// 筛选出<img class="pic">
			elems = doc.getElementsByClass("pic");
			src = elems.attr("src");
			title = elems.attr("title");
			picUrl = BASE_URL + src.substring(src.indexOf("img/"));
			logger.debug(picUrl);
			picList.add(getPic(picUrl, title));
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
		// String src = "../../../img/a.jpg";
		// src = src.substring(src.lastIndexOf("img/"));
		// logger.debug(src);
		new ChinaNavy().getNewPictureSet(null);
	}
}
