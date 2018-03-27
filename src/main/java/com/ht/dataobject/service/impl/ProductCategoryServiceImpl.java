package com.ht.dataobject.service.impl;

import com.ht.dataobject.ProductCategory;
import com.ht.dataobject.mapper.ProductCategoryMapper;
import com.ht.dataobject.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @auth Qiu
 * @time 2018/3/18
 **/
@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService{

    @Autowired
    private ProductCategoryMapper mapper;

    @Override
    public int insertByMap(Map<String, Object> map) {
        return mapper.insertByMap(map);
    }

    @Override
    public ProductCategory selectByCategoryType(Integer categoryType) {
        return mapper.selectByCategoryType(categoryType);
    }
}
