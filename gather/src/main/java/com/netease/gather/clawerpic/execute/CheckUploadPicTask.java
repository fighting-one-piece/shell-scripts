package com.netease.gather.clawerpic.execute;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.PoPo;
import com.netease.gather.domain.PictureSet;

public class CheckUploadPicTask {

	private static final Logger logger = LoggerFactory.getLogger(ClawerPicExecutor.class);

	private static String getParamValue(String[] args, String name) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && args[i].equals(name)) {
				if (i + 1 < args.length)
					return args[i + 1];
				else
					return null;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> getJobids(String groupid) {
		List<String> ret = new ArrayList<String>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ClassPathResource("clawerpic.xml").getInputStream());

			List<Element> list = document.selectNodes("/root/entitycontext");
			for (Element entitycontext : list) {
				String curGroupid=entitycontext.attributeValue("groupid");
				if(curGroupid.equals(groupid)){
					List<Element> entitys =entitycontext.selectNodes("entity");
					for(Element entity:entitys){
						String curJobid = entity.attributeValue("jobid");
						ret.add(curJobid);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private static String getUrlByJobid(String jobid) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ClassPathResource("clawerpic.xml").getInputStream());
			
			List<Element> list = document.selectNodes("/root/entitycontext");
			for (Element entitycontext : list) {
					List<Element> entitys =entitycontext.selectNodes("entity");
					for(Element entity:entitys){
						String curJobid = entity.attributeValue("jobid");
						if(curJobid.equals(jobid)){
							String url=entity.element("comment").element("url").getText();
							return url;
						}
					}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static void sendPoPo(String msg) {
		if (msg.length() > 500) {
			int pos = msg.indexOf("；", 500);
			if (pos != -1) {
				PoPo.send(msg.substring(0, pos + 1));
//				System.out.println(msg.substring(0, pos + 1)+"<");
				sendPoPo(msg.substring(pos + 1));
			} else {
				PoPo.send(msg.substring(0, 500));
//				System.out.println(msg.substring(0, 500)+"<");
				sendPoPo(msg.substring(500));
			}
		} else {
			PoPo.send(msg);
//			System.out.println(msg+"<");
		}
	}


	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		try {
			String groupid = getParamValue(args, "-g");
			assert StringUtils.isNotBlank(groupid) : "groupid is blank!";
			String day = getParamValue(args, "-d");
			if(StringUtils.isBlank(day)){
				day="1";
			}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(day)*(-1));
			Date start = cal.getTime();
			
//						String groupid = "tour";
//						String isAll = "y";

			String[] gArr = groupid.split(",");
			StringBuilder sb=new StringBuilder();
			sb.append("最近").append(day).append("天:\r\n");
			for (String gid : gArr) {
				List<String> jobLst=getJobids(gid);
				
				List<Map<String,Object>> lst=ScheduleContext.FACADE.getPictureSetService().statUploadInfo(gid, start);
				Map<String,Map> map=new HashMap<String,Map>();
				for(Map m:lst){
					map.put(m.get("jobid")+"", m);
				}
				
				for(String jid:jobLst){
					Map m=map.get(jid);
					if(m==null){
						sb.append(jid).append("上传0个图集，0张图片；\r\n");
						PictureSet last=ScheduleContext.FACADE.getPictureSetService().getLastPictureSet(jid);
						if(last!=null){
							sb.append("最近上传的图集标题是：《").append(last.getTitle()).append("》,请打开索引页检查：").append(getUrlByJobid(jid)).append("；\r\n");
						}
					}else{
						sb.append(jid).append("上传").append(m.get("psnum")).append("个图集，").append(m.get("picnum")).append("张图片；\r\n");
					}
				}
			}
//			System.out.println(sb.toString());
			sendPoPo(sb.toString());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			System.exit(0);
		}
	}
}
