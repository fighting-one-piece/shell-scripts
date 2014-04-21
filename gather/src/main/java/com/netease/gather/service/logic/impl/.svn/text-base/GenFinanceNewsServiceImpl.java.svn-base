package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.util.*;
import com.netease.gather.domain.ArticlePushed;
import com.netease.gather.domain.Doc;
import com.netease.gather.extapi.CMSUtil;
import com.netease.gather.service.data.ArticleService;
import com.netease.gather.service.logic.GenFinanceNewsService;
import com.netease.gather.service.logic.NewsHotService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("genFinanceNewsService")
public class GenFinanceNewsServiceImpl implements GenFinanceNewsService {
	private static final Logger logger = LoggerFactory
			.getLogger(GenFinanceNewsServiceImpl.class);

	@Resource(name = "articleService")
	ArticleService articleService;

	@Resource
	private NewsHotService newsHotService;

	private final static String TEST_ID = "002550IG";

	private static final String CHANNAL_ID = "00254R6C";

	private static final String FIRST = "0025421S";

	public static final Integer PRI = 80;

	private final static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private List<String> artPushedList = null;

    private static boolean demo = true;
    static {
        if ("demo".equals(Config.init().get("env"))) {
            // 测试
            demo = true;
        } else if ("prod".equals(Config.init().get("env"))) {
            demo = false;
        }
    }

	private final static Map<String, String> coltopic = new HashMap<String, String>() {
		{
			if ("demo".equals(Config.init().get("env"))) {
				// 测试
				put("CHANNAL_ID", TEST_ID);
				put("guonei", "00014R7I");
				put("guoji", "00014R7I");
				put("shehui", "00014R7I");
			} else if ("prod".equals(Config.init().get("env"))) {
				put("CHANNAL_ID", CHANNAL_ID);
                put("shehui", "0001122A");
			}
		}
	};

//	private final static Map<String, String> cmsListTrans = new HashMap<String, String>() {
//		{
//			//put("00014R7I", "http://news.163.com/special/000120FU/suetest05.js");
//			put("00014R7I", "http://news.163.com/special/00014OTP/suetest05.js");
//			put("0001122A", "http://news.163.com/special/00014OTP/gather_news_shehui.js");
//		}
//	};

	private final static Map<String, String> colUrlMap = new HashMap<String, String>() {
		{
			put("zhengquan", "http://money.163.com/stock/");
			put("hongguan", "http://money.163.com/special/00252G50/macro.html");
			put("jinrong", "http://money.163.com/finance/");
			put("chanjing", "http://money.163.com/chanjing/");
		}
	};

	private static Map<String, String> colNameMap = new HashMap<String, String>() {
		{
			put("zhengquan", "证券");
			put("hongguan", "宏观");
			put("jinrong", "金融");
			put("chanjing", "产经");
			put("finance", "财经 ");
		}
	};
	
