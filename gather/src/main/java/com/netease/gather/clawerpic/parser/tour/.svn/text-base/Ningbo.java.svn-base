package com.netease.gather.clawerpic.parser.tour;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

//中国宁波网
public class Ningbo extends AbstractParser {

	private static final String CHAR_SET = "GBK";
	private static final String PRE_FIX = "http://pic.cnnb.com.cn";

	private String getIndexUrl() {
		if ("ningbo_meiwei".equals(jobid)) {
			return "http://pic.cnnb.com.cn/more.php?columnid=15&propertyid=2";
		} else if ("ningbo_tianxia".equals(jobid)) {
			return "http://pic.cnnb.com.cn/more.php?columnid=photoclass13&propertyid=2";
		} else if ("ningbo_tuyou".equals(jobid)) {
			return "http://pic.cnnb.com.cn/more.php?columnid=12&propertyid=2";
		} else if ("ningbo_retu".equals(jobid)) {
			return "http://pic.cnnb.com.cn/more.php?columnid=photoclass10&propertyid=2";
		} else if ("ningbo_guoji".equals(jobid)) {
			return "http://pic.cnnb.com.cn/more.php?columnid=photoclass18&propertyid=2";
		}
		return null;
	}

	private String getImgUrl(String url) {
		if (url.indexOf("/") == 0) {
			url = PRE_FIX + url;
		} else if (url.indexOf("http://") == -1) {
			url = PRE_FIX + "/" + url;
		}
		return url;
	}

	private List<PictureSet> parsePictureSet(Document doc) {
		if (doc == null) {
			return new ArrayList<PictureSet>();
		}
		Elements es = doc.select("table[width=170] td.hei-12p18pnn p a");
		List<PictureSet> lst = new ArrayList<PictureSet>();
		for (Element e : es) {
			PictureSet ps = new PictureSet();
			ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
			ps.setUrl(getImgUrl(e.attr("href")));
			lst.add(ps);
		}
		return lst;
	}

