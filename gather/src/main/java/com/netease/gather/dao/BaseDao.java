package com.netease.gather.dao;

import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
	/**
	 * 获取所要操作的ibitas map的namespace名字
	 * @return
	 */
	public String getObjectName();
	
	/**
	 * 保存对象
	 *
     * @param obj
     * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	public Long save(T obj)throws DataAccessException;
	
	/**
	 * 更新一个对象的相应参数
	 * @param map
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	public int updateOne(Map map)throws DataAccessException;
	
	/**
	 * 依据参数获取对象列表
	 * @param map
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	public List<T> getListByParameters(Map map)throws DataAccessException;

    /**
     * 依据参数获取对象
     * @param map
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public T getOneByParameters(Map map)throws DataAccessException;

	/**
	 * 依据参数获取对象个数
	 * @param map
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	public int getCountByParameters(Map map)throws DataAccessException;
	
	/**
	 * 依据ids(以list形式存在，保存在 map中)集体更新参数
	 * @param map
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	public int updateSomeByIds(Map map)throws DataAccessException;

    /**
     * 依据ids(以list形式存在，保存在 map中)集体删除
     * @param map
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public int deleteSomeByIds(Map map) throws DataAccessException;

    int deleteSomeByParameters(Map map) throws DataAccessException;

    int signDelSomeByParameters(Map map);
}
