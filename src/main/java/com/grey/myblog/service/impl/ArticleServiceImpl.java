package com.grey.myblog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.grey.myblog.dao.ArticleDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.exception.AssertUtil;
import com.grey.myblog.model.PageResult;
import com.grey.myblog.model.dataobject.ArticleDO;
import com.grey.myblog.model.dataobject.ArticleTagDO;
import com.grey.myblog.model.dataobject.CategoryDO;
import com.grey.myblog.model.dataobject.TagDO;
import com.grey.myblog.model.dataobject.UserDO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.ArticleAddRequest;
import com.grey.myblog.model.request.ArticlePageListRequest;
import com.grey.myblog.model.request.ArticleUpdateRequest;
import com.grey.myblog.model.dto.ArticleDTO;
import com.grey.myblog.model.dto.ArticleArchiveDTO;
import com.grey.myblog.model.dto.CategoryDTO;
import com.grey.myblog.model.dto.TagDTO;
import com.grey.myblog.model.dto.AuthorDTO;
import com.grey.myblog.service.ArticleService;
import com.grey.myblog.service.ArticleTagService;
import com.grey.myblog.service.CategoryService;
import com.grey.myblog.service.TagService;
import com.grey.myblog.service.UserService;
import com.grey.myblog.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleDAO articleDAO;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private ArticleTagService articleTagService;

    @Resource
    private UserService userService;

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Override
    public PageResult<ArticleDTO> listArticles(ArticlePageListRequest request) {
        // 参数非空校验
        AssertUtil.isFalse(request == null, ErrorCode.PARAMS_ERROR);

        // 参数校验：页码和每页数量不能小于1，设置默认值
        int pageNum = request.getPageNum() < 1 ? 1 : (int) request.getPageNum();
        int pageSize = request.getPageSize() < 1 ? 10 : (int) request.getPageSize();

        try {
            PageHelper.startPage(pageNum, pageSize);
            List<ArticleDO> articleList = articleDAO.selectArticlePage(request);
            PageInfo<ArticleDO> pageInfo = new PageInfo<>(articleList);

            // 转换为DTO对象
            List<ArticleDTO> articleDTOList = articleList.stream()
                    .map(this::convertToArticleDTO)
                    .collect(Collectors.toList());

            // 填充关联数据（分类、作者、标签）
            fillAssociatedData(articleDTOList);

            return new PageResult<>(pageNum, pageSize, pageInfo.getTotal(), articleDTOList);
        } catch (Exception e) {
            log.error("分页查询文章列表异常：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "分页查询失败");
        }
    }

    @Override
    public ArticleDTO getArticleById(Long id, Integer requireStatus) {
        AssertUtil.isFalse(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "文章ID无效");

        ArticleDO article = articleDAO.selectById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        // 状态可见性控制：requireStatus 非 null 时，状态不匹配视为不存在（不泄露草稿/私密的存在性）
        if (requireStatus != null && !requireStatus.equals(article.getStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        // 增加阅读量（排除登录用户，避免管理端预览污染统计）
        if (!userService.isLoggedIn(currentRequest())) {
            incrementViewCount(id);
        }

        // 转换为DTO并填充关联数据
        ArticleDTO articleDTO = convertToArticleDTO(article);
        fillAssociatedData(Collections.singletonList(articleDTO));
        return articleDTO;
    }

    @Override
    public Map<String, Map<String, List<ArticleArchiveDTO>>> getArticleArchive(Integer year, Integer month) {
        // 查询公开文章，只查询必要字段
        List<ArticleDO> articles = articleDAO.selectByStatus(1, year, month);

        // 转换为轻量级归档VO，并保存articleId到categoryId的映射
        Map<Long, Long> articleCategoryMap = new HashMap<>();
        List<ArticleArchiveDTO> archiveVOList = articles.stream()
                .map(article -> {
                    ArticleArchiveDTO archiveVO = convertToArticleArchiveDTO(article);
                    if (article.getCategoryId() != null) {
                        articleCategoryMap.put(archiveVO.getId(), article.getCategoryId());
                    }
                    return archiveVO;
                })
                .collect(Collectors.toList());

        // 批量填充分类和标签信息
        fillArchiveAssociatedData(archiveVOList, articleCategoryMap);

        // 按年月分组
        Map<String, Map<String, List<ArticleArchiveDTO>>> archiveMap = new LinkedHashMap<>();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

        for (ArticleArchiveDTO archiveVO : archiveVOList) {
            if (archiveVO.getCreateTime() == null) {
                continue;
            }

            String yearStr = yearFormat.format(archiveVO.getCreateTime());
            String monthStr = monthFormat.format(archiveVO.getCreateTime());

            archiveMap.computeIfAbsent(yearStr, k -> new LinkedHashMap<>())
                    .computeIfAbsent(monthStr, k -> new ArrayList<>())
                    .add(archiveVO);
        }

        return archiveMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addArticle(ArticleAddRequest request, UserDO loginUser) {
        validateArticleRequest(request);

        ArticleDO article = ArticleDO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .wordCount(calculateWordCount(request.getContent()))
                .excerpt(request.getExcerpt())
                .coverImage(resolveCoverImage(request.getCoverImage(), null))
                .categoryId(request.getCategoryId())
                .status(request.getStatus())
                .sortWeight(request.getSortWeight())
                .authorId(loginUser.getId())
                .viewCount(0)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        int result = articleDAO.insert(article);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建文章失败");
        }

        // 如果指定了标签，批量保存文章-标签关联关系
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), request.getTagIds());
        }

        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateArticle(ArticleUpdateRequest request, UserDO loginUser) {
        if (request == null || request.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        validateArticleRequest(request);

        ArticleDO existingArticle = articleDAO.selectById(request.getId());
        if (existingArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        // 权限校验
        if (!userService.isAdmin(loginUser) && !existingArticle.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改此文章");
        }

        Integer wordCount = request.getContent() != null ? calculateWordCount(request.getContent()) : null;

        // 封面：请求传了用请求的；没传则沿用已有封面；已有也为空时从网站配置封面池随机兜底
        String coverImage = resolveCoverImage(request.getCoverImage(), existingArticle.getCoverImage());

        ArticleDO article = ArticleDO.builder()
                .id(request.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .wordCount(wordCount)
                .excerpt(request.getExcerpt())
                .coverImage(coverImage)
                .categoryId(request.getCategoryId())
                .status(request.getStatus())
                .sortWeight(request.getSortWeight())
                .updateTime(new Date())
                .build();

        int result = articleDAO.updateById(article);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新文章失败");
        }

        // 删除旧标签关联，重新保存新标签关联关系
        articleTagService.removeByArticleId(request.getId());
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveArticleTags(request.getId(), request.getTagIds());
        }

        return true;
    }

    @Override
    public Boolean deleteArticle(Long id, UserDO loginUser) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID无效");
        }

        ArticleDO article = articleDAO.selectById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        // 权限校验
        if (!userService.isAdmin(loginUser) && !article.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除此文章");
        }

        int result = articleDAO.deleteById(id);
        return result > 0;
    }

    @Override
    public Boolean incrementViewCount(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return articleDAO.incrementViewCount(id) > 0;
    }

    /**
     * 获取当前 HTTP 请求（从请求上下文）
     * 参考 AuthInterceptor 的取法；非 HTTP 上下文时返回 null
     */
    private HttpServletRequest currentRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将文章实体转换为VO对象
     */
    private ArticleDTO convertToArticleDTO(ArticleDO article) {
        if (article == null) {
            return null;
        }

        ArticleDTO articleVO = new ArticleDTO();
        BeanUtils.copyProperties(article, articleVO);

        return articleVO;
    }

    /**
     * 批量填充文章关联数据
     */
    private void fillAssociatedData(List<ArticleDTO> articleVOList) {
        if (articleVOList == null || articleVOList.isEmpty()) {
            return;
        }

        Set<Long> categoryIds = articleVOList.stream()
                .map(ArticleDTO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> authorIds = articleVOList.stream()
                .map(ArticleDTO::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> articleIds = articleVOList.stream()
                .map(ArticleDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 批量查询分类信息
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<CategoryDO> categories = categoryService.listByIds(categoryIds);
            categoryMap = categories.stream()
                    .map(this::convertToCategoryDTO)
                    .collect(Collectors.toMap(CategoryDTO::getId, vo -> vo));
        }

        // 批量查询作者信息
        Map<Long, AuthorDTO> authorMap = new HashMap<>();
        if (!authorIds.isEmpty()) {
            List<UserDO> users = userService.listByIds(authorIds);
            authorMap = users.stream()
                    .map(this::convertToAuthorDTO)
                    .collect(Collectors.toMap(AuthorDTO::getId, vo -> vo));
        }

        // 批量查询文章标签关联关系
        Map<Long, List<TagDTO>> articleTagMap = new HashMap<>();
        if (!articleIds.isEmpty()) {
            List<ArticleTagDO> articleTags = articleTagService.listByArticleIds(articleIds);

            Set<Long> tagIds = articleTags.stream()
                    .map(ArticleTagDO::getTagId)
                    .collect(Collectors.toSet());

            if (!tagIds.isEmpty()) {
                List<TagDO> tags = tagService.listByIds(tagIds);
                Map<Long, TagDTO> tagMap = tags.stream()
                        .map(this::convertToTagDTO)
                        .collect(Collectors.toMap(TagDTO::getId, vo -> vo));

                for (ArticleTagDO articleTag : articleTags) {
                    TagDTO tagVO = tagMap.get(articleTag.getTagId());
                    if (tagVO != null) {
                        articleTagMap.computeIfAbsent(articleTag.getArticleId(), k -> new ArrayList<>())
                                .add(tagVO);
                    }
                }
            }
        }

        // 填充关联数据
        for (ArticleDTO articleVO : articleVOList) {
            if (articleVO.getCategoryId() != null) {
                articleVO.setCategory(categoryMap.get(articleVO.getCategoryId()));
            }

            if (articleVO.getAuthorId() != null) {
                articleVO.setAuthor(authorMap.get(articleVO.getAuthorId()));
            }

            List<TagDTO> tags = articleTagMap.get(articleVO.getId());
            articleVO.setTags(tags != null ? tags : new ArrayList<>());
        }
    }

    /**
     * 批量保存文章标签关联关系
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        Date now = new Date();
        List<ArticleTagDO> articleTags = tagIds.stream()
                .map(tagId -> ArticleTagDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .createTime(now)
                        .updateTime(now)
                        .build())
                .collect(Collectors.toList());

        articleTagService.saveBatch(articleTags);
    }

    /**
     * 校验文章创建请求参数
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
     * 校验文章公共字段
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
     * 解析文章封面
     * - 请求传了封面：直接用
     * - 请求没传：沿用已有封面（新增文章 existingCover 传 null）；已有也为空时从网站配置封面池随机兜底
     */
    private String resolveCoverImage(String requestCover, String existingCover) {
        if (StrUtil.isNotBlank(requestCover)) {
            return requestCover;
        }
        if (StrUtil.isNotBlank(existingCover)) {
            return existingCover;
        }
        return pickRandomCover();
    }

    /**
     * 从网站配置封面池随机取一张兜底封面（按池大小取随机下标）
     * 池为空或网站配置未初始化时返回 null，不影响文章写入
     */
    private String pickRandomCover() {
        try {
            List<String> covers = websiteConfigService.getWebsiteConfig().getArticleCoverImages();
            if (covers == null || covers.isEmpty()) {
                return null;
            }
            return covers.get(ThreadLocalRandom.current().nextInt(covers.size()));
        } catch (Exception e) {
            log.warn("文章封面随机兜底失败（网站配置可能未初始化），文章将不带封面：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 转换为CategoryDTO
     */
    private CategoryDTO convertToCategoryDTO(CategoryDO category) {
        if (category == null) {
            return null;
        }
        CategoryDTO categoryVO = new CategoryDTO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 转换为TagDTO
     */
    private TagDTO convertToTagDTO(TagDO tag) {
        if (tag == null) {
            return null;
        }
        TagDTO tagVO = new TagDTO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }

    /**
     * 转换为AuthorDTO
     */
    private AuthorDTO convertToAuthorDTO(UserDO user) {
        if (user == null) {
            return null;
        }
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(user.getId());
        authorDTO.setNickname(user.getNickname());
        authorDTO.setAvatar(user.getAvatar());
        return authorDTO;
    }

    /**
     * 将文章实体转换为归档VO对象
     */
    private ArticleArchiveDTO convertToArticleArchiveDTO(ArticleDO article) {
        if (article == null) {
            return null;
        }
        ArticleArchiveDTO archiveVO = new ArticleArchiveDTO();
        archiveVO.setId(article.getId());
        archiveVO.setTitle(article.getTitle());
        archiveVO.setCreateTime(article.getCreateTime());
        return archiveVO;
    }

    /**
     * 批量填充归档文章的关联数据
     */
    private void fillArchiveAssociatedData(List<ArticleArchiveDTO> archiveVOList, Map<Long, Long> articleCategoryMap) {
        if (archiveVOList == null || archiveVOList.isEmpty()) {
            return;
        }

        Set<Long> categoryIds = new HashSet<>(articleCategoryMap.values());

        Map<Long, CategoryDTO> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<CategoryDO> categories = categoryService.listByIds(categoryIds);
            categoryMap = categories.stream()
                    .map(this::convertToCategoryDTO)
                    .collect(Collectors.toMap(CategoryDTO::getId, vo -> vo));
        }

        List<Long> articleIds = archiveVOList.stream()
                .map(ArticleArchiveDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, List<TagDTO>> articleTagMap = new HashMap<>();
        if (!articleIds.isEmpty()) {
            List<ArticleTagDO> articleTags = articleTagService.listByArticleIds(articleIds);

            Set<Long> tagIds = articleTags.stream()
                    .map(ArticleTagDO::getTagId)
                    .collect(Collectors.toSet());

            if (!tagIds.isEmpty()) {
                List<TagDO> tags = tagService.listByIds(tagIds);
                Map<Long, TagDTO> tagMap = tags.stream()
                        .map(this::convertToTagDTO)
                        .collect(Collectors.toMap(TagDTO::getId, vo -> vo));

                for (ArticleTagDO articleTag : articleTags) {
                    TagDTO tagVO = tagMap.get(articleTag.getTagId());
                    if (tagVO != null) {
                        articleTagMap.computeIfAbsent(articleTag.getArticleId(), k -> new ArrayList<>())
                                .add(tagVO);
                    }
                }
            }
        }

        for (ArticleArchiveDTO archiveVO : archiveVOList) {
            Long categoryId = articleCategoryMap.get(archiveVO.getId());
            if (categoryId != null) {
                archiveVO.setCategory(categoryMap.get(categoryId));
            }

            List<TagDTO> tags = articleTagMap.get(archiveVO.getId());
            archiveVO.setTags(tags != null ? tags : new ArrayList<>());
        }
    }
}