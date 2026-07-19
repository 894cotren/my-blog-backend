package com.grey.myblog.controller.blog;

import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.BloggerInfoDTO;
import com.grey.myblog.service.BloggerInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博主信息接口（博客端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/blog/blogger-info")
public class BloggerInfoBlogController {

    @Resource
    private BloggerInfoService bloggerInfoService;

    /**
     * 获取博主信息
     */
    @GetMapping("/get")
    public Result<BloggerInfoDTO> getBloggerInfo() {
        return Result.success(bloggerInfoService.getBloggerInfo());
    }
}
