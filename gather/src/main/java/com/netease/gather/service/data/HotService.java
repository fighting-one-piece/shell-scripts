package com.netease.gather.service.data;


import com.netease.gather.domain.Hot;

import java.util.List;
import java.util.Map;

public interface HotService extends BaseService<Hot> {

    List<String> getTimeSection(Map map) throws Exception;
}
