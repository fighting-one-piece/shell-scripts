package com.netease.gather.clawerpic.parser;

import java.util.List;

import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public interface ClawerPic {
	void setJobid(String jobid);
	
	List<PictureSet> getNewPictureSet(PictureSet lastPictureSet);

	List<Picture> getPictureList(PictureSet ps);
}
