package com.netease.gather.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.netease.gather.dao.ArticleDao;
import com.netease.gather.domain.ArticlePushed;

@SuppressWarnings({ "unchecked" })
@Repository("articleDao")
public class ArticleDaoImpl extends BaseDaoImpl<ArticlePushed> implements
		ArticleDao {

	@Override
	public String getObjectName() {
		return "article";
	}

	@Override
	public List<String> getDocnoListByParams(Map<String, Object> parmas) {

		return sqlMapClientTemplate.queryForList(getObjectName()
				+ ".getDocnoListByParams", parmas);
	}

	@Override
	public List<ArticlePushed> getArticleListByParams(Map<String, Object> parmas) {
		return sqlMapClientTemplate.queryForList(getObjectName()
				+ ".getArticlesByParams", parmas);
	}

}
