package com.ht.repository;

import com.ht.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @auth Qiu
 * @time 2018/3/8
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryRepositoryTest {
    @Autowired
    private ProductCategoryRepository repository;

    @Test
    public void findOneTest(){
        ProductCategory productCategory=repository.findOne(1);
        System.out.println(productCategory.toString());

    }

    @Test
    //此注解测试通过后也不会把数据插入数据库
    @Transactional
    public void savaTest(){
      ProductCategory productCategory=new ProductCategory("女生最爱",3);

      //新增或修改商品类目
      ProductCategory result=repository.save(productCategory);

      //判断结果期望不为空
        Assert.assertNotNull(result);
    }


    @Test
    public void findByCategoryTypeIn(){
        List<Integer> integerList= Arrays.asList(2,3,4);

        List<ProductCategory> productCategoryList=repository.findByCategoryTypeIn(integerList);
        Assert.assertNotEquals(0,productCategoryList.size());

    }

}