package com.netease.gather.clawerpic.parser.tour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//名城新闻网-图片频道列表
public class Mingcheng extends AbstractParser{
	
	private static final String PAGE_CHARSET = "gb2312";
	private static final String DOMAIN_PRIFIX = "http://news.2500sz.com";
	private static final String DOMAIN = DOMAIN_PRIFIX+ "/tppd/tppd.shtml";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		StringReader reader = new StringReader(html);
		BufferedReader br=new BufferedReader(reader);
		String temp0="";
		String d = "";
		try {
			temp0=br.readLine();
			while(temp0!=null){
				if(!temp0.contains("var source =")){
					temp0=br.readLine();
					continue;
				}else{
					d = temp0.substring("var source =".length()).trim();
					break;
				}
			}
			List<PictureSet> ret0 = new ArrayList<PictureSet>();
			Elements divones = doc.getElementById("container").select("div.cell");
			for(Element one : divones){
				Element temp = one.getElementsByTag("a").first();
				String title = one.getElementsByTag("h3").text().trim();
				PictureSet set = new PictureSet();
				set.setUrl(DOMAIN_PRIFIX + temp.attr("href"));
				set.setTitle(title);
				logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
				ret0.add(set);
			}
			
			Node[] nodes = JsonUtil.fromJson(d, Node[].class);
			for(Node one : nodes){
				PictureSet set = new PictureSet();
				set.setUrl(DOMAIN_PRIFIX + one.getHref());
				set.setTitle(one.getTitle().trim());
				set.setSummary(one.getInfo().trim());
				logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
				ret0.add(set);
			}
			if (lastPictureSet == null) {
				ret.addAll(ret0);
			} else {
				for (PictureSet ps : ret0) {
					if (ps.getUrl().equals(lastPictureSet.getUrl())) {
						break;
					}
					ret.add(ps);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return ret;
	}
	
	static class Node{
		
		private String href;
		private String title;
		private String src;
		private String info;
		
		public String getSrc() {
			return src;
		}
		public void setSrc(String src) {
			this.src = src;
		}
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		public String getHref() {
			return href;
		}
		public void setHref(String href) {
			this.href = href;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
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
		Elements elements = doc.getElementsByClass("pictitle");
		String[] pagesum = elements.select("span").text().replace("(", "").replace(")", "").trim().split("/")[1].trim().split(" ");
		String summary = "";
		for (int i = 1 ; i <= Integer.parseInt(pagesum[0]) ; i++) {
			try{
				String url0 = null;
				if(i > 1){
					String source = ps.getUrl();
					String[] array = source.split("\\.shtml");
					url0 = array[0]+"_"+i+".shtml";
				}
				String url = ( i == 1 ? ps.getUrl() : url0);
				String content = ClawerPicUtil.getHtmlByUrl(url,PAGE_CHARSET);
				Picture p = new Picture();
				Document temp = Jsoup.parse(content);
				Element one = temp.getElementsByClass("con").first();
				Element pic = temp.getElementsByClass("con").first().getElementsByTag("img").first();
				final String picurl = pic.attr("src");
				if(picurl.contains("news.2500sz.com")){
					p.setUrl(picurl);
				}else{
					if(picurl.contains("http://")){
						p.setUrl(picurl);
					}else 
						p.setUrl(DOMAIN_PRIFIX + picurl);
				}
				p.setDescription(ClawerPicUtil.removeBlankText(one.text().replace("/\\{(.*)\\}/", "")));
				if(i==1){
					summary = p.getDescription();
					ps.setSummary(summary);
				}
				logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary());
				list.add(p);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return list;
	}
	
}
