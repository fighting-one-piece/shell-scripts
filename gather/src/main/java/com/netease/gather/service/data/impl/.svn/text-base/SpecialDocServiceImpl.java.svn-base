package com.netease.gather.service.data.impl;

import com.netease.gather.dao.SpecialDocDao;
import com.netease.gather.domain.SpecialDoc;
import com.netease.gather.service.data.SpecialDocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("specialDocService")
public class SpecialDocServiceImpl extends BaseServiceImpl<SpecialDoc> implements SpecialDocService {

	@Resource(name="specialDocDao")
	private SpecialDocDao specialDocDao;

    @Override
    public SpecialDocDao getDomainDao() throws Exception {
        return specialDocDao;
    }

}
