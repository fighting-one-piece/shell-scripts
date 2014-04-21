package com.netease.gather.clawerpic.test;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import com.netease.gather.clawerpic.parser.ClawerPic;
import com.netease.gather.clawerpic.util.UploadPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class TestPictureSet {
	@SuppressWarnings("unchecked")
	private static ClawerPic getServiceByJobid(String jobid0){
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ClassPathResource("clawerpic.xml").getInputStream());

			List<Element> list = document.selectNodes("/root/entitycontext/entity");
			for (Element element : list) {
				String jobid = element.attributeValue("jobid");
				if(jobid.equals(jobid0)){
					List<Element> sList = element.selectNodes("source");
//					String source = sList.get(0).getText();
					sList = element.selectNodes("parser");
					String parser = sList.get(0).getText();
					
					ClawerPic parserObject = (ClawerPic) Class.forName(parser).newInstance();
					parserObject.setJobid(jobid);
					
					return parserObject;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static void addMoreValueToPS(PictureSet ps,String jobid0){
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ClassPathResource("clawerpic.xml").getInputStream());

			List<Element> list = document.selectNodes("/root/entitycontext/entity");
			for (Element element : list) {
				String jobid = element.attributeValue("jobid");
				if(jobid.equals(jobid0)){
					List<Element> sList = element.selectNodes("source");
					String source = sList.get(0).getText();
					
					ps.setJobid(jobid0);
					ps.setSource(source);
					String groupid=element.getParent().attributeValue("groupid");
					ps.setGroupid(groupid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		try {
			
			String jobid="carnoc_news";
			ClawerPic c= getServiceByJobid(jobid);
			
			PictureSet ps = new PictureSet();
			ps.setUrl("http://news.carnoc.com/list/274/274325.html");
			ps.setTitle("图集：海口美兰机场春运后期客流量出现小高峰");
//			ps.setAuthor("&碧水&");
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//			ps.setPtime(sdf.parse("2014-03-02 20:39:26"));
			addMoreValueToPS(ps,jobid);
			
			List<Picture> pics = c.getPictureList(ps);
			
			System.out.println(ps.getTitle() + " " + ps.getUrl() + " picNum:" + pics.size() + " " + ps.getSummary());
			
//			for(int i=0;i<ps.getTitle().length();i++){
//				char ch=ps.getTitle().charAt(i);
//				System.out.println(ch+"-"+Integer.toHexString(ch));
//			}
//			for(Picture p:pics){
//				System.out.println(p.getUrl());
//			}
			
			UploadPicUtil.uploadPhoto(ps, pics);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
