package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.*;
import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.domain.Doc;
import com.netease.gather.domain.Page;
import com.netease.gather.domain.Special;
import com.netease.gather.domain.SpecialDoc;
import com.netease.gather.extapi.CmsSearchApi;
import com.netease.gather.extapi.ExtractPhotosetUtil;
import com.netease.gather.extapi.TieApiUtil;
import com.netease.gather.nlp.SimilarityCalc;
import com.netease.gather.service.data.DocService;
import com.netease.gather.service.data.SpecialDocService;
import com.netease.gather.service.data.SpecialService;
import com.netease.gather.service.logic.GenSpecialService;
import com.netease.gather.service.logic.NewsHotService;
import com.netease.gather.summary.SummaryFacade;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("genSpecialService")
public class GenSpecialServiceImpl implements GenSpecialService {

    private static final Logger logger = LoggerFactory.getLogger(GenSpecialServiceImpl.class);

    @Resource(name="newsHotService")
    private NewsHotService newsHotService;
    @Resource(name="docService")
    private DocService docService;
    @Resource(name="specialService")
    private SpecialService specialService;
    @Resource(name="specialDocService")
    private SpecialDocService specialDocService;

    private final static String TEST_ID = "00014R7I";

    private static final String CHANNAL_ID = "00014RE2";

    private static boolean demo = true;
    static {
        if ("demo".equals(Config.init().get("env"))) {
            // 测试
            demo = true;
        } else if ("prod".equals(Config.init().get("env"))) {
            demo = false;
        }
    }

    private static Map<String, String> coltopic = new HashMap<String, String>() {
        {
            if ("demo".equals(Config.init().get("env"))) {
                // 测试
                put("CHANNAL_ID", TEST_ID);
            } else if ("prod".equals(Config.init().get("env"))) {
                put("CHANNAL_ID", CHANNAL_ID);
            }
        }
    };

    private static Map<String, String> colname = new HashMap<String, String>() {
        {
            put("guonei","国内");
            put("guoji","国际");
            put("shehui","社会");
            put("junshi","军事");
        }
    };



    @Override
    public void showSpecial(String channel, String starttime, String endtime, int clustersize, int showlimit) throws Exception{
        int hsize = newsHotService.genHots(channel, starttime, endtime, "163", clustersize);
        if(hsize==0){
            return;
        }
        List<Map> showhots = newsHotService.choiceHot(channel, "", starttime, endtime, 1, showlimit);

        List<Map> simphots = new ArrayList<Map>();
        for(Map remap:showhots){
            List<Doc> docs = (List<Doc>)remap.get("dlist");
            if(docs.size()>=5){
                simphots.add(remap);
            }
        }
        logger.info("初步筛选热点！");
        simphots = mergeHots2Special(simphots, endtime, channel);
//        simphots = newsHotService.rmRepeatHots(simphots);
        simphots = mergeSimSpecial(simphots);
        List<Map> lasthots = new ArrayList<Map>();
        for(Map remap:simphots){
            long specialid = Long.valueOf(remap.get("specialid").toString());
            lasthots.addAll(genSpecPage4One(specialid,channel));
        }
//        simphots = genSpecial(simphots,channel);
        genInterfaceData(lasthots, starttime, endtime, channel);
    }


