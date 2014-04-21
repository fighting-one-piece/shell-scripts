package com.netease.gather.clawerpic.parser.war;

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
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;



//新浪军事图集
public class SinaRollMil extends AbstractParser{
	
	private static final String PAGE_CHARSET = "gb2312";
	private static final String PRE_FIX = "http://roll.mil.news.sina.com.cn/photo_hz/photo_hdphoto";
	private static final String FIRST_PAGE = PRE_FIX + "/index.shtml";
	
	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		
		String html = ClawerPicUtil.getHtmlByUrl(FIRST_PAGE, PAGE_CHARSET);
		List<PictureSet> ret = new ArrayList<PictureSet>();
		Document doc = Jsoup.parse(html);
		Element one = doc.getElementById("wrap");
		Elements ones = one.select("div.con");
		if(ones != null && ones.size() > 0){
			try{
				Elements page = doc.select("div.pagebox span.pagebox_next");
				if(page != null && page.size() == 2){
					boolean iscontinue = true;
					Element pagenext = page.first();
					String url = FIRST_PAGE;
					while(pagenext != null){
						html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
						List<PictureSet> ret0 = new ArrayList<PictureSet>();
						doc = Jsoup.parse(html);
						one = doc.getElementById("wrap");
						ones = one.select("div.con");
						page = doc.select("div.pagebox span.pagebox_next");
						Elements last = doc.select("div.pagebox span.pagebox_next_nolink");
						try{
							if(page != null && (page.size() == 2 || page.size() == 1) || last != null){
								pagenext = page.first();
								if(ones != null && ones.size() > 0){
									for(int i = 0 ; i < ones.size() ; i++){
										PictureSet set = new PictureSet();
										try {
											Element pict = ones.get(i);
											String url0 = pict.select("a").first().attr("href").trim();
											String title = ClawerPicUtil.removeBlankText(pict.select("p.p1").first().text().trim());
											set.setUrl(url0);
											set.setTitle(title);
											logger.debug("Url = " + set.getUrl() + ", Title = " + set.getTitle());
											ret0.add(set);
										}catch(Exception e){
											logger.error(e.getMessage(),e);
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
								if(page.size() == 0 && last != null) break;
								url = PRE_FIX + pagenext.select("a").attr("href").substring(1).trim();
							}else break;
						}catch(Exception e){
							logger.error(e.getMessage(),e);
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
		Element one = doc.getElementById("SI_Player");
		if(one != null){
			try{
				Element eData = doc.getElementById("eData");
				Elements eDatadl = eData.select("dl");
				for(int i = 0 ; i < eDatadl.size() ; i++){
					try{
						Element temp = eDatadl.get(i);
						Picture p = new Picture();
						p.setUrl(temp.select("dd").first().text());
						String desc = temp.select("dd").get(4).text();
						if(desc != null && !"".equalsIgnoreCase(desc.trim())){
							p.setDescription(ClawerPicUtil.removeBlankText(desc.trim()));
						}else{
							Element sec = temp.select("dt").first();
							if(sec != null && !"".equalsIgnoreCase(sec.text().trim())){
								p.setDescription(ClawerPicUtil.removeBlankText(sec.text().trim()));
							}
						}
						if(i == 0){
							Element sec = temp.select("dt").first();
							if(sec != null && !"".equalsIgnoreCase(sec.text().trim())){
								ps.setTitle(ClawerPicUtil.removeBlankText(sec.text().trim()));
							}
						}
						logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
						list.add(p);
					}catch(Exception e){
						logger.error(e.getMessage(),e);
					}
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}else {
			//汇总页大于306页以后的样式
			Element total = doc.getElementById("total");
			if(total != null){
				Elements title = doc.select("div.titBer h1");
				if(title != null && title.size() == 1){
					String pstitle = title.first().text().trim().split(" ")[0];
					ps.setTitle(ClawerPicUtil.removeBlankText(pstitle.trim()));
				}
				StringReader reader = new StringReader(html);
				BufferedReader br=new BufferedReader(reader);
				String temp0="";
				try {
					temp0=br.readLine();
					int f = 0;
					Picture p = new Picture();
					while(temp0!=null){
						try{
							if(f == 2){
								logger.debug(p.getUrl() + " " + p.getDescription() + "  " + ps.getUrl() + " " + ps.getSummary() + ",  title = " + ps.getTitle());
								list.add(p);
								p = new Picture();
								f =  0;
							}
							if(temp0.contains("s.src=")){
								int startindex = "s.src=".length() + temp0.indexOf("s.src=");
								int endindex = ";".length() + temp0.indexOf(";");
								p.setUrl(temp0.substring(startindex,endindex).replace("\"", "").replace(";", ""));
								f++;
							}
							if(temp0.contains("s.text = ")){
								int startindex = "s.text = ".length() + temp0.indexOf("s.text = ");
								int endindex = ";".length() + temp0.indexOf(";");
								p.setDescription(ClawerPicUtil.removeBlankText(temp0.substring(startindex,endindex).replace("\"", "").replace(";", "")));
								f++;
							}
							temp0=br.readLine();
						}catch(Exception e){
							logger.error(e.getMessage(),e);
						}
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}
		return list;
	}
	
	public static void main(String[] args) {
		
		List<PictureSet> list1 = new SinaRollMil().getNewPictureSet(null);
		for(int i = 0 ; i < list1.size() ; i++){
			PictureSet p = new PictureSet();
			p.setUrl(list1.get(i).getUrl());
			new SinaRollMil().getPictureList(p);
		}
	}
}
