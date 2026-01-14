package com.grey.myblog.utils;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 数据格式校验工具类
 *
 * @author grey
 */
public class ValidationUtils {

    /**
     * 邮箱格式正则表达式
     * 支持标准邮箱格式：username@domain.com
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * 中国手机号格式正则表达式
     * 支持：11位数字，1开头，第二位为3-9
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    /**
     * 校验邮箱格式是否合法
     *
     * @param email 待校验的邮箱地址
     * @return true-格式合法，false-格式不合法或为空
     */
    public static boolean isValidEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 校验手机号格式是否合法（中国手机号）
     *
     * @param mobile 待校验的手机号
     * @return true-格式合法，false-格式不合法或为空
     */
    public static boolean isValidMobile(String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    /**
     * 校验邮箱格式，格式不合法时抛出异常
     *
     * @param email 待校验的邮箱地址
     * @throws com.grey.myblog.exception.BusinessException 邮箱格式不合法时抛出
     */
    public static void validateEmail(String email) {
        if (StrUtil.isNotBlank(email) && !isValidEmail(email)) {
            throw new com.grey.myblog.exception.BusinessException(
                    com.grey.myblog.model.enums.ErrorCode.PARAMS_ERROR,
                    "邮箱格式不正确"
            );
        }
    }

    /**
     * 校验手机号格式，格式不合法时抛出异常
     *
     * @param mobile 待校验的手机号
     * @throws com.grey.myblog.exception.BusinessException 手机号格式不合法时抛出
     */
    public static void validateMobile(String mobile) {
        if (StrUtil.isNotBlank(mobile) && !isValidMobile(mobile)) {
            throw new com.grey.myblog.exception.BusinessException(
                    com.grey.myblog.model.enums.ErrorCode.PARAMS_ERROR,
                    "手机号格式不正确，请输入11位有效手机号"
            );
        }
    }
}
