package com.grey.myblog.model.request;

import com.grey.myblog.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 分页查询用户request
 *
 * @author grey
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageListRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户昵称
     */
    private String nickname;

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
