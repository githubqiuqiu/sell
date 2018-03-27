package com.ht.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ht.dataobject.OrderDetail;
import com.ht.enums.OrderStatusEnum;
import com.ht.enums.PayStatusEnum;
import com.ht.utils.EnumUtil;
import com.ht.utils.serializer.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO  数据传输对象 data transfer object
 * @auth Qiu
 * @time 2018/3/9
 **/
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL) 这个注解可以设置当orderDTO里面的字段为空时不返回
//可在yml文件里配置成全局的
//jackson:
//     default-property-inclusion: non_null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    /** 订单id. */
    private String orderId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid. */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;

    /** 订单状态, 默认为0新下单. */
    private Integer orderStatus;

    /** 支付状态, 默认为0未支付. */
    private Integer payStatus;

    /** 创建时间.
     * @JsonSerialize(using = Date2LongSerializer.class)
     *  把Date类型的时间 转换成Long类型的时间
     *  毫秒转换成秒
     * */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    /** 更新时间.
     * @JsonSerialize(using = Date2LongSerializer.class)
     *  把Date类型的时间 转换成Long类型的时间
     *  毫秒转换成秒
     * */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

    //DTO 的作用  就是定义数据库没有的一些字段以便使用  这样就不需要在bean 字段修改
    /**每个订单对应多个订单详情.   */
    List<OrderDetail> orderDetailList=new ArrayList<>();

    //转json的时候忽略这个属性
    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(orderStatus, OrderStatusEnum.class);
    }
    //转json的时候忽略这个属性
    @JsonIgnore
    public PayStatusEnum getPayStatusEnum() {
        return EnumUtil.getByCode(payStatus, PayStatusEnum.class);
    }
}