	private List<PictureSet> getNewPictureSet(PictureSet lastPictureSet, int start, int limit) {
		List<PictureSet> ret = new ArrayList<PictureSet>();

		String indexUrl = getIndexUrl();
		//		String indexHtml = ClawerPicUtil.getHtmlByUrl(indexUrl, CHAR_SET);
		Document indexDoc = ClawerPicUtil.getDocByUrl(indexUrl, CHAR_SET);

		Element lastA = indexDoc.select("span.hei-14p19pnn td.hei-12p18pnn a").last();
		int last = Integer.parseInt(lastA.text());

		try {
			t1: for (int i = 1; i <= last; i++) {
				Document doc = null;
				String url = null;
				if (i > 1) {
					url = indexUrl + "&page=" + i;
					doc = ClawerPicUtil.getDocByUrl(url, CHAR_SET);
				} else {
					url = indexUrl;
					doc = indexDoc;
				}
				//				System.out.println(html);
				List<PictureSet> list = parsePictureSet(doc);
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
				if (start + limit > 0) {
					if (ret.size() >= start + limit) {
						break t1;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (start + limit > 0) {
			int end = start + limit;
			if (end > ret.size()) {
				end = ret.size();
			}
			return ret.subList(start, end);
		}
		return ret;
	}

	@Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
		return getNewPictureSet(lastPictureSet, 0, 0);
	}

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
		Document doc = ClawerPicUtil.getDocByUrl(ps.getUrl(), CHAR_SET);
		List<Picture> list = new ArrayList<Picture>();
		Element page = doc.select("span.hei-14p19pnn td.hei-12p18pnn a").last();
		if (page == null) {
			page = doc.select("td.hei-14p19pnn td.hei-12p18pnn a").last();
		}
		if (page == null) {
			page = doc.select("span.bai-14p18pnb a").last();
		}
		int lastPage = 1;
		if (page != null && StringUtils.isNotBlank(page.text()) && page.text().matches("\\d+")) {
			lastPage = Integer.parseInt(page.text());
		}
		StringBuilder summary = new StringBuilder();
		String lastDes = null;
		for (int i = 1; i <= lastPage; i++) {
			String url0 = null;
			Document temp = null;
			if (i > 1) {
				String source = ps.getUrl();
				url0 = source + "&page=" + i;
				temp = ClawerPicUtil.getDocByUrl(url0, CHAR_SET);
			} else {
				temp = doc;
			}
			Elements one = temp.select("span.hei-14p22pnn");
			Picture p = new Picture();
			if (one.size() > 0) {
				if (one.select("img").first() != null) {
					String outerSrc = one.select("img").first().attr("src");
					p.setUrl(getImgUrl(outerSrc));

					String des = ClawerPicUtil.removeBlankText(one.select("p").text());
					if (des.length() > 0) {
						if (summary.indexOf(des) == -1) {
							summary.append(des);
						}
						lastDes = des;
					} else {
						des = lastDes;
					}
					p.setDescription(des);
					list.add(p);
				}
			} else {
				Elements imgs = temp.select("a.pic1 img");
				if (imgs.size() > 0) {
					String outerSrc = imgs.first().attr("src");
					p.setUrl(getImgUrl(outerSrc));
					String next = ClawerPicUtil.getNextString(imgs.first(), 3,temp.select("span.hei-12p18pnn").first());
					String des = next;
					if (des.length() > 0) {
						if (summary.indexOf(des) == -1) {
							summary.append(des);
						}
						lastDes = des;
					} else {
						des = lastDes;
					}
					p.setDescription(des);
					list.add(p);
				}
			}
		}
		ps.setSummary(summary.toString());
		return list;
	}

	public static void main(String[] args) {
		//		Ningbo n = new Ningbo();
		////		n.setJobid("ningbo_meiwei");
		////		n.setJobid("ningbo_tianxia");
		////		n.setJobid("ningbo_tuyou");
		////		n.setJobid("ningbo_retu");
		//		n.setJobid("ningbo_guoji");
		//		List<PictureSet> lst = n.getNewPictureSet(null);
		//		System.out.println(lst.size());
		//		int i = 0;
		//		for (PictureSet p : lst) {
		//			if (i < 10 || i > lst.size() - 10)
		//				System.out.println(p.getTitle() + " " + p.getUrl());
		//			i++;
		//		}

								PictureSet p = new PictureSet();
				//				p.setUrl("http://pic.cnnb.com.cn/showtheme.php?themeid=112918&columnid=15");
				//				p.setUrl("http://pic.cnnb.com.cn/showtheme.php?themeid=114185&columnid=photoclass18");
								p.setUrl("http://pic.cnnb.com.cn/showtheme.php?themeid=28243&columnid=photoclass13");
								List<Picture> list = new Ningbo().getPictureList(p);
								for (Picture pic : list) {
									System.out.println(pic.getUrl() + " " + pic.getDescription());
								}

//		Ningbo n = new Ningbo();
//		//				n.setJobid("ningbo_meiwei");
//		n.setJobid("ningbo_tianxia");
//		//		n.setJobid("ningbo_tuyou");
//		//		n.setJobid("ningbo_retu");
//		//			n.setJobid("ningbo_guoji");
//		//				List<PictureSet> lst = n.getNewPictureSet(null);
//		int start = 1907;// 价值一亿七千万的奔驰车内部装饰;
//		List<PictureSet> lst = n.getNewPictureSet(null, start, 500);
//		System.out.println(lst.size());
//		for (PictureSet p : lst) {
//			List<Picture> list = n.getPictureList(p);
//			System.out.println(start + " " + p.getTitle() + " " + p.getUrl() + " picNum:" + (list != null ? list.size() : 0) + " " + p.getSummary());
//			if (list != null) {
//				for (Picture pic : list) {
//					System.out.println(pic.getUrl() + "\t" + pic.getDescription());
//				}
//			}
//			start++;
//		}
	}

}
