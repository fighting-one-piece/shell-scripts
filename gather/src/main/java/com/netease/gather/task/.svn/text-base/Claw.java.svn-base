package com.netease.gather.task;

import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.PoPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
*/
public class Claw {

	private static final Logger logger = LoggerFactory.getLogger(Claw.class);

	public static void clawNsaveDoc() throws Exception{
        try {
            logger.info("抓取开始！");
            ScheduleContext.FACADE.getClawService().clawNsaveDoc();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com","抓取出错！\n" + e.getMessage());
        }
        logger.info("抓取结束！");
    }

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
         try{
             long start=System.currentTimeMillis();
             clawNsaveDoc();
             long end=System.currentTimeMillis();
             logger.info("抓取耗时："+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com","抓取耗时："+(end-start)/1000+" s");
         }catch (Exception e){
             logger.error(e.getMessage(),e);
         }finally {
             System.exit(0);
         }

	}

}
