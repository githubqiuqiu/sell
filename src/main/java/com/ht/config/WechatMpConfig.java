package com.ht.config;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 微信公众号 配置文件
 * @auth Qiu
 * @time 2018/3/14
 **/
@Component
public class WechatMpConfig {

    /**
     * 注入WechatAccountConfig的属性
     */
    @Autowired
    private WechatAccountConfig accountConfig;

    /**
     * 微信API的Service  配置成bean
     * @return
     */
    @Bean
    public WxMpService wxMpService(){
        WxMpService wxMpService=new WxMpServiceImpl();
        //设置配置信息
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    /**
     * 微信客户端配置存储  配置成bean
     * @return
     */
    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        //基于内存的微信配置provider，在实际生产环境中应该将这些配置持久化
        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        //设置appid 和appsecret
        wxMpConfigStorage.setAppId(accountConfig.getMpAppId());
        wxMpConfigStorage.setSecret(accountConfig.getMpAppSecret());
        return wxMpConfigStorage;
    }

}
