package com.netease.gather.service.data;

import com.netease.gather.dao.BaseDao;

import java.util.List;
import java.util.Map;

public interface BaseService<T> {
	
	BaseDao getDomainDao()throws Exception;
	long saveOne(T t) throws Exception;
    T getOneByParameters(Map map) throws Exception;
    List<T> getListByParameters(Map map) throws Exception;
    int getCountByParameters(Map map) throws Exception;
	int deleteOneById(int id, String userid) throws Exception;
	int deleteSomeByIds(List ids, String userid) throws Exception;
	int updateOneById(int id, Map params) throws Exception;
	int releaseSomeByIds(List ids, String userid) throws Exception;
	int unreleaseSomeByIds(List ids, String userid) throws Exception;
    int deleteSomeByIds(List ids) throws Exception;

    int recoverOneById(int id, String userid) throws Exception;

    int recoverSomeByIds(List ids, String userid) throws Exception;

    T getOneById(long id) throws Exception;


    int updateOne(Map params) throws Exception;

    int deleteSomeByParameters(Map map) throws Exception;

    int signDelSomeByParameters(Map map) throws Exception;
}
