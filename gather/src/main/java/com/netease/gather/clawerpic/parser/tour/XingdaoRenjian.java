package com.netease.gather.clawerpic.parser.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//星岛环球网-人间万象列表
public class XingdaoRenjian extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://tu.stnn.cc/kan_culture/";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements contents = doc.select("ul.list-point li.item a.title");
		List<PictureSet> ret1 = new ArrayList<PictureSet>();
		for(Element content : contents){
			PictureSet set = new PictureSet();
			set.setUrl(content.attr("href").trim());
			set.setTitle(content.text().trim());
			logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
			ret1.add(set);
		}
		if (lastPictureSet == null) {
			ret.addAll(ret1);
		} else {
			for (PictureSet ps : ret1) {
				if (ps.getUrl().equals(lastPictureSet.getUrl())) {
					return ret;
				}
				ret.add(ps);
			}
		}
		Element pages = doc.select("ul.page a.next").first();
		boolean iscontinue = true;
		while(!pages.attr("href").trim().contains("javascript") && pages.attr("href").trim().contains("http:")){
			List<PictureSet> ret0 = new ArrayList<PictureSet>();
			html =ClawerPicUtil.getHtmlByUrl(pages.attr("href").trim(), PAGE_CHARSET);
			doc = Jsoup.parse(html);
			contents = doc.select("ul.list-point li.item a.title");
			for(Element content : contents){
				PictureSet set = new PictureSet();
				set.setUrl(content.attr("href").trim());
				set.setTitle(content.text().trim());
				logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
				ret0.add(set);
			}
			pages = doc.select("ul.page a.next").first();
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
			Elements info = doc.select("p.summary");
			String summary = info.text();
			ps.setSummary(ClawerPicUtil.removeBlankText(summary));
			Pattern urlPattern = Pattern.compile(".*photos\\.push.*(?<=\\()[^\\)]+");
			final String data = html;
			Matcher urlMatcher = urlPattern.matcher(data);
			while (urlMatcher.find()) {
				try{
					String[] array = urlMatcher.group().replace("\\}", "").trim().split("\\{");
					Picture p = new Picture();
					p.setUrl(array[1].split(",")[0].trim().substring(5).replace("'", "").trim());
					p.setDescription(ClawerPicUtil.removeBlankText(unicodeToString(array[1].split(",")[3].trim().replace("'", "").replace("note:", "").replace("}", "").replace("\"", ""))));
					logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary());
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
	
	private static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");    
        }
        return str;
    }
	
}
