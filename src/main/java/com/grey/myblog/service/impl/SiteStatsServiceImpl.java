package com.grey.myblog.service.impl;

import com.grey.myblog.dao.SiteStatsDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.SiteStatsDO;
import com.grey.myblog.model.dto.SiteStatsDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.service.SiteStatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 网站资讯统计服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class SiteStatsServiceImpl implements SiteStatsService {

    @Resource
    private SiteStatsDAO siteStatsDAO;

    @Override
    public SiteStatsDTO getSiteStats() {
        SiteStatsDO siteStats = getOrCreateSiteStats();
        return convertToSiteStatsDTO(siteStats);
    }

    @Override
    public Boolean refreshStats() {
        int result = siteStatsDAO.refreshStats();
        if (result <= 0) {
            // 记录不存在，不自动创建，打 warn 跳过（需手动初始化 site_stats 表数据）
            log.warn("网站资讯统计记录不存在，跳过刷新，请先初始化 site_stats 表数据");
            return false;
        }
        return true;
    }

    @Override
    public Boolean incrementVisitCount() {
        int result = siteStatsDAO.incrementVisitCount();
        if (result <= 0) {
            // 记录不存在，初始化后再 +1
            getOrCreateSiteStats();
            result = siteStatsDAO.incrementVisitCount();
        }
        return result > 0;
    }

    /**
     * 获取或初始化网站资讯（单行表）
     */
    private SiteStatsDO getOrCreateSiteStats() {
        SiteStatsDO siteStats = siteStatsDAO.selectFirst();
        if (siteStats != null) {
            return siteStats;
        }

        SiteStatsDO defaultSiteStats = SiteStatsDO.builder()
                .articleCount(0)
                .totalWords(0L)
                .visitCount(0L)
                .siteCreateDate(new Date())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        int result = siteStatsDAO.insert(defaultSiteStats);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "初始化网站资讯失败");
        }
        return defaultSiteStats;
    }

    /**
     * 转换为网站资讯响应对象（含运行时长计算）
     */
    private SiteStatsDTO convertToSiteStatsDTO(SiteStatsDO siteStats) {
        SiteStatsDTO dto = new SiteStatsDTO();
        BeanUtils.copyProperties(siteStats, dto);
        dto.setRunDays(calculateRunDays(siteStats.getSiteCreateDate()));
        return dto;
    }

    /**
     * 计算运行时长（天）：今日 - 建站日期
     * 不存派生值，每次查询时实时计算，保证永远准确
     */
    private Integer calculateRunDays(Date siteCreateDate) {
        if (siteCreateDate == null) {
            return 0;
        }
        LocalDate created = siteCreateDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        long days = ChronoUnit.DAYS.between(created, LocalDate.now());
        return days < 0 ? 0 : (int) days;
    }
}
