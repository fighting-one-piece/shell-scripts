package com.netease.gather.task;

import com.netease.gather.classifier.naivebayes.ClassifierWKeyWords;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.FileUtil;
import com.netease.gather.common.util.HessianUtil;
import com.netease.gather.common.util.PoPo;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
*
*/
public class ClassifyTest {

	private static final Logger logger = LoggerFactory.getLogger(ClassifyTest.class);



	public static void deliver() throws Exception{
        try {
            logger.info("分类分发开始！");

            String startday = "2014-03-13";
            String endday = "2014-03-19";
            long daysize = DateUtil.getDaysBetweenTwoDates(DateUtil.stringToDate(startday, "yyyy-MM-dd"), DateUtil.stringToDate(endday, "yyyy-MM-dd"));
            for(int i = 0;i < daysize; i++){
                String cday = DateUtil.DateToString(DateUtils.addDays(DateUtil.stringToDate(endday, "yyyy-MM-dd"), -i),"yyyy-MM-dd");
                logger.info(cday);
                boolean stop = false;
                int start = 0;
                int querysize = 20;
                Map<String,List<String>> cmap = new HashMap<String, List<String>>();
                while (!stop){
                    try {
                        Map<String,String>[] docindexs =  HessianUtil.getCmsWebService().getList("topicid="+"00014OMD"+";startday="+cday+";endday="+cday+";liststart="+start+";listnum="+querysize+";");
                        if(docindexs!=null&&docindexs.length>0){
                            for(Map<String,String> docindex:docindexs){
                                try{
                                    String cate = ClassifierWKeyWords.classify("war", docindex.get("title"), docindex.get("url"), false, true);
                                    List<String> dlist = cmap.get(cate);
                                    if(dlist==null){
                                        dlist = new ArrayList<String>();
                                    }
                                    dlist.add(docindex.get("title"));
                                    cmap.put(cate,dlist);
                                }catch (Exception e){
                                    logger.error(e.getMessage()+docindex.get("url"));
                                }

                            }
                            if(docindexs.length==querysize){
                                start = start + querysize;
                            }else {
                                stop = true;
                            }
                        }else {
                            stop = true;
                        }
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                }

                for(Map.Entry<String,List<String>> entry:cmap.entrySet()){
                    String cate = entry.getKey();
                    List<String> tits = entry.getValue();
                    File file = FileUtil.createFile("/home/workspace/test/"+cday+"/"+cate+".txt");
                    FileWriter fw = new FileWriter(file);
                    fw.write("分类"+cate+":共"+tits.size()+"篇"+"\r\n");
                    for(String tit : tits){
                        fw.write(tit+"\r\n");
                    }
                    fw.close();
                }
            }



        }catch (Exception e){
            logger.error(e.getMessage(),e);
            PoPo.send("ykxu@corp.netease.com","分类分发出错！" + e.getMessage());
        }
        logger.info("分类分发结束！");
    }

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        try{
            long start=System.currentTimeMillis();
            deliver();
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
