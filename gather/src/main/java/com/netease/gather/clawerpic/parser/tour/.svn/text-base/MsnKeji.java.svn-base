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

//MSN图片频道-创意·科技
public class MsnKeji extends AbstractParser{
	
	private static final String PAGE_CHARSET = "gb2312";
	private static final String DOMAIN = "http://msnphoto.eastday.com/TECHNOLOGY/index.html";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements pages = doc.select("div.page").first().getElementsByTag("select").first().getElementsByTag("option");
		int page = -1;
		for(Element page0 : pages){
			String page_ = page0.text();
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
				String url = DOMAIN;
				if(i > 1){
					url = DOMAIN.replace(".html", "") + (i-1) + ".html";
				}
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				List<PictureSet> ret0 = new ArrayList<PictureSet>();
				doc = Jsoup.parse(html);
				Elements ones = doc.select("div.pt6");
				for(Element one : ones){
					PictureSet set = new PictureSet();
					String url0 = one.select("p.pic4").first().getElementsByTag("a").attr("href");
					if(url0.contains("msnphoto.eastday.com")){
						set.setUrl(url0);
						set.setTitle(one.select("a.pix12a7").first().text().replace("·", "").replace("・", ""));
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
		try{
			Elements pages1 = doc.select("div.pages");
			if(pages1 != null && pages1.size() > 0){
				Elements pages = pages1.first().getElementsByTag("a");
				String title = ClawerPicUtil.removeBlankText(doc.select("div.block1").get(0).select("div.title").text());
				if(!"".equalsIgnoreCase(title)){
					ps.setTitle(title);
				}
				int page = -1;
				for(Element page0 : pages){
					String page_ = page0.text();
					int max = 0;
					try{
						max = Integer.parseInt(page_);
						if(max > page) page = max;
					}catch(Exception e){}
				}
				logger.debug("page.size = " + page);
				for(int i = 1; i < page ; i++){
					String url = ps.getUrl();
					if(!url.contains("index.html")) continue;
					if(i > 1) url = ps.getUrl().replace(".html", "") + i + ".html";
					html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
					doc = Jsoup.parse(html);
					try{
						Element element = doc.select("div.block1 div.title1").get(1).nextElementSibling();
						Picture p = new Picture();
						String url0 = element.getElementsByTag("img").first().attr("src");
						p.setUrl(url0);
						Elements elements = doc.select("div.block1 div.tx1");
						p.setDescription(ClawerPicUtil.removeBlankText(elements.first().text().trim()));
						logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
						list.add(p);
					}catch(Exception e){}
				}
			}else{
				Element element = doc.getElementById("demo");
				try{
					String title = ClawerPicUtil.removeBlankText(doc.select("table").get(1).text());
					if(!"".equalsIgnoreCase(title))
						ps.setTitle(title);
				}catch(Exception e){
				}
				if(element != null){
					Elements pics = doc.select("img.fimg");
					Elements ones = doc.getElementsByAttributeValue("style", "display:none;");
					String[] text = ones.text().split("\\[\\!\\-\\-empirenews\\.page\\-\\-\\]");
					List<String> textlist = new ArrayList<String>();
					if(text != null && text.length > 0){
						for(int i = 0 ; i < text.length ; i++){
							if(!"".equalsIgnoreCase(text[i].replace("|", "").trim())){
								textlist.add(text[i].replace("|", "").trim());
							}
						}
					}
					if(pics != null && pics.size() > 0){
						for(int i = 0 ; i < pics.size() ; i++){
							try{
								Element pic = pics.get(i);
								if(pic.attr("src").contains("fj2.eastday.com")){
									Picture p = new Picture();
									p.setUrl(pic.attr("src"));
									String des = "";
									if(i < textlist.size()){
										des = textlist.get(i);
									}
									p.setDescription(ClawerPicUtil.removeBlankText(des));
									logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
									list.add(p);
								}
							}catch(Exception e){
								logger.error(e.getMessage(),e);
							}
						}
					}
					for(Element pic : pics){
						try{
							if(pic.attr("src").contains("fj2.eastday.com")){
								Picture p = new Picture();
								p.setUrl(pic.attr("src"));
								p.setDescription("");
								logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
								list.add(p);
							}
						}catch(Exception e){
							logger.error(e.getMessage(),e);
						}
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
}
