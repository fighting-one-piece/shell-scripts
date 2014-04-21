package com.netease.gather.service.data.impl;

import com.netease.gather.dao.HotDao;
import com.netease.gather.domain.Hot;
import com.netease.gather.service.data.HotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("hotService")
public class HotServiceImpl extends BaseServiceImpl<Hot> implements HotService {

	@Resource(name="hotDao")
	private HotDao hotDao;

    @Override
    public HotDao getDomainDao() throws Exception {
        return hotDao;
    }

    @Override
    public List<String> getTimeSection(Map map) throws Exception{
        return getDomainDao().getTimeSection(map);
    }
}
