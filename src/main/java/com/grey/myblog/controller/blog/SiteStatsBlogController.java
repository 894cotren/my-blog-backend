package com.grey.myblog.controller.blog;

import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.SiteStatsDTO;
import com.grey.myblog.service.SiteStatsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站资讯统计接口（博客端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/blog/site-stats")
public class SiteStatsBlogController {

    @Resource
    private SiteStatsService siteStatsService;

    /**
     * 获取网站资讯统计
     */
    @GetMapping("/get")
    public Result<SiteStatsDTO> getSiteStats() {
        return Result.success(siteStatsService.getSiteStats());
    }
}
