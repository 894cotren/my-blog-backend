package com.grey.myblog.dao;

import com.grey.myblog.model.dataobject.WebsiteConfigDO;
import org.apache.ibatis.annotations.Param;

/**
 * 网站配置表 DAO
 *
 * @author grey
 */
public interface WebsiteConfigDAO {

    /**
     * 插入网站配置
     */
    int insert(WebsiteConfigDO websiteConfig);

    /**
     * 根据ID更新网站配置（动态更新非空字段）
     */
    int updateById(WebsiteConfigDO websiteConfig);

    /**
     * 根据ID删除网站配置
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询第一条网站配置（单行表）
     */
    WebsiteConfigDO selectFirst();
}
