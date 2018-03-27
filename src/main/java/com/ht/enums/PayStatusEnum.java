package com.ht.enums;

import lombok.Getter;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
@Getter
public enum  PayStatusEnum  implements  CodeEnum{
    WAIT(0,"等待支付"),
    SUCCESS(1,"支付成功")
    ;

    private Integer code;

    private String msg;

    PayStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
