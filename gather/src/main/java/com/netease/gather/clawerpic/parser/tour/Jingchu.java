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

//荆楚网
public class Jingchu extends AbstractParser  {

	
	private static final String CHAR_SET="UTF-8";

	private String getIndexUrl() {
		if ("jingchu_guoji".equals(jobid)) {
			return "http://photo.cnhubei.com/gj/";
		} else if ("jingchu_tianxia".equals(jobid)) {
			return "http://photo.cnhubei.com/qt/";
		} else if ("jingchu_fengguang".equals(jobid)) {
			return "http://photo.cnhubei.com/fg/";
		} else
			return null;
	}

	private int getPageNum(Document doc) {
		Elements es = doc.select("div.page ul li a");
		int size = es.size();
		Element end = es.get(size - 2);
		String last = end.text().replaceAll("[^\\d]", "");
		return Integer.parseInt(last);
	}

	private String getPictureSetUrl(int page) {
		if ("jingchu_guoji".equals(jobid)) {
			return String.format("http://photo.cnhubei.com/gj/%s.shtml", page);
		} else if ("jingchu_tianxia".equals(jobid)) {
			return String.format("http://photo.cnhubei.com/qt/%s.shtml", page);
		} else if ("jingchu_fengguang".equals(jobid)) {
			return String.format("http://photo.cnhubei.com/fg/%s.shtml", page);
		} else
			return null;
	}

	private List<PictureSet> parsePictureSet(String html) {
		if (html == null) {
			return new ArrayList<PictureSet>();
		}
		List<PictureSet> lst = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements es = doc.select("ul.piclist.c-lists li p a");
		for (Element e : es) {
			PictureSet ps = new PictureSet();
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			ps.setUrl(e.attr("href"));
			if (StringUtils.isNotBlank(ps.getUrl()))
				lst.add(ps);
		}
		return lst;
	}

	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet,int limit) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		String indexHtml = ClawerPicUtil.getHtmlByUrl(getIndexUrl(), CHAR_SET);
		Document doc = Jsoup.parse(indexHtml);

		int last = getPageNum(doc);

		try {
			t1: for (int i = 1; i <= last; i++) {
				String url = getPictureSetUrl(i);
				String html = null;
				if (i > 1) {
					html = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
				} else {
					html = indexHtml;
				}
				//				System.out.println(html);
				List<PictureSet> list = parsePictureSet(html);
				logger.info("{} job getPictureSet from {} return:{}", jobid, url, list.size());
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
		return getNewPictureSet(lastPictureSet,0);
	}

	private List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);
		List<Picture> list = new ArrayList<Picture>();
		Elements element = doc.getElementsByClass("gall-pageNumber");
		if(element.size()>0){
			String pagesum = element.text().trim().split("/")[1].trim();
			String summary = doc.select("p.pic-title-summary").last().text().replace("[收起]", "").trim();
			ps.setSummary(ClawerPicUtil.removeBlankText(summary));
			String lastDes=null;
			for (int i = 1; i <= Integer.parseInt(pagesum); i++) {
				String url0 = null;
				if (i > 1) {
					String source = ps.getUrl();
					String[] array = source.split("\\.shtml");
					url0 = array[0] + "_" + i + ".shtml";
					html = ClawerPicUtil.getHtmlByUrl(url0, CHAR_SET);
				}
				if(StringUtils.isNotBlank(html)){
					Document temp = Jsoup.parse(html);
					Element one = temp.getElementById("photo");
					Picture p = new Picture();
					p.setUrl(one.attr("src"));
					String des=one.attr("alt");
					if (des.length() > 0) {
						des = ClawerPicUtil.removeBlankText(des);
						lastDes = des;
					} else {
						des = lastDes;
					}
					p.setDescription(des);
					list.add(p);
				}
			}
		}
		return list;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHAR_SET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	public static void main(String[] args) {

//		PictureSet p = new PictureSet();
//		p.setUrl("http://photo.cnhubei.com/2013/0320/53189.shtml");
//		List<Picture> list = new Jingchu().getPictureList(p);
//		for (Picture pic : list) {
//			System.out.println(pic.getUrl() + " " + pic.getDescription());
//		}
		
//		Jingchu j=new Jingchu();
//		j.setJobid("jingchu_guoji");
//		List<PictureSet> lst= j.getNewPictureSet(null);
//		System.out.println(lst.size());
//		int i=0;
//		for(PictureSet p:lst){
//			System.out.println(p.getTitle()+"\t"+p.getUrl());
//			i++;
//			if(i>=3)
//				break;
//		}
		
		Jingchu n = new Jingchu();
//								n.setJobid("jingchu_guoji");
//								n.setJobid("jingchu_tianxia");
								n.setJobid("jingchu_fengguang");
//		if ("jingchu_guoji".equals(jobid)) {
//			return String.format("http://photo.cnhubei.com/gj/%s.shtml", page);
//		} else if ("jingchu_tianxia".equals(jobid)) {
//			return String.format("http://photo.cnhubei.com/qt/%s.shtml", page);
//		} else if ("jingchu_fengguang".equals(jobid)) {
//			return String.format("http://photo.cnhubei.com/fg/%s.shtml", page);
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
