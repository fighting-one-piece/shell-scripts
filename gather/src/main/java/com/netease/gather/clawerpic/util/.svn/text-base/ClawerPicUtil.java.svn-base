package com.netease.gather.clawerpic.util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClawerPicUtil {

	private static final Logger logger = LoggerFactory.getLogger(ClawerPicUtil.class);

	public static String getByUnicode(String unicodeStr) {
		Matcher matcher = Pattern.compile("\\\\u[a-fA-F0-9]{4}").matcher(unicodeStr);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, (char) Integer.parseInt(matcher.group().substring(2), 16) + "");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	public static boolean isContain(Node e,Node sub){
		List<Node> lst=e.childNodes();
		if(lst.contains(sub)){
			return true;
		}
		for(Node n:lst){
			boolean isContain=isContain(n,sub);
			if(isContain){
				return true;
			}
		}
		return false;
	}

	private static String getElementStringFromEnd(Node node) {
		String text = "";
		if (node instanceof Element) {
			text = removeBlankText(((Element) node).text());
		} else {
			Document doc = Jsoup.parse(node.toString());
			text = removeBlankText(doc.text());
		}

		if (StringUtils.isNotBlank(text)) {
			if (text.matches(".*(。|！|？)$")) {
				StringBuffer ret = new StringBuffer();
				text = text.replaceAll("\\$", "\\$\\\\");
				Matcher m = Pattern.compile("(。|！|？)[^。！？]+").matcher(new StringBuilder(text).reverse().toString());
				int i = 0;
				while (m.find()) {
					if (i == 2)
						break;
					m.appendReplacement(ret, m.group());
					i++;
				}
				String str = ret.reverse().toString();
				return str;
			} else {
				if (text.contains("。")) {
					int p = text.lastIndexOf("。");
					return text.substring(p + 1);
				} else {
					if (removeBlankText(text.replaceAll("[\\s|\\d|。|、|？|！]", "")).length() > 3) {
						return text;
					} else {
						return "";
					}
				}
			}
		} else
			return "";
	}

	public static String getPreviousString(Element img, Element textBody) {
		Node node = img.previousSibling();
		while (node != null) {
			String text = getElementStringFromEnd(node);
			if (StringUtils.isNotBlank(text)) {
				return removeBlankText(text);
			} else {
				node = node.previousSibling();
			}
		}
		if (img.parent().equals(textBody)) {
			return "";
		}
		return getPreviousString(img.parent(), textBody);
	}

	private static String getElementStringFromStart(Node e) {
		String text = "";
		if (e instanceof Element) {
			text = removeBlankText(((Element) e).text());
		} else {
			Document doc = Jsoup.parse(e.toString());
			text = removeBlankText(doc.text());
		}
		if (StringUtils.isNotBlank(text)) {
			if (text.matches(".*(。|！|？).*")) {
				StringBuffer ret = new StringBuffer();
				text = text.replaceAll("\\$", "\\\\\\$");
				Matcher m = Pattern.compile(".+?(。|！|？)+").matcher(text);
				while (m.find()) {
					m.appendReplacement(ret, m.group());
					break;
				}
				return ret.toString();
			} else {
				if (text.replaceAll("[\\s|\\d|。|、|？|！|\\.]", "").length() > 3) {
					return text;
				} else {
					return "";
				}
			}
		} else {
			return "";
		}
	}

	public static String getNextString(Element img, Element textBody) {
		Node e = img.nextSibling();
		while (e != null) {
			String text = getElementStringFromStart(e);
			if (StringUtils.isNotBlank(text)) {
				return text;
			} else {
				e = e.nextSibling();
			}
		}
		if (img.parent().equals(textBody)) {
			return "";
		}
		return getNextString(img.parent(), textBody);
	}
	
	private static int getElementStringFromStart(Node e, int limit, Element utilElement,StringBuilder sb) {
		String text = "";
		boolean isContain=false;
		if (e instanceof Element) {
			text = removeBlankText(((Element) e).text());
			if (utilElement != null) {
				isContain=isContain(e,utilElement);
				if(isContain){
					String utilText = removeBlankText(utilElement.text());
					if (text.indexOf(utilText) != -1) {
						text = text.substring(0, text.indexOf(utilText));
					}
				}
			}
		} else {
			Document doc = Jsoup.parse(e.toString());
			text = removeBlankText(doc.text());
			if (utilElement != null) {
				isContain=isContain(e,utilElement);
				if(isContain){
					String utilText = removeBlankText(utilElement.text());
					if (text.indexOf(utilText) != -1) {
						text = text.substring(0, text.indexOf(utilText));
					}
				}
			}
		}
		if (StringUtils.isNotBlank(text)) {
			if (text.matches(".*(。|！|？).*")) {
				StringBuffer ret = new StringBuffer();
				text = text.replaceAll("\\$", "\\\\\\$");
				Matcher m = Pattern.compile(".+?(。|！|？)+").matcher(text);
				int i = 0;
				while (m.find()) {
					i++;
					m.appendReplacement(ret, m.group());
					if (i == limit) {
						break;
					}
				}
				sb.append(ret.toString());
				if(isContain){
					return 0;
				}
				return limit-i;
			} else {
				if (text.replaceAll("[\\s|\\d|。|、|？|！|\\.]", "").length() > 3) {
					sb.append(text);
					if(isContain){
						return 0;
					}
					return limit-1;
				} else {
					if(isContain){
						return 0;
					}
					return limit;
				}
			}
		} else {
			if(isContain){
				return 0;
			}
			return limit;
		}
	}
	
	private static Node getNextNode(Node node,Node utilElement){
		if (node == null) {
			return null;
		}
		if(node.nextSibling()!=null){
			if(!node.nextSibling().equals(utilElement))
				return node.nextSibling();
			else
				return null;
		}else{
			return getNextNode(node.parent(),utilElement);
		}
	}

	public static String getNextString(Element img, int limit) {
		return getNextString(img, limit,null);
	}

	public static String getNextString(Element img, int limit, Element utilElement) {
		if (img == null) {
			return "";
		}
		Node next = getNextNode(img, utilElement);
		if (next == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Node cur = next;
		while (limit > 0) {
			limit = getElementStringFromStart(cur, limit, utilElement, sb);
			if (limit > 0) {
				cur = getNextNode(cur, utilElement);
				if (cur == null) {
					limit = 0;
				}
			}
		}
		if (StringUtils.isNotBlank(sb.toString())) {
			return sb.toString();
		} else {
			return "";
		}
	}
	
	public static String removeBlankText(String text) {
		if (text == null) {
			return "";
		}
		text = text.trim();
		text = text.replaceAll("\u00a0", "");
		text = text.replaceAll("\u0020", "");
		text = text.replaceAll("\u3000", "");
		text = text.replaceAll("\u00b7", "");
		text = text.replace("\\n", "");
		text = text.trim();
		return text;
	}

	public static Document getDocByUrl(String url, final String charSet) {
		String html = getHtmlByUrl(url, charSet, 3);
		return Jsoup.parse(html);
	}
	
	public static String getHtmlByUrl(String url, final String charSet) {
		return getHtmlByUrl(url, charSet, 3);
	}

	private static PoolingClientConnectionManager cm = null;
	private static HttpClient httpClient = null;

	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		cm = new PoolingClientConnectionManager(schemeRegistry);
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);

		//			// Increase max connections for localhost:80 to 50
		//			HttpHost localhost = new HttpHost("locahost", 80);
		//			cm.setMaxPerRoute(new HttpRoute(localhost), 50);

		httpClient = new DefaultHttpClient(cm);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
	}

	private static HttpClient getHttpClient() {
		return httpClient;
	}

	public static String getHtmlByUrl(String url, final String charSet, int tryNum) {
		if (tryNum <= 0) {
			logger.warn("finish try and return null when url is:{}", url);
			return "";
		}
		HttpClient httpclient = getHttpClient();
		//		HttpClient httpclient = new DefaultHttpClient();
		//		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		//		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		//		httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

		//		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		//			@Override
		//			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		//				return EntityUtils.toString(response.getEntity(), charSet);
		//			}
		//		};

		HttpEntity entity = null;
		try {
			HttpGet getHtml = new HttpGet(url);
			//			String html = httpclient.execute(getHtml, responseHandler);
			HttpResponse response = httpclient.execute(getHtml);
			entity = response.getEntity();
			String html = EntityUtils.toString(entity, charSet);
			return html;
		} catch (Exception e) {
			logger.error(e.getMessage() + " when url is:" + url);
		} finally {
			//			httpclient.getConnectionManager().shutdown();
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		tryNum--;
		logger.info("try again when url is:{}", url);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return getHtmlByUrl(url, charSet, tryNum);
	}

	public static String getRealUrl(String url) {
		HttpClient httpClient = getHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);
		HttpEntity entity = null;
		try {
			//将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			entity = response.getEntity();
			//获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
			HttpHost targetHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			//获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
			HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			//            System.out.println("主机地址:" + targetHost);
			//            System.out.println("URI信息:" + realRequest.getURI());
			return targetHost.toString() + realRequest.getURI().toString();
			//            HttpEntity entity = response.getEntity();
			//            if(null != entity){
			//                System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));
			//                EntityUtils.consume(entity);
			//            }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			//            httpClient.getConnectionManager().shutdown();
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String str = "莉迪亚·帕尼基夫";
		for(int i = 0 ; i < str.length() ; i++){
			char a = str.charAt(i);
			System.out.println(a + "  , " + Integer.toHexString(a));
		}
		//System.out.println(getRealUrl("http://pic.daqi.com/bbs/00/3561553.html"));
		//System.out.println(ClawerPicUtil.removeBlankText("&nbsp; &nbsp; 《国家地理》（National Geographic，原名《国家地理杂志》）是美国国家地理学会的官方杂志。在过去的几年，杂志的开篇文章总是涉及到环境，森林砍伐，化学污染，全球变暖和濒危物种，一系列的主题远远超过了地理探索的好奇心。"));
	}
}
