package com.netease.gather.clawerpic.parser.tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class NeteaseBbs extends AbstractParser {

	private SimpleDateFormat ptimeFormat = new SimpleDateFormat("MM-dd HH:mm");
	private SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String charSet="GBK";

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

	private String getPictureSetUrl(int page) {
		if(jobid.equals("163_bbs_tuyou")){
			String url = String.format("http://bbs.travel.163.com/tlist/tuyou-%s-1-a-a.html", page);
			return url;
		}else if(jobid.equals("163_bbs_yuanchuang")){
			String url = String.format("http://bbs.travel.163.com/tlist/youji-%s-1-a-a.html", page);
			return url;
		}
		return null;
	}

	private List<PictureSet> parsePictureSetTop(String html) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("div.board-list-one");
		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			if (e.select("span.icon-notice").size() > 0 || e.select("span.icon-global-up").size() > 0 || e.select("span.icon-column_up").size() > 0) {
				Elements hrefs = e.select("div.board-list-title-inner span.article-title a");
				if (hrefs.size() > 0) {
					Element href = hrefs.first();
					PictureSet ps = new PictureSet();
					ps.setAtLeast(10);
					String url = href.attr("href");
					if (url.indexOf("/") == 0) {
						url = "http://bbs.travel.163.com" + url;
					}
					ps.setUrl(url);
					ps.setTitle(ClawerPicUtil.removeBlankText(href.attr("title")));

					Elements authors = e.select("div.board-list-write a");
					if (authors.size() > 0) {
						ps.setAuthor(authors.first().text());
					}

					if (StringUtils.isNotBlank(ps.getUrl()))
						ret.add(ps);
				}
			}
		}

		return ret;
	}

	private List<PictureSet> parsePictureSet(String html) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("div.board-list-one");
		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			if (e.select("span.icon-notice").size() == 0 && e.select("span.icon-global-up").size() == 0
					&& e.select("span.icon-column_up").size() == 0) {
				Elements hrefs = e.select("div.board-list-title-inner span.article-title a");
				if (hrefs.size() > 0) {
					Element href = hrefs.first();
					PictureSet ps = new PictureSet();
					ps.setAtLeast(10);
					String url = href.attr("href");
					if (url.indexOf("/") == 0) {
						url = "http://bbs.travel.163.com" + url;
					}
					ps.setUrl(url);
					ps.setTitle(ClawerPicUtil.removeBlankText(href.attr("title")));

					Elements authors = e.select("div.board-list-write a");
					if (authors.size() > 0) {
						ps.setAuthor(authors.first().text());
					}

					Elements times = e.select("div.board-list-write span.date");
					if (times.size() > 0) {
						String time = times.first().text();
						ps.setPtime(parseTime(time));
					}

					if (StringUtils.isNotBlank(ps.getUrl()))
						ret.add(ps);
				}
			}
		}

		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		try {
			List<PictureSet> topList = null;
			for (int i = 1; i <= 5; i++) {
				String url = getPictureSetUrl(i);

				String html = ClawerPicUtil.getHtmlByUrl(url, charSet);
				List<PictureSet> list = parsePictureSet(html);
				if (i == 1) {
					topList = parsePictureSetTop(html);
				}
				logger.info("{} job getPictureSet from {} return:{}", jobid, url, list.size());
				if (lastPictureSet == null) {
					ret.addAll(list);
				} else {
					for (PictureSet ps : list) {
						if (lastPictureSet.getPtime() != null && lastPictureSet.getPtime().compareTo(ps.getPtime()) >= 0) {
							continue;
						}
						ret.add(ps);
					}
				}
			}
			if (topList != null) {
				ret.addAll(topList);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return ret;
	}

	private String removeUnuseText(String text, Element itemBody) {
		Elements modifys = itemBody.select("div.modify_log");
		for (int i = 0; i < modifys.size(); i++) {
			String modifyText = ClawerPicUtil.removeBlankText(modifys.get(i).text());
			if (text.indexOf(modifyText) != -1) {
				int pos = text.indexOf(modifyText);
				text = text.substring(0, pos) + text.substring(pos + modifyText.length());
			} else if (modifyText.contains(text)) {
				text = "";
			}
		}

		Elements tops = itemBody.select("div.cDRed");
		for (int i = 0; i < tops.size(); i++) {
			String topText = ClawerPicUtil.removeBlankText(tops.get(i).text());
			if (text.indexOf(topText) != -1) {
				int pos = text.indexOf(topText);
				text = text.substring(0, pos) + text.substring(pos + topText.length());
			} else if (topText.contains(text)) {
				text = "";
			}
		}

		Elements quotes = itemBody.select("div.quote");
		for (int i = 0; i < quotes.size(); i++) {
			String quoteText = ClawerPicUtil.removeBlankText(quotes.get(i).text());
			if (text.indexOf(quoteText) != -1) {
				int pos = text.indexOf(quoteText);
				text = text.substring(0, pos) + text.substring(pos + quoteText.length());
			} else if (quoteText.contains(text)) {
				text = "";
			}
		}
		return text;
	}
	
	private void parsePictureListInner(String html, PictureSet ps, StringBuilder summary, List<Picture> list) {
		Document doc = Jsoup.parse(html);

		Elements items = doc.select("div.tie-item");
		for (int i = 0; i < items.size(); i++) {
			String lastprev = "";
			String lastnext = "";
			Element item = items.get(i);
			Elements authors = item.select("div.tie-author span.info-name a");
			//			System.out.println(authors.first().text());
			if (authors.size() > 0 && authors.first().text().equals(ps.getAuthor())) {
				//			if (authors.size() > 0 ) {
				Element itemBody = item.select("div.tie-con div.tie-content").first();
				Elements imgs = itemBody.select("img");
				boolean addSummary = false;
				for (Element img : imgs) {
					String prev = ClawerPicUtil.getPreviousString(img, itemBody);
					prev = removeUnuseText(prev, itemBody);
					String next = ClawerPicUtil.getNextString(img, itemBody);
					next = removeUnuseText(next, itemBody);
					if (StringUtils.isNotBlank(prev) || StringUtils.isNotBlank(next)) {
						lastprev = prev;
						lastnext = next;
					}
					if (StringUtils.isBlank(prev) && StringUtils.isBlank(next)) {
						prev = lastprev;
						next = lastnext;
					}

					Picture p = new Picture();
					p.setUrl(img.attr("src"));
					p.setDescription(prev + "<br/>" + next);
					if (StringUtils.isNotBlank(p.getUrl())) {
						list.add(p);
						addSummary = true;
					}
				}
				if (addSummary) {
					String text = ClawerPicUtil.removeBlankText(itemBody.text());
					text = removeUnuseText(text, itemBody);
					summary.append(text);
				}
			}
		}

	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		try {
			String url = ps.getUrl();

			String html = ClawerPicUtil.getHtmlByUrl(url, charSet);

			StringBuilder summary = new StringBuilder();
			List<Picture> list = new ArrayList<Picture>();

			Document doc = Jsoup.parse(html);
			
			String time=doc.select("div.tie-first-item div.tie-con-hd-panel span.time").text();
			if(time.length()>19){
				time=time.substring(time.length()-19);
				try {
					Date ptime=detailFormat.parse(time);
					ps.setPtime(ptime);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			Elements pages = doc.select("div.tie-tab-bottom-nav div.tie-page a");
			if (pages.size() > 0) {
				pages = doc.select("div.tie-tab-bottom-nav div.tie-page").first().select("a");
				for (int i = 0; i < pages.size(); i++) {
					if (pages.get(i).text().matches("\\d+")) {
						String innerurl = pages.get(i).attr("href");
						if (innerurl.indexOf("/") == 0) {
							innerurl = "http://bbs.travel.163.com" + innerurl;
						}

						html = ClawerPicUtil.getHtmlByUrl(innerurl, charSet);

						parsePictureListInner(html, ps, summary, list);
					}
				}
			} else {
				parsePictureListInner(html, ps, summary, list);
			}

			ps.setSummary(summary.toString());

			if (list.size() < 10) {
				return null;
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage() + " when url is " + ps.getUrl(), e);
		} 

		return null;
	}

	public static void main(String[] args) {
		PictureSet p = new PictureSet();
		p.setAuthor("xinlinbian");
		p.setUrl("http://bbs.travel.163.com/bbs/youji/196036735.html");
		//		p.setAuthor("ch19wd72723");
		//		p.setUrl("http://bbs.travel.163.com/bbs/tuyou/363895663.html");
		//						List<PictureSet> list = new NeteaseBbsTuyou().getNewPictureSet(null);
		//				System.out.println(list.size());
		//				for (PictureSet ps : list) {
		//					//		 System.out.println(list.get(0).getUrl()+" "+list.get(0).getTitle());
		//					//		 System.out.println(list.get(list.size()-1).getUrl()+" "+list.get(list.size()-1).getTitle());
		//					System.out.println(ps.getUrl() + " " + ps.getTitle() + " " + ps.getAuthor());
		//				}

		List<Picture> list = new NeteaseBbs().getPictureList(p);
		//		System.out.println(p.getSummary());
		int i = 1;
		if (list != null) {
			for (Picture pic : list) {
				System.out.println((i++)+"\t"+pic.getUrl() + "\t" + pic.getDescription());
			}
		}
	}

}
