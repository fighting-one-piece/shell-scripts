package com.netease.gather.dao.impl;

import com.netease.gather.dao.HotDao;
import com.netease.gather.domain.Hot;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
@Repository("hotDao")
public class HotDaoImpl extends BaseDaoImpl<Hot> implements HotDao {

    @Override
    public String getObjectName() {
        return "hot";
    }

    @Override
    public List<String> getTimeSection(Map map){
        return sqlMapClientTemplate.queryForList(getObjectName()+".getTimeSection",map);
    }

}
