package com.netease.gather.clawerpic.parser.tour;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//大旗网-旅游热图
public class Daqi extends AbstractParser{

	private static final String CHAR_SET = "GBK";


	private static final String DOMAIN_URL = "http://pic.daqi.com";

	private String getPictureSetUrl(int page) {
		return String.format("http://pic.daqi.com/editor/pic3list/pic_picthree/10333/%s.html", page);
	}

	private List<PictureSet> parsePictureSet(String html) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		Document doc = Jsoup.parse(html);
		Elements info = doc.select("ul.pic_ul_4 li p a");
		for (Element e : info) {
			PictureSet ps = new PictureSet();
			String url = e.attr("href");
			if (url.indexOf("/") == 0) {
				url = DOMAIN_URL + url;
			}
			ps.setUrl(url);
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			if (StringUtils.isNotBlank(ps.getUrl()))
				ret.add(ps);
		}
		return ret;
	}

	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet, int limit) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		try {
			t1: for (int i = 1; i <= 20; i++) {
				String url = getPictureSetUrl(i);

				String html = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
				//				System.out.println(html);
				List<PictureSet> list = parsePictureSet(html);
				logger.debug("{} job getPictureSet from {} return:{}", jobid, url, list.size());
				if (lastPictureSet == null) {
					ret.addAll(list);
				} else {
					for (PictureSet ps : list) {
						if (ps.getUrl().equals(lastPictureSet.getUrl())) {
							break t1;
						}
						ret.add(ps);
					}
				}
				if (limit > 0) {
					if (ret.size() >= limit) {
						break t1;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		return getNewPictureSet(lastPictureSet, 0);
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHAR_SET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	private List<Picture> parsePictureList(String html, PictureSet ps) {
		String realUrl = ClawerPicUtil.getRealUrl(ps.getUrl());
//		ps.setUrl(realUrl);

		Document doc = Jsoup.parse(html);
		List<Picture> list = new ArrayList<Picture>();
		String pagesum = doc.select("div.pagenation_t span").last().text();
		StringBuilder summary = new StringBuilder();
		String lastDes = null;
		for (int i = 1; i <= Integer.parseInt(pagesum); i++) {
			String url0 = null;
			if (i > 1) {
				String source = realUrl;
				String[] array = source.split("\\.html");
				url0 = array[0] + "_" + i + ".html";
			}
			String url = (i == 1 ? realUrl : url0);
			String content = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
			if (StringUtils.isNotBlank(content)) {
				Picture p = new Picture();
				Document temp = Jsoup.parse(content);
				Elements imgs = temp.select("div.slidePic img").not("img[alt$=一张]");
				if (imgs.size() > 0) {
					p.setUrl(imgs.first().attr("src"));
					String des = temp.select("div.picDepict").text();
					if (des.length() > 0) {
						if (summary.indexOf(des) == -1) {
							summary.append(des);
						}
						des = ClawerPicUtil.removeBlankText(des);
						lastDes = des;
					} else {
						des = lastDes;
					}
					p.setDescription(des);
					list.add(p);
				} else {
					logger.warn("{} get no img.",url);
				}
			} else {
				logger.warn(url + " get null html.");
			}
		}
		ps.setSummary(summary.toString());
		return list;
	}

	//测试单个图集 （URL:http://news.2500sz.com/tppd/tppd.shtml）
	public static void main(String[] args) {

//		PictureSet p = new PictureSet();
//		p.setUrl("http://pic.daqi.com/slide/3561065.html");
//		List<Picture> list = new Daqi().getPictureList(p);
//		for (Picture pic : list) {
//			System.out.println(pic.getUrl() + " " + pic.getDescription());
//		}

				Daqi n = new Daqi();
				List<PictureSet> lst = n.getNewPictureSet(null, 20);
				System.out.println(lst.size());
				for (PictureSet p : lst) {
					List<Picture> list = n.getPictureList(p);
					System.out.println(p.getTitle() + " " + p.getUrl() + " picNum:" + list.size() + " " + p.getSummary());
					for (Picture pic : list) {
						System.out.println(pic.getUrl() + "\t" + pic.getDescription());
					}
				}
	}

}
