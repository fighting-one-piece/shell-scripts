package com.netease.gather.dao.impl;

import com.netease.gather.dao.BaseDao;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("baseDao")
public abstract class BaseDaoImpl<T> implements BaseDao<T> {

	@Resource(name = "sqlMapClientTemplate")
	protected SqlMapClientTemplate sqlMapClientTemplate;
	
	
	@Override
	public Long save(T obj) throws DataAccessException {
		return (Long)sqlMapClientTemplate.insert(getObjectName()+".insertOne", obj);
	}

	@Override
	public int updateOne(Map map) throws DataAccessException {
		return sqlMapClientTemplate.update(getObjectName()+".updateOne", map);
	}

	@Override
	public List<T> getListByParameters(Map map) throws DataAccessException {
		List<T> list = sqlMapClientTemplate.queryForList(getObjectName()+".getListByParameters", map);
		return list;
	}

    @Override
    public T getOneByParameters(Map map) throws DataAccessException {
        T Obj = (T) sqlMapClientTemplate.queryForObject(getObjectName() + ".getOneByParameters", map);
        return Obj;
    }

	@Override
	public int getCountByParameters(Map map) throws DataAccessException {
		return (Integer)sqlMapClientTemplate.queryForObject(getObjectName()+".getCountByParameters", map);
	}
	
	@Override
	public int updateSomeByIds(Map map) throws DataAccessException{
		return (Integer)sqlMapClientTemplate.update(getObjectName()+".updateSomeByIds", map);
	}

    @Override
    public int deleteSomeByIds(Map map) throws DataAccessException{
        return (Integer)sqlMapClientTemplate.delete(getObjectName()+".deleteSomeByIds", map);
    }
    @Override
    public int deleteSomeByParameters(Map map) throws DataAccessException{
        return (Integer)sqlMapClientTemplate.delete(getObjectName()+".deleteSomeByParameters", map);
    }

    @Override
    public int signDelSomeByParameters(Map map){
        return sqlMapClientTemplate.update(getObjectName()+".signDelSomeByParameters",map);
    }

}
