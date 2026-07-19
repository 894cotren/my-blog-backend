package com.grey.myblog.dao;

import com.grey.myblog.model.dataobject.SiteStatsDO;

/**
 * 网站资讯统计表 DAO
 *
 * @author grey
 */
public interface SiteStatsDAO {

    /**
     * 插入网站资讯
     */
    int insert(SiteStatsDO siteStats);

    /**
     * 根据ID更新网站资讯（动态更新非空字段）
     */
    int updateById(SiteStatsDO siteStats);

    /**
     * 查询第一条网站资讯（单行表）
     */
    SiteStatsDO selectFirst();

    /**
     * 刷新统计字段（文章数、全站字数、最近文章更新时间、刷新时间）
     */
    int refreshStats();

    /**
     * 访问次数 +1
     */
    int incrementVisitCount();
}
