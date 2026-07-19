package com.grey.myblog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.dao.BloggerInfoDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.BloggerInfoDO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.BloggerInfoUpdateRequest;
import com.grey.myblog.model.dto.BloggerInfoDTO;
import com.grey.myblog.service.BloggerInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 博主信息服务实现类
 *
 * @author grey
 */
@Service
public class BloggerInfoServiceImpl implements BloggerInfoService {

    private static final int MAX_BLOGGER_NAME_LENGTH = 100;
    private static final int MAX_INTRO_LENGTH = 500;

    @Resource
    private BloggerInfoDAO bloggerInfoDAO;

    @Override
    public BloggerInfoDTO getBloggerInfo() {
        BloggerInfoDO bloggerInfo = getOrCreateBloggerInfo();
        return convertToBloggerInfoDTO(bloggerInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBloggerInfo(BloggerInfoUpdateRequest request) {
        validateBloggerInfoUpdateRequest(request);
        BloggerInfoDO existingBloggerInfo = getOrCreateBloggerInfo();

        BloggerInfoDO bloggerInfo = BloggerInfoDO.builder()
                .id(existingBloggerInfo.getId())
                .bloggerName(normalizeBloggerName(request.getBloggerName()))
                .avatar(normalizeOptionalField(request.getAvatar()))
                .intro(normalizeIntro(request.getIntro()))
                .githubUrl(normalizeOptionalField(request.getGithubUrl()))
                .email(normalizeOptionalField(request.getEmail()))
                .aboutContent(normalizeOptionalField(request.getAboutContent()))
                .updateTime(new Date())
                .build();

        int result = bloggerInfoDAO.updateById(bloggerInfo);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新博主信息失败");
        }
        return true;
    }

    /**
     * 获取或初始化博主信息
     */
    private BloggerInfoDO getOrCreateBloggerInfo() {
        BloggerInfoDO bloggerInfo = bloggerInfoDAO.selectFirst();
        if (bloggerInfo != null) {
            return bloggerInfo;
        }

        BloggerInfoDO defaultBloggerInfo = BloggerInfoDO.builder()
                .bloggerName("Grey")
                .updateTime(new Date())
                .isDeleted(0)
                .build();

        int result = bloggerInfoDAO.insert(defaultBloggerInfo);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "初始化博主信息失败");
        }
        return defaultBloggerInfo;
    }

    /**
     * 校验博主信息更新请求
     */
    private void validateBloggerInfoUpdateRequest(BloggerInfoUpdateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        String bloggerName = normalizeBloggerName(request.getBloggerName());
        if (StrUtil.isBlank(bloggerName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博主名称不能为空");
        }
        if (bloggerName.length() > MAX_BLOGGER_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博主名称长度不能超过100");
        }

        String intro = normalizeIntro(request.getIntro());
        if (intro != null && intro.length() > MAX_INTRO_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博主简介长度不能超过500");
        }
    }

    /**
     * 转换为博主信息响应对象
     */
    private BloggerInfoDTO convertToBloggerInfoDTO(BloggerInfoDO bloggerInfo) {
        BloggerInfoDTO response = new BloggerInfoDTO();
        BeanUtils.copyProperties(bloggerInfo, response);
        return response;
    }

    /**
     * 标准化博主名称
     */
    private String normalizeBloggerName(String bloggerName) {
        return StrUtil.trim(bloggerName);
    }

    /**
     * 标准化博主简介
     */
    private String normalizeIntro(String intro) {
        String normalizedIntro = StrUtil.trim(intro);
        return StrUtil.isBlank(normalizedIntro) ? null : normalizedIntro;
    }

    /**
     * 标准化可空字段
     */
    private String normalizeOptionalField(String value) {
        String normalizedValue = StrUtil.trim(value);
        return StrUtil.isBlank(normalizedValue) ? null : normalizedValue;
    }
}