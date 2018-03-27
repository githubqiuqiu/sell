package com.ht.dto;

import lombok.Data;

/**
 * 购物车  加减库存时需要使用
 * @auth Qiu
 * @time 2018/3/11
 **/

@Data
public class CartDTO {
    /** 商品Id. */
    private String productId;

    /** 数量. */
    private Integer productQuantity;

    public CartDTO(String productId, Integer productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public CartDTO() {
    }
}
