package com.netease.gather.service.data;

import java.util.List;
import java.util.Map;

import com.netease.gather.domain.ArticlePushed;

public interface ArticleService extends BaseService<ArticlePushed> {
	List<String> getDocnoListByParams(Map<String, Object> parmas);
	
	List<ArticlePushed> getArticleListByParams(Map<String, Object> parmas);

    List<ArticlePushed> getArticlesPushedToday(String topicId);

    List<ArticlePushed> getArticlesPushed24H(String topicId);
}
