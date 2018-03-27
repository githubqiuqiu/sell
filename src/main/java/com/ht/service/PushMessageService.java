package com.ht.service;

import com.ht.dto.OrderDTO;

/**
 * 微信模板推送消息 接口
 * @auth Qiu
 * @time 2018/3/17
 **/
public interface PushMessageService {

    /**
     * 订单状态变更消息
     * @param orderDTO
     */
    void orderStatus(OrderDTO orderDTO);
}
