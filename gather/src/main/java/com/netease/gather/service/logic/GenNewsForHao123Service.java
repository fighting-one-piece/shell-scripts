package com.netease.gather.service.logic;

public interface GenNewsForHao123Service {
	public void getSocialNewsForHao123(String channel, String starttime,
			String endtime, int clustersize, int showlimit) throws Exception;
}
