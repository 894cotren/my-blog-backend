package com.grey.myblog.controller.admin;

import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.BloggerInfoUpdateRequest;
import com.grey.myblog.model.dto.BloggerInfoDTO;
import com.grey.myblog.service.BloggerInfoService;
import jakarta.annotation.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博主信息管理接口
 *
 * @author grey
 */
@RestController
@RequestMapping("/admin/blogger-info")
public class BloggerInfoAdminController {

    @Resource
    private BloggerInfoService bloggerInfoService;

    /**
     * 获取博主信息
     */
    @GetMapping("/get")
    @AuthCheck
    public Result<BloggerInfoDTO> getBloggerInfo() {
        return Result.success(bloggerInfoService.getBloggerInfo());
    }

    /**
     * 更新博主信息
     */
    @PostMapping("/update")
    @AuthCheck
    public Result<Boolean> updateBloggerInfo(@RequestBody BloggerInfoUpdateRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        return Result.success(bloggerInfoService.updateBloggerInfo(request));
    }
}
