package com.netease.gather.common.util;

import com.caucho.hessian.client.HessianProxyFactory;
import com.netease.cms.service.CmsWebService;
import com.netease.photo.service.PhotoService;
import com.netease.photo.service.PhotoService240;
import com.netease.tag.service.TagWebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class HessianUtil {
	private static final Logger logger = Logger.getLogger(HessianUtil.class);

	private static Map<String, Object> cache = new HashMap<String, Object>();
	private static HessianProxyFactory factory= new HessianProxyFactory();
//	static{
//		factory.setChunkedPost(false); //这是因为 Hessian 与服务端通信默认是采取分块的方式 (chunked encoding) 发送数据，而反向代理要获得 Content-Length 这个头才能处理请求，设为false可以不分块发送
//	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> clazz, String url) {
		if (clazz == null || StringUtils.isEmpty(url))
			return null;

		T service = null;

		Object o = null;
		if ((o = cache.get(clazz.getClass().getName() + url)) != null) {
			return (T) o;
		}

		try {
            factory.setOverloadEnabled(true);
			service = (T) factory.create(clazz, url);
		} catch (MalformedURLException e) {
			logger.info("获取webservcie错误，请检查您的配置");
			logger.error(e, e);
		}

		cache.put(clazz.getClass().getName() + url, service);

		return service;
	}

//	public static VideoLibWebService getVideoLibWebService() {
//		return create(VideoLibWebService.class, "http://ws.v.163.com/remote/videoinfo.do");
//	}
//
//	public static MarketService getMarketService() {
//		return create(MarketService.class, "http://ws.m.163.com/hessian/service");
//	}
//
	public static PhotoService240 getPhotoService240() {
		return create(PhotoService240.class, "http://oracle.service.240.photo.163.com/pic");
	}
	
    public static PhotoService getPhotoService() {
		return create(PhotoService.class, "http://oracle.service.photo.163.com/pic");
	}
	
	public static CmsWebService  getCmsWebService() {
		return create(CmsWebService.class, "http://ws.cms.163.com/webservice/CmsWebService");
	}
	
	public static TagWebService  getTagWebService() {
		return create(TagWebService.class, "http://go.ws.netease.com/tag/service");
	}

	public static void main(String arg[]) throws Exception {
//		List photos = getVideoLibWebService().getVideoByTag("三星", 1, 0, 10);
//		for (Object map : photos) {
//			System.out.println(map);
//			String[] tags = (String[]) ((Map) map).get("tags");
//			for (String tag : tags) {
//				System.out.println(tag);
//			}
//		}
		
//		Map<String, String>[] photos2 = getCmsWebService().getList("topicid=0001124K;startday=2013-10-10;endday=2013-10-10;liststart=0;listnum=20;");
		Map<String, String> doc = getCmsWebService().getArticle("9G6I5Q5J00252G50");
//		Map<String, String>[] photos2 = getCmsWebService().getArticleList("topicid=00052UUB;liststart=0;listnum=20;");
//		Map<String, String>[] photos2 = getCmsWebService().get;
//        System.out.println(Arrays.toString(photos2));
//        System.out.println(photos2.length);
//        String context = Jsoup.clean(doc.get("body"), Whitelist.none()).replace("&nbsp;"," ").replace("&middot;","·");
//        System.out.println(context);
        System.out.println(doc);
//		for (Object map : photos2) {
//			System.out.println(map);
////			String[] tags = (String[]) ((Map) map).get("tags");
////			for (String tag : tags) {
////				System.out.println(tag);
////			}
//		}
		
//		List photos3 = getMarketService().getWebNewSubject(2);
//		for (Object map : photos3) {
//			System.out.println(map);
//		}
//		List photos3 = getMarketService().getGameNcssEachCtgr(2,6);
//		for (Object map : photos3) {
//			System.out.println(map);
//		}
//
//		List photos4 = getPhotoService240().getDelPhotoList();
//		for (Object map : photos4) {
//			System.out.println(map);
//		}
	}

}
