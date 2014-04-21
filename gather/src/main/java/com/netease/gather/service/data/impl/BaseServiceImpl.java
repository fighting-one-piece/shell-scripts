package com.netease.gather.service.data.impl;

import com.netease.gather.service.data.BaseService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseServiceImpl<T> implements BaseService<T> {
	

	@Override
	public T getOneById(long id) throws Exception {
		if(id>=0){
			Map map = new HashMap();
			map.put("autoid", id);
            return (T)getDomainDao().getOneByParameters(map);
		}
		return null;
	}

    @Override
    public T getOneByParameters(Map map) throws Exception {
        if(map!=null){
            return (T) getDomainDao().getOneByParameters(map);
        }
        return null;
    }

	@Override
	public List<T> getListByParameters(Map map) throws Exception {
		if(map!=null){
			return getDomainDao().getListByParameters(map);
		}
		return null;
	}

	@Override
	public int getCountByParameters(Map map) throws Exception {
		if(map!=null){
			map.remove("start");
			map.remove("size");
			return getDomainDao().getCountByParameters(map);
		}
		return 0;
	}

	@Override
	public long saveOne(T t) throws Exception {
		if(t!=null){
			return getDomainDao().save(t);
		}
		return 0;
	}

    @Override
    public int deleteOneById(int id, String userid) throws Exception {
        if(id>0){
            Map map = new HashMap();
            map.put("autoid", id);
            map.put("adminid", userid);
            map.put("admintime", new Date());
            map.put("del", "y");
            return getDomainDao().updateOne(map);
        }
        return 0;
    }

    @Override
    public int deleteSomeByIds(List ids, String userid) throws Exception {
        if(ids!=null&&ids.size()>0){
            Map map = new HashMap();
            map.put("ids", ids);
            map.put("adminid", userid);
            map.put("admintime", new Date());
            map.put("del", "y");
            return getDomainDao().updateSomeByIds(map);
        }
        return -1;
    }

    @Override
    public int deleteSomeByParameters(Map map) throws Exception {
        if(map!=null){
            return getDomainDao().deleteSomeByParameters(map);
        }
        return -1;
    }

    @Override
    public int recoverOneById(int id, String userid) throws Exception {
        if(id>0){
            Map map = new HashMap();
            map.put("autoid", id);
            map.put("adminid", userid);
            map.put("admintime", new Date());
            map.put("del", "n");
            return getDomainDao().updateOne(map);
        }
        return 0;
    }

    @Override
    public int recoverSomeByIds(List ids, String userid) throws Exception {
        if(ids!=null&&ids.size()>0){
            Map map = new HashMap();
            map.put("ids", ids);
            map.put("adminid", userid);
            map.put("admintime", new Date());
            map.put("del", "n");
            return getDomainDao().updateSomeByIds(map);
        }
        return -1;
    }

    @Override
    public int deleteSomeByIds(List ids) throws Exception {
        if(ids!=null&&ids.size()>0){
            Map map = new HashMap();
            map.put("ids", ids);
            return getDomainDao().deleteSomeByIds(map);
        }
        return -1;
    }

	@Override
	public int updateOneById(int id, Map params) throws Exception {
		if(id>0&&params!=null){
			params.put("autoid", id);
			return getDomainDao().updateOne(params);
		}
		return -1;
	}

	@Override
	public int updateOne(Map params) throws Exception {
		if(params!=null){
			return getDomainDao().updateOne(params);
		}
		return -1;
	}
	@Override
	public int releaseSomeByIds(List ids , String userid) throws Exception{
		if(ids!=null&&ids.size()>0){
			Map map = new HashMap();
			map.put("ids", ids);
			map.put("adminid", userid);
			map.put("admintime", new Date());
			map.put("publication", 1);
			return getDomainDao().updateSomeByIds(map);
		}
		return -1;
	}
	public int unreleaseSomeByIds(List ids, String userid) throws Exception{
		if(ids!=null&&ids.size()>0){
			Map map = new HashMap();
			map.put("ids", ids);
			map.put("adminid", userid);
			map.put("admintime", new Date());
			map.put("publication", 0);
			return getDomainDao().updateSomeByIds(map);
		}
		return -1;
	}

    @Override
    public int signDelSomeByParameters(Map map) throws Exception{
        return getDomainDao().signDelSomeByParameters(map);
    }
}
