package com.netease.gather.task;

import com.netease.gather.classifier.naivebayes.TopicInit;
import com.netease.gather.classifier.naivebayes.Trainer;
import com.netease.gather.common.util.PoPo;
import com.netease.gather.common.util.exception.ApplicationException;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
*
*/
public class ClassifierTrain {

	private static final Logger logger = LoggerFactory.getLogger(ClassifierTrain.class);

	public static void train(String modelname,String colname) throws Exception{
        try {
            logger.info("训练开始！");
            Trainer trainer = new Trainer();
            if(modelname.equals("all")){
                trainer.train(modelname, TopicInit.flattpc);
            }else if(modelname.equals("chan")) {
                trainer.train(modelname, TopicInit.chantpc);
            }else  if(modelname.equals("col")) {
                if("".equals(colname)){
                    for(Map.Entry<String,List<DefaultKeyValue>> entry:TopicInit.chancol.entrySet()){
                        trainer.train(entry.getKey(), entry.getValue());
                    }
                }else {
                    trainer.train(colname, TopicInit.chancol.get(colname));
                }

            }else {
                throw new ApplicationException("参数错误！");
            }
//            ScheduleContext.FACADE.getClawService().clawNsaveDoc();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com","训练出错！\n" + e.getMessage());
        }
        logger.info("训练结束！");
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
             String modelname = args[0];
             String colname = "";
             if(args.length>1){
                 colname = args[1];
             }
             long start=System.currentTimeMillis();
             train(modelname,colname);
             long end=System.currentTimeMillis();
             logger.info("训练耗时："+(end-start)/1000+" s");
             PoPo.send("ykxu@corp.netease.com","训练耗时："+(end-start)/1000+" s");
         }catch (Exception e){
             logger.error(e.getMessage(),e);
         }finally {
			System.exit(0);
		}

	}

}
