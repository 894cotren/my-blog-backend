package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论添加请求
 *
 * @author grey
 */
@Data
public class BlogCommentAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱（可选）
     */
    private String email;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 父评论ID（回复时使用，可选）
     */
    private Long parentId;

    /**
     * 被回复者昵称（回复时使用，可选）
     */
    private String replyNickname;
}
