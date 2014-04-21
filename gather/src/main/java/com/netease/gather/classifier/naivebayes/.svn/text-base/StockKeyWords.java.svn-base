package com.netease.gather.classifier.naivebayes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AzraelX
 * Date: 13-10-12
 * Time: 上午11:14
 */
public class StockKeyWords {

    public static Map<String,List<String>> classwords = new LinkedHashMap<String,List<String>>();

    static {
        List<String> qihuowords = Arrays.asList("期货","金融期货","股指期货","商品期货","金融期货","国债期货","期权","黄金期货","金属","原油","大豆","期货行情","商品","粮食","黄金","农产品","指数");//"巴",
        List<String> waihuiwords = Arrays.asList("外汇","汇市","行情","国际市场","央行","美联储","人民币","美元","日元","欧元","英镑","加元","澳元","新西兰元","汇率","美元对人民币","中国银行","港币对人民币","欧元对人民币","日元对人民币","美元汇率","人民币","港元","外汇","人民币牌价","外汇行情","外币报价");//
        List<String> xinguwords = Arrays.asList("新股","上会","发行","发审委","新股申购","新股上市","新股发行","新股在线","申购新股","新股认购","新股中签","再融资","IPO");
        List<String> chuangyebanwords = Arrays.asList("创业板","创投","A股","风险投资","PE","VC","纳斯达克","创投概念股","风险投资","海外创业板","投资银行");
        List<String> meiguwords = Arrays.asList("美股","美国股市","道琼斯","纳斯达克","美股行情","美股新闻","华尔街","盘面报道","美东时间","中国概念股","国际财经","国际股指");
        List<String> gangguwords = Arrays.asList("港股","港股行情","h股"," 港股资讯","恒生","国企","红筹","蓝筹","窝轮资讯","港股资讯、","港股行情报价","A+H股比价表","机构评级","沽空记录","公司回购","新股上市","港股ADR","港股日志");

        //强顺序
        classwords.put("qihuo",qihuowords);
        classwords.put("waihui",waihuiwords);
        classwords.put("xingu",xinguwords);
        classwords.put("chuangyeban",chuangyebanwords);
        classwords.put("meigu",meiguwords);
        classwords.put("ganggu",gangguwords);
    }
}
