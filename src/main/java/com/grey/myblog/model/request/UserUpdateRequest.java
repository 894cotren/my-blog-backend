package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新request
 *
 * @author grey
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户角色：user/admin
     */
    private String role;


}
