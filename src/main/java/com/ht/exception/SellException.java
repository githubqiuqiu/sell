package com.ht.exception;

import com.ht.enums.ResultEnum;
import lombok.Getter;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
@Getter
public class SellException extends  RuntimeException{
    private Integer code;

    public SellException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public SellException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
