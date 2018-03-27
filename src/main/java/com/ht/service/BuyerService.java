package com.ht.service;

import com.ht.dto.OrderDTO;

/**
 * 买家service
 * @auth Qiu
 * @time 2018/3/13
 **/
public interface BuyerService {

    //查询一个订单
    OrderDTO findOrderOne(String openid, String orderId);

    //取消订单
    OrderDTO cancelOrder(String openid, String orderId);

}
