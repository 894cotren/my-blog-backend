-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`            bigint          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`       varchar(256)    NOT NULL                COMMENT '用户账号',
    `password`      varchar(512)    NOT NULL                COMMENT '用户密码',
    `nickname`      varchar(256)    DEFAULT NULL            COMMENT '用户昵称',
    `email`         varchar(256)    DEFAULT NULL            COMMENT '用户邮箱',
    `mobile`        varchar(20)     DEFAULT NULL            COMMENT '用户手机号码',
    `gender`        tinyint(1)      DEFAULT NULL            COMMENT '用户性别(0女1男)',
    `avatar`        varchar(1024)   DEFAULT NULL            COMMENT '用户头像',
    `profile`       varchar(512)    DEFAULT NULL            COMMENT '用户简介',
    `role`          varchar(256)    NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint         NOT NULL DEFAULT 0      COMMENT '是否删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    KEY `idx_user_name` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
    `id`            bigint          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`         varchar(255)    NOT NULL                COMMENT '文章标题',
    `content`       longtext                                COMMENT '文章内容',
    `excerpt`       varchar(500)    DEFAULT NULL            COMMENT '文章摘要',
    `cover_image`   varchar(1024)   DEFAULT NULL            COMMENT '封面图片URL',
    `sort_weight`   int             NOT NULL DEFAULT 0      COMMENT '排序权重（0为默认，1000为置顶）',
    `word_count`    int             NOT NULL DEFAULT 0      COMMENT '字数统计',
    `view_count`    int             NOT NULL DEFAULT 0      COMMENT '阅读量',
    `category_id`   bigint          DEFAULT NULL            COMMENT '所属分类ID',
    `author_id`     bigint          NOT NULL                COMMENT '作者ID（关联用户表）',
    `status`        tinyint         NOT NULL DEFAULT 0      COMMENT '文章状态（0-草稿，1-公开，2-私密）',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记（0-正常，1-删除）',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id`            bigint          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          varchar(50)     NOT NULL                COMMENT '分类名称',
    `sort_weight`   int             NOT NULL DEFAULT 0      COMMENT '排序权重（0为默认，1000为置顶）',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记（0-正常，1-删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id`            bigint          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          varchar(50)     NOT NULL                COMMENT '标签名称',
    `color`         varchar(50)     DEFAULT NULL            COMMENT '标签颜色',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记（0-正常，1-删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 文章-标签关联表
CREATE TABLE IF NOT EXISTS `article_tag` (
    `id`            bigint          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`    bigint          NOT NULL                COMMENT '文章ID',
    `tag_id`        bigint          NOT NULL                COMMENT '标签ID',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- 博主信息表
CREATE TABLE IF NOT EXISTS `blogger_info` (
    `id`             bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `blogger_name`   varchar(100)   NOT NULL                COMMENT '博主名称',
    `avatar`         varchar(1024)  DEFAULT NULL            COMMENT '博主头像',
    `intro`          varchar(500)   DEFAULT NULL            COMMENT '博主简介',
    `github_url`     varchar(255)   DEFAULT NULL            COMMENT 'GitHub链接',
    `email`          varchar(255)   DEFAULT NULL            COMMENT '联系邮箱',
    `about_content`  text           DEFAULT NULL            COMMENT '关于我内容',
    `update_time`    datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     tinyint        NOT NULL DEFAULT 0      COMMENT '逻辑删除标记（0-未删除，1-已删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博主信息表';



-- 博客评论表
CREATE TABLE IF NOT EXISTS `blog_comment` (
                                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
                                              `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（可选）',
    `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示一级评论',
    `reply_nickname` VARCHAR(50) DEFAULT NULL COMMENT '被回复者昵称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_parent_id` (`parent_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客评论表';

-- 留言表
CREATE TABLE IF NOT EXISTS `leave_word` (
                                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言ID',
                                            `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（可选）',
    `content` VARCHAR(1000) NOT NULL COMMENT '留言内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 网站资讯统计表（单行表，约定只存一条记录）
CREATE TABLE IF NOT EXISTS `site_stats` (
    `id`                        bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_count`            int      NOT NULL DEFAULT 0       COMMENT '文章数目（定时任务刷新）',
    `total_words`              bigint   NOT NULL DEFAULT 0       COMMENT '全站字数（定时任务刷新）',
    `visit_count`              bigint   NOT NULL DEFAULT 0       COMMENT '全站访问次数PV（拦截器实时+1）',
    `site_create_date`         date     DEFAULT NULL             COMMENT '建站日期（用于计算运行时长）',
    `last_article_update_time` datetime DEFAULT NULL             COMMENT '最近文章更新时间（定时任务刷新）',
    `stats_refresh_time`       datetime DEFAULT NULL             COMMENT '统计最近刷新时间（定时任务写入）',
    `create_time`              datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`              datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站资讯统计表';

-- 网站配置表（单行表，约定只存一条记录）
CREATE TABLE IF NOT EXISTS `website_config` (
    `id`                      bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `website_name`            varchar(100)   NOT NULL                COMMENT '网站标题',
    `logo`                    varchar(1024)  DEFAULT NULL            COMMENT '网站 Logo 图片URL',
    `favicon`                 varchar(1024)  DEFAULT NULL            COMMENT '站点图标（浏览器页签）URL',
    `blogger_card_background` varchar(1024)  DEFAULT NULL            COMMENT '博主卡片背景图URL',
    `banner_images`           varchar(2000)  DEFAULT NULL            COMMENT '首页 banner 轮播图列表（URL 以换行分隔）',
    `article_cover_images`    varchar(2000)  DEFAULT NULL            COMMENT '文章默认封面图列表（文章未传封面时随机取一张兜底，URL 以换行分隔）',
    `typing_texts`            varchar(1000)  DEFAULT NULL            COMMENT '首页打字机滚动文案列表（多条以换行分隔，预留）',
    `header_notification`     varchar(500)   DEFAULT NULL            COMMENT '首页公告条（文章列表上方）',
    `sidebar_announcement`    varchar(500)   DEFAULT NULL            COMMENT '侧边栏公告',
    `daily_soup`              varchar(1000)  DEFAULT NULL            COMMENT '侧边栏每日鸡汤',
    `record_info`             varchar(100)   DEFAULT NULL            COMMENT 'ICP备案号',
    `extend_info`             varchar(2000)  DEFAULT NULL            COMMENT '扩展信息（预留，按需使用）',
    `create_time`             datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站配置表';
