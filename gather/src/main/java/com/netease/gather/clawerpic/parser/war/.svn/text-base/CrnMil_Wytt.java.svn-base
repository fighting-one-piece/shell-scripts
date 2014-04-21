package com.netease.gather.clawerpic.parser.war;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.StringUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//中广网图集
/*http://mil.cnr.cn/wqzb/zcwq/ 战场武器
http://mil.cnr.cn/jstp/yczp/   新闻现场
http://mil.cnr.cn/zgwj/tpcz/   图片传真
http://mil.cnr.cn/zglj/wytt/   网友贴图
http://mil.cnr.cn/gmws/tpxw/   图说边防
http://mil.cnr.cn/jszl/syly/   岁月留影
*/
public class CrnMil_Wytt extends AbstractParser{

    private static final String CHAR_SET = "gb2312";

    private String getIndexUrl() {
        return "http://mil.cnr.cn/zglj/wytt/";
    }

    private String getPictureSetUrl(String index,int page) {
        if(page<=1){
            return index+"index.html";
        }
        return index + String.format("index_%s.html", page-1);
    }

    private List<PictureSet> parsePictureSet(Document doc) {
        Elements es = doc.select("td.link04 a");
        List<PictureSet> lst = new ArrayList<PictureSet>();
        for (Element e : es) {
            PictureSet ps = new PictureSet();
            ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
            String url = e.attr("href");
            if(!"".equals(url)&&!url.toLowerCase().startsWith("http://")){
                try {
                    URI base=new URI(getIndexUrl());//基本网页URI
                    URI abs=base.resolve(url);//解析于上述网页的相对URL，得到绝对URI
                    URL absURL=abs.toURL();//转成URL
                    url = absURL.toString();
                }catch (Exception ex){
                    logger.error(ex.getMessage());
                }
            }
            ps.setUrl(url);
            lst.add(ps);
        }
        return lst;
    }

    @Override
    public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
        List<PictureSet> ret = new ArrayList<PictureSet>();
        try {
            String index = getIndexUrl();
            t1: for (int i = 1; i <= 20; i++) {
                String url = getPictureSetUrl(index,i);
                String html = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
                Document doc = Jsoup.parse(html);
                List<PictureSet> list = parsePictureSet(doc);
                logger.debug("{} job getPictureSet from {} return:{}", jobid, url, list.size());
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

    @Override
    public List<Picture> getPictureList(PictureSet ps) {
        String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl(), CHAR_SET);
        List<Picture> list = parsePictureList(html, ps);
        return list;
    }

    private List<Picture> parsePictureList(String html, PictureSet ps) {
        try {
            StringBuilder summary = new StringBuilder();
            List<Picture> list = new ArrayList<Picture>();

            String url = ps.getUrl();
            int count = 0;
            t1: while (true) {
                if (url == null || count > 40) {
                    break;
                }
                String pagehtml = ClawerPicUtil.getHtmlByUrl(url, CHAR_SET);
                parsePictureListInner(pagehtml, ps, summary, list);

                url = null;
                Document doc = Jsoup.parse(pagehtml);
                Elements nexts = doc.select("div.pg a.nxt");
                if(nexts!=null&&nexts.size()>0){
                    Element nxt = nexts.first();
                    url = "http://bbs.cnr.cn/" + nxt.attr("href");
                }
            }

            ps.setSummary(summary.toString());

            if (list.size() == 0)
                return null;
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is " + ps.getUrl(), e);
        }

        return null;
    }

    private void parsePictureListInner(String html, PictureSet ps, StringBuilder summary, List<Picture> list) {
        Document doc = Jsoup.parse(html);

        String psurl = ps.getUrl();

        try {
            Elements items = doc.select("div[id^=post_]");
            String author = StringUtil.isEmpty(ps.getAuthor())?"":ps.getAuthor();
            String desp = "";
            for (int i = 0; i < items.size(); i++) {
                Element item = items.get(i);
                String postauthor = "";
                Elements as = item.select("div.bm_user a");
                if(as!=null&&as.size()>0){
                    postauthor = as.get(0).text();
                }
                if(i==0&&StringUtil.isEmpty(author)){
                    author = postauthor;
                }

                if(!StringUtil.isEmpty(author)&&postauthor.equals(author)){
                    Element postmsg = item.nextElementSibling();
                    Elements msgs = postmsg.select("div.postmessage");
                    for(Element msg:msgs){
                        String text = ClawerPicUtil.removeBlankText(msg.text());
                        Elements imgs = postmsg.select("a:has(img)");
                        for(Element img:imgs){
                            Picture p = new Picture();
                            URI base=new URI(psurl);//基本网页URI
                            URI abs=base.resolve(img.attr("href"));//解析于上述网页的相对URL，得到绝对URI
                            URL absURL=abs.toURL();//转成URL
                            p.setUrl(absURL.toString());
                            String pdes = ClawerPicUtil.getNextString(img, msg);
                            if(StringUtil.isEmpty(pdes)){
                                pdes = desp;
                            }else {
                                desp = pdes;
                            }
                            p.setDescription(pdes);
                            list.add(p);
                        }
                        summary.append(text);
                    }
                }
            }
            ps.setAuthor(author);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public static void main(String[] args) {
        CrnMil_Wytt crnMil = new CrnMil_Wytt();
//		List<PictureSet> list1 = crnMil.getNewPictureSet(null);
//
//		System.out.println(list1.size());
//		for (int i = 0; i < 1; i++) {
//			System.out.println(list1.get(i).getUrl() + " " + list1.get(i).getTitle());
//		    PictureSet p = new PictureSet();
//			p.setUrl(list1.get(i).getUrl());
//			crnMil.getPictureList(p);
//		}

        PictureSet ps=new PictureSet();
        ps.setUrl("http://bbs.cnr.cn/forum.php?mod=viewthread&tid=11320768&extra=page%3D1");
        List<Picture> l=crnMil.getPictureList(ps);
        System.out.println(ps.getSummary());
        System.out.println(l.size());

        for (int i = 0; i < l.size(); i++) {
            System.out.println(i);
			System.out.println(l.get(i).getUrl() + l.get(i).getDescription());
		}
//        System.out.println(ps.getTitle());
//        System.out.println(ps.getSummary());
//        for(Picture p:l){
//            System.out.println(p.getUrl());
//        }
    }
}
