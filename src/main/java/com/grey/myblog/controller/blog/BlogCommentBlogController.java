package com.grey.myblog.controller.blog;

import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.BlogCommentDTO;
import com.grey.myblog.model.request.BlogCommentAddRequest;
import com.grey.myblog.service.BlogCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客评论接口（博客端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/blog/comment")
public class BlogCommentBlogController {

    @Resource
    private BlogCommentService blogCommentService;

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public Result<Long> addComment(@RequestBody BlogCommentAddRequest request) {
        Long commentId = blogCommentService.addComment(request);
        return Result.success(commentId);
    }

    /**
     * 根据文章ID获取评论列表
     */
    @GetMapping("/list/{articleId}")
    public Result<List<BlogCommentDTO>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<BlogCommentDTO> comments = blogCommentService.getCommentsByArticleId(articleId);
        return Result.success(comments);
    }

    /**
     * 统计文章评论数
     */
    @GetMapping("/count/{articleId}")
    public Result<Integer> countByArticleId(@PathVariable Long articleId) {
        Integer count = blogCommentService.countByArticleId(articleId);
        return Result.success(count);
    }
}
