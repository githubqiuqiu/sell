package com.ht.controller;


import com.ht.config.ProjectUrlConfig;
import com.ht.constant.CookieConstant;
import com.ht.constant.RedisConstant;
import com.ht.dataobject.SellerInfo;
import com.ht.enums.ResultEnum;
import com.ht.service.SellerService;
import com.ht.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 卖家用户controller
 * @auth Qiu
 * @time 2018/3/16
 **/
@Controller
@RequestMapping("/seller")
public class SellerUserController {
    @Autowired
    private SellerService sellerService;

    /**
     * spring data redis 包下的连接redis的模板类
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    /**
     * 商家登录
     * @param openid  数据库里存储的商家的openid  不一定是真实的openid 和数据库保持一致就可以
     * @param response
     * @param map
     * @return
     */
    @GetMapping("/login")
    public ModelAndView login(@RequestParam("openid") String openid,
                              HttpServletResponse response,
                              Map<String, Object> map) {

        //1. openid去和数据库里的数据匹配
        SellerInfo sellerInfo = sellerService.findSellerInfoByOpenid(openid);
        if (sellerInfo == null) {
            map.put("msg", ResultEnum.LOGIN_FAIL.getMessage());
            map.put("url", "/sell/seller/order/list");
            return new ModelAndView("common/error");
        }

        //2. 设置token至redis
        //javaJDK提供的一个自动生成主键的方法
        String token = UUID.randomUUID().toString();

        //常量  redis参数的过期时间
        Integer expire = RedisConstant.EXPIRE;

        //把值设置到redis里
        // 第一个参数String.format(RedisConstant.TOKEN_PREFIX, token)是 相当于redis的key  格式--->   前缀+token值
        // 第二个参数openid  是用户扫码登入后  获取的 微信开放平台的 openid 相当于redis的value
        //第三个参数expire 参数的过期时间
        //第四个参数TimeUnit.SECONDS  是过期时间的单位
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), openid, expire, TimeUnit.SECONDS);

        //3. 设置token至cookie  往HttpServletResponse 写cookie cookie的值就是 redis的key 去掉redis的前缀token_
        //注意此处的cookie设置 如果我们用localhost或127.0.0.1来访问这个路径 这样会把cookie设置到localhost这个页面下
        //但是我们执行完设置到redis和cookie的操作后 跳转的页面是我们的域名+项目名+url地址 所以在cookie里看不到值
        //如果要在cookie里拿到值  需要用域名访问该路径
        CookieUtil.set(response, CookieConstant.TOKEN, token, expire);

        return new ModelAndView("redirect:" + projectUrlConfig.getSell() + "/sell/seller/order/list");

    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response,
                               Map<String, Object> map) {
        //1. 从cookie里查询
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null) {
            //2. 清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));

            //3. 清除cookie  把cookie的过期时间设置为0
            CookieUtil.set(response, CookieConstant.TOKEN, null, 0);
        }

        map.put("msg", ResultEnum.LOGOUT_SUCCESS.getMessage());
        map.put("url", "/sell/seller/order/list");
        return new ModelAndView("common/success", map);
    }


}
