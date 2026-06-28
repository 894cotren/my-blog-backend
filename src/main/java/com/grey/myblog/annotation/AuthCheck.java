package com.grey.myblog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 标注在接口方法上，默认校验登录态，可通过 mustRole 指定角色
 *
 * @author grey
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 指定必须具有的角色（可选）
     * 不指定则只校验是否登录
     */
    String mustRole() default "";
}
