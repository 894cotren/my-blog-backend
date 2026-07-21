package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 网站配置新增/修改请求
 * <p>
 * 单行配置表，新增与修改字段一致，共用此请求
 *
 * @author grey
 */
@Data
public class WebsiteConfigRequest implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 首页 banner 轮播图列表
     */
    private List<String> bannerImages;

    /**
     * 文章默认封面图列表（文章未传封面时随机取一张兜底）
     */
    private List<String> articleCoverImages;

    /**
     * 首页打字机滚动文案列表（预留）
     */
    private List<String> typingTexts;

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
     * 技术栈列表
     */
    private List<String> techStack;

    /**
     * 扩展信息（预留，按需使用）
     */
    private String extendInfo;
}
