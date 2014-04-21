package com.netease.gather.service.logic;

public interface GenSpecialService {

    void showSpecial(String channel, String starttime, String endtime, int clustersize, int showlimit) throws Exception;

    void changeHeaderline(String docurl, String specurl, String channelid) throws Exception;
}
