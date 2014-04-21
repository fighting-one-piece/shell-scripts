package com.netease.gather.clawerpic.parser.tour;

import java.util.ArrayList;
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

public class NeteaseBlog extends AbstractParser {

	private String charSet="GBK";

	private String getPictureSetUrl(int page) {
		if (page == 1) {
			return "http://blog.163.com/special/001264EF/sec-page-travel.html";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("http://blog.163.com/special/001264EF/sec-data-travel");
			if (page > 1) {
				sb.append("_");
				if (page < 10) {
					sb.append("0");
				}
				sb.append(page);
			}
			sb.append(".html");
			return sb.toString();
		}
	}

	private List<PictureSet> parsePictureSet(String html) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		Document doc = Jsoup.parse(html);
		Elements info = doc.select("li.left.news-item div.news-item-innner h2 a");
		for (Element e : info) {
			PictureSet ps = new PictureSet();
			ps.setUrl(e.attr("href"));
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			if (StringUtils.isNotBlank(ps.getUrl()))
				ret.add(ps);
		}
		return ret;
	}

	private List<PictureSet> parsePictureSetTop(String html) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		Document doc = Jsoup.parse(html);
		Elements head = doc.select("li.head-title.left div.head-title-text-box h2 a");
		if (head.size() > 0) {
			PictureSet ps = new PictureSet();
			ps.setUrl(head.attr("href"));
			ps.setTitle(ClawerPicUtil.removeBlankText(head.text()));
			if (StringUtils.isNotBlank(ps.getUrl()))
				ret.add(ps);
		}

		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		try {
			List<PictureSet> topList = null;
			t1: for (int i = 1; i <= 20; i++) {
				String url = getPictureSetUrl(i);

				String html = ClawerPicUtil.getHtmlByUrl(url, charSet);
				//				System.out.println(html);
				List<PictureSet> list = parsePictureSet(html);
				if (i == 1) {
					topList = parsePictureSetTop(html);
				}
				logger.info("{} job getPictureSet from {} return:{}", jobid, url, list.size());
				if (lastPictureSet == null) {
					ret.addAll(list);
				} else {
					for (PictureSet ps : list) {
						if (ps.getUrl().equals(lastPictureSet.getUrl())) {
							break t1;
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

	private List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);

		Elements meta = doc.select("meta[name=author]");
		String author = meta.attr("content");
		author = author.substring(author.lastIndexOf(",") + 1);
		ps.setAuthor(ClawerPicUtil.removeBlankText(author.trim()));

		Elements title = doc.select("div.multicnt span.tcnt");
		ps.setTitle(ClawerPicUtil.removeBlankText(title.text()));

		Elements body = doc.select("div.nbw-blog-start + div");
		ps.setSummary(ClawerPicUtil.removeBlankText(body.text()));

		List<Picture> list = new ArrayList<Picture>();
		Elements elements = doc.select("div.nbw-blog-start + div img");
		String lastprev = "";
		String lastnext = "";
		for (Element img : elements) {
			String prev = ClawerPicUtil.getPreviousString(img, body.first());
			String next = ClawerPicUtil.getNextString(img, body.first());
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
			}
		}
		return list;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		try {
			String url = ps.getUrl();

			String html = ClawerPicUtil.getHtmlByUrl(url, charSet);
			//			System.out.println(html);
			List<Picture> list = parsePictureList(html, ps);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage() + " when url is " + ps.getUrl(), e);
		} 

		return null;
	}

	public static void main(String[] args) {
		PictureSet p = new PictureSet();
		//		p.setUrl("http://xixilvyouxianche.blog.163.com/blog/static/7257602014022011513/");
		//		p.setUrl("http://xlynn.card.blog.163.com/blog/static/71958002201402211305552/");
		//		p.setUrl("http://roamgun.blog.163.com/blog/static/19433827620136493755707/");
		//		p.setUrl("http://blog.163.com/gxx_x/blog/static/119455254201342301021146/");
		//		p.setUrl("http://hai20pan.blog.163.com/blog/static/21203300720136510640147/");
		p.setUrl("http://beihan116.blog.163.com/blog/static/34943282013102291819428/");
		//				List<PictureSet> list = new NeteaseBlog().getNewPictureSet(p);
		//		System.out.println(list.size());
		//		for (PictureSet ps : list) {
		//			//		 System.out.println(list.get(0).getUrl()+" "+list.get(0).getTitle());
		//			//		 System.out.println(list.get(list.size()-1).getUrl()+" "+list.get(list.size()-1).getTitle());
		//			System.out.println(ps.getUrl() + " " + ps.getTitle() + " " + ps.getAuthor());
		//		}

		List<Picture> list = new NeteaseBlog().getPictureList(p);
		System.out.println(p.getSummary());
		int i = 1;
		if (list != null) {
			for (Picture pic : list) {
				System.out.println(">" + (i++));
				System.out.println(pic.getUrl() + "\n" + pic.getDescription());
			}
		}
	}

}
