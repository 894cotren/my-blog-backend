package com.grey.myblog.model.dataobject;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站资讯统计表
 *
 * @author grey
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteStatsDO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 文章数目（定时任务刷新）
     */
    private Integer articleCount;

    /**
     * 全站字数（定时任务刷新）
     */
    private Long totalWords;

    /**
     * 全站访问次数PV（访问拦截器实时+1）
     */
    private Long visitCount;

    /**
     * 建站日期（用于计算运行时长，手动配置）
     */
    private Date siteCreateDate;

    /**
     * 最近文章更新时间（定时任务刷新）
     */
    private Date lastArticleUpdateTime;

    /**
     * 统计最近刷新时间（定时任务写入）
     */
    private Date statsRefreshTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
