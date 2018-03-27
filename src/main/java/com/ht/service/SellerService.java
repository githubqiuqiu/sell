package com.ht.service;

import com.ht.dataobject.SellerInfo;

/**
 * 卖家端 登录service
 * @auth Qiu
 * @time 2018/3/16
 **/
public interface SellerService {
    /**
     * 通过openid查询卖家端信息
     * @param openid
     * @return
     */
    SellerInfo findSellerInfoByOpenid(String openid);
}
