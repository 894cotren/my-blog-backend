package com.grey.myblog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类视图对象
 *
 * @author grey
 */
@Data
public class CategoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
