package com.ht.repository;

import com.ht.dataobject.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
public interface ProductInfoRepository extends JpaRepository<ProductInfo, String> {

    //根据商品状态 查询商品的信息
    List<ProductInfo> findByProductStatus(Integer productStatus);

}
