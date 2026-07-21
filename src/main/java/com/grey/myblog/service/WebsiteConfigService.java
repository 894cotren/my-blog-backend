package com.grey.myblog.service;

import com.grey.myblog.model.dto.WebsiteConfigDTO;
import com.grey.myblog.model.request.WebsiteConfigRequest;

/**
 * 网站配置服务接口
 *
 * @author grey
 */
public interface WebsiteConfigService {

    /**
     * 获取网站配置（单行表，命中本地缓存直接返回）
     *
     * @return 网站配置
     */
    WebsiteConfigDTO getWebsiteConfig();

    /**
     * 新增网站配置（初始化单行记录）
     *
     * @param request 网站配置请求
     * @return 新增记录ID
     */
    Long addWebsiteConfig(WebsiteConfigRequest request);

    /**
     * 修改网站配置
     *
     * @param request 网站配置请求
     * @return 是否成功
     */
    Boolean updateWebsiteConfig(WebsiteConfigRequest request);

    /**
     * 删除网站配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    Boolean deleteWebsiteConfig(Long id);
}