    @Override
    public void changeHeaderline(String docurl, String specurl, String channelid) throws Exception{

//        String codestr = "smart"+specurl;
//        String genstr = DigestUtils.sha1Hex(codestr);
//        logger.info(specurl);
//        logger.info("signature:{},genstr{}",signature,genstr);
//        if(!genstr.equals(signature)){
//            throw new ApplicationException("请求不合法！");
//        }


        Pattern pattern = Pattern.compile("/([0-9]+)/([0-9]+)/([0-9]+)\\.html");
        Matcher matcher = pattern.matcher(specurl);
        long specialid = 0;
        if (matcher.find()) {
            specialid = Long.valueOf(matcher.group(2));
        }else {
            throw new ApplicationException("专题链接格式出错！");
        }
        logger.info("specurl:"+specurl+",docurl:"+docurl);
        try {
            String channel = CommonUtil.getChannelById(channelid);
            String docid = CommonUtil.get163DocidFromUrl(docurl);
            Map<String,String> article = HessianUtil.getCmsWebService().getArticle(docid);
            String ourl = article.get("url");
            String docno = ShortUrlGenerator.generatorAllStr(ourl);

            //如果没有，增加文章
            Map dparamap = new HashMap();
            dparamap.put("docno",docno);
            Doc adddoc = docService.getOneByParameters(dparamap);
            if(adddoc==null){
                adddoc = new Doc();
                adddoc.setDocno(docno);
                adddoc.setTitle(article.get("title"));
                adddoc.setUrl(ourl);
                adddoc.setPtime(DateUtil.stringToDate(article.get("ptime"), "yyyy-MM-dd HH:mm:ss"));
                adddoc.setSource("163");
                adddoc.setCreatetime(new Date());
                adddoc.setChannel(channel);
                docService.saveOne(adddoc);
            }

            //更新专题，信息
            Map smap = new HashMap();
            smap.put("docno",docno);
            smap.put("autoid",specialid);
            specialService.updateOne(smap);

            //更新专题包含文章信息。
            Map docpara = new HashMap();
            docpara.put("specialid",specialid);
            docpara.put("docno",docno);
            List<SpecialDoc> specialDocs = specialDocService.getListByParameters(docpara);
            if(specialDocs.size()<1){
                SpecialDoc specialDoc = new SpecialDoc();
                specialDoc.setDocno(docno);
                specialDoc.setSpecialid(specialid);
                specialDoc.setCreatetime(new Date());
                specialDocService.saveOne(specialDoc);
            }

            //组装数据重新生成页面。
            genSpecPage4One(specialid,channel);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new ApplicationException("获取文章数据失败！");
        }
    }

    private List<Map> genSpecPage4One(long specialid,String channel) throws Exception{
        Map remap = new HashMap();
        Map para = new HashMap();
        para.put("autoid",specialid);
        Special special = specialService.getOneByParameters(para);
        remap.put("specialid",specialid);
        remap.put("col",special.getCol());
        remap.put("channel",channel);
        String cdocno = special.getDocno();
        if(!StringUtil.isEmpty(cdocno)){
            para = new HashMap();
            para.put("docno",cdocno);
            Doc cdoc = docService.getOneByParameters(para);
            remap.put("cdoc",cdoc);
            remap.put("cdocmap",dealDocData(cdoc));
        }

        List<Doc> dlist = new ArrayList<Doc>();
        Set<String> soset = new HashSet<String>();
        para = new HashMap();
        para.put("specialid",specialid);
        List<SpecialDoc> docs = specialDocService.getListByParameters(para);
        for (SpecialDoc specialDoc:docs){
            Map dmap = new HashMap();
            dmap.put("docno",specialDoc.getDocno());
            Doc doc = docService.getOneByParameters(dmap);
            if(doc!=null){
                dlist.add(doc);
                soset.add(doc.getSource());
            }
        }

        remap.put("dlist", dlist);
        remap.put("soset",soset);

        List<Map> rehots = new ArrayList<Map>();
        rehots.add(remap);
        rehots = genSpecial(rehots,channel);
        return rehots;
    }


