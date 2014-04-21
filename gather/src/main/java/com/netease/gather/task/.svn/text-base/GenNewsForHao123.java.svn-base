package com.netease.gather.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.context.ScheduleContext;

public class GenNewsForHao123 {
	private static final Logger logger = LoggerFactory
			.getLogger(GenNewsForHao123.class);

	public static void main(String[] args) {
		try {
			logger.info("开始为Hao123生成社会新闻...");
			logger.info(Arrays.deepToString(args));
			String channel = "news";
			if (args.length > 0) {
				channel = args[0];
			}
			String starttime = "";
			String endtime = "";
			if (args.length > 1) {
				starttime = args[1];
			} else {
				DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				Calendar cal = TimeControl.clawEndTime();
				cal.add(Calendar.MINUTE, -60);
				starttime = df.format(cal.getTime());
			}

			if (args.length > 2) {
				endtime = args[2];
			} else {
				DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				endtime = df.format(TimeControl.clawEndTime().getTime());
			}

			ScheduleContext.FACADE.getGenNewsForHao123Service()
					.getSocialNewsForHao123(channel, starttime, endtime, 2, 2);
			logger.info("为Hao123生成社会新闻结束 .");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
