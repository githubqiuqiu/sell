package com.ht.service.impl;

import com.ht.dto.OrderDTO;
import com.ht.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @auth Qiu
 * @time 2018/3/17
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PushMessageServiceImplTest {

    @Autowired
    private PushMessageServiceImpl pushMessageService;

    @Autowired
    private OrderService orderService;

    /**
     * 测试号可以测试成功
     * @throws Exception
     */
    @Test
    public void orderStatus() throws Exception {

        OrderDTO orderDTO = orderService.findOne("1520839442653992763");
        pushMessageService.orderStatus(orderDTO);
    }
}