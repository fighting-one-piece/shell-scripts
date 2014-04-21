package com.netease.gather.dao.impl;

import org.springframework.stereotype.Repository;

import com.netease.gather.dao.PictureDao;
import com.netease.gather.domain.Picture;

@Repository("pictureDao")
public class PictureDaoImpl extends BaseDaoImpl<Picture> implements PictureDao {

    @Override
    public String getObjectName() {
        return "picture";
    }

}
