package com.ht.service.impl;

import com.ht.dataobject.SellerInfo;
import com.ht.repository.SellerInfoRepository;
import com.ht.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @auth Qiu
 * @time 2018/3/16
 **/
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerInfoRepository repository;

    @Override
    public SellerInfo findSellerInfoByOpenid(String openid) {
        return repository.findByOpenid(openid);
    }
}
