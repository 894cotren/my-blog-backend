package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 留言添加请求
 *
 * @author grey
 */
@Data
public class LeaveWordAddRequest implements Serializable {
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
     * 留言内容
     */
    private String content;
}
