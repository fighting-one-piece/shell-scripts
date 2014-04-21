package com.netease.gather.clawerpic.parser.tour;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//战略网-纵览全球列表
public class ZhanlueQuanqiu extends AbstractParser{
	
	private static final String PAGE_CHARSET = "UTF-8";
	private static final String DOMAIN = "http://www.chinaiiss.com/list/58.html";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		String html = ClawerPicUtil.getHtmlByUrl(DOMAIN, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Elements pages = doc.select("div.pages").first().getElementsByTag("a");
		int page = -1;
		for(Element page0 : pages){
			String page_ = page0.attr("href");
			String[] label = page_.split("/");
			String a = label[label.length - 1].replace(".html", "");
			int max = 0;
			try{
				max = Integer.parseInt(a);
				if(max > page) page = max;
			}catch(Exception e){}
		}
		logger.debug("page.size = " + page);
		boolean iscontinue = true;
		for(int i = 1 ; i <= page ;i++){
			List<PictureSet> ret0 = new ArrayList<PictureSet>();
			String url = "http://www.chinaiiss.com/list/58/" + i + ".html";
			html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
			doc = Jsoup.parse(html);
			Elements ones = doc.select("div.pic_image");
			for(Element one : ones){
				PictureSet set = new PictureSet();
				Element temp = one.getElementsByTag("a").first();
				if(temp.attr("href").contains("society.chinaiiss.com")){
					set.setUrl(temp.attr("href"));
					set.setTitle(temp.attr("title"));
					ret0.add(set);
					logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
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
			Element element = doc.getElementById("total");
			if(element != null){
				String[] pagesum = element.text().replace("(", "").replace(")", "").trim().split("/");
				StringReader reader = new StringReader(html);
				BufferedReader br=new BufferedReader(reader);
				String temp0="";
				String d = "";
				try {
					temp0=br.readLine();
					while(temp0!=null){
						if(!temp0.contains("var slide_data = ")){
							temp0=br.readLine();
							continue;
						}else{
							int startsign = temp0.indexOf("[{");
							int endsign = temp0.indexOf("}]");
							if(endsign > startsign){
								d = temp0.substring(startsign,endsign + 2).trim();
							}
							break;
						}
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
				if(!"".equalsIgnoreCase(d)){
					Node[] nodes = JsonUtil.fromJson(d, Node[].class);
					Element intr = doc.getElementById("d_picIntro");
					if(intr != null){
						ps.setSummary(ClawerPicUtil.removeBlankText(intr.text()));
					}
					if(Integer.parseInt(pagesum[1]) == nodes.length){
						for (int i = 0 ; i < Integer.parseInt(pagesum[1]) ; i++) {
							try{
								Picture p = new Picture();
								p.setUrl(nodes[i].image_url);
								if(intr != null){
									p.setDescription(ClawerPicUtil.removeBlankText(intr.text()));
								}
								logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary());
								list.add(p);
							}catch(Exception e){
								logger.error(e.getMessage(),e);
							}
						}
					}
					ps.setSummary("");
				}
			}else{
				Element page = doc.select("div.pic_h span.sz").first();
				if(page != null){
					String pagenum = page.text().replace("(", "").replace(")", "").split("/")[1];
					for(int i = 1 ; i <= Integer.parseInt(pagenum) ; i++){
						try{
							String url = ps.getUrl();
							if(i>1){
								url = ps.getUrl().replace(".html", "") + "_" + (i-1) + ".html";
							}
							html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
							doc = Jsoup.parse(html);
							Picture p = new Picture();
							p.setUrl(doc.getElementById("nextpage").getElementsByTag("img").first().attr("src"));
							p.setDescription(ClawerPicUtil.removeBlankText(doc.select("div.pic_pic").first().getElementsByTag("p").text()));
							logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary());
							list.add(p);
						}catch(Exception e){
							logger.error(e.getMessage(),e);
						}
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
static class Node{
		
		private String title;
		private String intro;
		private String thumb_160;
		private String thumb_95;
		private String image_url;
		private String createtime;
		private String id;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getIntro() {
			return intro;
		}
		public void setIntro(String intro) {
			this.intro = intro;
		}
		public String getThumb_160() {
			return thumb_160;
		}
		public void setThumb_160(String thumb_160) {
			this.thumb_160 = thumb_160;
		}
		public String getThumb_95() {
			return thumb_95;
		}
		public void setThumb_95(String thumb_95) {
			this.thumb_95 = thumb_95;
		}
		public String getImage_url() {
			return image_url;
		}
		public void setImage_url(String image_url) {
			this.image_url = image_url;
		}
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	
}
