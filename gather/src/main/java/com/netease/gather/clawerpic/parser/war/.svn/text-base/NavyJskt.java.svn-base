package com.netease.gather.clawerpic.parser.war;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
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

//中国海军 http://navy.81.cn/jskt.htm
public class NavyJskt extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://navy.81.cn/jskt.htm";
	
	/**图集列表坐标*/
	private static final String CONTENT_POS = "div.conleft .content li a";
	
	/**下一页坐标*/
	private static final String NEXT_PAGE_POS = "#displaypagenum .change";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		List<PictureSet> setList = new ArrayList<PictureSet>();
		
		Result result = new Result();
		result.setNextPage(DOMAIN);		
		while(result.isContinue_flag()){			
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
	
	//获得某页的图集列表
	private Result getPicSetOnePage(String url, PictureSet lastPictureSet){
		Result result = new Result();
		
		String html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
		Document doc = Jsoup.parse(html);
		Elements list = doc.select(CONTENT_POS);
				
		PictureSet pictureSet = null;
		String setUrl;
		String domain = URLUtil.getDomain(url);
		for(Element one : list){	
			setUrl = StringUtil.trim(one.attr("href"));
			if(lastPictureSet != null && lastPictureSet.getUrl() != null){
				if(lastPictureSet.getUrl().equals(setUrl)){
					result.setContinue_flag(false);					
					return result;
				}
			}			
			
			pictureSet = new PictureSet();
			pictureSet.setUrl(domain + setUrl);;
			pictureSet.setTitle(StringUtil.trim(one.text()));
			result.getPictureSetList().add(pictureSet);
		}
		
		String nextUrl = parseNextPageUrl(doc);
		if(nextUrl == null){
			result.setContinue_flag(false);			
			return result;
		}
		
		result.setNextPage(nextUrl);
		
		return result;
	}
	
	private String parseNextPageUrl(Document doc){
		Elements pages = doc.select(NEXT_PAGE_POS);
				
		for(Element page : pages){			
			if("下一页".equals(StringUtil.trim(page.text()))){
				return DOMAIN.substring(0, DOMAIN.lastIndexOf("/") + 1) + StringUtil.trim(page.attr("href"));
			}
		}
		return null;
	}

	protected List<Picture> parsePictureList(String html, PictureSet ps) {
		Document doc = Jsoup.parse(html);
		List<Picture> result = new ArrayList<Picture>();
		
		String domain = URLUtil.getDomain(ps.getUrl());
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
			Element image = doc.select("img.pic").first();
			if(image == null){
				
				continue;
			}
			imageUrl = image.attr("src");
			imageUrl = domain + imageUrl.replace("../", "");
			p = new Picture();
			p.setUrl(imageUrl);
			p.setDescription(image.attr("title"));
			result.add(p);
		}
		return result;
	}
	
	private int getPageNum(Document doc){
		Elements pages = doc.select("#displaypagenum .change");
		
		String href = null;
		for(Element page : pages){
			if("尾页".equals(page.text())){
				href = StringUtil.trim(page.attr("href"));
				StringUtil.trim(page.attr("href")).split("");
				return Integer.parseInt(href.substring(href.lastIndexOf("_")+1, href.lastIndexOf("."))) - 1;
			}
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		
		List<PictureSet> list1 = new NavyJskt().getNewPictureSet(null);
		System.out.println(list1.size());
		for(int i = 0 ; i < list1.size() ; i++){
			PictureSet ps = new PictureSet();
			
			System.out.println(list1.get(i).getTitle() + "------------" + list1.get(i).getUrl());
			ps.setUrl(list1.get(i).getUrl());
			List<Picture> list = new NavyJskt().getPictureList(ps);
			for(Picture p : list){
				System.out.println(p.getUrl() + ":" + p.getDescription());
			}
			
		}
		
		String html = ClawerPicUtil.getHtmlByUrl("http://navy.81.cn/content/2014-01/24/content_5749142.htm", PAGE_CHARSET);	
		Document doc = Jsoup.parse(html);
		Element pages = doc.select(".picShow img").first();
		System.out.println(pages.attr("src"));
		
		PictureSet ps = new PictureSet();
		
		//System.out.println(list1.get(i).getTitle() + "------------" + list1.get(i).getUrl());
		ps.setUrl("http://navy.81.cn/content/2014-02/21/content_5776985.htm");
		List<Picture> list = new NavyJskt().getPictureList(ps);
		for(Picture p : list){
			System.out.println(p.getUrl() + ":" + p.getDescription());
		}
	}
}
