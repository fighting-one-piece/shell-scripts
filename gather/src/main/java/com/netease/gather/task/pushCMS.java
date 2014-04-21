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

/**
*
*/
public class pushCMS {

	private static final Logger logger = LoggerFactory.getLogger(pushCMS.class);

	public static void showCms(String channel,String starttime,String endtime,int clustersize,int showlimit) throws Exception{
        try {
            logger.info(channel+"pushCMS开始计算！");
            ScheduleContext.FACADE.getPushCmsService().showCms(channel, starttime, endtime, clustersize, showlimit);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com",channel+"pushCMS计算出错！\n" + e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        logger.info(channel+"pushCMS计算完成！");
    }

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
         try{
             logger.info(Arrays.deepToString(args));
             String channel="";
             if(args.length>0){
                 channel = args[0];
             }
             String starttime = "";
             String endtime = "";
             if(args.length>1){
                 starttime = args[1];
             }else {
                 DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                 Calendar cal = TimeControl.clawEndTime();
                 cal.add(Calendar.MINUTE,-60);
                 starttime = df.format(cal.getTime());
             }

             if(args.length>2){
                 endtime = args[2];
             }else {
                 DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                 endtime = df.format(TimeControl.clawEndTime().getTime());
             }

             int clustersize = 2;
             if(args.length>3){
                 clustersize = Integer.valueOf(args[3]);
             }

             int showlimit = 2;
             if(args.length>4){
                 showlimit = Integer.valueOf(args[4]);
             }
             long start=System.currentTimeMillis();
             showCms(channel, starttime, endtime, clustersize, showlimit);
             long end=System.currentTimeMillis();
             logger.info(channel+":一小时"+channel+"pushCMS计算完成！"+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com",channel+":一小时新闻pushCMS计算完成！"+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com","一小时热点--"+out);
         }catch (Exception e){
             logger.error(e.getMessage(),e);
         }finally {
			System.exit(0);
		}

	}

}
