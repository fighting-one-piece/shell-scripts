package com.netease.gather.clawer;

import java.util.ArrayList;
import java.util.List;

import com.netease.gather.domain.PictureSet;

public class Result {

	private boolean continue_flag = true;//是否继续解析
	
	private List<PictureSet> pictureSetList;//图集列表
	
	private String nextPage;//下一页的url
	
	public Result(){
		pictureSetList = new ArrayList<PictureSet>();
	}
	
	public boolean isContinue_flag() {
		return continue_flag;
	}
	public void setContinue_flag(boolean continue_flag) {
		this.continue_flag = continue_flag;
	}
	public List<PictureSet> getPictureSetList() {
		return pictureSetList;
	}

	public String getNextPage() {
		return nextPage;
	}
	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
}
