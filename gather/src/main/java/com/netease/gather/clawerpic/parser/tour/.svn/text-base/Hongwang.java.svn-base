package com.netease.gather.clawerpic.parser.tour;

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

//红网-图片栏目 - 纪实
public class Hongwang extends AbstractParser{
	
	private static final String PAGE_CHARSET = "gb2312";
	private static final String URL_PREFIX = "http://photo.rednet.cn/";
	private static final String DOMAIN = URL_PREFIX + "space.php?do=viewcate&id=2";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements pages = doc.select("div.page").first().getElementsByTag("a");
		int page = -1;
		for(Element page0 : pages){
			String page_ = page0.attr("href");
			String[] label = page_.split("=");
			String a = label[label.length - 1];
			int max = 0;
			try{
				max = Integer.parseInt(a);
				if(max > page) page = max;
			}catch(Exception e){}
		}
		logger.debug("page.size = " + page);
		if(page > 0){
			boolean iscontinue = true;
			for(int i = 1 ; i <= page; i++){
				String url = DOMAIN + "&page=" + i;
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				List<PictureSet> ret0 = new ArrayList<PictureSet>();
				doc = Jsoup.parse(html);
				Elements ones = doc.select("table.photo_list").first().getElementsByTag("a");
				for(Element one : ones){
					PictureSet set = new PictureSet();
					String url0 = one.attr("href").trim();
					if(url0.contains("space.php?uid")){
						set.setUrl(URL_PREFIX + url0);
						logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
						ret0.add(set);
					}
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
		Elements elements = doc.getElementsByClass("h_status");
		try{
			if(doc.select("div.c_header") != null){
				String title = doc.select("div.c_header").first().getElementsByTag("p").text().split("-")[1].trim();
				ps.setTitle(title);
				String[] pagesum = elements.text().trim().split("\\|");
				String summary = "";
				String pagesum0 = pagesum[pagesum.length - 1];
				String str2 = "";
				for(int i=0;i<pagesum0.length();i++){
					if(pagesum0.charAt(i)>=48 && pagesum0.charAt(i)<=57){
						str2 += pagesum0.charAt(i);
					}
				}
				for (int i = 1 ; i <= Integer.parseInt(str2) ; i++) {
					try{
						Elements ones = doc.getElementsByClass("yinfo");
						Element one = ones.get(0).getElementsByTag("p").get(0);
						Picture p = new Picture();
						p.setDescription(ClawerPicUtil.removeBlankText(one.text().trim()));
						p.setUrl(URL_PREFIX + doc.getElementById("pic").attr("src"));
						String nextUrl = URL_PREFIX + doc.getElementById("nextlink").attr("href").trim();
						String content = ClawerPicUtil.getHtmlByUrl(nextUrl,PAGE_CHARSET);
						logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
						list.add(p);
						doc = Jsoup.parse(content);
						if(i==1) summary = p.getDescription();
					}catch(Exception e){
						logger.error(e.getMessage(),e);
					}
				}
				ps.setSummary(summary);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
}
