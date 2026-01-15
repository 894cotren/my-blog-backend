package com.grey.myblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.entity.Article;
import com.grey.myblog.model.entity.ArticleTag;
import com.grey.myblog.model.entity.Category;
import com.grey.myblog.model.entity.Tag;
import com.grey.myblog.model.entity.User;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.ArticleAddRequest;
import com.grey.myblog.model.request.ArticlePageListRequest;
import com.grey.myblog.model.request.ArticleUpdateRequest;
import com.grey.myblog.model.vo.ArticleVO;
import com.grey.myblog.model.vo.CategoryVO;
import com.grey.myblog.model.vo.TagVO;
import com.grey.myblog.service.ArticleService;
import com.grey.myblog.service.ArticleTagService;
import com.grey.myblog.service.CategoryService;
import com.grey.myblog.service.TagService;
import com.grey.myblog.service.UserService;
import com.grey.myblog.mapper.ArticleMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private ArticleTagService articleTagService;

    @Resource
    private UserService userService;

    @Override
    public Page<ArticleVO> listArticles(ArticlePageListRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 参数校验：页码和每页数量不能小于1，设置默认值
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }

        try {
            // 构建查询条件（分类、标签、状态筛选，排序）
            QueryWrapper<Article> queryWrapper = buildQueryWrapper(request);
            // 执行分页查询
            Page<Article> articlePage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
            
            // 转换为VO对象
            List<ArticleVO> articleVOList = articlePage.getRecords().stream()
                    .map(this::convertToArticleVO)
                    .collect(Collectors.toList());
            
            // 填充关联数据（分类、作者、标签）
            fillAssociatedData(articleVOList);
            
            // 构建分页结果
            Page<ArticleVO> articleVOPage = new Page<>(pageNum, pageSize, articlePage.getTotal());
            articleVOPage.setRecords(articleVOList);
            return articleVOPage;
        } catch (Exception e) {
            log.error("分页查询文章列表异常：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "分页查询失败");
        }
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID无效");
        }

        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        incrementViewCount(id);
        
        ArticleVO articleVO = convertToArticleVO(article);
        fillAssociatedData(Collections.singletonList(articleVO));
        return articleVO;
    }

    @Override
    public Map<String, Map<String, List<ArticleVO>>> getArticleArchive(Integer year, Integer month) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Article::getStatus, 1);
        
        if (year != null) {
            queryWrapper.lambda().apply("YEAR(create_time) = {0}", year);
        }
        if (month != null) {
            queryWrapper.lambda().apply("MONTH(create_time) = {0}", month);
        }
        
        queryWrapper.lambda().orderByDesc(Article::getCreateTime);
        
        List<Article> articles = this.list(queryWrapper);
        List<ArticleVO> articleVOList = articles.stream()
                .map(this::convertToArticleVO)
                .collect(Collectors.toList());
        
        fillAssociatedData(articleVOList);
        
        Map<String, Map<String, List<ArticleVO>>> archiveMap = new LinkedHashMap<>();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        
        for (ArticleVO articleVO : articleVOList) {
            if (articleVO.getCreateTime() == null) {
                continue;
            }
            
            String yearStr = yearFormat.format(articleVO.getCreateTime());
            String monthStr = monthFormat.format(articleVO.getCreateTime());
            
            archiveMap.computeIfAbsent(yearStr, k -> new LinkedHashMap<>())
                    .computeIfAbsent(monthStr, k -> new ArrayList<>())
                    .add(articleVO);
        }
        
        return archiveMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addArticle(ArticleAddRequest request, User loginUser) {
        validateArticleRequest(request);
        
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        article.setAuthorId(loginUser.getId());
        article.setViewCount(0);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        
        boolean saved = this.save(article);
        if (!saved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建文章失败");
        }
        
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), request.getTagIds());
        }
        
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateArticle(ArticleUpdateRequest request, User loginUser) {
        if (request == null || request.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        
        validateArticleRequest(request);
        
        Article existingArticle = this.getById(request.getId());
        if (existingArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        
        if (!userService.isAdmin(loginUser) && !existingArticle.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改此文章");
        }
        
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        article.setUpdateTime(new Date());
        
        boolean updated = this.updateById(article);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新文章失败");
        }
        
        deleteArticleTags(request.getId());
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveArticleTags(request.getId(), request.getTagIds());
        }
        
        return true;
    }

    @Override
    public Boolean deleteArticle(Long id, User loginUser) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID无效");
        }
        
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        
        if (!userService.isAdmin(loginUser) && !article.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除此文章");
        }
        
        return this.removeById(id);
    }

    @Override
    public Boolean incrementViewCount(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        Article article = this.getById(id);
        if (article == null) {
            return false;
        }
        
        article.setViewCount(article.getViewCount() + 1);
        return this.updateById(article);
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<Article> buildQueryWrapper(ArticlePageListRequest request) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        
        // 分类和状态筛选
        queryWrapper.lambda()
                .eq(ObjectUtil.isNotNull(request.getCategoryId()), Article::getCategoryId, request.getCategoryId())
                .eq(ObjectUtil.isNotNull(request.getStatus()), Article::getStatus, request.getStatus());
        
        // 标签筛选：通过关联表查询文章ID列表
        if (request.getTagId() != null) {
            List<ArticleTag> articleTags = articleTagService.list(
                    new LambdaQueryWrapper<ArticleTag>()
                            .eq(ArticleTag::getTagId, request.getTagId())
            );
            if (articleTags.isEmpty()) {
                // 无匹配标签时，设置不可能的条件，返回空结果
                queryWrapper.lambda().eq(Article::getId, -1);
            } else {
                // 提取文章ID列表，使用IN查询
                List<Long> articleIds = articleTags.stream()
                        .map(ArticleTag::getArticleId)
                        .collect(Collectors.toList());
                queryWrapper.lambda().in(Article::getId, articleIds);
            }
        }
        
        // 排序处理：支持按创建时间或阅读量排序
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        if (StrUtil.isNotBlank(sortField)) {
            boolean isDesc = "descend".equals(sortOrder) || "desc".equals(sortOrder);
            if ("create_time".equals(sortField) || "createTime".equals(sortField)) {
                queryWrapper.lambda().orderBy(true, isDesc, Article::getCreateTime);
            } else if ("view_count".equals(sortField) || "viewCount".equals(sortField)) {
                queryWrapper.lambda().orderBy(true, isDesc, Article::getViewCount);
            } else {
                // 未知排序字段，默认按创建时间降序
                queryWrapper.lambda().orderByDesc(Article::getCreateTime);
            }
        } else {
            // 未指定排序字段，默认按创建时间降序
            queryWrapper.lambda().orderByDesc(Article::getCreateTime);
        }
        
        return queryWrapper;
    }

    /**
     * 转换为ArticleVO
     */
    private ArticleVO convertToArticleVO(Article article) {
        if (article == null) {
            return null;
        }
        
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        
        if (article.getContent() != null) {
            articleVO.setWordCount(calculateWordCount(article.getContent()));
        }
        
        return articleVO;
    }

    /**
     * 填充关联数据（分类、作者、标签）
     * 采用批量查询策略，避免N+1查询问题
     */
    private void fillAssociatedData(List<ArticleVO> articleVOList) {
        if (articleVOList == null || articleVOList.isEmpty()) {
            return;
        }
        
        // 收集所有需要查询的ID（分类ID、作者ID、文章ID）
        Set<Long> categoryIds = articleVOList.stream()
                .map(ArticleVO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        Set<Long> authorIds = articleVOList.stream()
                .map(ArticleVO::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        List<Long> articleIds = articleVOList.stream()
                .map(ArticleVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // 批量查询分类信息，构建ID到VO的映射
        Map<Long, CategoryVO> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryService.listByIds(categoryIds);
            categoryMap = categories.stream()
                    .map(this::convertToCategoryVO)
                    .collect(Collectors.toMap(CategoryVO::getId, vo -> vo));
        }
        
        // 批量查询作者信息，构建ID到VO的映射
        Map<Long, ArticleVO.AuthorVO> authorMap = new HashMap<>();
        if (!authorIds.isEmpty()) {
            List<User> users = userService.listByIds(authorIds);
            authorMap = users.stream()
                    .map(this::convertToAuthorVO)
                    .collect(Collectors.toMap(ArticleVO.AuthorVO::getId, vo -> vo));
        }
        
        // 批量查询文章标签关联关系
        Map<Long, List<TagVO>> articleTagMap = new HashMap<>();
        if (!articleIds.isEmpty()) {
            // 查询文章-标签关联表
            List<ArticleTag> articleTags = articleTagService.list(
                    new LambdaQueryWrapper<ArticleTag>()
                            .in(ArticleTag::getArticleId, articleIds)
            );
            
            // 提取标签ID集合
            Set<Long> tagIds = articleTags.stream()
                    .map(ArticleTag::getTagId)
                    .collect(Collectors.toSet());
            
            // 批量查询标签信息
            if (!tagIds.isEmpty()) {
                List<Tag> tags = tagService.listByIds(tagIds);
                Map<Long, TagVO> tagMap = tags.stream()
                        .map(this::convertToTagVO)
                        .collect(Collectors.toMap(TagVO::getId, vo -> vo));
                
                // 构建文章ID到标签列表的映射
                for (ArticleTag articleTag : articleTags) {
                    TagVO tagVO = tagMap.get(articleTag.getTagId());
                    if (tagVO != null) {
                        articleTagMap.computeIfAbsent(articleTag.getArticleId(), k -> new ArrayList<>())
                                .add(tagVO);
                    }
                }
            }
        }
        
        // 将查询到的关联数据填充到ArticleVO中
        for (ArticleVO articleVO : articleVOList) {
            if (articleVO.getCategoryId() != null) {
                CategoryVO categoryVO = categoryMap.get(articleVO.getCategoryId());
                articleVO.setCategory(categoryVO);
            }
            
            if (articleVO.getAuthorId() != null) {
                ArticleVO.AuthorVO authorVO = authorMap.get(articleVO.getAuthorId());
                articleVO.setAuthor(authorVO);
            }
            
            // 设置标签列表，如果为空则设置为空列表
            List<TagVO> tags = articleTagMap.get(articleVO.getId());
            articleVO.setTags(tags != null ? tags : new ArrayList<>());
        }
    }

    /**
     * 保存文章标签关联
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        List<ArticleTag> articleTags = tagIds.stream()
                .map(tagId -> {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    articleTag.setCreateTime(new Date());
                    articleTag.setUpdateTime(new Date());
                    return articleTag;
                })
                .collect(Collectors.toList());
        
        articleTagService.saveBatch(articleTags);
    }

    /**
     * 删除文章标签关联
     */
    private void deleteArticleTags(Long articleId) {
        articleTagService.remove(
                new LambdaQueryWrapper<ArticleTag>()
                        .eq(ArticleTag::getArticleId, articleId)
        );
    }

    /**
     * 校验文章请求参数（创建）
     */
    private void validateArticleRequest(ArticleAddRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        validateArticleCommonFields(request.getTitle(), request.getContent());
    }

    /**
     * 校验文章请求参数（更新）
     */
    private void validateArticleRequest(ArticleUpdateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        validateArticleCommonFields(request.getTitle(), request.getContent());
    }

    /**
     * 校验文章公共字段（标题和内容）
     */
    private void validateArticleCommonFields(String title, String content) {
        if (StrUtil.isBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
        }
        if (StrUtil.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        }
    }

    /**
     * 计算字数
     */
    private Integer calculateWordCount(String content) {
        if (content == null) {
            return 0;
        }
        return content.length();
    }

    /**
     * 转换为CategoryVO
     */
    private CategoryVO convertToCategoryVO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 转换为TagVO
     */
    private TagVO convertToTagVO(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }

    /**
     * 转换为AuthorVO
     */
    private ArticleVO.AuthorVO convertToAuthorVO(User user) {
        if (user == null) {
            return null;
        }
        ArticleVO.AuthorVO authorVO = new ArticleVO.AuthorVO();
        authorVO.setId(user.getId());
        authorVO.setNickname(user.getNickname());
        authorVO.setAvatar(user.getAvatar());
        return authorVO;
    }
}