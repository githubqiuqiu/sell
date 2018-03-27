package com.ht.utils;

import java.util.Random;

/**
 * @auth Qiu
 * @time 2018/3/10
 **/
public class KeyUtil {

    /**
     * 获得唯一的编号/主键  格式  当前毫秒数+随机数
     * @return
     */
    public static synchronized String getUniqueKey(){
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;

        return System.currentTimeMillis()+String.valueOf(number);
    }

}
