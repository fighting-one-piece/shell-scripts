package com.netease.gather.service.data.impl;

import com.netease.gather.dao.SpecialDao;
import com.netease.gather.domain.Special;
import com.netease.gather.service.data.SpecialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("specialService")
public class SpecialServiceImpl extends BaseServiceImpl<Special> implements SpecialService {

	@Resource(name="specialDao")
	private SpecialDao specialDao;

    @Override
    public SpecialDao getDomainDao() throws Exception {
        return specialDao;
    }

}
