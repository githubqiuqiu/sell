package com.ht.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品(包含类目)
 * @auth Qiu
 * @time 2018/3/9
 **/
@Data
public class ProductVO implements Serializable{

    //虽然这个商品的名字是 categoryName  但是加上 @JsonProperty("name") 这个注解  返回前端时的名字就为name了
    @JsonProperty("name")
    private String categoryName;

    @JsonProperty("type")
    private Integer categoryType;

    @JsonProperty("foods")
    private List<ProductInfoVO> productInfoVOList;
}
