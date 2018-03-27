package com.ht.controller;

import com.ht.VO.ResultVO;
import com.ht.converter.OrderForm2OrderDTOConverter;
import com.ht.dto.OrderDTO;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import com.ht.form.OrderForm;
import com.ht.service.BuyerService;
import com.ht.service.OrderService;
import com.ht.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auth Qiu
 * @time 2018/3/12
 **/
@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    //创建订单
    @PostMapping("/create")
    public ResultVO<Map<String,String >> create(@Valid OrderForm orderForm , BindingResult bindingResult){
        //先判断一下表单校验有没有错误
        if(bindingResult.hasErrors()){
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        //如果验证参数通过 则可以将orderForm类型转换成orderDTO类型
        OrderDTO orderDTO= OrderForm2OrderDTOConverter.convert(orderForm);

        //转换后需要判断一下购物车是否还是空的
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        //创建订单
        OrderDTO result=orderService.create(orderDTO);

        //返回的参数
        Map<String,String> map=new HashMap<>();
        map.put("orderId",result.getOrderId());

        ResultVO<Map<String,String>> resultVO=ResultVOUtil.success(map);
        return resultVO;
    }


    //订单列表
    @GetMapping("/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid")String openid,
            @RequestParam(value = "page",defaultValue = "0")Integer page,
            @RequestParam(value = "size",defaultValue = "10")Integer size){

            //先要判断买家的微信id是否为空
            if(StringUtils.isEmpty(openid)){
                log.error("【查询订单列表】openid为空");
                throw new SellException(ResultEnum.PARAM_ERROR);
            }

            //新建一个page对象 存储分页信息
            PageRequest pageRequest=new PageRequest(page,size);

            //根据用户的微信id  查询用户的所有订单
            Page<OrderDTO> result=orderService.findList(openid,pageRequest);

            //返回的数据对象
            ResultVO<List<OrderDTO>> resultVO=ResultVOUtil.success(result.getContent());
            return resultVO;
    }


    //订单详情
    @GetMapping("/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openid")String openid,
                                     @RequestParam("orderid")String orderid) {

        //根据用户微信号openid 和用户订单号 orderid 查询用户的订单信息 findOrderOne()里面判断了是否是该用户和订单存不存在
        OrderDTO orderDTO=buyerService.findOrderOne(openid,orderid);

        ResultVO<OrderDTO> resultVO=ResultVOUtil.success(orderDTO);

        return resultVO;
    }

    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid")String openid,
                           @RequestParam("orderid")String orderid){

        //根据用户微信号openid 和用户订单号 orderid 取消用户的订单 cancelOrder()里面判断了是否是该用户和订单存不存在
        OrderDTO orderDTO=buyerService.cancelOrder(openid,orderid);

        //返回的数据
        ResultVO resultVO=ResultVOUtil.success();

        return resultVO;
    }


}
