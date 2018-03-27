package com.ht.controller;

import com.ht.dataobject.ProductCategory;
import com.ht.dataobject.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试mybatis 的使用的 controller
 * @auth Qiu
 * @time 2018/3/18
 **/
@RestController
@RequestMapping("/testMybatis")
public class TestMybatisController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 测试 用注解 开发mybatis的方式
     * @return
     */
    @RequestMapping("/insertByMap")
    public Integer insertByMap(){

        Map<String, Object> map = new HashMap<>();
        map.put("category_name", "师兄最不爱");
        map.put("category_type", 101);
        int result = productCategoryService.insertByMap(map);

        return  result;
    }

    /**
     * 测试 用xml 开发mybatis的方式
     * @return
     */
    @RequestMapping("/selectByCategoryType")
    public ProductCategory selectByCategoryType(){
        return productCategoryService.selectByCategoryType(101);
    }


}
