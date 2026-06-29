package com.grey.myblog.dao;

import com.grey.myblog.model.dataobject.BlogCommentDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客评论表 DAO
 *
 * @author grey
 */
public interface BlogCommentDAO {

    /**
     * 插入评论
     */
    int insert(BlogCommentDO comment);

    /**
     * 根据ID删除评论
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询评论
     */
    BlogCommentDO selectById(@Param("id") Long id);

    /**
     * 根据文章ID查询评论列表
     */
    List<BlogCommentDO> selectByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询全部评论列表
     */
    List<BlogCommentDO> selectAll();

    /**
     * 根据父评论ID查询子评论列表
     */
    List<BlogCommentDO> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 统计文章评论数
     */
    int countByArticleId(@Param("articleId") Long articleId);
}
