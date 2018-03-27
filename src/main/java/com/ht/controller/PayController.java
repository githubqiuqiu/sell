package com.ht.controller;

import com.ht.dto.OrderDTO;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import com.ht.service.OrderService;
import com.ht.service.PayService;
import com.lly835.bestpay.model.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 微信支付controller
 * @auth Qiu
 * @time 2018/3/15
 **/
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;

    /**
     * 创建订单的方法  对应微信支付的业务流程的第五步
     * @param orderId 主订单id
     * @param returnUrl  回调的url
     * @param map 要注入freemarker 模板的参数
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("returnUrl") String returnUrl,
                               Map<String, Object> map) {
        //1. 查询订单
        OrderDTO orderDTO = orderService.findOne(orderId);
        if (orderDTO == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        //2. 发起支付  返回预付单的信息 包括prepay_id的信息
        PayResponse payResponse = payService.create(orderDTO);

        //动态把信息加载到 freemarker模板中
        map.put("payResponse", payResponse);
        map.put("returnUrl", returnUrl);

        //注意这个路径  是在 resources 目录下的templates里面的pay 下面的create.ftl文件 .ftl是freemarker 默认的文件名
        return new ModelAndView("pay/create", map);
    }


    /**
     * 微信异步通知 获取用户支付是否成功  对应微信支付的第十步
     * @RequestBody 注解则是将 HTTP 请求正文插入方法中
     * @param notifyData 微信端会传来一串xml格式的字符串
     */
    @PostMapping("/notify")
    public ModelAndView notify(@RequestBody String notifyData) {

        //
        payService.notify(notifyData);

        //返回给微信处理结果  对应微信支付的第十一步 告诉微信程序处理的结果 同步返回
        //信息保存在freemarker模板中 目的是告诉微信订单已经成功了  不让微信端总发异步消息
        return new ModelAndView("pay/success");
    }

}
