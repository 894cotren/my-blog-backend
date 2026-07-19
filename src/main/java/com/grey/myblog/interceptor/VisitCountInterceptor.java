package com.grey.myblog.interceptor;

import com.grey.myblog.service.SiteStatsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全站访问计数拦截器
 * 拦截博客端请求，每次访问 visit_count + 1
 *
 * @author grey
 */
@Slf4j
@Component
public class VisitCountInterceptor implements HandlerInterceptor {

    @Resource
    private SiteStatsService siteStatsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求不计入访问
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        try {
            siteStatsService.incrementVisitCount();
        } catch (Exception e) {
            // 计数失败不影响正常请求
            log.error("访问计数异常", e);
        }
        return true;
    }
}
