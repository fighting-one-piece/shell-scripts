package com.netease.gather.clawerpic.parser.war;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//凤凰网军事图集
/*http://news.ifeng.com/mil/toppic/list_0/0.shtml
http://news.ifeng.com/mil/chinapic/list_0/0.shtml
http://news.ifeng.com/mil/weapon/list_0/0.shtml
http://news.ifeng.com/mil/pictitle/list_0/0.shtml
*/
public class IfengZhuangbei extends AbstractParser {

	private static final String CHAR_SET = "UTF-8";

	private String getIndexUrl() {
		return "http://news.ifeng.com/mil/toppic/list_0/0.shtml";
	}

	private List<PictureSet> parsePictureSet(Document doc) {
		Elements es = doc.select("div.picList div h3 a");
		List<PictureSet> lst = new ArrayList<PictureSet>();
		for (Element e : es) {
			PictureSet ps = new PictureSet();
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			ps.setUrl(e.attr("href"));
			lst.add(ps);
		}
		return lst;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> ret = new ArrayList<PictureSet>();
		try {
			String url = getIndexUrl();
			int count = 0;
			t1: while (true) {
				if (url == null || count > 40) {
					break;
				}
				String html = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
				//								System.out.println(html);
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

				url = null;
				Elements hrefs = doc.select("div.pageNum a");
				for (Element e : hrefs) {
					if (e.text().contains("下一页") && StringUtils.isNotBlank(e.attr("href"))) {
						url = e.attr("href");
						break;
					}
				}
				count++;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHAR_SET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	private List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);

		String title = doc.select("div.photoMHD div.title div.txt h1").text();
		String num = doc.select("div.photoMHD div.title div.txt h1 span").text();
		title = title.substring(0, title.indexOf(num));
		ps.setTitle(ClawerPicUtil.removeBlankText(title));
		//		num=num.substring(2,num.length()-1);

		List<Picture> list = new ArrayList<Picture>();
		Elements scripts = doc.select("script");
		for (Element script : scripts) {
			if (script.html().contains("var detail={")) {
				//				System.out.println(script.html());
				String code = script.html();
				int p1 = code.indexOf("{");
				int p2 = code.indexOf("};");
				String json = code.substring(p1, p2 + 1);
				try {
					@SuppressWarnings("unchecked")
					Map<String, String> map = JsonUtil.fromJson(json, Map.class);
					if (StringUtils.isNotBlank(map.get("summary"))) {
						ps.setSummary(ClawerPicUtil.removeBlankText(map.get("summary")));
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (script.html().contains("var _listdata= [];")) {
				String code = script.html();
				String[] arr = code.split("_listdata\\[\\d+\\]");
				//				System.out.println(arr.length);
				for (String s : arr) {
					if (s.contains(" = {")) {
						int p1 = s.indexOf("{");
						int p2 = s.indexOf(";");
						String json = s.substring(p1, p2);
						try {
							@SuppressWarnings("unchecked")
							Map<String, String> map = JsonUtil.fromJson(json, Map.class);
							if (StringUtils.isNotBlank(map.get("timg"))) {
								Picture p = new Picture();
								p.setUrl(map.get("timg"));
								p.setDescription(ClawerPicUtil.removeBlankText(map.get("title")));
								list.add(p);
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		if (list.size() == 0)
			return null;
		return list;
	}

	public static void main(String[] args) {
		IfengZhuangbei feng = new IfengZhuangbei();
		feng.setJobid("ifeng_zhuangbeibaodao");
//		List<PictureSet> list1 = feng.getNewPictureSet(null);
//		System.out.println(list1.size());
//		for (int i = 0; i < list1.size(); i++) {
//			System.out.println(list1.get(i).getUrl() + " " + list1.get(i).getTitle());
//			//			PictureSet p = new PictureSet();
//			//			p.setUrl(list1.get(i).getUrl());
//			//			new Ifeng().getPictureList(p);
//		}
		
		PictureSet ps=new PictureSet();
		ps.setUrl("http://news.ifeng.com/mil/bigpicture/detail_2014_02/21/34044360_0.shtml");
		List<Picture> l=feng.getPictureList(ps);
		System.out.println(ps.getTitle());
		System.out.println(ps.getSummary());
		for(Picture p:l){
			System.out.println(p.getUrl());
		}
	}

}
