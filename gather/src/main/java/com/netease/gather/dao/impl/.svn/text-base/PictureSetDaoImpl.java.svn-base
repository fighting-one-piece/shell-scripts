package com.netease.gather.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.netease.gather.dao.PictureSetDao;
import com.netease.gather.domain.PictureSet;

@Repository("pictureSetDao")
public class PictureSetDaoImpl extends BaseDaoImpl<PictureSet> implements PictureSetDao {

    @Override
    public String getObjectName() {
        return "picture_set";
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> statUploadInfo(String groupid, Date startDate) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupid", groupid);
		map.put("starttime", startDate);
		List<Map<String,Object>> list = sqlMapClientTemplate.queryForList(getObjectName()+".statUploadInfo", map);
		return list;
	}

}
