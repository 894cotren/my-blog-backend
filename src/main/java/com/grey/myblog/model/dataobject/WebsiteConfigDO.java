package com.grey.myblog.model.dataobject;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站配置表
 *
 * @author grey
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteConfigDO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 网站标题
     */
    private String websiteName;

    /**
     * 网站 Logo 图片URL
     */
    private String logo;

    /**
     * 站点图标（浏览器页签）URL
     */
    private String favicon;

    /**
     * 博主卡片背景图URL
     */
    private String bloggerCardBackground;

    /**
     * 首页 banner 轮播图列表（URL 以换行分隔）
     */
    private String bannerImages;

    /**
     * 文章默认封面图列表（文章未传封面时随机取一张兜底，URL 以换行分隔）
     */
    private String articleCoverImages;

    /**
     * 首页打字机滚动文案列表（多条以换行分隔，预留）
     */
    private String typingTexts;

    /**
     * 首页公告条（文章列表上方）
     */
    private String headerNotification;

    /**
     * 侧边栏公告
     */
    private String sidebarAnnouncement;

    /**
     * 侧边栏每日鸡汤
     */
    private String dailySoup;

    /**
     * ICP备案号
     */
    private String recordInfo;

    /**
     * 关于本站的说明内容
     */
    private String aboutSite;

    /**
     * 技术栈列表（多个以换行分隔）
     */
    private String techStack;

    /**
     * 扩展信息（预留，按需使用）
     */
    private String extendInfo;

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
