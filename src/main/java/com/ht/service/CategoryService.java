package com.ht.service;

import com.ht.dataobject.ProductCategory;

import java.util.List;

/**
 * 商品类目表 service
 * @auth Qiu
 * @time 2018/3/8
 **/
public interface CategoryService {
    ProductCategory findOne(Integer categoryId);

    List<ProductCategory> findAll();

    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);

    ProductCategory save(ProductCategory productCategory);
}
