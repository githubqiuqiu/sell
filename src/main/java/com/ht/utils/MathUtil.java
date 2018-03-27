package com.ht.utils;

/**
 * 比较数值大小
 * @auth Qiu
 * @time 2018/3/15
 **/
public class MathUtil {

    private static final Double MONEY_RANGE = 0.01;

    /**
     * 比较2个金额是否相等
     * @param d1
     * @param d2
     * @return
     */
    public static Boolean equals(Double d1, Double d2) {
        //两个数相减
        Double result = Math.abs(d1 - d2);
        //如果结果小于0.01 就判断两个数相等
        if (result < MONEY_RANGE) {
            return true;
        }else {
            return false;
        }
    }
}
