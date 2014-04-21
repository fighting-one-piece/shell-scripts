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

//环球网-眼界列表
public class HuanqiuYanjie extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://photo.huanqiu.com/vision/index.html";
	
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
		boolean iscontinue = true;
		for(int j = 1 ; j <= pagenum ;j++){
			List<PictureSet> ret0 = new ArrayList<PictureSet>();
			String url = DOMAIN;
			if(j > 1){
				url = url.replace("index.html", "") + j + ".html";
			}
			html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
			doc = Jsoup.parse(html);
			Element content = doc.getElementsByClass("sightList").get(0);
			Elements ones = content.select("a.right");
			for(Element one : ones){
				PictureSet set = new PictureSet();
				set.setTitle(one.attr("title"));
				url = one.attr("href");
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				doc = Jsoup.parse(html);
				if(!url.contains("index.html")){
					set.setUrl(url);
					logger.debug("set.url = " + set.getUrl() + ", set.title = " + set.getTitle());
					ret0.add(set);
				}else{
					try{
						set.setUrl(doc.getElementsByClass("pic_frontover").get(0).getElementsByTag("a").get(0).attr("href"));
						logger.debug("set.url = " + set.getUrl() + ", set.title = " + set.getTitle());
						ret0.add(set);
					}catch(Exception e){
						logger.error(e.getMessage(),e);
					}
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
				if(content != null && !"".equalsIgnoreCase(content)){
					Picture p = new Picture();
					Document temp = Jsoup.parse(content);
					p.setUrl(temp.getElementById("d_BigPic").select("img").attr("src").trim());
					p.setDescription(ClawerPicUtil.removeBlankText(temp.getElementById("efpTxt").text().trim()));
					logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary());
					list.add(p);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
}
