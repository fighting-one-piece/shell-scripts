package com.netease.gather.task;

import com.netease.gather.common.context.ScheduleContext;
import com.netease.gather.common.util.PoPo;
import com.netease.gather.common.util.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
*
*/
public class DeliverByClassify {

	private static final Logger logger = LoggerFactory.getLogger(DeliverByClassify.class);



	public static void deliver(String channel) throws Exception{
        try {
            logger.info(channel+"分类分发开始！");
            if("gov".equals(channel)||"whzg".equals(channel)){
                ScheduleContext.FACADE.getDeliverCmsService().filter4Gov(channel);
            }else if("ldr".equals(channel)){
                ScheduleContext.FACADE.getDeliverCmsService().filter4Ldr(channel);
            }else if("stock".equals(channel)){
                ScheduleContext.FACADE.getDeliverCmsService().deliver4Stock(channel);
            }else {
                ScheduleContext.FACADE.getDeliverCmsService().deliver(channel);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com",channel+"分类分发出错！" + e.getMessage());
        }
        logger.info(channel+"分类分发结束！");
    }

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        try{
            if(args.length<1){
                throw new ApplicationException("参数错误！");
            }
            long start=System.currentTimeMillis();
            String channel = args[0];
            deliver(channel);
             long end=System.currentTimeMillis();
             logger.info("分类耗时："+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com","分类耗时："+(end-start)/1000+" s");
         }catch (Exception e){
             logger.error(e.getMessage(),e);
             PoPo.send("ykxu@corp.netease.com", Arrays.toString(args) +"分类分发出错！" + e.getMessage());
         }finally {
			System.exit(0);
		}

	}

}
