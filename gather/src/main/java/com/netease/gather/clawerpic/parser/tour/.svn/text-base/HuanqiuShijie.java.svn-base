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

//环球网-图说世界列表
public class HuanqiuShijie extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://photo.huanqiu.com/gallery/";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Element page = doc.getElementById("pages");
		Elements ahref = page.getElementsByTag("a");
		Integer pagenum = -1;
		for(int i = ahref.size()-1 ; i >= 0 ; i--){
			String page0 = ahref.get(i).text().trim();
			try{pagenum = Integer.parseInt(page0);}catch(Exception e){}
			if(pagenum > 0){break;}
		}
		String url = "";
		boolean iscontinue = true;
		for(int i = 1 ; i <= pagenum ; i++){
			if(i == 1){
				url = DOMAIN;
			}else{
				url = DOMAIN + i + ".html";
			}
			List<PictureSet> ret0 = new ArrayList<PictureSet>();
			html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
			doc = Jsoup.parse(html);
			Element ul = doc.getElementsByClass("picAll").get(0);
			Elements ones = ul.getElementsByTag("li");
			for(Element one : ones){
				Element a = one.getElementsByClass("txt").get(0);
				PictureSet set = new PictureSet();
				set.setUrl(a.attr("href"));
				set.setTitle(a.text().trim());
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
		return ret;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(),PAGE_CHARSET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	protected List<Picture> parsePictureList(String html, PictureSet ps) {
		
		List<Picture> list = new ArrayList<Picture>();
		try{
			Document doc = Jsoup.parse(html);
			Elements info = doc.getElementsByClass("conText");
			String summary = info.text().replace("图集详情：", "").trim();
			ps.setSummary(ClawerPicUtil.removeBlankText(summary));
			Element element = doc.getElementById("d_picTit").getElementsByTag("span").first();
			String[] pagesum = element.text().split("/");
			for (int i = 1 ; i <= Integer.parseInt(pagesum[1].replace(")", "")) ; i++) {
				String url0 = null;
				if(i > 1){
					String source = ps.getUrl();
					String[] array = source.split("\\.html");
					url0 = array[0]+"_"+i+".html";
				}
				String url = ( i == 1 ? ps.getUrl() : url0);
				String content = ClawerPicUtil.getHtmlByUrl(url,PAGE_CHARSET);
				Picture p = new Picture();
				Document temp = Jsoup.parse(content);
				p.setUrl(temp.getElementById("d_BigPic").select("img").attr("src").trim());
				p.setDescription(ClawerPicUtil.removeBlankText(temp.getElementById("efpTxt").text().trim()));
				logger.debug(p.getUrl() + " ," + p.getDescription().trim() + "  ," + ps.getUrl() + " ," + ps.getSummary());
				list.add(p);
			}
		}catch(Exception e){
			logger.error(e.getMessage()+" when ps.getUrl() = " + ps.getUrl());
		}
		return list;
	}
	
}
