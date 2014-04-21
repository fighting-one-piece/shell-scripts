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

public class GenNewsByChannelTask {
	private static final Logger logger = LoggerFactory
			.getLogger(GenNewsByChannelTask.class);

	public static void main(String[] args) throws Exception {
		try {

			logger.info(Arrays.deepToString(args));
			String channel = null;
			if (args.length > 0) {
				channel = args[0];
			} else {
				logger.info("no channel specified");
				System.exit(0);
			}
			logger.info("开始收集{}新闻", channel);

			String starttime = "";
			String endtime = "";
			if (args.length > 1) {
				starttime = args[1];
			} else {
				DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				Calendar cal = TimeControl.clawEndTime();
				cal.add(Calendar.MINUTE, -120);
				starttime = df.format(cal.getTime());
			}

			if (args.length > 2) {
				endtime = args[2];
			} else {
				DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				endtime = df.format(TimeControl.clawEndTime().getTime());
			}

			String col = "";

			if (args.length > 3) {
				col = args[3];
				logger.info("开始收集{}新闻", col);
				ScheduleContext.FACADE.getGenFinanceNewsService()
						.genAndSendNewsByCol(col, channel, starttime, endtime,
								2, 2);
			} else {
				throw new Exception("参数少于3个，需要栏目名");
			//	ScheduleContext.FACADE
				//		.getGenFinanceNewsService()
					//	.genAndSendSheHuiNews(channel, starttime, endtime, 2, 2);
			}
			logger.info("收集{}新闻完毕", channel);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
            PoPo.send("ykxu@corp.netease.com", "genAndSendSheHuiNews计算出错！\n" + e.getMessage() + Arrays.toString(e.getStackTrace()));
		} finally {
			System.exit(0);
		}
	}
}
