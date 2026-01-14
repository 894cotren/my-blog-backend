package com.grey.myblog.common;

import com.grey.myblog.model.enums.ErrorCode;
import lombok.Data;

/**
 * 统一请求返回
 * 内部有静态方法方便快速成功、失败的返回，搭配ErrorCodeEnum枚举类使用
 * @author grey
 */
@Data
public class Result<T>{
    /**
     * 返回给前端的状态码 (搭配搭配ErrorCodeEnum枚举类使用)
     */
    private Integer code;
    /**
     * 数据体
     */
    private T data;

    /**
     * 消息
     */
    private String message;

    /**
     * 私有化构造函数
     */
    private Result(){}

    /**
     * 成功统一返回对象
     * @param data  数据体
     */
    public static <T> Result<T> success(T data){
        Result<T> result=new Result<>();
        result.code= ErrorCode.SUCCESS.getCode();
        result.data=data;
        result.message="成功";
        return result;
    }


    public static <T> Result<T> fail(int code,String message){
        Result<T> result=new Result<>();
        result.code=code;
        result.data=null;
        result.message=message;
        return result;
    }

    public static <T> Result<T> fail(ErrorCode errorCode){
        Result<T> result=new Result<>();
        result.code= errorCode.getCode();
        result.data=null;
        result.message= errorCode.getMessage();
        return result;
    }


    public static <T> Result<T> fail(ErrorCode errorCode, String message){
        Result<T> result=new Result<>();
        result.code= errorCode.getCode();
        result.data=null;
        result.message=message;
        return result;
    }

    /**
     * 自定义返回 参数自定
     * @param code 状态码
     * @param data 数据体
     * @param message 发给前端的消息
     */
    public static <T> Result<T> build(Integer code,T data,String message){
        Result<T> result=new Result<>();
        result.code=code;
        result.data=data;
        result.message=message;
        return result;
    }
}
