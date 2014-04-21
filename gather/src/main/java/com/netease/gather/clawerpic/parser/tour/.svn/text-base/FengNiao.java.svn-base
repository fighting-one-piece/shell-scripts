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

//蜂鸟网
public class FengNiao extends AbstractParser{
	
	private static final String PAGE_CHARSET = "GBK";
	private static final String FIRST_PAGE = "http://travel.fengniao.com/list_1607.html";

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		List<PictureSet> ret = new ArrayList<PictureSet>();
		String html = ClawerPicUtil.getHtmlByUrl(FIRST_PAGE ,PAGE_CHARSET);
		Document doc = Jsoup.parse(html);
		String pageCount = doc.getElementsByClass("next").get(0).previousElementSibling().text();
		boolean iscontinue = true;
		for(int i = 1 ; i <= Integer.parseInt(pageCount) ; i++){
			String url = getPictureSetUrl(i);
			html = ClawerPicUtil.getHtmlByUrl(url ,PAGE_CHARSET);
			List<PictureSet> temp = new ArrayList<PictureSet>();
			temp = parsePictureSet(html);
			if (lastPictureSet == null) {
				ret.addAll(temp);
			} else {
				for (PictureSet ps : temp) {
					if (ps.getUrl().equals(lastPictureSet.getUrl())) {
						iscontinue = false;
						break;
					}
					ret.add(ps);
				}
			}
			if(!iscontinue) break;
		}
		return ret;
	}
	
	private List<PictureSet> parsePictureSet(String html) {
		
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Element info = doc.getElementsByClass("list_news").get(0);
		if(info != null){
			List<Element> dtElements = info.getElementsByTag("dt");
			for(int i = 0 ; i < dtElements.size() ; i++){
				PictureSet ps = new PictureSet();
				Element a = dtElements.get(i).getElementsByTag("a").get(0);
				//String date = dtElements.get(i).select("span.date").text().trim();
				ps.setUrl(a.attr("href"));
				ps.setTitle(a.text().trim());
				logger.debug("Url = " + ps.getUrl() + ", Title = " + ps.getTitle());
				ret.add(ps);
			}
		}
		return ret;
	}
	
	private String getPictureSetUrl(int page) {
		
		if (page == 1) {
			return FIRST_PAGE;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("http://travel.fengniao.com/list_1607_");
			sb.append(page);
			sb.append(".html");
			return sb.toString();
		}
	}
	
	protected List<Picture> parsePictureList(String html, PictureSet ps) {
		
		Document doc = Jsoup.parse(html);
		Elements info = doc.getElementsByClass("tu_nr");
		String summary = info.text().trim();
		ps.setSummary(ClawerPicUtil.removeBlankText(summary));
		List<Picture> list = new ArrayList<Picture>();
		Elements elements = doc.getElementsByClass("page_ul");
		try{
			Elements ones = elements.select("li");
			if(ones == null || ones.size() == 0){
				/*info = doc.getElementsByClass("article");
				summary = info.get(0).text().trim();
				if (summary.length() > MAX_PICSET_LABEL_LIMIT) {
					summary = summary.substring(0, MAX_PICSET_LABEL_LIMIT - 3) + "...";
				}
				ps.setSummary(summary);
				Elements imgs = info.get(0).getElementsByTag("img");
				Element pagenum = doc.getElementById("next_page");
				for(int j = 0 ; j < imgs.size() ; j++){
					String source = ps.getUrl();
					String[] t = source.split("/");
					String url = "http://" + t[2] + "/" + t[3] + "/pic_" + t[4].replace(".html", "") + "_" + (j+1) + ".html";
					String content = super.getPictureList0(ps,url,PAGE_CHARSET);
					doc = Jsoup.parse(content);
					String href = doc.getElementById("big_img").attr("src");
					if(href != null){
						Picture p = new Picture();
						p.setUrl(href.trim());
						p.setDescription("");
						logger.info("picUrl = " + p.getUrl());
						list.add(p);
					}
				}
				while(true){
					if(pagenum != null){
						String url = pagenum.attr("href");
						String content = super.getPictureList0(ps,url,PAGE_CHARSET);
						doc = Jsoup.parse(content);
						info = doc.getElementsByClass("article");
						imgs = info.get(0).getElementsByTag("img");
						pagenum = doc.getElementById("next_page");
						for(int j = 0 ; j < imgs.size() ; j++){
							String source = ps.getUrl();
							String[] t = source.split("/");
							url = "http://" + t[2] + "/" + t[3] + "/pic_" + t[4].replace(".html", "") + "_" + (j+1) + ".html";
							content = super.getPictureList0(ps,url,PAGE_CHARSET);
							doc = Jsoup.parse(content);
							String href = doc.getElementById("big_img").attr("src");
							if(href != null){
								Picture p = new Picture();
								p.setUrl(href.trim());
								p.setDescription("");
								logger.info("picUrl = " + p.getUrl());
								list.add(p);
							}
						}
						
					}else{
						break;
					}
				}*/
			}else{
				String[] pagesum = elements.select("li").get(3).text().split("/");
				for (int i = 1 ; i <= Integer.parseInt(pagesum[1]) ; i++) {
					try{
						String source = ps.getUrl();
						String[] array = source.split("_");
						String[] temp0 = array[1].split("\\.");
						String url = array[0]+"_"+i+"."+temp0[1];
						String content = ClawerPicUtil.getHtmlByUrl(url,PAGE_CHARSET);
						Picture p = new Picture();
						Document temp = Jsoup.parse(content);
						p.setUrl(temp.getElementById("mainPic").attr("src").trim());
						p.setDescription(ClawerPicUtil.removeBlankText(temp.getElementById("mainPic").attr("alt").trim()));
						list.add(p);
					}catch(Exception e){
						logger.error(e.getMessage(), e);
					}
				}
			}
			return list;
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(),PAGE_CHARSET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}
	
}
