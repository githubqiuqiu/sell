package com.ht.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ht.dataobject.OrderDetail;
import com.ht.dataobject.OrderMaster;
import com.ht.dto.OrderDTO;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import com.ht.form.OrderForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将orderForm类型 转换成orderDTO类型
 * @auth Qiu
 * @time 2018/3/12
 **/

@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        OrderDTO orderDTO=new OrderDTO();
        Gson gson=new Gson();

        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());

        //orderDTO里面的 orderDetailList 字段 存储的是订单里面的所有商品信息
        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            //把orderFrom里面的json格式的字符items
            //转换成orderDTO里面的orderDetailList字段 是一个list
            orderDetailList=gson.fromJson(orderForm.getItems(),new TypeToken<List<OrderDetail>>(){
            }.getType());
        }catch (Exception e){
            log.error("【对象转换】错误, string={}", orderForm.getItems());
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        //设置orderDTO里面的 orderDetailList字段
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }




}
