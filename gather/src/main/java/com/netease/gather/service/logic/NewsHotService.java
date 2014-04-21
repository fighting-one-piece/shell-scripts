package com.netease.gather.service.logic;

import java.util.List;
import java.util.Map;

public interface NewsHotService {

    @SuppressWarnings({"unchecked"})
    int genHots(String channel, String starttime, String endtime, String source, int clustersize) throws Exception;

    List<Map> choiceHot(String channel, String col, String starttime, String endtime, int repeat, int showlimit) throws Exception;

    List<Map> rmRepeatHots(List<Map> allhots) throws Exception;

    boolean isSim(Map remap, Map anomap) throws Exception;

    boolean hasSame(Map remap, Map anomap) throws Exception;

    List<Map> choice163Art(List<Map> allhots, String channel) throws Exception;

    void calSimWithHeadlines(List<Map> rehots, String channel, String endtime, int calsize) throws Exception;

    List<Map> pick163Hots(List<Map> allhots) throws Exception;

    void sortHotCommen(List<Map> rehots) throws Exception;
}
