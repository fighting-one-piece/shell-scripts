package com.netease.gather.common.constants;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class Constants {

    public static final String FRONTENCODE = "GBK";

    public static final String POSTAGGER = "models/pos.m";
    public static final String CWSTAGGER = "models/seg.m";
    public static final String CLASSIFIER = "models/classifier.m";
    public static final String HTMLROOT = "/home/workspace/gather/html/";

    public static final String ROBOT_PUSH_CMS_URL = "https://cms.ws.netease.com/servlet/webservice.do?target=robot&data=";
//    https://cms.ws.netease.com/servlet/webservice.do?target=robot&action=modifyLspri&topicid=xxx&docid=xxx&lspri=xxx
    public static final String ROBOT_MODLSPRI_CMS_URL = "https://cms.ws.netease.com/servlet/webservice.do?target=robot&action=modifyLspri";

    public static enum CHANNEL {
        news,ent,sports,tech,finance;
        public static CHANNEL getChannel(String channel){
            return valueOf(channel);
        }
    }
}