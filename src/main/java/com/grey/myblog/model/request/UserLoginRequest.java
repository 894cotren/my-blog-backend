package com.grey.myblog.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录接参类
 *
 * @author grey
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

}
