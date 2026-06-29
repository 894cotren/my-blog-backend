package com.grey.myblog.service;

import com.grey.myblog.model.dto.BlogCommentDTO;
import com.grey.myblog.model.request.BlogCommentAddRequest;

import java.util.List;

/**
 * 博客评论服务接口
 *
 * @author grey
 */
public interface BlogCommentService {

    /**
     * 添加评论
     */
    Long addComment(BlogCommentAddRequest request);

    /**
     * 删除评论
     */
    Boolean deleteComment(Long id);

    /**
     * 根据文章ID获取评论列表（树形结构）
     */
    List<BlogCommentDTO> getCommentsByArticleId(Long articleId);

    /**
     * 获取全部评论列表（后台管理）
     */
    List<BlogCommentDTO> getAllComments();

    /**
     * 统计文章评论数
     */
    Integer countByArticleId(Long articleId);
}
