package com.netease.gather.service.data.impl;

import com.netease.gather.dao.ArticleDao;
import com.netease.gather.dao.BaseDao;
import com.netease.gather.domain.ArticlePushed;
import com.netease.gather.service.data.ArticleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("articleService")
public class ArticleServiceImpl extends BaseServiceImpl<ArticlePushed>
		implements ArticleService {
	@Resource(name = "articleDao")
	ArticleDao articleDao;

	@Override
	public BaseDao getDomainDao() throws Exception {
		return articleDao;
	}

	@Override
	public List<String> getDocnoListByParams(Map<String, Object> parmas) {
		return articleDao.getDocnoListByParams(parmas);
	}

	@Override
	public List<ArticlePushed> getArticleListByParams(Map<String, Object> parmas) {		
		return articleDao.getArticleListByParams(parmas);
	}

    /**
     * 取当天推送的新闻
     * @param topicId
     * @return
     */
    @Override
    public List<ArticlePushed> getArticlesPushedToday(String topicId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("topicid", topicId);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        param.put("starttime", cal.getTime());
        return articleDao.getArticleListByParams(param);
    }


    /**
     * 取24小时内推送的新闻
     * @param topicId
     * @return
     */
    @Override
    public List<ArticlePushed> getArticlesPushed24H(String topicId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("topicid", topicId);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        param.put("starttime", cal.getTime());
        return articleDao.getArticleListByParams(param);
    }

}
