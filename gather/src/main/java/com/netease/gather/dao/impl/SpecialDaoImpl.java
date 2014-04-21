package com.netease.gather.dao.impl;

import com.netease.gather.dao.SpecialDao;
import com.netease.gather.domain.Special;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"unchecked"})
@Repository("specialDao")
public class SpecialDaoImpl extends BaseDaoImpl<Special> implements SpecialDao {

    @Override
    public String getObjectName() {
        return "special";
    }

}
