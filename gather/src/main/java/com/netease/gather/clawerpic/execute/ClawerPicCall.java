package com.netease.gather.clawerpic.execute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.gather.clawerpic.parser.ClawerPic;
import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class ClawerPicCall implements Callable<LinkedHashMap<PictureSet, List<Picture>>> {
	private static final Logger logger = LoggerFactory.getLogger(ClawerPicCall.class);

	private String groupid;
	private String jobid;
	private String source;
	private ClawerPic parser;

	ClawerPicCall(String groupid,String jobid, String source, ClawerPic parser) {
		this.groupid = groupid;
		this.jobid = jobid;
		this.source = source;
		this.parser = parser;
		parser.setJobid(jobid);
	}
	
	public String toString() {
		return new StringBuilder("groupid:").append(groupid).append(",jobid:").append(jobid).append(",source:").append(source).append(",parser:").append(parser.getClass().getName())
				.toString();
	}

//	private CountDownLatch doneSignal;
//	public void setDoneSignal(CountDownLatch doneSignal) {
//		this.doneSignal = doneSignal;
//	}
//
//	@Override
//	public void run() {
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		try {
//			Future<LinkedHashMap<PictureSet, List<Picture>>> future = executor.submit(new Callable<LinkedHashMap<PictureSet, List<Picture>>>() {
//				@Override
//				public LinkedHashMap<PictureSet, List<Picture>> call() throws Exception {
//					long t1 = System.currentTimeMillis();
//					PictureSet last = ScheduleContext.FACADE.getPictureSetService().getLastPictureSet(jobid);
//					List<PictureSet> list = parser.getNewPictureSet(last);
//					logger.info("{} job getNewPictureSet number is:{}", jobid, list.size());
//					LinkedHashMap<PictureSet, List<Picture>> map = new LinkedHashMap<PictureSet, List<Picture>>();
//					for (int i = list.size() - 1; i >= 0; i--) {
//						PictureSet p = list.get(i);
//						List<Picture> pics = parser.getPictureList(p);
//						map.put(p, pics);
//					}
//					long t2 = System.currentTimeMillis();
//					logger.info("{} job claw page cost time:{} s", jobid, (t2 - t1) / 1000);
//					return map;
//				}
//			});
//
//			LinkedHashMap<PictureSet, List<Picture>> map = future.get();
//			for (Entry<PictureSet, List<Picture>> entry : map.entrySet()) {
//				PictureSet ps = entry.getKey();
//				List<Picture> pics = entry.getValue();
//				UploadPicUtil.uploadPhoto(ps, pics);
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage() + " when source is " + source, e);
//		} finally {
//			executor.shutdown();
//			doneSignal.countDown();
//		}
//	}

	@Override
	public LinkedHashMap<PictureSet, List<Picture>> call() {
		long t1 = System.currentTimeMillis();
		PictureSet last = null;
		try {
			last = ScheduleContext.FACADE.getPictureSetService().getLastPictureSet(jobid);
			logger.info("{} job lastPictureSet is null:{}, url is {}", jobid, last == null, last == null ? "" : last.getUrl());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<PictureSet> list = parser.getNewPictureSet(last);
		logger.info("{} job getNewPictureSet number is:{}", jobid, list.size());
		LinkedHashMap<PictureSet, List<Picture>> map = new LinkedHashMap<PictureSet, List<Picture>>();
		boolean sortByTime = true;
		for (int i = list.size() - 1; i >= 0; i--) {
			PictureSet p = list.get(i);
			p.setGroupid(groupid);
			p.setJobid(jobid);
			p.setSource(source);
			try {
				logger.info("{} job begin getPictureList: ps.url is {}", jobid, p.getUrl());
				List<Picture> pics = parser.getPictureList(p);
				logger.info("{} job finish getPictureList: ps.url is {}", jobid, p.getUrl());
				if (pics != null) {
					map.put(p, pics);
					if (p.getPtime() == null) {
						sortByTime = false;
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage()+" when ps.url is:"+p.getUrl(),e);
			}
		}
		long t2 = System.currentTimeMillis();
		logger.info("{} job claw page cost time:{} s", jobid, (t2 - t1) / 1000);
		if (sortByTime) {
			List<PictureSet> kl = new ArrayList<PictureSet>();
			kl.addAll(map.keySet());
			Collections.sort(kl, new Comparator<PictureSet>() {
				@Override
				public int compare(PictureSet o1, PictureSet o2) {
					return o1.getPtime().compareTo(o2.getPtime());
				}
			});
			logger.info("{} job sort {} PictureSet by ptime.", jobid, kl.size());
			LinkedHashMap<PictureSet, List<Picture>> map2 = new LinkedHashMap<PictureSet, List<Picture>>();
			for (PictureSet ps : kl) {
				if (last == null || ps.getPtime().compareTo(last.getPtime()) > 0) {
					map2.put(ps, map.get(ps));
				}else{
					logger.info("discard by time:"+ps.toString());
				}
			}
			return map2;
		} else
			return map;
	}
}
