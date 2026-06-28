package com.grey.myblog.aop;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.constant.UserConstant;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.UserDO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.enums.UserRoleEnum;
import com.grey.myblog.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {


    @Resource
    private UserService userService;

    /**
     * 接口鉴权
     * 默认校验是否登录，mustRole 指定具体角色时才校验角色
     */
    @Around("@annotation(authCheck)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取当前请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 校验请求头是否携带 token
        String token = request.getHeader(UserConstant.TOKEN_HEADER_KEY);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 获取当前登录用户（内部会校验 token 有效性）
        UserDO loginUser = userService.getLoginUser(request);

        // 获取注解指定的角色
        String mustRole = authCheck.mustRole();

        // 如果指定了角色，则校验角色权限
        if (StrUtil.isNotBlank(mustRole)) {
            UserRoleEnum mustRoleEnum = UserRoleEnum.getRoleEnumByValue(mustRole);
            if (mustRoleEnum == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注解指定的角色不存在");
            }

            UserRoleEnum userRoleEnum = UserRoleEnum.getRoleEnumByValue(loginUser.getRole());
            if (userRoleEnum == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户角色异常");
            }

            // 用户角色不匹配且不是管理员，则拒绝
            if (!mustRoleEnum.equals(userRoleEnum) && !userRoleEnum.equals(UserRoleEnum.ADMIN_USER)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 放行
        return joinPoint.proceed();
    }
}

























