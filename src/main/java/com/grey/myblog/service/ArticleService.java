package com.grey.myblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grey.myblog.model.entity.Article;
import com.grey.myblog.model.entity.User;
import com.grey.myblog.model.request.ArticleAddRequest;
import com.grey.myblog.model.request.ArticlePageListRequest;
import com.grey.myblog.model.request.ArticleUpdateRequest;
import com.grey.myblog.model.vo.ArticleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 *
 * @author grey
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章列表
     *
     * @param request 分页查询请求
     * @return 分页文章列表
     */
    Page<ArticleVO> listArticles(ArticlePageListRequest request);

    /**
     * 获取文章详情（自动增加阅读量）
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id);

    /**
     * 文章归档（按年月）
     *
     * @param year  年份（可选）
     * @param month 月份（可选）
     * @return 按年月分组的文章列表 Map<年份, Map<月份, List<文章>>>
     */
    Map<String, Map<String, List<ArticleVO>>> getArticleArchive(Integer year, Integer month);

    /**
     * 创建文章
     *
     * @param request   创建请求
     * @param loginUser 当前登录用户
     * @return 文章ID
     */
    Long addArticle(ArticleAddRequest request, User loginUser);

    /**
     * 更新文章
     *
     * @param request   更新请求
     * @param loginUser 当前登录用户
     * @return 是否成功
     */
    Boolean updateArticle(ArticleUpdateRequest request, User loginUser);

    /**
     * 删除文章
     *
     * @param id        文章ID
     * @param loginUser 当前登录用户
     * @return 是否成功
     */
    Boolean deleteArticle(Long id, User loginUser);

    /**
     * 增加阅读量
     *
     * @param id 文章ID
     * @return 是否成功
     */
    Boolean incrementViewCount(Long id);
}
