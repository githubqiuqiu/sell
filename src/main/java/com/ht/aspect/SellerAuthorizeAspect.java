package com.ht.aspect;

import com.ht.constant.CookieConstant;
import com.ht.constant.RedisConstant;
import com.ht.exception.SellerAuthorizeException;
import com.ht.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 商家登入的切面类
 * @auth Qiu
 * @time 2018/3/17
 **/

@Aspect
@Component
@Slf4j
public class SellerAuthorizeAspect {

    /**
     *  spring data redis 包下的redis 连接类
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 定义一个切入点方法
     * 切入controller包下的 以Seller开头的controller 里面的所有方法
     * 并且除掉SellerUserController 这个controller不需要切入(因为这个方法是 商家的登入登出 不需要拦截登入)
     */
    @Pointcut("execution(public * com.ht.controller.Seller*.*(..))" +
            "&& !execution(public * com.ht.controller.SellerUserController.*(..))")
    public void verify() {}


    /**
     * 在切入点之前 判断用户是否登入
     */
    @Before("verify()")
    public void doVerify() {
        //获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //查询cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null) {
            log.warn("【登录校验】Cookie中查不到token");
            //抛出异常  通过SellExceptionHandler 全局异常出来类来处理
            throw new SellerAuthorizeException();
        }

        //去redis里查询 看redis里是否有值
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)) {
            log.warn("【登录校验】Redis中查不到token");
            //抛出商家的登入异常 会被SellExceptionHandler 异常处理类捕获
            throw new SellerAuthorizeException();
        }
    }

}
