package com.netease.gather.service.logic;

public interface GenFinanceNewsService {
	void genAndSendFinanceNews(String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception;

	//void genAndSendSheHuiNews(String channel, String starttime, String endtime,
		//	int clustersize, int showlimit) throws Exception;
	
	void genAndSendNewsByCol(final String COL, String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception;
}
