package com.netease.gather.dao.impl;

import com.netease.gather.dao.SpecialDocDao;
import com.netease.gather.domain.SpecialDoc;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"unchecked"})
@Repository("specialDocDao")
public class SpecialDocDaoImpl extends BaseDaoImpl<SpecialDoc> implements SpecialDocDao {

    @Override
    public String getObjectName() {
        return "special_doc";
    }

}
