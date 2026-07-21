package com.grey.myblog.controller.admin;

import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.model.DeleteRequest;
import com.grey.myblog.model.dto.WebsiteConfigDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.WebsiteConfigRequest;
import com.grey.myblog.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 网站配置管理接口
 *
 * @author grey
 */
@RestController
@RequestMapping("/admin/website-config")
public class WebsiteConfigAdminController {

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 获取网站配置
     */
    @GetMapping("/get")
    @AuthCheck
    public Result<WebsiteConfigDTO> getWebsiteConfig() {
        return Result.success(websiteConfigService.getWebsiteConfig());
    }

    /**
     * 新增网站配置
     */
    @PostMapping("/add")
    @AuthCheck
    public Result<Long> addWebsiteConfig(@RequestBody WebsiteConfigRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        return Result.success(websiteConfigService.addWebsiteConfig(request));
    }

    /**
     * 修改网站配置
     */
    @PostMapping("/update")
    @AuthCheck
    public Result<Boolean> updateWebsiteConfig(@RequestBody WebsiteConfigRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        return Result.success(websiteConfigService.updateWebsiteConfig(request));
    }

    /**
     * 删除网站配置
     */
    @PostMapping("/delete")
    @AuthCheck
    public Result<Boolean> deleteWebsiteConfig(@RequestBody DeleteRequest deleteRequest) {
        if (ObjectUtils.isEmpty(deleteRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        return Result.success(websiteConfigService.deleteWebsiteConfig(deleteRequest.getId()));
    }
}
