package com.netease.gather.clawerpic.parser.tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.Html2Text;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//中华网-帖子列表
public class China extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN_PRIFIX = "http://club.china.com";
	private static final String DOMAIN = DOMAIN_PRIFIX + "/data/threads/3316/1.html";
	private SimpleDateFormat ptimeFormat = new SimpleDateFormat("MM-dd HH:mm");
	
	private Date parseTime(String text) {
		Date tmp = null;
		try {
			tmp = ptimeFormat.parse(text);
			Calendar now = Calendar.getInstance();
			int nowYear = now.get(Calendar.YEAR);
			Calendar cal = Calendar.getInstance();
			cal.setTime(tmp);
			cal.set(Calendar.YEAR, nowYear);
			if (cal.compareTo(now) < 0) {
				return cal.getTime();
			} else {
				cal.set(Calendar.YEAR, --nowYear);
				return cal.getTime();
			}
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Element element = doc.select("div.threadBody").first();
		String content = element.text();
		int startsign = content.indexOf("共");
		String content0 = content.substring(startsign);
		int endsign = content0.indexOf("页");
		if(startsign > -1 && endsign > -1){
			try{
			String pageString = content0.substring(1, endsign);	
			for(int i = 1 ; i <= Integer.parseInt(pageString) ; i++){
				String url = DOMAIN;
				if(i > 1) url = DOMAIN.replace("1.html", "") + i + ".html";
				html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
				List<PictureSet> ret0 = new ArrayList<PictureSet>();
				doc = Jsoup.parse(html);
				Elements elements = doc.select("tr td.subject span.mainTitle");
				Elements elementsTime = doc.select("tr td.replyUser span");
				if(elements != null && elementsTime != null && elements.size() == elementsTime.size()){
					for(int j = 0 ; j < elements.size() ; j++){
						Element one = elements.get(j);
						Element oneTime = elementsTime.get(j);
						Element tempa = one.getElementsByTag("a").first();
						String time = oneTime.text().trim();
						String title = tempa.text().trim();
						if(title.contains("(组图)")){
							PictureSet set = new PictureSet();
							set.setUrl(DOMAIN_PRIFIX + tempa.attr("href"));
							set.setTitle(title);
							set.setPtime(parseTime(time));
							logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle() + ", time = " + set.getPtime());
							ret0.add(set);
						}
					}
					if (lastPictureSet == null) {
						ret.addAll(ret0);
					} else {
						for (PictureSet ps : ret0) {
							if (lastPictureSet.getPtime() != null && lastPictureSet.getPtime().compareTo(ps.getPtime()) >= 0) {
								continue;
							}
							ret.add(ps);
						}
					}
				}
			}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
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
			Element element = doc.getElementById("galleryViewList");
			if(element != null){
				Element time = doc.select("div.imgTopicTit h4 span").get(1);
				String ptime = time.text();
				ps.setPtime(DateUtil.stringToDate(ptime, DateUtil.DEFAULT_DATE_FORMAT));
				Elements ones = element.getElementsByTag("li");
				for(Element one : ones){
					try{
						Element one0 = one.getElementsByTag("a").first();
						Element one1 = one.getElementsByTag("img").first();
						if(one0.attr("href").contains("image.club.china.com")){
							Picture p = new Picture();
							p.setUrl(one0.attr("href"));
							p.setDescription(Html2Text.html2Text(one1.attr("name")).trim());
							logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle() + ", ptime = " + ps.getPtime());
							list.add(p);
						}
					}catch(Exception e){
						logger.error(e.getMessage(),e);
					}
				}
			}else{
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
}