    private List<Map> mergeHots2Special(List<Map> simphots,String endtime,String channel) throws Exception{
        Map para = new HashMap();
        para.put("updatetime_start", DateUtils.addDays(DateUtil.stringToDate(DateUtil.DateToString(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), -3));
        List<Special> specials = specialService.getListByParameters(para);


        for(Special special:specials){
            long specialid = special.getAutoid();
            Map docpara = new HashMap();
            docpara.put("specialid",specialid);
            List<SpecialDoc> specialDocs = specialDocService.getListByParameters(docpara);
            for(Map remap:simphots){
                if(remap.containsKey("specialid")){
                    continue;
                }
                List<Doc> docs = (List<Doc>)remap.get("dlist");

                Set<String> same = new HashSet<String>();
                boolean ismixed = false;
                for(SpecialDoc specialDoc:specialDocs){
                    for (Doc doc:docs){
                        if(specialDoc.getDocno().equals(doc.getDocno())){
                            ismixed=true;
                            same.add(doc.getDocno());
                        }
                    }
                }

                List<Doc> diff = new ArrayList<Doc>();
                for(Doc doc:docs){
                    if(!same.contains(doc.getDocno())){
                        diff.add(doc);
                    }
                }


                //之前有包含相同文章的专题
                if(ismixed){
                    List<Doc> containdoc = new ArrayList<Doc>();
                    for(SpecialDoc specialDoc:specialDocs){
                        Map dmap = new HashMap();
                        dmap.put("docno",specialDoc.getDocno());
                        Doc doc = docService.getOneByParameters(dmap);
                        containdoc.add(doc);
                    }
                    containdoc.addAll(diff);
                    if(diff.size()>0){
                        for(Doc doc:diff){
                            SpecialDoc specialDoc = new SpecialDoc();
                            specialDoc.setDocno(doc.getDocno());
                            specialDoc.setSpecialid(specialid);
                            specialDoc.setCreatetime(new Date());
                            specialDocService.saveOne(specialDoc);
                        }
                        Map upmap = new HashMap();
                        upmap.put("autoid",specialid);
                        upmap.put("updatetime",DateUtil.stringToDate(endtime,"yyyyMMddHH:mm:ss"));
                        specialService.updateOne(upmap);
                    }

                    String cdocno = special.getDocno();
                    if(!StringUtil.isEmpty(cdocno)){
                        logger.info(cdocno);
                        Map dmap = new HashMap();
                        dmap.put("docno",cdocno);
                        Doc doc = docService.getOneByParameters(dmap);
                        Map cdocmap = dealDocData(doc);
                        remap.put("cdocmap",cdocmap);
                    }
                    Iterator<Doc> iterator = containdoc.iterator();
                    while (iterator.hasNext()){
                        Doc doc = iterator.next();
                        Map odoc = new HashMap();
                        odoc.put("specialid",specialid);
                        odoc.put("docno",doc.getDocno());
                        List<SpecialDoc> sdocs = specialDocService.getListByParameters(odoc);
                        if(sdocs!=null){
                            for(SpecialDoc sdoc : sdocs){
                                if("1".equals(sdoc.getDel())){
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    remap.put("dlist",containdoc);
                    remap.put("specialid",specialid);
                }
            }
        }

        for(Map remap:simphots){
            if(!remap.containsKey("specialid")){
                List<Doc> docs = (List<Doc>)remap.get("dlist");
                Special special = new Special();
                special.setCol((String)remap.get("col"));
                special.setChannel(channel);
                special.setCreatetime(new Date());
                special.setUpdatetime(DateUtil.stringToDate(endtime,"yyyyMMddHH:mm:ss"));
                long specialid = specialService.saveOne(special);
                for(Doc doc:docs){
                    SpecialDoc specialDoc = new SpecialDoc();
                    specialDoc.setDocno(doc.getDocno());
                    specialDoc.setSpecialid(specialid);
                    specialDoc.setCreatetime(new Date());
                    specialDocService.saveOne(specialDoc);
                }
                remap.put("specialid",specialid);
            }
        }

        List<Map> merged = new ArrayList<Map>();
        Set puted = new HashSet();
        for(Map remap:simphots){
            if(puted.contains(remap.get("specialid"))){
                continue;
            }
            Map nodelpara = new HashMap();
            nodelpara.put("autoid",Long.valueOf(remap.get("specialid").toString()));
            Special special = specialService.getOneByParameters(nodelpara);
            if("0".equals(special.getDel())){
                merged.add(remap);
            }
            puted.add(remap.get("specialid"));
        }

        logger.info("增量合并专题完成！");
        return merged;
    }

    private List<Map> mergeSimSpecial(List<Map> simphots) throws Exception{
        List<Map> merged = new ArrayList<Map>();
        Set<Long> exhotid = new HashSet<Long>();

        int hotsize = simphots.size();
        for (int i=0;i<hotsize;i++){
            Map remap = simphots.get(i);
            long specialid = (Long)remap.get("specialid");
            if(!exhotid.contains(specialid)){

                for (int j=i+1;j<hotsize;j++){
                    Map omap = simphots.get(j);
                    long ospecialid = (Long)omap.get("specialid");
                    if(!exhotid.contains(ospecialid)){
//                        boolean issim = newsHotService.isSim(remap, omap);
                        boolean issim = newsHotService.hasSame(remap, omap);
                        if(issim){

                            exhotid.add(ospecialid);
                            List<Doc> docs1 = (List<Doc>)remap.get("dlist");
                            List<Doc> docs2 = (List<Doc>)omap.get("dlist");
                            Iterator<Doc> it = docs2.iterator();
                            while (it.hasNext()){
                                Doc doc2 = it.next();
                                for(Doc doc1:docs1){
                                    if(doc2.getDocno().equals(doc1.getDocno())){
                                        Map deldoc = new HashMap();
                                        deldoc.put("specialid",ospecialid);
                                        deldoc.put("docno",doc2.getDocno());
                                        specialDocService.deleteSomeByParameters(deldoc);
                                        it.remove();
                                    }
                                }
                            }
                            for(Doc doc2:docs2){
                                Map updoc = new HashMap();
                                updoc.put("new_specialid",specialid);
                                updoc.put("old_specialid",ospecialid);
                                updoc.put("docno",doc2.getDocno());
                                specialDocService.updateOne(updoc);
                                docs1.add(doc2);
                            }
                            Map del = new HashMap();
                            del.put("autoid",ospecialid);
                            specialService.deleteSomeByParameters(del);
                            remap.put("dlist",docs1);
                        }
                    }
                }
                merged.add(remap);
            }
        }
        logger.info("合并相似专题完成！");
        return merged;
    }

    private List<Map> genSpecial(List<Map> rehots,String channel) throws Exception{

        for(Map remap:rehots){
            List<Doc> docs = (List<Doc>)remap.get("dlist");
            Map<Integer,Integer> hotphotosetids = new HashMap<Integer,Integer>();
            for(Doc doc:docs){
                if(doc.getUrl().contains("photoview")){
                    Integer photosetid = Integer.valueOf(CommonUtil.get163DocidFromUrl(doc.getUrl()));
                    hotphotosetids.put(photosetid,hotphotosetids.containsKey(photosetid)?hotphotosetids.get(photosetid)+1:1);
                }else {
                    List<Integer> photosetsids = ExtractPhotosetUtil.extractPhotosetFromCmsCode(CommonUtil.get163DocidFromUrl(doc.getUrl()));
                    if(photosetsids.size()>0){
                        for(Integer photosetid:photosetsids){
                            hotphotosetids.put(photosetid,hotphotosetids.containsKey(photosetid)?hotphotosetids.get(photosetid)+1:1);
                        }
                    }
                }
            }

            int pickedphotosetid = 0;
            if(hotphotosetids.size()>0){
                int maxcount =0;
                for(Map.Entry<Integer,Integer> entry:hotphotosetids.entrySet()){
                    if(entry.getValue()>maxcount){
                        pickedphotosetid = entry.getKey();
                        maxcount = entry.getValue();
                    }
                }
            }else {
                Doc cdoc = (Doc)remap.get("cdoc");
                String keywordstr = CommonUtil.getKeywords(cdoc.getUrl());
                List<String> keywords = new ArrayList<String>(Arrays.asList(keywordstr.split(",")));
                logger.info(String.valueOf(keywords));
                List<Map> photosets = CmsSearchApi.searchPhotoset(keywords, CommonUtil.getChannelid(channel), 0, 1, true);
                if(photosets.size()>0){
                    pickedphotosetid = Integer.valueOf((String)photosets.get(0).get("setid"));
                }
            }

            if(pickedphotosetid>0){
                Map photoset = new HashMap();
                Map<String,String> setinfo = HessianUtil.getPhotoService240().getSetInfo(CommonUtil.getChannelid(channel), pickedphotosetid);
                if(setinfo!=null){
                    photoset.put("setid",setinfo.get("setid"));
                    photoset.put("setname",setinfo.get("setname"));
                    photoset.put("prevue",setinfo.get("prevue"));
                    photoset.put("seturl",setinfo.get("seturl"));
                    photoset.put("img",setinfo.get("img"));
                    photoset.put("simg",setinfo.get("simg"));
                    photoset.put("ptime",setinfo.get("createdate"));
                    photoset.put("tienum", TieApiUtil.parseTieRs(setinfo.get("commentdataurl")));
                    photoset.put("tieurl", setinfo.get("commenturl"));
                    List<Map> photos = new ArrayList<Map>();
                    List<Map<String,String>> phs = HessianUtil.getPhotoService240().getPhotoListBySetid(CommonUtil.getChannelid(channel), pickedphotosetid);
                    for(Map<String,String> pht:phs){
                        Map photo = new HashMap();
                        photo.put("photoid",pht.get("photoid"));
                        photo.put("spimg",pht.get("spimg"));
                        photo.put("simg",pht.get("simg"));
                        photo.put("s16img",pht.get("s16img"));
                        photo.put("img",pht.get("img"));
                        photo.put("timg",pht.get("timg"));
                        photo.put("oimg",pht.get("oimg"));
                        photo.put("nimg",pht.get("nimg"));
                        photo.put("img_600x450",pht.get("img_600x450"));
                        photo.put("img_900x600",pht.get("img_900x600"));
                        photo.put("note",pht.get("note"));
                        photo.put("viewlink",pht.get("viewlink"));
                        photos.add(photo);
                    }
                    photoset.put("photos",photos);
                    remap.put("photoset",photoset);
                }

            }
        }
        logger.info("图集数据组装完成！");

        for(Map remap:rehots){
            List<Doc> docs = (List<Doc>)remap.get("dlist");
            Collections.sort(docs,new Comparator<Doc>() {
                @Override
                public int compare(Doc doc1, Doc doc2) {
                    return doc2.getPtime().compareTo(doc1.getPtime());
                }
            });
            Set<String> exdocid = new HashSet<String>();
            List<Doc> bakdocs = new ArrayList<Doc>();
            int docsize = docs.size();
            for (int i=0;i<docsize;i++){
                Doc doc1 = docs.get(i);
                if(!exdocid.contains(doc1.getDocno())){
                    bakdocs.add(doc1);
                    for (int j=i+1;j<docsize;j++){
                        Doc doc2 = docs.get(j);
                        if(!exdocid.contains(doc2.getDocno())){
                            double docsim = SimilarityCalc.calcByCosWUrl(doc1.getUrl(),doc2.getUrl());
                            double titsim = SimilarityCalc.calcByCosWStringByChar(doc1.getTitle(),doc2.getTitle());
                            double sim = Math.max(docsim,titsim);
                            if(sim>=0.7){
                                exdocid.add(doc2.getDocno());
                                Map deldoc = new HashMap();
                                deldoc.put("specialid",remap.get("specialid"));
                                deldoc.put("docno",doc2.getDocno());
                                specialDocService.signDelSomeByParameters(deldoc);
                            }
                        }
                    }
                }
            }
            remap.put("dlist",bakdocs);
        }

        logger.info("删除基本相同文章！");

        List<Map> limithots = new ArrayList<Map>();
        for(Map remap:rehots){
            List<Doc> docs = (List<Doc>)remap.get("dlist");
            if(docs.size()>=5){
                limithots.add(remap);
            }
        }
        logger.info("确定生成专题的热点！");


        List<Map> spechots = new ArrayList<Map>();
        for(Map remap:limithots){
            List<Doc> docList = (List<Doc>)remap.get("dlist");

            List<Map> alldocs = new ArrayList<Map>();
            for(Doc doc:docList){
                try {
                    Map docmap = dealDocData(doc);
                    alldocs.add(docmap);
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
            }

            List<Map> docs = new ArrayList<Map>();

            Map cdocmap = new HashMap();
            if(!remap.containsKey("cdocmap")){
                List<List> scores = new ArrayList<List>();
                for(Map map:alldocs){
                    List score = Arrays.asList(map,CommonUtil.hacknews4Doc(Long.valueOf(map.get("tienum").toString()),(Date)map.get("ptime")));
                    scores.add(score);
                }

                Collections.sort(scores,new Comparator<List>() {
                    @Override
                    public int compare(List o1, List o2) {
                        return Double.valueOf(o2.get(1).toString()).compareTo(Double.valueOf(o1.get(1).toString()));
                    }
                });

                for(List score:scores){
                    cdocmap = (Map)score.get(0);
                    String url = (String) cdocmap.get("url");
                    if(!url.contains("photoview")){
                        remap.put("cdocmap",cdocmap);
                        break;
                    }
                }
            }else {
                cdocmap = (Map) remap.get("cdocmap");
            }

//            Map cdocmap = (Map)scores.get(0).get(0);
//            remap.put("cdocmap",cdocmap);

            for(Map map:alldocs){
                if(!map.get("docno").equals(cdocmap.get("docno"))){
                    docs.add(map);
                }
            }
            logger.info("文章列表处理完成！");


            boolean tieall0 = true;
            for(Map map:docs){
                int tienum = Integer.valueOf(map.get("tienum").toString());
                if(tienum>0){
                    tieall0 = false;
                    break;
                }
            }

            if(tieall0){
                continue;
            }


            List<Map> docs_new = new ArrayList<Map>();
            List<Map> docs_hot = new ArrayList<Map>();
            docs_new.addAll(docs);
            docs_hot.addAll(docs);

            Collections.sort(docs_new,new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    Date ptime1 = (Date)o1.get("ptime");
                    Date ptime2 = (Date)o2.get("ptime");
                    return ptime2.compareTo(ptime1);
                }
            });
            logger.info("最新文章排序完成！");

            Collections.sort(docs_hot,new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    Long tienum1 = (Long)o1.get("tienum");
                    Long tienum2 = (Long)o2.get("tienum");
                    int rint = tienum2.intValue()-tienum1.intValue();
                    if(rint==0){
                        Date ptime1 = (Date)o1.get("ptime");
                        Date ptime2 = (Date)o2.get("ptime");
                        rint = ptime2.compareTo(ptime1);
                    }
                    return rint;
                }
            });
            logger.info("最热文章排序完成！");

            remap.put("docs_new",docs_new);
            remap.put("docs_hot",docs_hot);

            spechots.add(remap);
            logger.info(remap.get("specialid")+":"+"文章数据组装完成！");

        }

        logger.info("文章数据组装完成！");

        for(Map remap:spechots){
            Map cdocmap = (Map)remap.get("cdocmap");
            String url = (String) cdocmap.get("url");
            String keywordstr = CommonUtil.getKeywords(url);
            String jsonstr = HessianUtil.getTagWebService().getRelevantTag(0, 20, keywordstr);
            List<Map> reltag = (List<Map>) JsonUtil.fromJson(jsonstr, Map.class).get("hits");
            if(reltag.size()>0){
                List<Map> relread = new ArrayList<Map>(reltag.subList(0,reltag.size()>=5?5:reltag.size()));
                remap.put("relread", relread);
            }
        }
        logger.info("获取相关TAG完成！");

//        newsHotService.calSimWithHeadlines(rehots,channel,endtime,9);
//        logger.info("头条去重完成！");

        for(Map remap:spechots){
            genSubject4Pagers(remap,channel);
            Long specialid = (Long) remap.get("specialid");
            String urlpre = "";
            if(demo){
                urlpre = "http://test.gather.163.com/" +channel+"/special/"+specialid/1000+"/" + specialid +"/";
            }else{
                urlpre = "http://news.163.com/smart/"+specialid/1000+"/" + specialid +"/";
            }
            remap.put("specurl",urlpre+"1.html");


        }
        logger.info("专题页生成完成！");

        return spechots;
    }

    private void genInterfaceData(List<Map> spechots,String starttime,String endtime,String channel) throws Exception{

        final String startstr = starttime;
        Collections.sort(spechots,new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                Date start = DateUtil.stringToDate(startstr, "yyyyMMddHH:mm:ss");
                List<Map> o1doc = (List<Map>)o1.get("docs_new");
                List<Map> o2doc = (List<Map>)o2.get("docs_new");
                int r1=0;
                int r2=0;
                Date p1 = DateUtil.stringToDate(startstr, "yyyyMMddHH:mm:ss");
                Date p2 = DateUtil.stringToDate(startstr, "yyyyMMddHH:mm:ss");
                for(Map doc:o1doc){
                    Date ptime = (Date)doc.get("ptime");
                    if(!ptime.before(start)){
                        r1++;
                    }

                    if(ptime.after(p1)){
                        p1 = ptime;
                    }
                }

                for(Map doc:o2doc){
                    Date ptime = (Date)doc.get("ptime");
                    if(!ptime.before(start)){
                        r2++;
                    }

                    if(ptime.after(p2)){
                        p2 = ptime;
                    }
                }
                int rint = r2-r1;
                if(rint==0){
                    rint = p2.compareTo(p1);
                }

                return rint;
            }
        });

        logger.info("排序完成！");

        List<Map> headhotlist = new ArrayList<Map>();
        Set dealed = new HashSet();
        for(Map remap:spechots){
            if(dealed.contains(remap.get("specialid"))){
                continue;
            }
            Map cdocmap = (Map)remap.get("cdocmap");
            Map map=new HashMap();
            map.put("title",cdocmap.get("title"));
            map.put("summary",cdocmap.get("summary"));
            if(cdocmap.containsKey("picurl")){
                map.put("picurl",cdocmap.get("picurl"));
            }
            map.put("url", remap.get("specurl"));
            map.put("col", remap.get("col"));
            map.put("specialid", remap.get("specialid"));

            if(!demo){
                try {
                    boolean suc = HessianUtil.getCmsWebService().insertBlankArticle(null,coltopic.get("CHANNAL_ID"), "["+colname.get(map.get("col").toString())+"]"+map.get("title").toString(), null, null, null,0, 60, null, null, "jiqiren", "机器人", null,map.get("url").toString(), null);
                    logger.info("title: " + map.get("title") + ", suc : " + suc);
                }catch (Exception e){
                    logger.error("推送CMS空文章出错!");
                    logger.error(e.getMessage());
                }
            }


//            String col = (String) remap.get("col");
//            if(col.equals("shehui")||col.equals("guonei")){
//                headhotlist.add(map);
//            }
            //2013.12.26生成所有数据，前端判断自取。
            headhotlist.add(map);
            dealed.add(remap.get("specialid"));
        }

//        if(headhotlist.size()>=4){
        Map jsonmap = new HashMap();
        Map printmap = new HashMap();
        jsonmap.put("hotlist",headhotlist);
        printmap.put("printstr", "specheader("+JsonUtil.toJsonStr(jsonmap)+")");
        HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/specheader.json", "print.vm", "GBK", printmap);
//        }

        if(demo){
            Map pagePara = new HashMap();
            pagePara.put("hotlist",spechots);
            pagePara.put("start",DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"));
            pagePara.put("end",DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"));
            pagePara.put("channel",channel);
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/rank.html", "specialrank.vm", "GBK", pagePara);
//        HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel +"/special/"+starttime.substring(0,8)+"/"+DateUtil.DateToString(DateUtil.stringToDate(starttime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+"-"+DateUtil.DateToString(DateUtil.stringToDate(endtime, "yyyyMMddHH:mm:ss"),"yyyyMMddHHmmss")+".html", "specialrank.vm", "GBK", pagePara);
        }
    }

    private static Map dealDocData(Doc doc) throws Exception{
        if(doc==null){
            throw new ApplicationException("文章数据为空！");
        }
        Map docmap = new HashMap();
        docmap.put("docno",doc.getDocno());
        docmap.put("title",doc.getTitle());
        docmap.put("url",doc.getUrl());
        docmap.put("ptime",doc.getPtime());
        docmap.put("source",doc.getSource());

        String docid = CommonUtil.get163DocidFromUrl(doc.getUrl());
        logger.info("文章处理开始:"+docid);
        if(doc.getUrl().contains("photoview")){
            Map<String,String> setinfo = HessianUtil.getPhotoService240().getSetInfo(CommonUtil.getChannelid(doc.getChannel()), Integer.valueOf(docid));
            if(setinfo!=null){
                docmap.put("boardid", "photoview_bbs");
                docmap.put("docid", TieApiUtil.generatePostid(Integer.valueOf(docid),setinfo.get("topicid")));
                docmap.put("picurl",setinfo.get("img"));
                docmap.put("tienum", TieApiUtil.parseTieRs(setinfo.get("commentdataurl")));
                docmap.put("tieurl",setinfo.get("commenturl"));
                String summary = StringUtil.isEmpty(setinfo.get("prevue"))?"":setinfo.get("prevue");
                docmap.put("summary",summary);
            }else {
                docmap.put("boardid","");
                docmap.put("docid",docid);
                docmap.put("tienum",0);
                docmap.put("tieurl","");
                docmap.put("summary","");
            }
        }else {
            Map<String,String> article = new HashMap<String, String>();
            try {
                article = HessianUtil.getCmsWebService().getArticle(docid);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }

            if(article!=null&&article.size()>0){
                String body = article.get("body");
                String context = Jsoup.clean(body, Whitelist.none());
                String boardid = article.get("boardid");

                docmap.put("boardid",boardid);
                docmap.put("docid",docid);
                docmap.put("tienum",TieApiUtil.getArticleTieTotal(boardid,doc.getChannel(),docid));
                docmap.put("tieurl",TieApiUtil.getArticleTieUrl(boardid,doc.getChannel(),docid));
                String picurl = CommonUtil.getPicFromHtml(body);
                if(!StringUtil.isEmpty(picurl)){
                    docmap.put("picurl",picurl);
                }

                String summary = "";
                if(body!=null && body.trim().length()>100){//自动化摘要
                    try {
//                        summary = HessianUtil.getTagWebService().getSummary(body);
                        summary = SummaryFacade.auto(body);
                    }catch (Exception e){
                        logger.error(doc.getUrl()+e.getMessage(),e);
                    }
                    if(summary!=null && summary.trim().length()<10){//自动化摘要失败
                        summary = context.substring(0,context.length()>=100?100:context.length());
                    }
                    docmap.put("summary",summary);
                }else {
                    docmap.put("summary",context);
                }
            }else {
                docmap.put("boardid","");
                docmap.put("docid",docid);
                docmap.put("tienum",0);
                docmap.put("tieurl","");
                docmap.put("summary","");
            }
        }

        logger.info("文章处理完成:"+docid);
        return docmap;
    }


    private static void genSubject4Pagers(Map remap,String channel) throws Exception{
        Map pagePara = new HashMap();
        pagePara.putAll(remap);
//        pagePara.put("hotlist",hotrank);
        @SuppressWarnings("unchecked")
        List<Map> docs_new = (List<Map>)pagePara.get("docs_new");
        @SuppressWarnings("unchecked")
        List<Map> docs_hot = (List<Map>)pagePara.get("docs_hot");
        Long specialid = (Long) pagePara.get("specialid");
        final int PAGE_LENGTH = 10;

        //当前页码
        if(docs_new != null && docs_new.size() > 0){
//            String urlob = channel+"/special/"+specialid/1000+"/" + specialid +"/";
            String routepre = Constants.HTMLROOT +channel+"/special/"+specialid/1000+"/" + specialid +"/";
//            String urlpre = "/" +urlob;
            int pageCount = 0;
            if(docs_new.size() >= 10 && docs_new.size() % PAGE_LENGTH == 0)
                pageCount = docs_new.size() / PAGE_LENGTH;
            else
                pageCount = docs_new.size() / PAGE_LENGTH + 1;	//总分页数量
            int pageNum   = 0;

            //拼接分页
            List<Page> pageList = new ArrayList<Page>();
            for(int count = 0 ; count < pageCount ; count++){
                String pageUrl = (count+1) + ".html";
                String beforePageUrl = (count) + ".html";
                String afterPageUrl = (count+2) + ".html";
                Page page = new Page();
                page.setPageNum(String.valueOf(count+1));
                page.setPageUrl(pageUrl);
                if(count != 0)
                    page.setBeforePageUrl(beforePageUrl);
                if(count != pageCount -1)
                    page.setAfterPageUrl(afterPageUrl);

                pageList.add(page);
            }

            pagePara.put("pageList", pageList);
            List<Map> list_new = new ArrayList<Map>();
            List<Map> list_hot = new ArrayList<Map>();
            int j = 0;
            logger.info("当前list长度为 = " + docs_new.size());
            for(j = 0 ; j < docs_new.size() ; j++){
                list_new.add(docs_new.get(j));
                list_hot.add(docs_hot.get(j));
                if((j+1) % 10 == 0){
                    pageNum++;
                    String htmlFile = routepre + pageNum + ".html";
                    try {
                        pagePara.put("docs_new", list_new);
                        pagePara.put("docs_hot", list_hot);
                        pagePara.put("currentPage", pageNum + ".html");
                        if(pageNum != 1){
                            pagePara.put("beforePage", (pageNum-1) + ".html");
                        }else{
                            pagePara.put("beforePage", null);
                        }
                        if(j == docs_new.size() - 1){
                            pagePara.put("afterPage", null);
                        }else{
                            pagePara.put("afterPage", (pageNum+1) + ".html");
                        }
                        pagePara.put("pageNum",pageNum);
                        HtmlBuildUtil.buildHTML(htmlFile, "emergency.vm", "GBK", pagePara);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw e;
                    }finally{
                        list_new = new ArrayList<Map>();
                        list_hot = new ArrayList<Map>();
                    }
                }
            }
            if(j == docs_new.size() || docs_new.size() < 10 ){
                String htmlFile = null;
                if(docs_new.size() <= 10){
                    htmlFile = routepre + "1.html";
                    try {
                        if(htmlFile != null && list_new != null && list_new.size() > 0){
                            pagePara.put("docs_new", list_new);
                            pagePara.put("docs_hot", list_hot);
                            pagePara.put("currentPage", (pageNum+1)+ ".html");
                            pagePara.put("beforePage", null);
                            pagePara.put("pageNum",1);
                            HtmlBuildUtil.buildHTML(htmlFile, "emergency.vm", "GBK", pagePara);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw e;
                    }
                }else if(j == docs_new.size()){
                    htmlFile = routepre + (pageNum+1) + ".html";
                    try {
                        if(htmlFile != null && docs_new != null && docs_new.size() > 0){
                            pagePara.put("docs_new", list_new);
                            pagePara.put("docs_hot", list_hot);
                            pagePara.put("currentPage", (pageNum+1) + ".html");
                            pagePara.put("beforePage", (pageNum) + ".html");
                            pagePara.put("afterPage", null);
                            pagePara.put("pageNum",(pageNum+1));
                            HtmlBuildUtil.buildHTML(htmlFile, "emergency.vm", "GBK", pagePara);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw e;
                    }
                }
            }

        }
    }

}
