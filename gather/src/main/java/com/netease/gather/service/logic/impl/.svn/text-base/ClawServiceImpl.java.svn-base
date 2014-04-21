package com.netease.gather.service.logic.impl;

import com.netease.gather.clawer.Clawer;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.PoPo;
import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.domain.Doc;
import com.netease.gather.service.data.DocService;
import com.netease.gather.service.logic.ClawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("clawService")
public class ClawServiceImpl implements ClawService {

    private static final Logger logger = LoggerFactory.getLogger(ClawServiceImpl.class);

    @Resource(name="docService")
    DocService docService;

    @Override
    public void clawNsaveDoc() throws Exception{
        List<String> channels = new ArrayList<String>(Arrays.asList("news","ent","sports","finance","tech"));
        for (String channel:channels){
            List<Doc> docs = clawDoc(channel);
//            if(docs.size()>0){
//                Map map = new HashMap();
//                map.put("starttime", TimeControl.clawStartTime().getTime());
//                map.put("endtime", TimeControl.clawEndTime().getTime());
//                map.put("channel", channel);
//                docService.deleteSomeByParameters(map);
//            }
            for(Doc doc:docs){
                try {
                    Map para = new HashMap();
                    para.put("docno",doc.getDocno());
                    Doc edoc = docService.getOneByParameters(para);
                    if(edoc==null){
                        docService.saveOne(doc);
                    }
                }catch (Exception e){
                    logger.error(doc.getUrl());
                    logger.error(e.getMessage());
                    PoPo.send("ykxu@corp.netease.com", "抓取出错！\n" + e.getMessage());
                }
            }
        }
    }

    public List<Doc> clawDoc(String channel) throws Exception{
        List<Doc> clawlist;
        switch (Constants.CHANNEL.getChannel(channel)){
            case news: clawlist = Clawer.clawNews(); break;
            case ent: clawlist = Clawer.clawEnt(); break;
            case sports: clawlist = Clawer.clawSports(); break;
            case finance: clawlist = Clawer.clawFinance(); break;
            case tech: clawlist = Clawer.clawTech(); break;
            default: throw new ApplicationException("频道错误！");
        }

        return clawlist;
    }

}
