package com.ht.repository;

import com.ht.dataobject.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auth Qiu
 * @time 2018/3/16
 **/
public interface SellerInfoRepository  extends JpaRepository<SellerInfo, String> {
    SellerInfo findByOpenid(String openid);
}
