package com.grey.myblog.dao;

import com.grey.myblog.model.dataobject.BloggerInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博主信息表 DAO
 *
 * @author grey
 */
public interface BloggerInfoDAO {

    /**
     * 插入博主信息
     */
    int insert(BloggerInfoDO bloggerInfo);

    /**
     * 根据ID更新博主信息
     */
    int updateById(BloggerInfoDO bloggerInfo);

    /**
     * 根据ID逻辑删除博主信息
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询博主信息
     */
    BloggerInfoDO selectById(@Param("id") Long id);

    /**
     * 查询第一条博主信息（用于获取唯一配置）
     */
    BloggerInfoDO selectFirst();

    /**
     * 查询博主信息列表
     */
    List<BloggerInfoDO> selectList(@Param("bloggerInfo") BloggerInfoDO bloggerInfo);
}