package com.grey.myblog.exception;

import com.grey.myblog.model.enums.ErrorCode;

/**
 *  抛异常工具，快速判断并抛出异常
 */
public class ThrowUtil {

    /**
     * 条件成立则抛出异常,传入需要抛的异常
     *
     * @param condition 条件
     * @param runtimeException  自定义运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常，根据ErrorCode封装BusinessException业务异常抛出
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
