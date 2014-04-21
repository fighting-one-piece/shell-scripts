package com.netease.gather.dao.impl;

import com.netease.gather.dao.HotDocDao;
import com.netease.gather.domain.HotDoc;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"unchecked"})
@Repository("hotDocDao")
public class HotDocDaoImpl extends BaseDaoImpl<HotDoc> implements HotDocDao {

    @Override
    public String getObjectName() {
        return "hot_doc";
    }

}
