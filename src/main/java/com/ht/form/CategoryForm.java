package com.ht.form;

import lombok.Data;

/**
 * @auth Qiu
 * @time 2018/3/16
 **/
@Data
public class CategoryForm {
    private Integer categoryId;

    /** 类目名字. */
    private String categoryName;

    /** 类目编号. */
    private Integer categoryType;
}
