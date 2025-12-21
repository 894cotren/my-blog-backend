package com.grey.myblog.aop;

import com.grey.myblog.annotation.AuthCheck;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

//@Aspect
//@Component
public class AuthInterceptor {

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 获取当前登录用户 实现鉴权逻辑
        // mustRole为注解传入必须要的角色字段，通过这个字段进行简单鉴权。
        // 通过普通用户的权限校验，放行
        return joinPoint.proceed();
    }
}

























