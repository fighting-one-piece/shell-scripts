package com.netease.gather.clawerpic.execute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.netease.gather.clawerpic.parser.ClawerPic;
import com.netease.gather.clawerpic.util.UploadPicUtil;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class ClawerPicExecutor {
	private static final Logger logger = LoggerFactory.getLogger(ClawerPicExecutor.class);

	private static ExecutorService executor = Executors.newFixedThreadPool(5);
	
	@SuppressWarnings("unchecked")
	private static List<ClawerPicCall> getTasks(String groupid,String jobid) {
		List<ClawerPicCall> ret = new ArrayList<ClawerPicCall>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ClassPathResource("clawerpic.xml").getInputStream());

			List<Element> list = document.selectNodes("/root/entitycontext");
			for (Element entitycontext : list) {
				String curGroupid=entitycontext.attributeValue("groupid");
				if(StringUtils.isBlank(groupid)||curGroupid.equals(groupid)){
					List<Element> entitys =entitycontext.selectNodes("entity");
					for(Element entity:entitys){
						String curJobid = entity.attributeValue("jobid");
						if(StringUtils.isBlank(jobid)||curJobid.equals(jobid)){
							List<Element> sList = entity.selectNodes("source");
							String source = sList.get(0).getText();
							sList = entity.selectNodes("parser");
							String parser = sList.get(0).getText();
							if (StringUtils.isNotBlank(curJobid) && StringUtils.isNotBlank(source) && StringUtils.isNotBlank(parser)) {
								try {
									ClawerPic parserObject = (ClawerPic) Class.forName(parser).newInstance();
									ClawerPicCall call = new ClawerPicCall(curGroupid,curJobid, source, parserObject);
									ret.add(call);
								} catch (Exception e) {
									logger.error(e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	private static List<ClawerPicCall> getTasksByGroupids(String[] groupids) {
		List<ClawerPicCall> ret=new ArrayList<ClawerPicCall>();
		for(String gid:groupids){
			List<ClawerPicCall> list = getTasks(gid,null);
			ret.addAll(list);
		}
		return ret;
	}
	
	private static String getParamValue(String[] args,String name){
		for(int i=0;i<args.length;i++){
			if(args[i]!=null&&args[i].equals(name)){
				if(i+1<args.length)
					return args[i+1];
				else
					return null;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		try {
			CompletionService<LinkedHashMap<PictureSet, List<Picture>>> ecs = new ExecutorCompletionService<LinkedHashMap<PictureSet, List<Picture>>>(
					executor);
			
			String groupid=getParamValue(args,"-g");
			String job=getParamValue(args,"-j");
			
			List<ClawerPicCall> list = null;
			if (StringUtils.isNotBlank(groupid) && groupid.contains(",")) {
				list = getTasksByGroupids(groupid.split(","));
			} else {
				list = getTasks(groupid, job);
			}
			for (ClawerPicCall call : list) {
				ecs.submit(call);
			}
			
			for (int i = 0; i < list.size(); ++i) {
				try {
					LinkedHashMap<PictureSet, List<Picture>> r = ecs.take().get();
					PictureSet last=null;
					if (r != null) {
						for (Entry<PictureSet, List<Picture>> entry : r.entrySet()) {
							PictureSet ps = entry.getKey();
							List<Picture> pics = entry.getValue();
							UploadPicUtil.uploadPhoto(ps, pics, ps.getAtLeast());
							last=ps;
						}
					}
					if(last!=null){
						logger.info("{} job's work finish.",last.getJobid());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			long t2 = System.currentTimeMillis();
			logger.info("all work finish cost time:{} s,groupid:{},jobid:{}", (t2 - t1) / 1000, groupid, job);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			executor.shutdown();
			System.exit(0);
		}
	}

}
