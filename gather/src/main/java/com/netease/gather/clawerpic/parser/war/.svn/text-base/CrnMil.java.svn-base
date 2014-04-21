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
public class CrnMil extends AbstractParser{

    private static final String CHAR_SET = "gb2312";

    private String getIndexUrl() {
        if ("cnr_zcwq".equals(jobid)) {
            return "http://mil.cnr.cn/wqzb/zcwq/";
        }else if ("cnr_yczp".equals(jobid)) {
            return "http://mil.cnr.cn/jstp/yczp/";
        }else if ("cnr_tpcz".equals(jobid)) {
            return "http://mil.cnr.cn/zgwj/tpcz/";
        }else if ("cnr_tpxw".equals(jobid)) {
            return "http://mil.cnr.cn/gmws/tpxw/";
        }else if ("cnr_syly".equals(jobid)) {
            return "http://mil.cnr.cn/jszl/syly/";
        }
        return null;
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
        Document doc = Jsoup.parse(html);

        String title = doc.select("p[class=lh24 f24 f12_355329 yahei txtcenter]").text();
        ps.setTitle(ClawerPicUtil.removeBlankText(title));

        List<Picture> list = new ArrayList<Picture>();
        try{
            String url = ps.getUrl();
            String prefix = url.substring(0,url.lastIndexOf("/")+1);
            Elements scripts = doc.select("script");

            int pagenum = 0;
            String setid = "";
            t1:for (Element script : scripts) {
                String scode = script.html();
                if (scode.contains("createPageHTML")) {
                    String[] codes = scode.split("\\n");
                    for(String code:codes){
                        code = code.trim();
                        if (code.contains("createPageHTML")&&code.endsWith(";")) {
                            code = code.replaceAll("\\(",",");
                            code = code.replaceAll("\\);","");
                            String[] args = code.split(",");
                            pagenum = Integer.valueOf(args[1]);
                            setid = args[3].trim().replaceAll("\"","");
                            break t1;
                        }
                    }

                }
            }

            for(int j = 1 ; j <= pagenum ;j++){
                String pagename = "";
                if(j==1){
                    pagename = setid;
                }else {
                    pagename = setid + "_" + (j-1);
                }
                String pageurl = prefix + pagename + ".html";
                String pagehtml = ClawerPicUtil.getHtmlByUrl(pageurl, CHAR_SET);
                Document pagedoc = Jsoup.parse(pagehtml);
                Elements imgs = pagedoc.select("div[class=left f14  lh24 f12_5a5a5a] p img");
                for(Element img:imgs){
                    URI base=new URI(pageurl);//基本网页URI
                    URI abs=base.resolve(img.attr("src"));//解析于上述网页的相对URL，得到绝对URI
                    URL absURL=abs.toURL();//转成URL
                    String picurl = absURL.toString();
                    Picture p = new Picture();
                    p.setUrl(picurl);
					p.setDescription(ClawerPicUtil.getNextString(img, 3, pagedoc.select("div.right span.fb").first()));
                    list.add(p);
                }
                if(j==1){
                    pagedoc.select("div[class=left f14  lh24 f12_5a5a5a] div").remove();
                    pagedoc.select("div[class=left f14  lh24 f12_5a5a5a] p font").remove();
                    Elements texts = pagedoc.select("div[class=left f14  lh24 f12_5a5a5a] p:not(font):not(img)");
                    String summary = "";
                    for(Element text:texts){
                        summary = summary + ClawerPicUtil.removeBlankText(text.text());
                    }
                    if(StringUtil.isEmpty(summary)){
                        for(Picture pic:list){
                            summary = summary + pic.getDescription();
                        }
                    }
                    ps.setSummary(summary);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        if (list.size() == 0)
            return null;
        return list;
    }

    public static void main(String[] args) {
        CrnMil crnMil = new CrnMil();
        crnMil.setJobid("cnr_tpcz");
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
        ps.setUrl("http://mil.cnr.cn/zgwj/tpcz/201402/t20140219_514883656.html");
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
