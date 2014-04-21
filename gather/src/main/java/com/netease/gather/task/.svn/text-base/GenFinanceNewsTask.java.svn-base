package com.netease.gather.task;

import com.netease.gather.clawer.TimeControl;
import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.PoPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class GenFinanceNewsTask {
	private static final Logger logger = LoggerFactory
			.getLogger(GenFinanceNewsTask.class);

	public static void main(String[] args) throws Exception {
		try {
			logger.info("开始收集财经新闻");
			logger.info(Arrays.deepToString(args));
			String channel = "finance";
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
			ScheduleContext.FACADE.getGenFinanceNewsService()
					.genAndSendFinanceNews(channel, starttime, endtime, 2, 2);
			logger.info("收集财经新闻完毕");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
            PoPo.send("ykxu@corp.netease.com", "genAndSendFinanceNews计算出错！\n" + e.getMessage() + Arrays.toString(e.getStackTrace()));
		} finally {
			System.exit(0);
		}
	}
}
