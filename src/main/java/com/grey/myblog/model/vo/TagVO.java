package com.grey.myblog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签视图对象
 *
 * @author grey
 */
@Data
public class TagVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;
}
