package com.netease.gather.clawerpic.parser.war;

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

//凤凰网军事图集
/*http://news.ifeng.com/mil/toppic/list_0/0.shtml
http://news.ifeng.com/mil/chinapic/list_0/0.shtml
http://news.ifeng.com/mil/weapon/list_0/0.shtml
http://news.ifeng.com/mil/pictitle/list_0/0.shtml
*/
public class IfengGuoji extends AbstractParser {

	private static final String CHAR_SET = "UTF-8";

	private String getIndexUrl() {
		return "http://news.ifeng.com/mil/weapon/list_0/0.shtml";
	}

	private List<PictureSet> parsePictureSet(Document doc) {
		Elements es = doc.select("div.comListCon  div.comListBox h2 a");
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
				Elements hrefs = doc.select("div.m_page a");
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

		String title = doc.select("h1#artical_topic").text();
		ps.setTitle(ClawerPicUtil.removeBlankText(title));
		String summary=doc.select("div#main_content").text();
		ps.setSummary(ClawerPicUtil.removeBlankText(summary));
		
		List<Picture> list = new ArrayList<Picture>();
		parsePictureList(doc,list);
		
		Elements otherPages = doc.select("div.pageNum a");
		for(Element another:otherPages){
			String url=another.attr("href");
			doc=ClawerPicUtil.getDocByUrl(url, CHAR_SET);
			parsePictureList(doc,list);
		}
		
		if (list.size() == 0)
			return null;
		return list;
	}
	
	private void parsePictureList(Document doc,List<Picture> list){
		Elements imgs=doc.select("div#main_content img");
		for(Element img:imgs){
			String picUrl=img.attr("src");
			if (StringUtils.isNotBlank(picUrl) && picUrl.indexOf(".gif") == -1) {
				Picture p = new Picture();
				p.setUrl(picUrl);
				p.setDescription(ClawerPicUtil.getNextString(img, doc.select("div#main_content").first()));
				list.add(p);
			}
		}
	}

}
