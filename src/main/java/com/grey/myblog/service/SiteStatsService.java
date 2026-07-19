package com.grey.myblog.service;

import com.grey.myblog.model.dto.SiteStatsDTO;

/**
 * 网站资讯统计服务接口
 *
 * @author grey
 */
public interface SiteStatsService {

    /**
     * 获取网站资讯统计
     *
     * @return 网站资讯统计
     */
    SiteStatsDTO getSiteStats();

    /**
     * 刷新统计字段（文章数、全站字数、最近文章更新时间），定时任务调用
     *
     * @return 是否成功
     */
    Boolean refreshStats();

    /**
     * 访问次数 +1，访问拦截器调用
     *
     * @return 是否成功
     */
    Boolean incrementVisitCount();
}
