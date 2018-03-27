package com.ht.service.impl;

import com.ht.dto.OrderDTO;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import com.ht.service.BuyerService;
import com.ht.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @auth Qiu
 * @time 2018/3/13
 **/
@Service
@Transactional
@Slf4j
public class BuyerServiceImpl implements BuyerService{

    @Autowired
    private OrderService orderService;

    /**
     * 查询一个订单的信息
     * @param openid 用户的微信号
     * @param orderId 用户的订单号
     * @return
     */
    @Override
    public OrderDTO findOrderOne(String openid, String orderId) {
        //需要判断改订单是否属于当前用户
        return checkOrderOwner(openid, orderId);
    }

    /**
     * 取消订单
     * @param openid  用户的微信号
     * @param orderId 用户的订单号
     * @return
     */
    @Override
    public OrderDTO cancelOrder(String openid, String orderId) {
        OrderDTO orderDTO = checkOrderOwner(openid, orderId);

        //需要判断改订单是否为空  不为空就是自己的订单  为空就没有该订单信息
        if (orderDTO == null) {
            log.error("【取消订单】查不到改订单, orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        return null;
    }


    /**
     * 判断订单是否属于当前用户
     * @param openid  用户的微信号
     * @param orderId 用户的订单号
     * @return
     */
    private OrderDTO checkOrderOwner(String openid, String orderId) {
        OrderDTO orderDTO = orderService.findOne(orderId);
        if (orderDTO == null) {
            return null;
        }
        //判断是否是自己的订单
        if (!orderDTO.getBuyerOpenid().equalsIgnoreCase(openid)) {
            log.error("【查询订单】订单的openid不一致. openid={}, orderDTO={}", openid, orderDTO);
            throw new SellException(ResultEnum.ORDER_OWNER_ERROR);
        }

        //存在就取消该订单
        OrderDTO result=orderService.cancel(orderDTO);

        return result;
    }

}
