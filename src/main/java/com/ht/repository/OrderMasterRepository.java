package com.ht.repository;

import com.ht.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String>{

    //根据用户的微信Openid 查询用户的订单信息  带分页查询
   Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);
}
