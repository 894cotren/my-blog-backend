package com.grey.myblog.controller.admin;

import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.enums.UploadEnum;
import com.grey.myblog.utils.MinioUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 *
 * @author grey
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private MinioUtils minioUtils;

    /**
     * 上传文章封面图片
     *
     * @param file 封面图片文件
     * @return 图片访问 URL
     */
    @PostMapping("/article/cover")
    @AuthCheck
    public Result<String> uploadArticleCover(@RequestParam("file") MultipartFile file) {
        String url = minioUtils.upload(UploadEnum.ARTICLE_COVER, file);
        return Result.success(url);
    }

    /**
     * 上传文章内容图片（Markdown 编辑器中插入）
     *
     * @param file 图片文件
     * @return 图片访问 URL
     */
    @PostMapping("/article/image")
    @AuthCheck
    public Result<String> uploadArticleImage(@RequestParam("file") MultipartFile file) {
        String url = minioUtils.upload(UploadEnum.ARTICLE_IMAGE, file);
        return Result.success(url);
    }

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 图片访问 URL
     */
    @PostMapping("/user/avatar")
    @AuthCheck
    public Result<String> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        String url = minioUtils.upload(UploadEnum.USER_AVATAR, file);
        return Result.success(url);
    }

    /**
     * 上传博主头像
     *
     * @param file 头像文件
     * @return 图片访问 URL
     */
    @PostMapping("/blogger/avatar")
    @AuthCheck
    public Result<String> uploadBloggerAvatar(@RequestParam("file") MultipartFile file) {
        String url = minioUtils.upload(UploadEnum.BLOGGER_AVATAR, file);
        return Result.success(url);
    }

    /**
     * 上传网站配置图片（logo/favicon/banner/文章默认封面图等）
     *
     * @param file 图片文件
     * @return 图片访问 URL
     */
    @PostMapping("/website-config/image")
    @AuthCheck
    public Result<String> uploadWebsiteConfigImage(@RequestParam("file") MultipartFile file) {
        String url = minioUtils.upload(UploadEnum.WEBSITE_CONFIG, file);
        return Result.success(url);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件 URL
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @AuthCheck
    public Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        boolean success = minioUtils.deleteFile(fileUrl);
        return Result.success(success);
    }
}
