package com.grey.myblog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 网站资讯统计 DTO
 *
 * @author grey
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文章数目
     */
    private Integer articleCount;

    /**
     * 全站字数
     */
    private Long totalWords;

    /**
     * 全站访问次数（PV）
     */
    private Long visitCount;

    /**
     * 建站日期
     */
    private Date siteCreateDate;

    /**
     * 运行时长（天，由建站日期计算得出）
     */
    private Integer runDays;

    /**
     * 最近文章更新时间
     */
    private Date lastArticleUpdateTime;

    /**
     * 统计最近刷新时间
     */
    private Date statsRefreshTime;
}
