package com.netease.gather.clawerpic.parser.aviation;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//民航资源网
public class Carnoc extends AbstractParser{
	
	private static final String PAGE_CHARSET = "gb2312";
	private static final String URL_PREFIX = "http://news.carnoc.com/";
	private static final String DOMAIN = URL_PREFIX + "cache/list/pic_list_3_1.html";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		
		Elements pages = doc.select("div.nextpage a");
		int page = -1;
		for(Element page0 : pages){
			String page_ = page0.text().trim();
			int max = 0;
			try{
				max = Integer.parseInt(page_);
				if(max > page) page = max;
			}catch(Exception e){}
		}
		logger.debug("page.size = " + page);
		if(page > 0){
			boolean iscontinue = true;
			for(int i = 1 ; i <= page; i++){
				String url = URL_PREFIX + "cache/list/pic_list_3_" + i + ".html";
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				List<PictureSet> ret0 = new ArrayList<PictureSet>();
				doc = Jsoup.parse(html);
				Elements ones = doc.select("div.spnewsli li");
				for(Element one : ones){
					PictureSet set = new PictureSet();
					String url0 = one.getElementsByTag("a").attr("href").trim();
					String title = one.getElementsByTag("img").attr("alt").trim();
					set.setUrl(url0);
					set.setTitle(ClawerPicUtil.removeBlankText(title));
					logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
					ret0.add(set);
				}
				if (lastPictureSet == null) {
					ret.addAll(ret0);
				} else {
					for (PictureSet ps : ret0) {
						if (ps.getUrl().equals(lastPictureSet.getUrl())) {
							iscontinue = false;
							break;
						}
						ret.add(ps);
					}
				}
				if(!iscontinue) break;
			}
		}
		return ret;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(),PAGE_CHARSET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	protected List<Picture> parsePictureList(String html, PictureSet ps) {
		
		Document doc = Jsoup.parse(html);
		List<Picture> list = new ArrayList<Picture>();
		Element desc = doc.getElementById("descriptions");
		try{
			if(desc != null && desc.text() != null){
				ps.setSummary(ClawerPicUtil.removeBlankText(desc.text().trim()));
			}
			Elements lis = doc.select("table div.ad-thumbs ul.ad-thumb-list li");
			for (int i = 0 ; i < lis.size() ; i++) {
				try{
					Element li = lis.get(i);
					String url = li.select("a").first().attr("href").trim();
					String des = li.select("img").first().attr("alt").trim();
					Picture p = new Picture();
					p.setDescription(ClawerPicUtil.removeBlankText(des));
					p.setUrl(url);
					logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
					list.add(p);
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
	/*public static void main(String[] args) {
		
		//List<PictureSet> list1 = new Carnoc().getNewPictureSet(null);
		//for(int i = 0 ; i < list1.size() ; i++){
			PictureSet p = new PictureSet();
			p.setUrl("http://news.carnoc.com/list/274/274991.html");
			//p.setUrl("http://news.carnoc.com/list/275/275331.html");
			new Carnoc().getPictureList(p);
		//}
	}*/
}
