package com.ht.enums;

import lombok.Getter;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
@Getter
public enum OrderStatusEnum implements  CodeEnum{
        NEW(0,"新订单"),
        FINISHED(1,"完结"),
        CANCEL(2,"取消");

        private Integer code;

        private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
