package com.netease.gather.clawer;

import com.netease.gather.clawer.spiders.*;
import com.netease.gather.domain.Doc;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AzraelX
 * Date: 13-8-20
 * Time: 下午6:03
 */
public class Clawer {

    private static final Logger logger = Logger.getLogger(Clawer.class);

    public static List<Doc> clawNews(){
        try {
            List<Doc> rlist = new ArrayList<Doc>();
            rlist.addAll(_163Spider.clawNews());
            rlist.addAll(IfengSpider.clawNews());
            rlist.addAll(PeopleSpider.clawNews());
            rlist.addAll(QQSpider.clawNews());
            rlist.addAll(SinaSpider.clawNews());
            rlist.addAll(SohuSpider.clawNews());
            rlist.addAll(XinhuaSpider.clawNews());
            return rlist;
        }catch (Exception e){
            logger.error(e,e);
            return clawNews();
        }
    }

    public static List<Doc> clawEnt(){
        try {
            List<Doc> rlist = new ArrayList<Doc>();
            rlist.addAll(_163Spider.clawEnt());
            rlist.addAll(IfengSpider.clawEnt());
            rlist.addAll(PeopleSpider.clawEnt());
            rlist.addAll(QQSpider.clawEnt());
            rlist.addAll(SinaSpider.clawEnt());
            rlist.addAll(SohuSpider.clawEnt());
            rlist.addAll(XinhuaSpider.clawEnt());
            return rlist;
        }catch (Exception e){
            logger.error(e,e);
            return clawEnt();
        }
    }

    public static List<Doc> clawSports(){
        try {
            List<Doc> rlist = new ArrayList<Doc>();
            rlist.addAll(_163Spider.clawSports());
            rlist.addAll(IfengSpider.clawSports());
            rlist.addAll(PeopleSpider.clawSports());
            rlist.addAll(QQSpider.clawSports());
            rlist.addAll(SinaSpider.clawSports());
            rlist.addAll(SohuSpider.clawSports());
            rlist.addAll(XinhuaSpider.clawSports());
            return rlist;
        }catch (Exception e){
            logger.error(e,e);
            return clawSports();
        }
    }

    public static List<Doc> clawFinance(){
        try {
            List<Doc> rlist = new ArrayList<Doc>();
            rlist.addAll(_163Spider.clawFinance());
            rlist.addAll(IfengSpider.clawFinance());
            rlist.addAll(QQSpider.clawFinance());
            rlist.addAll(SinaSpider.clawFinance());
            rlist.addAll(SohuSpider.clawFinance());
//            rlist.addAll(XinhuaSpider.clawFinance());
//            rlist.addAll(PeopleSpider.clawFinance());
            rlist.addAll(HexunSpider.clawFinance());
            rlist.addAll(EastmoneySpider.clawFinance());
            return rlist;
        }catch (Exception e){
            logger.error(e,e);
            return clawFinance();
        }
    }

    public static List<Doc> clawTech(){
        try {
            List<Doc> rlist = new ArrayList<Doc>();
            rlist.addAll(_163Spider.clawTech());
            rlist.addAll(IfengSpider.clawTech());
            rlist.addAll(QQSpider.clawTech());
            rlist.addAll(SinaSpider.clawTech());
            rlist.addAll(SohuSpider.clawTech());
//            rlist.addAll(XinhuaSpider.clawTech());
//            rlist.addAll(PeopleSpider.clawTech());
            rlist.addAll(_36KrSpider.clawTech());
            rlist.addAll(HuxiuSpider.clawTech());
            rlist.addAll(IheimaSpider.clawTech());
            rlist.addAll(TmtpostSpider.clawTech());
            return rlist;
        }catch (Exception e){
            logger.error(e,e);
            return clawTech();
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println(clawNews());
        System.out.println(clawEnt());
        System.out.println(clawSports());
        System.out.println(clawFinance());
        System.out.println(clawTech());
    }
}
