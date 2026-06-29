package com.grey.myblog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.dao.BlogCommentDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.BlogCommentDO;
import com.grey.myblog.model.dto.BlogCommentDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.BlogCommentAddRequest;
import com.grey.myblog.service.BlogCommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 博客评论服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class BlogCommentServiceImpl implements BlogCommentService {

    @Resource
    private BlogCommentDAO blogCommentDAO;

    @Override
    public Long addComment(BlogCommentAddRequest request) {
        // 参数校验
        if (StrUtil.hasBlank(request.getNickname(), request.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称和评论内容不能为空");
        }
        if (request.getArticleId() == null || request.getArticleId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID无效");
        }
        if (request.getNickname().length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称长度不能超过50");
        }
        if (request.getContent().length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容长度不能超过1000");
        }

        BlogCommentDO comment = BlogCommentDO.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .content(request.getContent())
                .articleId(request.getArticleId())
                .parentId(request.getParentId() == null ? 0L : request.getParentId())
                .replyNickname(request.getReplyNickname())
                .createTime(new Date())
                .build();

        blogCommentDAO.insert(comment);
        log.info("action=add_comment, articleId={}, nickname={}, result=success", request.getArticleId(), request.getNickname());
        return comment.getId();
    }

    @Override
    public Boolean deleteComment(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID无效");
        }
        int result = blogCommentDAO.deleteById(id);
        log.info("action=delete_comment, commentId={}, result={}", id, result > 0 ? "success" : "fail");
        return result > 0;
    }

    @Override
    public List<BlogCommentDTO> getCommentsByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return new ArrayList<>();
        }

        // 查询该文章的所有评论
        List<BlogCommentDO> allComments = blogCommentDAO.selectByArticleId(articleId);

        // 转换为 DTO
        List<BlogCommentDTO> dtoList = allComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildCommentTree(dtoList);
    }

    @Override
    public List<BlogCommentDTO> getAllComments() {
        List<BlogCommentDO> comments = blogCommentDAO.selectAll();
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer countByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return 0;
        }
        return blogCommentDAO.countByArticleId(articleId);
    }

    /**
     * 转换为 DTO
     */
    private BlogCommentDTO convertToDTO(BlogCommentDO commentDO) {
        BlogCommentDTO dto = new BlogCommentDTO();
        BeanUtils.copyProperties(commentDO, dto);
        return dto;
    }

    /**
     * 构建评论树形结构
     */
    private List<BlogCommentDTO> buildCommentTree(List<BlogCommentDTO> comments) {
        // 分离一级评论和子评论
        Map<Boolean, List<BlogCommentDTO>> grouped = comments.stream()
                .collect(Collectors.partitioningBy(c -> c.getParentId() == null || c.getParentId() == 0));

        List<BlogCommentDTO> rootComments = grouped.get(true);
        List<BlogCommentDTO> childComments = grouped.get(false);

        if (rootComments == null) {
            rootComments = new ArrayList<>();
        }
        if (childComments == null) {
            childComments = new ArrayList<>();
        }

        // 按父评论ID分组
        Map<Long, List<BlogCommentDTO>> childrenMap = childComments.stream()
                .collect(Collectors.groupingBy(BlogCommentDTO::getParentId));

        // 为每个一级评论设置子评论
        rootComments.forEach(root -> {
            List<BlogCommentDTO> children = childrenMap.get(root.getId());
            if (children != null) {
                root.setChildren(children);
            }
        });

        return rootComments;
    }
}
