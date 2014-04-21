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

//驴妈妈旅游网-世界奇观
public class LvmamaShijie extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://www.lvmama.com/info/photo/qiguan/";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements pages = doc.select("div.pages").first().getElementsByTag("a");
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
				String url = DOMAIN + i + ".html";
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				List<PictureSet> ret0 = new ArrayList<PictureSet>();
				doc = Jsoup.parse(html);
				Elements ones = doc.getElementById("endText").getElementsByTag("a");
				for(Element one : ones){
					PictureSet set = new PictureSet();
					String url0 = one.attr("href").trim();
					if(url0.contains("www.lvmama.com")){
						set.setUrl(url0);
						set.setTitle(one.attr("title").trim());
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
		Element element = doc.getElementById("cntItem");
		try{
			String pagesum = element.text();
			String summary = "";
			for (int i = 1 ; i <= Integer.parseInt(pagesum) ; i++) {
				String url0 = null;
				try{
					if(i > 1){
						String source = ps.getUrl();
						String[] array = source.split("\\.html");
						url0 = array[0]+"_"+i+".html";
					}
					String url = ( i == 1 ? ps.getUrl() : url0);
					String content = ClawerPicUtil.getHtmlByUrl(url,PAGE_CHARSET);
					Picture p = new Picture();
					Document temp = Jsoup.parse(content);
					Element one = temp.getElementsByClass("fir").select("img").first();
					p.setUrl(one.attr("src"));
					p.setDescription(ClawerPicUtil.removeBlankText(temp.getElementsByClass("left").select("div").get(i).text()));
					logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
					list.add(p);
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
			ps.setSummary(summary);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
}
