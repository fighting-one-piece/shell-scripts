package com.netease.gather.dao;

import java.util.List;
import java.util.Map;

import com.netease.gather.domain.ArticlePushed;

public interface ArticleDao extends BaseDao<ArticlePushed> {
	List<String> getDocnoListByParams(Map<String, Object> parmas);
	
	List<ArticlePushed> getArticleListByParams(Map<String, Object> parmas);
}
