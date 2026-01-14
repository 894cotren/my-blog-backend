package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册接参类
 *
 * @author grey
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String checkPassword;
}
