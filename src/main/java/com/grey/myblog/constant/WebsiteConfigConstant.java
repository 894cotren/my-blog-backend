package com.grey.myblog.constant;


/**
 * 网站配置模块常量
 *
 * @author grey
 */
public interface WebsiteConfigConstant {

    /**
     * 多值字段的分隔符（banner 图、文章默认封面图、打字机文案等列表均以换行分隔）
     * 复用同一分隔符，保证存储与解析口径一致
     */
    String LIST_SEPARATOR = "\n";
}
