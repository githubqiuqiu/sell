package com.ht.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 转json 格式工具类
 * @auth Qiu
 * @time 2018/3/15
 **/
public class JsonUtil {
    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
}
