package com.grey.myblog.controller.admin;

import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.BlogCommentDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.service.BlogCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客评论接口（管理端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/admin/comment")
public class BlogCommentAdminController {

    @Resource
    private BlogCommentService blogCommentService;

    /**
     * 获取全部评论列表
     */
    @GetMapping("/list")
    @AuthCheck
    public Result<List<BlogCommentDTO>> getAllComments() {
        List<BlogCommentDTO> comments = blogCommentService.getAllComments();
        return Result.success(comments);
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete/{id}")
    @AuthCheck
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "评论ID无效");
        }
        Boolean result = blogCommentService.deleteComment(id);
        return Result.success(result);
    }
}
