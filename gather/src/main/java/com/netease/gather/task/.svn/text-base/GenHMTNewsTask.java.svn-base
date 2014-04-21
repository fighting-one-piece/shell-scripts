package com.netease.gather.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.gather.common.context.ScheduleContext;

public class GenHMTNewsTask {
	private static final Logger logger = LoggerFactory
			.getLogger(GenHMTNewsTask.class);

	public static void main(String[] args) throws Exception {
		try {
			logger.info("开始收集港澳台新闻");
			ScheduleContext.FACADE.getGetHMTNewsService()
					.getHMTNewsAndSendToCMS();
			logger.info("收集港澳台新闻完毕");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			System.exit(0);
		}
	}
}
