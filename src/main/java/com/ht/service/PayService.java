package com.ht.service;

import com.ht.dto.OrderDTO;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;

/**
 * 支付接口
 * @auth Qiu
 * @time 2018/3/15
 **/
public interface PayService {
    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    PayResponse create(OrderDTO orderDTO);

    /**
     * 微信异步通知程序支付结果
     * @param notifyData
     * @return
     */
    PayResponse notify(String notifyData);


    /**
     * 退款
     * @param orderDTO
     * @return
     */
    RefundResponse refund(OrderDTO orderDTO);
}
