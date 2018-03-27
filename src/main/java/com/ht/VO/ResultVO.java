package com.ht.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回视图层的对象信息  view object
 * http 请求 返回的最外层对象
 * @auth Qiu
 * @time 2018/3/9
 **/
@Data
public class ResultVO<T> implements Serializable{

    /** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体内容. */
    private T data;

}
