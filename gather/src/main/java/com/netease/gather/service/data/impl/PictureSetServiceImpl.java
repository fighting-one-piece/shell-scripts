package com.netease.gather.service.data.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.netease.gather.dao.PictureSetDao;
import com.netease.gather.domain.PictureSet;
import com.netease.gather.service.data.PictureSetService;

@Service("pictureSetService")
public class PictureSetServiceImpl extends BaseServiceImpl<PictureSet> implements PictureSetService {

	@Resource(name="pictureSetDao")
	private PictureSetDao pictureSetDao;

    @Override
    public PictureSetDao getDomainDao() throws Exception {
        return pictureSetDao;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PictureSet getLastPictureSet(String jobid) throws Exception {
		Map map=new HashMap();
		map.put("jobid", jobid);
		map.put("order", "autoid desc");
		map.put("start", 0);
		map.put("size", 1);
		List<PictureSet> list=this.getListByParameters(map);
		if(list.size()>0){
			return list.get(0);
		}else
			return null;
	}

	@Override
	public List<Map<String,Object>> statUploadInfo(String groupid, Date startDate) throws Exception {
		return pictureSetDao.statUploadInfo(groupid, startDate);
	}

}
