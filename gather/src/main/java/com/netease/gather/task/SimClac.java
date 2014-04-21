package com.netease.gather.task;

import com.netease.gather.common.util.PoPo;
import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.nlp.SimilarityCalc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
*/
public class SimClac {

	private static final Logger logger = LoggerFactory.getLogger(SimClac.class);

	public static double similarityCalc(String url1,String url2) throws Exception{
        double sim = 0.0;
        try {
            logger.info("计算相似度开始！");
            sim = SimilarityCalc.calcByCosWUrl(url1, url2);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com","计算相似度出错！\n" + e.getMessage());
        }
        logger.info("计算相似度结束！");
        return sim;
    }

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
         try{
             if(args.length<2){
                 throw new ApplicationException("参数错误！");
             }
             String url1 = args[0];
             String url2 = args[1];
             logger.info(url1+","+url2);
             long start=System.currentTimeMillis();
             double sim = similarityCalc(url1, url2);
             long end=System.currentTimeMillis();
             logger.info(String.valueOf(sim));
             logger.info("计算相似度耗时："+(end-start)/1000+" s");
//             PoPo.send("ykxu@corp.netease.com","分类耗时："+(end-start)/1000+" s");
         }catch (Exception e){
             logger.error(e.getMessage(),e);
         }finally {
			System.exit(0);
		}

	}

}
