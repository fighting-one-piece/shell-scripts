package com.netease.gather.task;

import com.netease.gather.classifier.naivebayes.Classifier;
import com.netease.gather.common.util.PoPo;
import com.netease.gather.common.util.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
*/
public class Classify {

	private static final Logger logger = LoggerFactory.getLogger(Classify.class);

	public static String classify(String url,String channel) throws Exception{
        String cate = "";
        try {
            logger.info("分类开始！");
            cate = Classifier.classifyForOne(url,channel);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com","分类出错！\n" + e.getMessage());
        }
        logger.info("分类结束！");
        return cate;
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
             String url = args[0];
             String channel = "news";
             if(args.length>1){
                 channel = args[1];
             }
             logger.info(url);
             long start=System.currentTimeMillis();
             String cate = classify(url,channel);
             long end=System.currentTimeMillis();
             logger.info(cate);
             logger.info("分类耗时："+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com","分类耗时："+(end-start)/1000+" s");
         }catch (Exception e){
             logger.error(e.getMessage(),e);
         }finally {
			System.exit(0);
		}

	}

}
