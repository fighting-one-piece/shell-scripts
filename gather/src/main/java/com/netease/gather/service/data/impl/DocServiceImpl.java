package com.netease.gather.service.data.impl;

import com.netease.gather.dao.DocDao;
import com.netease.gather.domain.Doc;
import com.netease.gather.service.data.DocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("docService")
public class DocServiceImpl extends BaseServiceImpl<Doc> implements DocService {

	@Resource(name="docDao")
	private DocDao docDao;

    @Override
    public DocDao getDomainDao() throws Exception {
        return docDao;
    }

}
