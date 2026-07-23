package com.grey.myblog.interceptor;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.service.SiteStatsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 全站访问计数拦截器
 * 拦截博客端请求，按 IP 去重：同一 IP 在窗口期内只计一次访问量（PV）
 *
 * @author grey
 */
@Slf4j
@Component
public class VisitCountInterceptor implements HandlerInterceptor {

    @Resource
    private SiteStatsService siteStatsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 访问计数 IP 去重 key 前缀 */
    private static final String PV_IP_KEY_PREFIX = "blog:pv:ip:";

    /** IP 去重窗口期（分钟）：同一 IP 在此期间内只计一次访问 */
    private static final long PV_IP_EXPIRE_MINUTES = 2L;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求不计入访问
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        try {
            String ip = getClientIp(request);

            if (StrUtil.isBlank(ip)) {
                return true;
            }
            // setIfAbsent 原子操作：key 不存在才写入并设置过期，返回 true 表示本窗口内首次访问
            Boolean acquired = stringRedisTemplate.opsForValue()
                    .setIfAbsent(PV_IP_KEY_PREFIX + ip, "1", PV_IP_EXPIRE_MINUTES, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(acquired)) {
                siteStatsService.incrementVisitCount();
            }
        } catch (Exception e) {
            // 计数失败不影响正常请求
            log.error("访问计数异常", e);
        }
        return true;
    }

    /**
     * 获取客户端真实 IP
     * 优先取代理头（Nginx 等反代场景），取不到再回退到 getRemoteAddr
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能为 "ip1, ip2, ..."，取第一个（即客户端真实 IP）
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }
}
