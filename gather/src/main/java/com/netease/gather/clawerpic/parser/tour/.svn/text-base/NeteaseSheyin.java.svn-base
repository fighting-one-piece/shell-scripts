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

public class NeteaseSheyin extends AbstractParser {

	private String charSet="GBK";
	
	private String getPictureSetUrl(int page) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://photo.163.com/share/dwr/call/plaincall/PictureSetBean.getPictureSetRecommendListByDirId.dwr?callCount=1&scriptSessionId=%24%7BscriptSessionId%7D187&c0-scriptName=PictureSetBean&c0-methodName=getPictureSetRecommendListByDirId&c0-id=0&c0-param0=number%3A41&c0-param1=number%3A");
		sb.append((page - 1) * 20);
		sb.append("&c0-param2=number%3A20&batchId=");
		sb.append((int) Math.random() * 1000000);
		return sb.toString();
	}

	private String getPropertyValue(String script, String propertyName) {
		int p1 = script.indexOf(propertyName);
		String leave = script.substring(p1);
		p1 = leave.indexOf("=");
		int p2 = leave.indexOf(";");
		if (p2 >= p1 + 1) {
			leave = leave.substring(p1 + 1, p2);
			leave = leave.replaceAll("\"(.*?)\"", "$1");
			return leave;
		}
		return "";
	}

	private List<PictureSet> parseJs(String script) {
		List<PictureSet> ret = new ArrayList<PictureSet>();
		for (int i = 0; i < 20; i++) {
			if (script.contains("s" + i + "={}")) {
				PictureSet ps = new PictureSet();
				String domainName = getPropertyValue(script, "s" + i + ".domainName");
				String id = getPropertyValue(script, "s" + i + ".id");
				String url = String.format("http://pp.163.com/%s/pp/%s.html", domainName, id);
				ps.setUrl(url);
				String title = getPropertyValue(script, "s" + i + ".name");
				title = ClawerPicUtil.getByUnicode(title);
				ps.setTitle(title);
				String author = getPropertyValue(script, "s" + i + ".nname");
				author = ClawerPicUtil.getByUnicode(author);
				ps.setAuthor(author);
				ret.add(ps);
			}
		}
		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		try {
			t1: for (int i = 1; i <= 25; i++) {
				String url = getPictureSetUrl(i);

				String js = ClawerPicUtil.getHtmlByUrl(url, charSet);
				List<PictureSet> list = parseJs(js);
				logger.info("{} job getPictureSet from {} return:{}",jobid,url,list.size());
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
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return ret;
	}

	private List<Picture> parseHtml(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);

		Elements info = doc.select("div.g-mainwraper.g-mainwraper-picsetinfo");
		ps.setSummary(ClawerPicUtil.removeBlankText(info.text()));

		List<Picture> list = new ArrayList<Picture>();
		Elements elements = doc.select("div.main-area");
		for (Element div : elements) {
			Picture p = new Picture();
			p.setUrl(div.select("img").attr("data-lazyload-src"));
			p.setDescription(div.select("p.pic-description.z-tag").text());
			list.add(p);
		}
		return list;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		try {
			String url = ps.getUrl();

			String html = ClawerPicUtil.getHtmlByUrl(url, charSet);

			List<Picture> list = parseHtml(html, ps);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 

		return null;
	}

	public static void main(String[] args) {
		PictureSet p = new PictureSet();
		//		p.setUrl("http://pp.163.com/wurenshejiphoto/pp/11706009.html");
		p.setUrl("http://pp.163.com/sunshine/pp/11706011.html");
		//		List<PictureSet> list = new NeteaseSheyin().getNewPictureSet(null);
		//		System.out.println(list.size());
		//		for (PictureSet ps : list) {
		//			//		 System.out.println(list.get(0).getUrl()+" "+list.get(0).getTitle());
		//			//		 System.out.println(list.get(list.size()-1).getUrl()+" "+list.get(list.size()-1).getTitle());
		//			System.out.println(ps.getUrl() + " " + ps.getTitle() + " " + ps.getAuthor());
		//		}

		List<Picture> list = new NeteaseSheyin().getPictureList(p);
				for(Picture pic:list){
					System.out.println(pic.getUrl()+" "+pic.getDescription());
				}
	}

}
