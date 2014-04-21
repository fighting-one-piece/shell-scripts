package com.netease.gather.clawerpic.parser.war;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawer.Result;
import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.StringUtil;
import com.netease.gather.common.util.URLUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//环球网军事图集
public class HuanqiuMil extends AbstractParser {

	private static final String PAGE_CHARSET = "UTF-8";
	private static final String FIRST_PAGE = "http://mil.huanqiu.com/milpicroll/";

	/** 图集列表坐标 */
	private static final String CONTENT_POS = "ul.picAll li a";
	/**下一页坐标*/
	private static final String NEXT_PAGE_POS = "#pages .a1";

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> setList = new ArrayList<PictureSet>();

		Result result = new Result();
		result.setNextPage(FIRST_PAGE);
		while (result.isContinue_flag()) {
			result = getPicSetOnePage(result.getNextPage(), lastPictureSet);
			setList.addAll(result.getPictureSetList());			
		}

		return setList;
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(),PAGE_CHARSET);
		List<Picture> list = parsePictureList(html, ps);
		return list;
	}

	// 获得某页的图集列表
	public Result getPicSetOnePage(String url, PictureSet lastPictureSet) {
		Result result = new Result();

		String html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
		Document doc = Jsoup.parse(html);
		Elements list = doc.select(CONTENT_POS);

		PictureSet pictureSet = null;
		String setUrl;
		Element one;
		for(int i = 0; i < list.size(); i+=2){
			one = list.get(i);
			setUrl = StringUtil.trim(one.attr("href"));
			if (lastPictureSet != null && lastPictureSet.getUrl() != null) {
				if (lastPictureSet.getUrl().equals(setUrl)) {
					result.setContinue_flag(false);
					return result;
				}
			}

			pictureSet = new PictureSet();
			pictureSet.setUrl(setUrl);
			pictureSet.setTitle(ClawerPicUtil.removeBlankText(one.attr("title")));
			result.getPictureSetList().add(pictureSet);
		}

		String nextUrl = parseNextPageUrl(doc, url);
		if (nextUrl == null) {
			result.setContinue_flag(false);
			return result;
		}

		result.setNextPage(nextUrl);

		return result;
	}

	private String parseNextPageUrl(Document doc, String currentUrl) {
		Elements pages = doc.select(NEXT_PAGE_POS);

		String url;
		for (Element page : pages) {
			if ("下一页".equals(StringUtil.trim(page.text()))) {
				url = StringUtil.trim(page.attr("href"));
				if(!currentUrl.equals(url)){
					return url;
				}
				
			}
		}
		return null;
	}

	protected List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);
		List<Picture> result = new ArrayList<Picture>();
		
		String prefix =  URLUtil.removePage(ps.getUrl());
		String page = URLUtil.getPage(ps.getUrl());
		String pagePrefix = page.substring(0, page.indexOf("."));
		String pageSuffix = page.substring(page.indexOf("."), page.length());
		int pageLength = getPageNum(doc);
		String url, imageUrl;
		Picture p;
		for(int i = 1; i <= pageLength; i++){
			if( i == 1){
				url = ps.getUrl();
			}else{
				url = prefix + pagePrefix + "_" + i + pageSuffix;
				doc = Jsoup.parse(ClawerPicUtil.getHtmlByUrl(url,  PAGE_CHARSET));
			}
			Element image = doc.select("#d_BigPic img").first();
			if(image == null){				
				continue;
			}
			imageUrl = image.attr("src");
			p = new Picture();
			p.setUrl(imageUrl);
			p.setDescription(ClawerPicUtil.removeBlankText(doc.getElementById("efpTxt").text()));
			result.add(p);
			
			if(StringUtils.isBlank(ps.getSummary())){
				Element se = doc.getElementsByClass("conText").first();
				if(se != null && se.text() != null){
					ps.setSummary(ClawerPicUtil.removeBlankText(se.text().replace("图集详情：", "")));
				}
			}
		}
		return result;
	}
	
	private int getPageNum(Document doc){
		Element page = doc.select("#d_picTit span").first();
		if(page == null){
			return 0;
		}
		String url = page.text();
		return Integer.parseInt(url.substring(url.indexOf("/") + 1, url.indexOf(")")));
	}

	public static void main(String[] args) {
		
		/*PictureSet lastPictureSet = new PictureSet();
		lastPictureSet.setUrl("http://mil.huanqiu.com/photo_china/2014-03/2729025.html");
		

		List<PictureSet> list1 = new HuanqiuMil().getNewPictureSet(null);
		System.out.println(list1.size());
		for (int i = 0; i < list1.size(); i++) {
			if(i < 352){
				continue;
			}
			System.out.println(i);
			PictureSet ps = new PictureSet();
			ps.setUrl(list1.get(i).getUrl());
			
			System.out.println(list1.get(i).getTitle() + "--------------" + list1.get(i).getUrl());
			List<Picture> list = new HuanqiuMil().getPictureList(ps);
			
			for(Picture p : list){
				System.out.println(p.getUrl() + ":" + p.getDescription());
			}
		}
		System.out.println(list1.size());*/
		
		/*String html = ClawerPicUtil.getHtmlByUrl("http://bbs.huanqiu.com/thread-1528429-1-1.html", PAGE_CHARSET);
		Document doc = Jsoup.parse(html);
		*/
		/*Elements es = doc.select("p.mbn img");
		System.out.println(es.size());
		for(Element e : es){
			System.out.println("----------------------------");
			Attributes attrs = e.attributes();
			for(Attribute attr : attrs){
				System.out.println(attr.getKey() + "---" + attr.getValue());
			}
			
		}*/
		
		/*Attributes attrs = doc.getElementById("aimg_888655").attributes();
		for(Attribute attr : attrs){
			System.out.println(attr.getKey() + "---" + attr.getValue());
		}*/
		
		PictureSet ps = new PictureSet();
		ps.setUrl("http://mil.huanqiu.com/photo_china/2014-03/2729073.html");
		
		List<Picture> list = new HuanqiuMil().getPictureList(ps);
		
		for(Picture p : list){
			System.out.println(p.getUrl() + ":" + p.getDescription());
		}
		
	}
}
