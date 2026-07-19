package com.grey.myblog.service;

import com.grey.myblog.model.request.BloggerInfoUpdateRequest;
import com.grey.myblog.model.dto.BloggerInfoDTO;

/**
 * 博主信息服务接口
 *
 * @author grey
 */
public interface BloggerInfoService {

    /**
     * 获取博主信息
     *
     * @return 博主信息
     */
    BloggerInfoDTO getBloggerInfo();

    /**
     * 更新博主信息
     *
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateBloggerInfo(BloggerInfoUpdateRequest request);
}