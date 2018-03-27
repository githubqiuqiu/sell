package com.ht.constant;

/**
 * redis常量
 * @auth Qiu
 * @time 2018/3/16
 **/
public interface RedisConstant {
    String TOKEN_PREFIX = "token_%s";//前缀

    Integer EXPIRE = 7200; //2小时
}
