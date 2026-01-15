package com.grey.myblog.model.request;

import com.grey.myblog.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文章分页查询请求
 *
 * @author grey
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticlePageListRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（可选）
     */
    private Long categoryId;

    /**
     * 标签ID（可选）
     */
    private Long tagId;

    /**
     * 文章状态（0-草稿，1-公开，2-私密，默认1）
     */
    private Integer status = 1;
}
