package com.netease.gather.service.data.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.netease.gather.dao.PictureDao;
import com.netease.gather.domain.Picture;
import com.netease.gather.service.data.PictureService;

@Service("pictureService")
public class PictureServiceImpl extends BaseServiceImpl<Picture> implements PictureService {

	@Resource(name="pictureDao")
	private PictureDao pictureDao;

    @Override
    public PictureDao getDomainDao() throws Exception {
        return pictureDao;
    }

}
