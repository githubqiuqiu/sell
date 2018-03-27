package com.ht.utils;

import com.ht.enums.CodeEnum;

/**
 * @auth Qiu
 * @time 2018/3/16
 **/
public class EnumUtil {
    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
