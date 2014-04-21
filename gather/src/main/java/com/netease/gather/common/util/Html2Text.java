package com.netease.gather.common.util;

import com.netease.gather.summary.BasicMatrixCompute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;


public class Html2Text {
	
	public static String html2Text(String inputString) {    
        
		String htmlStr = inputString; // 含html标签的字符串    
        String textStr = "";    
        java.util.regex.Pattern p_script;    
        java.util.regex.Matcher m_script;    
        java.util.regex.Pattern p_style;    
        java.util.regex.Matcher m_style;    
        java.util.regex.Pattern p_html;    
        java.util.regex.Matcher m_html;    
  
        java.util.regex.Pattern p_html1;    
        java.util.regex.Matcher m_html1;    
  
       try {    
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>    
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>    
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式    
            String regEx_html1 = "<[^>]+";    
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);    
            m_script = p_script.matcher(htmlStr);    
            htmlStr = m_script.replaceAll(""); // 过滤script标签    
  
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);    
            m_style = p_style.matcher(htmlStr);    
            htmlStr = m_style.replaceAll(""); // 过滤style标签    
  
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);    
            m_html = p_html.matcher(htmlStr);    
            htmlStr = m_html.replaceAll(""); // 过滤html标签    
  
            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);    
            m_html1 = p_html1.matcher(htmlStr);    
            htmlStr = m_html1.replaceAll(""); // 过滤html标签    
  
            textStr = htmlStr;    
  
        } catch (Exception e) {    
            System.err.println("Html2Text: " + e.getMessage());    
        }    
  
       return textStr;// 返回文本字符串    
    }

    public static String tidySentence(String text,boolean isAotuSubject){

        if(isAotuSubject){
            Document d = Jsoup.parse(text);
            StringBuffer sb = new StringBuffer();
            Elements elements = d.getElementsByTag(BasicMatrixCompute._P);
            if(elements != null && elements.size() > 0){
                Iterator<Element> iterator = elements.iterator();
                for(;iterator.hasNext();){
                    Element one = iterator.next();
                    String className = one.className();
                    if(!BasicMatrixCompute._F_CENTER.equalsIgnoreCase(className)){
                        String textOne = one.text();

                        if(textOne != null && textOne.length() > 0 && !BasicMatrixCompute.NIL_STR.equalsIgnoreCase(textOne)){
                            if(textOne.contains(BasicMatrixCompute.ZW_JH) && !textOne.contains(BasicMatrixCompute.SOURCE_TITLE)){
                                char lastCharacter = textOne.trim().charAt(textOne.length() -1 );
                                if(lastCharacter != '。' && lastCharacter != '？' && lastCharacter != '！'
                                        && lastCharacter != '.'&& lastCharacter != '!' && lastCharacter != '?'
                                        && lastCharacter != ':' &&  lastCharacter != '：' && lastCharacter != ','
                                        && lastCharacter != '，'){
                                    textOne = textOne + "。";
                                }
                                sb.append(genSubject(textOne));
                            }
                        }
                    }
                }
            }
            return StringUtil.removeBracket(sb.toString());
        }else{
            return StringUtil.removeBracket(text);
        }
    }

	public static String genSubject(String srcBody) {
		
		try{
			String body = srcBody.replace("&nbsp;", "").replace("&#8203;", "").replace("&#160;", "").replace("&#8226;", "·").replace("(", "（").replace(")", "）");//替换英文括弧为中文括弧，目的：防止replaceFirst匹配正则
			String digest = "";
			Set<String> picStrongs = new HashSet<String>();
			Set<String> picFonts = new HashSet<String>();
			Set<String> aligns = new HashSet<String>();
			Set<String> styles = new HashSet<String>();
			Document d = Jsoup.parse(body);
			if(null==digest || digest.length()==0){
				if( body.trim().length()>3001){
					body = body.trim().substring(0, 3000);
				}

				Elements picStrongE = d.getElementsByTag("strong");
				for(Element e: picStrongE){
					picStrongs.add(e.text());
				}
				Elements picFontsE = d.getElementsByTag("font");
				for(Element e: picFontsE){
					picFonts.add(e.text());
				}
				Elements picAligns = d.getElementsByAttribute("align");
				for(Element align: picAligns){
					if(align.attr("align").trim().equals("center")){
						aligns.add(align.text());
					}
				}
				Elements picStyles = d.getElementsByAttribute("style");
				for(Element style: picStyles){
					if(style.attr("style").trim().replaceAll(":", "").replaceAll(" ", "").contains("text-aligncenter")){
						styles.add(style.text());
					}
				}
				String bodyString = Jsoup.parse(body).text();
				digest = bodyString.trim();
			}
			//digest = digest.replace("\\\"", "\"").replace("核心提示：", "").replace("　　", " ");
			if(picFonts.size()>0){
					for(String picf : picFonts){
						if(picf.contains("(") && digest.indexOf(picf)!=-1){
							digest = digest.substring(0, digest.indexOf(picf)).concat(digest.substring(digest.indexOf(picf)+picf.length()));
						}else{
							digest = digest.replaceFirst(picf, "");
						}
					}
			}
			if(picStrongs.size()>0){
					for(String pics : picStrongs){
						digest = digest.replaceFirst(pics, "");
					}
			}
			if (styles.size() > 0) {
				for (String style : styles) {
					if(!picFonts.contains(style)){
						digest = digest.replaceFirst(style, "");
					}
				}
			}
			if (aligns.size() > 0) {
				for (String align : aligns) {
					if(!picFonts.contains(align) && !styles.contains(align)){
						digest = digest.replaceFirst(align, "");
					}
				}
			}
			if(digest.trim().startsWith("<a")){
				digest = "";
			}
			
			//digest = digest.trim().replace("\\\"", "\"").replace("核心提示：", "").replace("　　", " ");
			if(digest.trim().startsWith("[")){
				String rt = digest.substring(0, digest.length()).trim();
				if(rt.trim().startsWith("]")){
					digest = rt.substring(0, rt.length()).trim();
				}
			}
			
			if(digest.trim().startsWith("（）")){
				digest = StringUtil.replaceStart(digest, "（）");
			}
	
			return StringUtil.escapeHTMLTag(Jsoup.clean(digest, Whitelist.none())).trim();
		} catch(Exception e) {
		}
		return null;
	}
	
	public static void main(String[] args){
	}
}
