package com.grey.myblog.config;

import com.grey.myblog.interceptor.VisitCountInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置（拦截器注册）
 *
 * @author grey
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private VisitCountInterceptor visitCountInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截博客端所有请求，用于全站访问计数（PV）
        registry.addInterceptor(visitCountInterceptor)
                .addPathPatterns("/blog/**");
    }
}
