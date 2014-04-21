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
import com.netease.gather.common.util.StringUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//中国江苏网
public class Jiangsu extends AbstractParser  {

	private static final String CHAR_SET = "GBK";

	private String getIndexUrl() {
		if ("jiangsu_jiazuo".equals(jobid)) {
			return "http://photo.jschina.com.cn/jzds/";
		} else if ("jiangsu_wangchong".equals(jobid)) {
			return "http://photo.jschina.com.cn/wchs/";
		} else
			return null;
	}

	private List<PictureSet> parsePictureSet(Document doc) {
		Elements es = doc.select("div.picBox h2 a");
		List<PictureSet> lst = new ArrayList<PictureSet>();
		for (Element e : es) {
			PictureSet ps = new PictureSet();
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			ps.setUrl(e.attr("href"));
			lst.add(ps);
		}
		return lst;
	}

	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet, int start, int limit) {
		List<PictureSet> ret = new ArrayList<PictureSet>();
		try {
			boolean con = true;
			String url = getIndexUrl();
			int count = 0;
			t1: while (con) {
				if (url == null || count > 40) {
					break;
				}
				String html = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
				//				System.out.println(html);
				Document doc = Jsoup.parse(html);
				List<PictureSet> list = parsePictureSet(doc);
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
				if (start + limit > 0) {
					if (ret.size() >= start + limit) {
						break t1;
					}
				}
				Element next = doc.select("div.partB").first().parent().parent().nextElementSibling().select("a").last();
				if (next != null && next.text().equals("下一页")) {
					url = next.attr("href");
				} else {
					url = null;
				}
				count++;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (start + limit > 0) {
			int end = start + limit;
			if (end > ret.size()) {
				end = ret.size();
			}
			return ret.subList(start, end);
		}
		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		return getNewPictureSet(lastPictureSet, 0, 0);
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHAR_SET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	private boolean parseImgAndDesc(Document doc, Picture p) {
		Elements divs = doc.select("div#divall");
		if (divs.size() > 0) {
			if (divs.select("img").size() > 0) {
				//like http://photo.jschina.com.cn/system/2014/02/20/020316864.shtml
				p.setUrl(divs.select("img").first().attr("src"));
				StringBuilder des = new StringBuilder();
				des.append(ClawerPicUtil.getPreviousString(divs.select("img").first(), divs.first()));
				if (des.length() > 0) {
					des.append("<br/>");
				}
				des.append(ClawerPicUtil.getNextString(divs.select("img").first(), 3, doc.select("div#news_more_page_div_id").first()));
				p.setDescription(des.toString());
				return true;
			} else {
				//like http://photo.jschina.com.cn/system/2013/01/24/016054009_06.shtml
				if (divs.first().nextElementSibling() != null && divs.first().nextElementSibling().select("img").size() > 0) {
					Element img = divs.first().nextElementSibling().select("img").first();
					p.setUrl(img.attr("src"));
					String des = ClawerPicUtil.getNextString(img, 3, doc.select("div#news_more_page_div_id").first());
					des = ClawerPicUtil.removeBlankText(des);
					p.setDescription(des);
					return true;
				}
			}
		} else {
			divs = doc.select("div#content");
			if (divs.size() > 0) {
				Elements imgs = divs.select("img");
				if (imgs.size() > 0) {
					//like http://photo.jschina.com.cn/system/2011/11/16/012085746.shtml
					p.setUrl(imgs.first().attr("src"));
					StringBuilder des = new StringBuilder();
					des.append(ClawerPicUtil.getPreviousString(imgs.first(), divs.first()));
					if (des.length() > 0) {
						des.append("<br/>");
					}
					des.append(ClawerPicUtil.getNextString(imgs.first(), 3, doc.select("div#news_more_page_div_id").first()));
					p.setDescription(des.toString());
					return true;
				} else {
					//like http://photo.jschina.com.cn/system/2011/11/16/012085746_01.shtml
					if (divs.first().nextElementSibling() != null && divs.first().nextElementSibling().select("img").size() > 0) {
						Element img = divs.first().nextElementSibling().select("img").first();
						p.setUrl(img.attr("src"));
						String des = ClawerPicUtil.getNextString(img, 3, doc.select("div#news_more_page_div_id").first());
						des = ClawerPicUtil.removeBlankText(des);
						p.setDescription(des);
						return true;
					}
				}
			}
		}
		return false;
	}

	private List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);
		String title = doc.select("span.zi24").text();
		if (StringUtils.isNotBlank(title)) {
			ps.setTitle(title);
		}

		Element pageElement = doc.getElementById("news_more_page_div_id");
		StringBuilder summary = new StringBuilder();
		List<Picture> list = new ArrayList<Picture>();

		Elements imgs = doc.select("div#divall img");
		if (imgs.size() > 1 && pageElement == null) {
			//like http://photo.jschina.com.cn/system/2011/12/28/012400106.shtml
			for (Element img : imgs) {
				Picture p = new Picture();
				p.setUrl(img.attr("src"));
				p.setDescription(ClawerPicUtil.getNextString(img, 1));
				list.add(p);
			}
		} else {
			Picture p = new Picture();
			boolean isAdd = parseImgAndDesc(doc, p);
			if (isAdd) {
				if (StringUtils.isNotBlank(p.getDescription())) {
					summary.append(p.getDescription());
				}
				list.add(p);
			}
			String lastDes = p.getDescription();

			if (!isAdd && pageElement == null) {
				//like 404 or http://photo.jschina.com.cn/system/2011/05/24/010924354.shtml
				logger.warn("{} get no img.", ps.getUrl());
				return null;
			}

			String pageContent = pageElement == null ? "" : pageElement.text();
			while (!StringUtil.isEmpty(pageContent) && pageContent.contains("下一页")) {
				Element urlElement = null;
				for (Element e : pageElement.select("a")) {
					if ("下一页".equals(e.text())) {
						urlElement = e;
						break;
					}
				}
				String url = urlElement.attr("href").trim();
				String content = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
				if (StringUtils.isNotBlank(content)) {
					p = new Picture();
					Document temp = Jsoup.parse(content);

					isAdd = parseImgAndDesc(temp, p);
					if (isAdd) {
						if (StringUtils.isNotBlank(p.getDescription())) {
							if (summary.indexOf(p.getDescription()) == -1) {
								summary.append(p.getDescription());
							}
							lastDes = p.getDescription();
						} else {
							p.setDescription(lastDes);
						}
						list.add(p);
					}

					pageElement = temp.getElementById("news_more_page_div_id");
					pageContent = pageElement == null ? "" : pageElement.text();
				} else {
					break;
				}
			}
		}
		ps.setSummary(summary.toString());
		return list;
	}

	//测试单个图集 （URL:http://photo.jschina.com.cn/jzds/）
	public static void main(String[] args) {
		Jiangsu j = new Jiangsu();
		j.setJobid("jiangsu_jiazuo");
		//		j.setJobid("jiangsu_wangchong");
		//		List<PictureSet> lst = j.getNewPictureSet(null);
		//		System.out.println(lst.size());
		//		int i = 0;
		//		for (PictureSet p : lst) {
		//			if (i < 10 || i > lst.size() - 10)
		//				System.out.println(p.getTitle() + " " + p.getUrl());
		//			i++;
		//		}

		//		PictureSet p = new PictureSet();
		//		p.setUrl("http://photo.jschina.com.cn/system/2011/10/08/011807531.shtml");
		//		List<Picture> list = j.getPictureList(p);
		//		for (Picture pic : list) {
		//			System.out.println(pic.getUrl() + " " + pic.getDescription());
		//		}

		Jiangsu n = new Jiangsu();
//		n.setJobid("jiangsu_jiazuo");
						n.setJobid("jiangsu_wangchong");
				List<PictureSet> lst = n.getNewPictureSet(null);
		int start = 0;
//		List<PictureSet> lst = n.getNewPictureSet(null, start, 500);
		System.out.println(lst.size());
		for (PictureSet p : lst) {
			List<Picture> list = n.getPictureList(p);
			System.out.println(start + " " + p.getTitle() + " " + p.getUrl() + " picNum:" + (list != null ? list.size() : 0) + " " + p.getSummary());
			if (list != null) {
				for (Picture pic : list) {
					System.out.println(pic.getUrl() + "\t" + pic.getDescription());
				}
			}
			start++;
		}
	}

}
