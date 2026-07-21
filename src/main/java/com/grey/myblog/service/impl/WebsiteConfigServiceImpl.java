package com.grey.myblog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.constant.WebsiteConfigConstant;
import com.grey.myblog.dao.WebsiteConfigDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.WebsiteConfigDO;
import com.grey.myblog.model.dto.WebsiteConfigDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.WebsiteConfigRequest;
import com.grey.myblog.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网站配置服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    private static final int MAX_WEBSITE_NAME_LENGTH = 100;

    @Resource
    private WebsiteConfigDAO websiteConfigDAO;

    /**
     * 本地缓存：网站配置查询走此成员属性，写操作（增/改/删）失效后下次查询重新加载
     */
    private volatile WebsiteConfigDTO configCache;

    @Override
    public WebsiteConfigDTO getWebsiteConfig() {
        WebsiteConfigDTO cached = configCache;
        if (cached != null) {
            return cached;
        }
        WebsiteConfigDO config = websiteConfigDAO.selectFirst();
        if (config == null) {
            log.warn("网站配置记录不存在，请先初始化 website_config 表数据");
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "网站配置不存在，请先初始化");
        }
        WebsiteConfigDTO dto = convertToWebsiteConfigDTO(config);
        configCache = dto;
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWebsiteConfig(WebsiteConfigRequest request) {
        validateWebsiteConfigRequest(request);
        // 单行配置表：已存在则不允许重复新增，避免出现多条记录导致 selectFirst 命中旧数据
        if (websiteConfigDAO.selectFirst() != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "网站配置已存在，请使用修改接口");
        }
        WebsiteConfigDO config = buildWebsiteConfigDO(request, null);
        config.setCreateTime(new Date());
        int result = websiteConfigDAO.insert(config);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增网站配置失败");
        }
        invalidateCache();
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateWebsiteConfig(WebsiteConfigRequest request) {
        validateWebsiteConfigRequest(request);
        WebsiteConfigDO existing = websiteConfigDAO.selectFirst();
        if (existing == null) {
            log.warn("网站配置记录不存在，更新失败");
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "网站配置不存在，请先初始化");
        }
        WebsiteConfigDO config = buildWebsiteConfigDO(request, existing.getId());
        int result = websiteConfigDAO.updateById(config);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新网站配置失败");
        }
        invalidateCache();
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWebsiteConfig(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "配置ID非法");
        }
        int result = websiteConfigDAO.deleteById(id);
        if (result <= 0) {
            log.warn("网站配置记录不存在，删除失败，id={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "网站配置不存在，删除失败");
        }
        invalidateCache();
        return true;
    }

    /**
     * 校验网站配置请求（新增/修改共用）
     */
    private void validateWebsiteConfigRequest(WebsiteConfigRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        String websiteName = StrUtil.trim(request.getWebsiteName());
        if (StrUtil.isBlank(websiteName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "网站标题不能为空");
        }
        if (websiteName.length() > MAX_WEBSITE_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "网站标题长度不能超过100");
        }
    }

    /**
     * 根据请求构建网站配置 DO（id 为 null 表示新增，非 null 表示修改指定记录）
     * 多值字段由 List 按分隔符常量拼接为存储字符串
     */
    private WebsiteConfigDO buildWebsiteConfigDO(WebsiteConfigRequest request, Long id) {
        return WebsiteConfigDO.builder()
                .id(id)
                .websiteName(StrUtil.trim(request.getWebsiteName()))
                .logo(normalizeOptional(request.getLogo()))
                .favicon(normalizeOptional(request.getFavicon()))
                .bloggerCardBackground(normalizeOptional(request.getBloggerCardBackground()))
                .bannerImages(joinToRaw(request.getBannerImages()))
                .articleCoverImages(joinToRaw(request.getArticleCoverImages()))
                .typingTexts(joinToRaw(request.getTypingTexts()))
                .headerNotification(normalizeOptional(request.getHeaderNotification()))
                .sidebarAnnouncement(normalizeOptional(request.getSidebarAnnouncement()))
                .dailySoup(normalizeOptional(request.getDailySoup()))
                .recordInfo(normalizeOptional(request.getRecordInfo()))
                .extendInfo(normalizeOptional(request.getExtendInfo()))
                .updateTime(new Date())
                .build();
    }

    /**
     * 转换为网站配置响应对象（多值字段按分隔符常量拆分为 List）
     */
    private WebsiteConfigDTO convertToWebsiteConfigDTO(WebsiteConfigDO config) {
        WebsiteConfigDTO dto = new WebsiteConfigDTO();
        BeanUtils.copyProperties(config, dto);
        // 多值字段类型不一致（DO 为 String，DTO 为 List），BeanUtils 不会拷贝，需手动拆分
        dto.setBannerImages(splitToList(config.getBannerImages()));
        dto.setArticleCoverImages(splitToList(config.getArticleCoverImages()));
        dto.setTypingTexts(splitToList(config.getTypingTexts()));
        return dto;
    }

    /**
     * 将存储字符串按分隔符常量拆分为 List（剔除空白元素）
     */
    private List<String> splitToList(String raw) {
        if (StrUtil.isBlank(raw)) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.split(WebsiteConfigConstant.LIST_SEPARATOR))
                .map(StrUtil::trim)
                .filter(s -> !StrUtil.isBlank(s))
                .collect(Collectors.toList());
    }

    /**
     * 将 List 按分隔符常量拼接为存储字符串（剔除空白元素，空列表返回 null）
     */
    private String joinToRaw(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream()
                .filter(s -> !StrUtil.isBlank(s))
                .map(StrUtil::trim)
                .collect(Collectors.joining(WebsiteConfigConstant.LIST_SEPARATOR));
    }

    /**
     * 标准化可空字段：去空白，空串返回 null
     */
    private String normalizeOptional(String value) {
        String normalizedValue = StrUtil.trim(value);
        return StrUtil.isBlank(normalizedValue) ? null : normalizedValue;
    }

    /**
     * 失效本地缓存
     */
    private void invalidateCache() {
        configCache = null;
    }
}
