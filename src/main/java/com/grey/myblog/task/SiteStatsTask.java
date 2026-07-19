package com.grey.myblog.task;

import com.grey.myblog.service.SiteStatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 网站资讯统计定时任务
 *
 * @author grey
 */
@Slf4j
@Component
public class SiteStatsTask {

    @Resource
    private SiteStatsService siteStatsService;

    /**
     * 每天凌晨 3 点刷新统计（文章数、全站字数、最近文章更新时间）
     * 访问次数 visit_count 由拦截器实时 +1，不在此任务处理
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void refreshSiteStats() {
        try {
            long start = System.currentTimeMillis();
            siteStatsService.refreshStats();
            log.info("网站资讯统计刷新完成，耗时 {} ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("网站资讯统计刷新异常", e);
        }
    }
}
