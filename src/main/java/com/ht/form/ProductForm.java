package com.ht.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @auth Qiu
 * @time 2018/3/16
 **/
@Data
public class ProductForm {
    private String productId;

    /** 名字. */
    private String productName;

    /** 单价. */
    private BigDecimal productPrice;

    /** 库存. */
    private Integer productStock;

    /** 描述. */
    private String productDescription;

    /** 小图. */
    private String productIcon;

    /** 类目编号. */
    private Integer categoryType;
}
