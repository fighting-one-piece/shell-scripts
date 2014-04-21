package com.netease.gather.service.data.impl;

import com.netease.gather.dao.HotDocDao;
import com.netease.gather.domain.HotDoc;
import com.netease.gather.service.data.HotDocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("hotDocService")
public class HotDocServiceImpl extends BaseServiceImpl<HotDoc> implements HotDocService {

	@Resource(name="hotDocDao")
	private HotDocDao hotDocDao;

    @Override
    public HotDocDao getDomainDao() throws Exception {
        return hotDocDao;
    }

}