	public void genAndSendNewsByCol(final String COL, String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception {
		int hsize = newsHotService.genHots(channel, starttime, endtime, "",
				clustersize);
		if (hsize == 0) {
			return;
		}

		// col传入null取所有社会的新闻，然后判断返回的map中的col属性进行分类
		List<Map> showhots = newsHotService.choiceHot(channel, null, starttime,
				endtime, 2, showlimit);

		showhots = newsHotService.choice163Art(showhots, channel);
		
		if (showhots != null) {
			// 预处理一下，将非163的新闻去掉
			chooseNewsByCol(COL, showhots);
			Iterator<Map> it = showhots.iterator();
			while (it.hasNext()) {
				Map map = it.next();
				Doc doc = (Doc) map.get("cdoc");
				
                if (inHead(doc.getUrl())) {
                    it.remove();
                }
			}
		}

		newsHotService.sortHotCommen(showhots);
		LinkedHashMap<String, Integer> pointMap = new LinkedHashMap<String, Integer>();		
		pointMap.put("97-97", 1);
		pointMap.put("86-96", 11);
		checkPointAndPush(pointMap, showhots, coltopic.get(COL));
	}
	
	/*public void genAndSendSheHuiNews(String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception {
		int hsize = newsHotService.genHots(channel, starttime, endtime, "",
				clustersize);
		if (hsize == 0) {
			return;
		}

		// col传入null取所有社会的新闻，然后判断返回的map中的col属性进行分类
		List<Map> showhots = newsHotService.choiceHot(channel, null, starttime,
				endtime, 2, showlimit);

		showhots = newsHotService.choice163Art(showhots, channel);
		
		if (showhots != null) {
			// 预处理一下，将非163的新闻去掉
			chooseNewsByCol("shehui", showhots);
			Iterator<Map> it = showhots.iterator();
			while (it.hasNext()) {
				Map map = it.next();
				Doc doc = (Doc) map.get("cdoc");
				
                if (inHead(doc.getUrl())) {
                    it.remove();
                }
			}
		}

		newsHotService.sortHotCommen(showhots);
		LinkedHashMap<String, Integer> pointMap = new LinkedHashMap<String, Integer>();
		pointMap.put("86-97", 12);
		checkPointAndPush(pointMap, showhots, coltopic.get("shehui"));
	}*/
	
	private void chooseNewsByCol(final String COL, List<Map> showhots){
		if(COL == null || showhots == null)
			return;
		
		Iterator<Map> it = showhots.iterator();
		while (it.hasNext()) {
			Map map = it.next();
			Doc doc = (Doc) map.get("cdoc");

			// 取163来源的新闻
			if (!"163".equals(doc.getSource())) {
				it.remove();
				continue;
			}

			String col = (String) map.get("col");
			if (!COL.equals(col)) {
				logger.info("remove[" + col + "]" + doc.getTitle());
				it.remove();
				continue;
			}

			// 标题长度小于16的去掉
			float length = StringUtil.chineseLength(doc.getTitle()) / 2.0f;
			if (length < 16.5 || length > 22) {
				it.remove();
                continue;
			}

            //if (inHead(doc.getUrl())) {
            //    it.remove();
            //}
		}
	}

	@Override
	public void genAndSendFinanceNews(String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception {
		int hsize = newsHotService.genHots(channel, starttime, endtime, "",
				clustersize);
		if (hsize == 0) {
			return;
		}

		// col传入null取所有财经的新闻，然后判断返回的map中的col属性进行分类
		List<Map> showhots = newsHotService.choiceHot(channel, null, starttime,
				endtime, 2, showlimit);

		showhots = newsHotService.choice163Art(showhots, channel);

		Calendar cal = Calendar.getInstance();
		String today = dateFormat.format(cal.getTime());

		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
		String yesterday = dateFormat.format(cal.getTime());

		String condition = "topicid=" + FIRST + ";startday=" + yesterday
				+ ";endday=" + today + ";liststart=0;listnum=3;";

		Map<String, String>[] articleArr = HessianUtil.getCmsWebService()
				.getList(condition + "pointstart=130; pointend=139;");

		List<String> toutiaoIdList = new ArrayList<String>();

		for (Map<String, String> map : articleArr) {
			for (String key : map.keySet()) {
				if ("docid".equals(key)) {
					toutiaoIdList.add(map.get(key));
					break;
				}
			}
		}

		articleArr = HessianUtil.getCmsWebService().getList(
				condition + "pointstart=110; pointend=119;");

		for (Map<String, String> map : articleArr) {
			for (String key : map.keySet()) {
				if ("docid".equals(key)) {
					toutiaoIdList.add(map.get(key));
					break;
				}
			}
		}

		// 预处理一下，将非163的新闻去掉
		if (showhots != null) {
			Iterator<Map> it = showhots.iterator();
			while (it.hasNext()) {
				Map map = it.next();
				Doc doc = (Doc) map.get("cdoc");

				// 取163来源的新闻
				if (!"163".equals(doc.getSource())) {
					it.remove();
					continue;
				}
				String col = (String) map.get("col");
				if ("zhengquan".equals(col)) {
					logger.info("remove[" + col + "]" + doc.getTitle());
					it.remove();
					continue;
				}
				// 与头条重复，去掉
				if (toutiaoIdList.contains(doc.getDocno())) {
					logger.info("remove toutiao[" + doc.getDocno() + "],"
							+ doc.getTitle());
					continue;
				}

				// 标题长度小于16的去掉
				if (doc.getTitle().length() < 16) {
					it.remove();
				}
			}
		}

		newsHotService.sortHotCommen(showhots);

		if (showhots.size() < 10) {
			// 小于10条，全推，且去重
			getArticlePushedFromDB();
			pushArticle(showhots, true);
			logger.info("推送{}条财经新闻", showhots.size());
		} else {
			// 大于10条，只推前10条
			pushArticle(showhots.subList(0, 10), false);
			logger.info("只推送10条财经新闻. size: " + showhots.size());
		}

	}

	private void pushArticle(List<Map> showhots, boolean check)
			throws Exception {
		if (showhots != null) {
			for (int i = showhots.size() - 1; i >= 0; i--) {
				Map map = showhots.get(i);
				String col = (String) map.get("col");
				// if (col == null || col.trim().length() == 0)
				// continue;

				Doc doc = (Doc) map.get("cdoc");

				String docid = CommonUtil.get163DocidFromUrl(doc.getUrl());

				if (check && artPushedList != null) {
					// 需要检查是否重复发送
					if (artPushedList.contains(doc.getDocno())) {
						// 24小时之内推过这篇文章
						logger.info("24hours内pushed: " + doc.getDocno());
						continue;
					}
				}
                try{
                    // 取文章的来源
                    Map<String, String> article = HessianUtil.getCmsWebService().getArticle(docid);
                    String source = article.get("source");
                    if (source != null && source.indexOf("&") != -1)
                        source = source.substring(0, source.indexOf("&"));
                    logger.info("采纳[" + col + "], url: " + doc.getUrl()
                            + ", source: " + source);
                    sendEmptyArtile(col, doc.getUrl(), doc.getTitle(), source);
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }

				// 推送过的文章存到数据库里
				saveArt(doc.getDocno());
				Thread.sleep(1000);
			}
		}
	}

	private void checkPointAndPush(LinkedHashMap<String, Integer> pointMap,
			List<Map> showhots, String topicId) throws Exception {
		if (showhots == null)
			return;

		logger.info("artile to push num:" + showhots.size());

		List<Map<String, String>> articleCMSList = new ArrayList<Map<String, String>>();
		// 1、取CMS权重0-255的文章, articleCMSList中map都包含docno, 用来做判断用
		getCMSArticles(articleCMSList, topicId);
		logger.info("1、get Articles[point: 0-255] from CMS, size:"
				+ articleCMSList.size());

		// 2、取DB 24小时内推送的文章
		List<ArticlePushed> pushedList = getArticlesPushedFromDB(topicId);
		logger.info("2、get Articles from DB within 24 hours, size:"
				+ pushedList.size());

		// 3、找出权限被修改过的文章docno
		// pointsModList包含articleCMSList中map
		// articleCMSList已经去掉小编改过权重的文章
		List<Map<String, String>> pointsModList = checkPriority(articleCMSList,
				pushedList);
		logger.info("3、get Articles modified by xiaobian, size:"
				+ pointsModList.size());
		logger.info(String.valueOf(pointsModList));
		Map<String, List<Map>> articleToPushMap = new HashMap<String, List<Map>>();

		int index = 0;
		// 4、先把文章按照权重区间放到push列表里
		// articleToPushMap中包含2部分数据，1、CMS拿回来的改过权重的文章map，2、聚出来的新文章map
		Iterator<String> pointIt = pointMap.keySet().iterator();
		while (pointIt.hasNext()) {
			String key = pointIt.next();
			articleToPushMap.put(key, new ArrayList<Map>());
			int pointStart = Integer
					.valueOf(key.substring(0, key.indexOf("-")));
			int pointEnd = Integer.valueOf(key.substring(key.indexOf("-") + 1));
			logger.info("start to handle articles between {}-{}", pointStart,
					pointEnd);
			int num = pointMap.get(key);
			if (pointsModList.size() > 0) {
				for (Map map : pointsModList) {
					Integer point = Integer.valueOf((String) map.get("lspri"));
					if (point >= pointStart && point <= pointEnd) {
						if (num-- > 0) {
							map.put("changed", "yes");
							articleToPushMap.get(key).add(map);
							logger.info("4-1、push MODIFIED ARTICLES to articleToPushMap, id:"
									+ map.get("docid")
									+ ", "
									+ map.get("title"));
							if (num <= 0)
								break;

						}
					}
				}
			}

			while (num > 0 && showhots.size() > index) {
				Map map = showhots.get(index++);
				Doc doc = (Doc) map.get("cdoc");
				boolean pushed = false;
				for (Map modMap : pointsModList) {
					if (modMap.get("docno").equals(doc.getDocno())) {
						logger.info("modMap.get(docno):{}, doc.getDocno :{}",
								modMap.get("docno"), doc.getDocno());
						pushed = true;
						break;
					}

				}
				if (!pushed) {
					// 如果推过需要存cms中的文章map
					for (Map<String, String> m : articleCMSList) {
						if (doc.getDocno().equals(m.get("docno"))) {
							// 这条新闻在头条里，放弃
							if (inHead(doc.getUrl()))
								break;

							articleToPushMap.get(key).add(m);
							m.put("pushed", "yes");
							logger.info("4-2、push cms doc to articleToPushMap, "
									+ doc.getTitle());
							num--;
							pushed = true;
							break;
						}
					}
					/*
					 * for (ArticlePushed art : pushedList) { if
					 * (art.getDocno().equals(doc.getDocno())) { // 这条新闻在头条里，放弃
					 * if (inHead(art.getDocno())) break; Map<String, String>
					 * mapNew = new HashMap<String, String>();
					 * articleToPushMap.get(key).add(mapNew);
					 * mapNew.put("pushed", "yes"); String did =
					 * CommonUtil.get163DocidFromUrl(doc .getUrl()); String
					 * docid = ""; if (!StringUtil.isEmpty(did)) docid =
					 * did.substring(0, 8) + "jiqiren"; mapNew.put("docid",
					 * docid); mapNew.put("docno", doc.getDocno());
					 * logger.info("4-2、push cms doc to articleToPushMap, " +
					 * doc.getTitle()); num--; pushed = true; break; } }
					 */
					// 没推过就存新文章的map
					if (!pushed) {
						if (!doc.getUrl().contains("news.163.com"))
							continue;
						// 这条新闻在头条里，放弃
						if (inHead(doc.getUrl()))
							continue;
						articleToPushMap.get(key).add(map);
						num--;
						logger.info("4-2、push new doc to articleToPushMap, "
								+ doc.getTitle());
					}
				}
			}

		}

		logger.info("articleToPushMap is ready, size: "
				+ articleToPushMap.size());

		// 5、articleCMSList去掉权限<=80的文章,剩下的更新到80
		Iterator<Map<String, String>> it = articleCMSList.iterator();
		while (it.hasNext()) {
			Map<String, String> map = it.next();
			Integer point = Integer.valueOf((String) map.get("lspri"));
			if (point <= PRI) {
				logger.info("remove point<=80 from articleCMSList, : "
						+ map.get("title"));
				it.remove();
			}
		}
		it = articleCMSList.iterator();
		while (it.hasNext()) {
			Map<String, String> map = it.next();
			// CMS旧文章更新到80
			String result = CMSUtil
					.modiLspriCMS(topicId, map.get("docid"), PRI);
			logger.info("modify old CMS doc {} point to 80, {}",
					map.get("title"), result);
		}

        int maxpoint = 0;
        for(Map.Entry<String, Integer> entry:pointMap.entrySet()){
            int pointEnd = Integer.valueOf(entry.getKey().substring(entry.getKey().indexOf("-") + 1));
            if(pointEnd>maxpoint){
                maxpoint = pointEnd;
            }
        }

        for (String key : articleToPushMap.keySet()) {
            int pointEnd = Integer.valueOf(key.substring(key.indexOf("-") + 1));
            if(pointEnd==maxpoint){
                List<Map> list = articleToPushMap.get(key);
                Map first = null;
                for(Map map:list){
                    if(!"yes".equals(map.get("changed"))){
                        first = map;
                    }
                }
                if(first!=null){
                    String ftitle = "";
                    if(first.containsKey("title")){
                        ftitle = (String) first.get("title");
                    }else{
                        Doc fdoc = (Doc) first.get("cdoc");
                        ftitle =fdoc.getTitle();

                    }
                    logger.info("当前头条【{}】,字数为{}",ftitle,StringUtil.chineseLength(ftitle)/2.0d);
                    if(StringUtil.chineseLength(ftitle)/2.0d > 21){
                        Map replaceMap = null;
                        List<Map> changeList = null;
                        boolean find = false;
                        for (String skey : articleToPushMap.keySet()) {
                            int spointEnd = Integer.valueOf(skey.substring(skey.indexOf("-") + 1));
                            if(pointEnd!=spointEnd){
                                List<Map> slist = articleToPushMap.get(skey);
                                for(Map smap:slist){
                                    if(!"yes".equals(smap.get("changed"))){
                                        String stitle = "";
                                        if(smap.containsKey("title")){
                                            stitle = (String) smap.get("title");
                                        }else{
                                            Doc sdoc = (Doc) smap.get("cdoc");
                                            stitle = sdoc.getTitle();
                                        }
                                        if(StringUtil.chineseLength(stitle)/2.0d<=21){
                                            replaceMap = smap;
                                            changeList = slist;
                                            find = true;
                                            logger.info("找到替换头条【{}】,字数为{}",stitle,StringUtil.chineseLength(stitle)/2.0d);
                                            break;
                                        }
                                    }
                                }

                                if(find){
                                    break;
                                }
                            }
                        }

                        if(replaceMap != null){
                            list.remove(first);
                            list.add(replaceMap);
                            changeList.remove(replaceMap);
                            changeList.add(0,first);

                        }else {
                            String msg = "栏目"+topicId+"中，权重为"+pointEnd+"的文章，标题“"+ftitle+"”字数"+StringUtil.chineseLength(ftitle)/2.0d+"超限，请修改。";
                            if(demo){
                                PoPo.send("ykxu@corp.netease.com",msg);
                                PoPo.send("jjqi@corp.netease.com",msg);
                            }else {
                                PoPo.send("yang_liu@corp.netease.com",msg);
                                PoPo.send("jhding@corp.netease.com",msg);
                                PoPo.send("hdli@corp.netease.com",msg);
                                PoPo.send("wangziyu@corp.netease.com",msg);
                                PoPo.send("mqfan@corp.netease.com",msg);
                                PoPo.send("ysxin@corp.netease.com",msg);
                                PoPo.send("syguo@corp.netease.com",msg);
                                PoPo.send("bj_wangchao@corp.netease.com",msg);
                                PoPo.send("wujing@corp.netease.com",msg);
                                PoPo.send("bjzhangfei@corp.netease.com",msg);
                                PoPo.send("yqcheng@corp.netease.com",msg);
                                PoPo.send("gjgu@corp.netease.com",msg);
                                PoPo.send("jiasiman@corp.netease.com",msg);
                                PoPo.send("zhangqi2010@corp.netease.com",msg);
                            }

                        }
                    }
                }
            }
        }


		// 已经推过的文章，在DB中更新权重
		Map<ArticlePushed, Integer> articlePointMap = new HashMap<ArticlePushed, Integer>();

		for (String key : articleToPushMap.keySet()) {
			int pointStart = Integer
					.valueOf(key.substring(0, key.indexOf("-")));
			int pointEnd = Integer.valueOf(key.substring(key.indexOf("-") + 1));
			int point = pointEnd;

			List<Map> list = articleToPushMap.get(key);
			Iterator<Map> it1 = list.iterator();
			while (it1.hasNext()) {
				boolean pushed = false;
				Map map = it1.next();
				String push = (String) map.get("pushed");
				// 小编改过的，跳过
				if ("yes".equals(map.get("changed"))) {
					logger.info("小编modified,do not update {}", map.get("title"));
					continue;
				}
				if (push != null && push.equals("yes")) {
					// 推过的文章，只更新权重到CMS，不再发送空文章
					String result = CMSUtil.modiLspriCMS(topicId,
							(String) map.get("docid"), point);
					logger.info("modify " + map.get("title")
							+ " point to {}, {}", point, result);

					pushed = true;

					// pushedList中文章更新权限
					Iterator<ArticlePushed> pushIt = pushedList.iterator();
					while (pushIt.hasNext()) {
						ArticlePushed article = pushIt.next();
						if (article.getDocno().equals(map.get("docno"))) {
							if (article.getPriority() == point) {
								logger.info(
										"{} point not changed, donot update DB",
										article.getTitle());
								pushIt.remove();
							} else {
								articlePointMap.put(article, point);
							}

							break;
						}
					}
					/*
					 * Doc doc = (Doc) map.get("cdoc"); if
					 * (doc.getDocno().equals(article.getDocno())) { String
					 * docid = CommonUtil.get163DocidFromUrl(doc .getUrl());
					 * 
					 * // 推过的文章，只更新权重到CMS，不再发送空文章 String result =
					 * CMSUtil.modiLspriCMS(topicId, docid, pointEnd);
					 * articlePointMap.put(article, pointEnd);
					 * 
					 * logger.info("modify " + docid +
					 * " point to {}, result:{}", pointEnd, result); pushed =
					 * true; break; }
					 */
				}
				if (!pushed) {
					// 6、推送空文章到CMS
					sendEmptyArtile(map, topicId, point);
					logger.info("sendEmptyArtile, point:" + point);
				}
				// 推2条81的文章
				if (point > pointStart)
					point--;
			}

		}

		Iterator<ArticlePushed> iter = pushedList.iterator();
		while (iter.hasNext()) {
			ArticlePushed article = iter.next();

			// 权限已经<=80，不用更新
			if (article.getPriority() <= PRI) {
				iter.remove();
			}
		}

		if (pointsModList.size() > 0) {
			// pushedList去掉pointsModList中的文章
			Iterator<ArticlePushed> it1 = pushedList.iterator();
			while (it1.hasNext()) {
				ArticlePushed article = it1.next();
				for (Map<String, String> map : pointsModList) {
					if (map.get("docno").equals(article.getDocno())) {
						// 编辑改过权限，不更新权重
						it1.remove();
						logger.info("编辑modify point of " + article.getTopicid()
								+ "," + article.getDocno());
						break;
					}
				}
			}

		}

		// 更新DB中文章权限
		refreshPriority(pushedList);
		if (articlePointMap.size() > 0) {
			for (ArticlePushed article : articlePointMap.keySet())
				refreshPriority(article, articlePointMap.get(article));
		}
		logger.info(" ++++ ALL Done.");
	}

	private boolean inHead(String url) throws Exception {
		List<Doc> headList = catchHeadlines4News();
		if (headList == null || url == null)
			return false;

		String docid = CommonUtil.get163DocidFromUrl(url);
		for (Doc doc : headList) {
			String did = CommonUtil.get163DocidFromUrl(doc.getUrl());
			if (docid.equals(did)) {
				logger.info(doc.getTitle() + " in Head List.");
				return true;
			}
		}
		return false;
	}

	private List<Map<String, String>> checkPriority(
			List<Map<String, String>> articleCMSList,
			List<ArticlePushed> pushedList) {
		if (articleCMSList == null || pushedList == null)
			return null;

		// 记录被更改过权重的文章
		List<Map<String, String>> pointsModList = new ArrayList<Map<String, String>>();
		Iterator<Map<String, String>> it = articleCMSList.iterator();
		while (it.hasNext()) {
			boolean inDB = false;
			Map<String, String> map = it.next();
			for (ArticlePushed pushed : pushedList) {
				// 数据库里的文章跟CMS里的文章比较权重是否更改
				if (map.get("docno").equals(pushed.getDocno())) {
					logger.info("==, map.docno:{}, pushed:{}",
							map.get("docno"), pushed.getDocno());
					inDB = true;
					Integer point = Integer.valueOf(map.get("lspri"));
					if (pushed.getPriority().equals(point)) {
						logger.info("pushed:{},point:{}", pushed.getPriority(),
								point);
						break;
					} else {
						// 小编更新过权重
						pointsModList.add(map);
						it.remove();
						logger.info(" 小编更新过权重, not handle " + map.get("title"));
						break;
					}
				}
			}
			if (!inDB) {
				// 文章不在DB中，认为是编辑发布的
				pointsModList.add(map);
			}
		}
		return pointsModList;
	}

	private boolean sendEmptyArtile(Map map, String topicId, int point)
			throws Exception {
		if (map == null || topicId == null)
			return false;

		String col = (String) map.get("col");

		Doc doc = (Doc) map.get("cdoc");

        String source = "网易";
		String docid = CommonUtil.get163DocidFromUrl(doc.getUrl());
        try{
		    // 取文章的来源
            Map<String, String> article = HessianUtil.getCmsWebService()
                    .getArticle(docid);
            source = article.get("source");
            if (source != null && source.indexOf("&") != -1)
                source = source.substring(0, source.indexOf("&"));
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
		// sendEmptyArtile(col, doc.getUrl(), doc.getTitle(), source,
		// 75);

		String url = doc.getUrl();
		String docTitle = doc.getTitle();

        int retry = 5;
        boolean suc = false;
        while (!suc && retry > 0){
            suc = HessianUtil.getCmsWebService().insertBlankArticle(null,
                    topicId, docTitle, null, null, null, 0, point, null, null,
                    "jiqiren", "机器人", source, url, null);
            logger.info("[title: " + docTitle + ", point:" + point + ", suc:" + suc + ", url:" + url + ", source: " + source);
            retry--;
        }

		logger.info("采纳[" + col + "]," + docTitle + ", suc : " + suc);

		// 推送过的文章存到数据库里
        if(suc){
            saveArt(doc.getDocno(), doc.getTitle(), point, topicId);
        }

        return suc;
	}

	private void sendEmptyArtile(String key, String url, String docTitle,
			String source) throws Exception {

		// 放到CMS相应的栏目中

		// args are
		// docid,topicid,title,stitle,imgsrc,digest,daynum,lspri,ptime,modelmode,userid,nickname,source,url,search
		// 必填项 topicid, title, lspri, userid, nickname, source, url
		// 推送的栏目id，要拼的字符串，权重，用户id，昵称，新闻来源，分类对应的url
		// 机器推送userid=jiqiren, nickname="机器人"
		// title: [财经]</a> <a target="_blank"
		// href="http://money.163.com/13/1210/10/9FNQBQ2800252G50.html">中央经济工作会议在京开幕
		// 部署明年工作
		// url: http://money.163.com
		// <a href='url'>title</a>
		// StringBuilder title = new StringBuilder("[");
		// title.append(colNameMap.get(key)).append("]")
		// .append("</a> <a target=\"_blank\" href=\"").append(url)
		// .append("\">").append(docTitle).append("</a>");
		
		int i = 5;
		while(i-- >= 0){
			boolean suc = HessianUtil.getCmsWebService().insertBlankArticle(null,
				coltopic.get("CHANNAL_ID"), docTitle, null, null, null, 0, 75,
				null, null, "jiqiren", "机器人", source, url, null);			
			logger.info("[title: " + docTitle + ", suc:" + suc + ", url:" + url + ", source: " + source);
			if(suc)
				break;
			Thread.sleep(1000);
		}
	}

	private void sendEmptyArtile(List<Map> showhots, String topicId)
			throws Exception {
		if (showhots == null || topicId == null)
			return;

		for (int i = 0; i < showhots.size(); i++) {
			Map map = showhots.get(i);

			// 放到CMS相应的栏目中
			String col = (String) map.get("col");

			Doc doc = (Doc) map.get("cdoc");

			String docid = CommonUtil.get163DocidFromUrl(doc.getUrl());

            String source = "网易";
            try{
                // 取文章的来源
                Map<String, String> article = HessianUtil.getCmsWebService()
                        .getArticle(docid);
                source = article.get("source");
                if (source != null && source.indexOf("&") != -1)
                    source = source.substring(0, source.indexOf("&"));

                logger.info("采纳[" + col + "], url: " + doc.getUrl() + ", source: "
                        + source);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }


			// sendEmptyArtile(col, doc.getUrl(), doc.getTitle(), source, //
			// 75);

			String url = doc.getUrl();
			String docTitle = doc.getTitle();

			boolean suc = HessianUtil.getCmsWebService().insertBlankArticle(
					null, topicId, docTitle, null, null, null, 0, 90, null,
					null, "jiqiren", "机器人", source, url, null);

			logger.info("title: " + docTitle + ", suc : " + suc);

			// 推送过的文章存到数据库里 saveArt(doc.getDocno(), pointEnd, topicId);
			Thread.sleep(1000);
		}

		// args are
		// docid,topicid,title,stitle,imgsrc,digest,daynum,lspri,ptime,modelmode,userid,nickname,source,url,search
		// 必填项 topicid, title, lspri, userid, nickname, source, url
		// 推送的栏目id，要拼的字符串，权重，用户id，昵称，新闻来源，分类对应的url
		// 机器推送userid=jiqiren, nickname="机器人"
		// title: [财经]</a> <a target="_blank"
		// href="http://money.163.com/13/1210/10/9FNQBQ2800252G50.html">中央经济工作会议在京开幕
		// 部署明年工作
		// url: http://money.163.com
		// <a href='url'>title</a>
		// StringBuilder title = new StringBuilder("[");
		// title.append(colNameMap.get(key)).append("]")
		// .append("</a> <a target=\"_blank\" href=\"").append(url)
		// .append("\">").append(docTitle).append("</a>");
	}

	private void getCMSArticles(List<Map<String, String>> articleCMSList,
			String topicId) throws Exception {
		List<Map<String, String>> articleArr = CMSUtil.getListByTrans(topicId);

		for (Map map : articleArr) {
			// 找出机器人发送的文章
			logger.info(map.toString());
			if ("jiqiren".equals(map.get("userid"))) {
				String docno = ShortUrlGenerator.generatorAllStr((String) map
						.get("url"));
				if (docno == null) {
					logger.error("docno is null for " + map.get("url"));
				}
				map.put("docno", docno);
				articleCMSList.add(map);
			}
		}
	}

	private void saveArt(String docno, String title, int priority,
			String topicId) throws Exception {
		ArticlePushed art = new ArticlePushed();
		art.setPushtime(new Date());
		art.setDocno(docno);
		art.setTopicid(topicId);
		art.setPriority(priority);
		art.setTitle(title);
		articleService.saveOne(art);
	}

	private void saveArt(String docno) throws Exception {
		ArticlePushed art = new ArticlePushed();
		art.setPushtime(new Date());
		art.setDocno(docno);
		art.setTopicid(coltopic.get("CHANNAL_ID"));
		articleService.saveOne(art);
		logger.info("保存文章:{}, date:{}", docno, new Date());
	}

	private void refreshPriority(ArticlePushed article, int point) {
		if (article == null)
			return;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("autoid", article.getAutoid());
		param.put("priority", point);
		try {
			articleService.updateOne(param);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		logger.info("refresh DB point id:{} point:{}.", article.getAutoid(),point);
	}

	private void refreshPriority(List<ArticlePushed> pushedList) {
		if (pushedList == null)
			return;
		try {
			// 把之前的文章都改成80
			for (ArticlePushed article : pushedList) {
				if (!article.getPriority().equals(PRI)) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("autoid", article.getAutoid());
					param.put("priority", PRI);
					articleService.updateOne(param);
					logger.info("refresh DB point id:{} point:{}.",article.getAutoid(), PRI);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void getArticlePushedFromDB() {
		// 从数据库取出推送到CHANNAL_ID的24小时之内的新闻
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicid", coltopic.get("CHANNAL_ID"));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		param.put("starttime", cal.getTime());
		artPushedList = articleService.getDocnoListByParams(param);
	}

	private List<ArticlePushed> getArticlesPushedFromDB(String topicId) {
		// 从数据库取出推送到CHANNAL_ID的24小时之内的新闻
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicid", topicId);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		param.put("starttime", cal.getTime());
		logger.info("get from DB, starttime is " + cal.getTime());
		return articleService.getArticleListByParams(param);
	}

	public static List<Doc> catchHeadlines4News() throws Exception {
		String url = "http://news.163.com";

		String rs = HttpUtil.getURL(url, "GBK", null);
		List<Doc> dlist = new ArrayList<Doc>();
		if (rs != null && !StringUtil.isEmpty(rs)) {
			Document document = Jsoup.parse(rs);
			// 头条
//			Elements headline = document.select("div#news h2.bigsize a");
			Elements headline = document.select("div#news a");
			// 热点
			Elements hotnews = document.select("div.hot-news a");
			// 国内
			Elements guoneinews = document
					.select("div.domestic div.ui-til3-tab-c a");

			Elements eles = new Elements();
			eles.addAll(headline);
			eles.addAll(hotnews);
			eles.addAll(guoneinews);
			for (Element ele : eles) {
				Elements aeles = ele.select("a");
				for (Element aele : aeles) {
					Doc doc = new Doc();
					doc.setTitle(aele.text());
					doc.setUrl(aele.attr("href"));
					doc.setChannel("news");
					dlist.add(doc);
				}
			}
		}

		return dlist;
	}

	public static void main(String[] args) throws Exception {
		String topicId = args[0];		
		int point = Integer.valueOf(args[1]);
		String source = args[2];
		String url = args[3];
		String docTitle = "饲养员质疑动物园说法 不满大熊猫锦意饮食住宿";//苏州警方破获“2·13凶案”:凶手跨省杀三人被拘
		boolean suc = false;
		int retry = 10;
        while (!suc && retry > 0){
            suc = HessianUtil.getCmsWebService().insertBlankArticle(null,
                    topicId, docTitle, null, null, null, 0, point, null, null,
                    "jiqiren", "机器人", source, url, null);
            logger.info("[title: " + docTitle + ", point:" + point + ", suc:" + suc + ", url:" + url + ", source: " + source);
            retry--;
            Thread.sleep(1000);
        }
        
		/*List<Map<String, String>> articleCMSList = new ArrayList<Map<String, String>>();
		// 1、取CMS权重0-255的文章, articleCMSList中map都包含docno, 用来做判断用
		new GenFinanceNewsServiceImpl().getCMSArticles(articleCMSList,
				"00014R7I");
		logger.info("1、get Articles[point: 0-255] from CMS, size:"
				+ articleCMSList.size());

		for (Map<String, String> map : articleCMSList) {
			System.out.println(map.get("lspri"));
		}*/

		// logger.info(String.valueOf(GenFinanceNewsServiceImpl.catchHeadlines4News()));

		/*
		 * String result = CMSUtil.modiLspriCMS("002550IG", "9J3S2LM700011229",
		 * PRI); logger.info("modify point to {}, result:{}", PRI, result);
		 */

		/*
		 * Calendar cal = Calendar.getInstance();
		 * 
		 * String day = dateFormat.format(cal.getTime());
		 * 
		 * cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
		 * String yesterday = dateFormat.format(cal.getTime());
		 * 
		 * 
		 * Map<String, String>[] articleArr = HessianUtil .getCmsWebService()
		 * .getList( "topicid=" + FIRST + ";startday=" + yesterday + ";endday="
		 * + day + ";liststart=0;listnum=3;pointstart=100; pointend=139;"); for
		 * (Map<String, String> map : articleArr) { for (String key :
		 * map.keySet()) { System.out.println(key + "," + map.get(key)); } }
		 */
		/*
		 * modelmode:u lspri:128 topicid:0025421S nickname:霍峰岭 no:1 userid:huofl
		 * url:http://money.163.com/special/dsgl16/ docid:9IK8TO8Uhuofl
		 * title:5新股申购</a> source:网易财经 search:5 4862 7155 5522 7170 daynum:16085
		 * ptime:2014-01-15 08:24:10
		 */
	}
}
