package com.grey.myblog.controller.blog;

import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.WebsiteConfigDTO;
import com.grey.myblog.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站配置接口（博客端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/blog/website-config")
public class WebsiteConfigBlogController {

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 获取网站配置（供前端读取网站标题、Logo、Banner 等）
     */
    @GetMapping("/get")
    public Result<WebsiteConfigDTO> getWebsiteConfig() {
        return Result.success(websiteConfigService.getWebsiteConfig());
    }
}
