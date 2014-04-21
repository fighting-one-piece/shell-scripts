package com.netease.gather.clawerpic.parser.war;

import com.netease.gather.clawerpic.parser.AbstractParser;
import com.netease.gather.clawerpic.util.ClawerPicUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//腾讯军事图集
public class TencentMil extends AbstractParser{

    private static final Logger logger = LoggerFactory.getLogger(TencentMil.class);
	private static final String PAGE_CHARSET = "GBK";
	private static final String FIRST_PAGE = "http://news.qq.com/l/milite/gaoqingtuku/listgaoqingtuku2012.htm";

    @Override
    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    private String getPictureSetUrl(int page) {
        if(page<=1){
            return FIRST_PAGE;
        }
        return String.format("http://news.qq.com/l/milite/gaoqingtuku/listgaoqingtuku2012_%s.htm", page);
    }


    @Override
	public List<PictureSet> getNewPictureSet(PictureSet lastPictureSet) {
        List<PictureSet> ret = new ArrayList<PictureSet>();

        try {
            t1: for (int i = 1; i <= 20; i++) {
                String url = getPictureSetUrl(i);

                String html = ClawerPicUtil.getHtmlByUrl(url, PAGE_CHARSET);
                List<PictureSet> list = parsePictureSet(html);
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

    private List<PictureSet> parsePictureSet(String html) {
        List<PictureSet> ret = new ArrayList<PictureSet>();

        Document doc = Jsoup.parse(html);
        Elements info = doc.select("ul#piclist li td.pdlr20 a");
        for (Element e : info) {
            PictureSet ps = new PictureSet();
            String url = e.attr("href");
            ps.setUrl(url);
            ps.setTitle(ClawerPicUtil.removeBlankText(e.text()));
            if (StringUtils.isNotBlank(ps.getUrl()))
                ret.add(ps);
        }
        return ret;
    }

	@Override
	public List<Picture> getPictureList(PictureSet ps) {
        String html = ClawerPicUtil.getHtmlByUrl(ps.getUrl().replace("htm","hdBigPic.js"), PAGE_CHARSET);
        List<Picture> list = parsePictureList(html, ps);
        return list;
	}

	protected List<Picture> parsePictureList(String html, PictureSet ps) {
        List<Picture> list = new ArrayList<Picture>();
        try{
            Map map = JsonUtil.fromJson(html,Map.class);
            List groupimg = (List)((Map)((List)map.get("Children")).get(0)).get("Children");
            List imgs = new ArrayList();
            for(int i = 0;i < groupimg.size();i++){
                Map child = (Map) groupimg.get(i);
                if("groupimg".equals(child.get("Name"))){
                    imgs = (List) child.get("Children");
                }
            }
            for(int i = 0;i < imgs.size();i++){
                Map img = (Map) imgs.get(i);
                List attrs = (List) img.get("Children");
                Picture p = new Picture();
                for(int j = 0;j < attrs.size();j++){
                    Map attr = (Map) attrs.get(j);
                    if("bigimgurl".equals(attr.get("Name"))){
                        p.setUrl(((Map)((List) attr.get("Children")).get(0)).get("Content").toString());
                    }
                    if("cnt_article".equals(attr.get("Name"))){
                        p.setDescription(ClawerPicUtil.removeBlankText(((Map)((List) attr.get("Children")).get(0)).get("Content").toString()));
                    }
                }
                list.add(p);
            }
        }catch(Exception e){
            logger.error(e.getMessage()+" when ps.getUrl() = " + ps.getUrl(),e);
        }

        return list;
	}
	
	public static void main(String[] args) {
		
//		List<PictureSet> list1 = new TencentMil().getNewPictureSet(null);
//		logger.info("*********************list1.size() = " + list1.size());
//		for(int i = 0 ; i < 1 ; i++){
//			PictureSet p = new PictureSet();
//			p.setUrl("http://news.qq.com/a/20140305/013269.htm");
//			new TencentMil().getPictureList(p);
//		}
        PictureSet p = new PictureSet();
        p.setUrl("http://news.qq.com/a/20140305/013269.htm");
        new TencentMil().getPictureList(p);
	}

}
