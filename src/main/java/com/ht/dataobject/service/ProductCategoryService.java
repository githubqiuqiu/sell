package com.ht.dataobject.service;

import com.ht.dataobject.ProductCategory;
import com.ht.dataobject.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * mybatis 对商品类目的增删改查的dao
 * @auth Qiu
 * @time 2018/3/18
 **/
public interface ProductCategoryService {

    int insertByMap(Map<String, Object> map);

    ProductCategory selectByCategoryType(Integer categoryType);
}
