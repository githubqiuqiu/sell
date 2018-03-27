package com.ht.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 使用redis 加锁和解锁的方法
 * @auth Qiu
 * @time 2018/3/18
 **/
@Component
@Slf4j
public class RedisLock {

    /**
     * 引入redis的使用组件
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value) {
        //将key设置值为value，如果key不存在，这种情况下等同SET命令。
        // 当key存在时，什么也不做。SETNX是”SET if Not eXists”的简写。

        //setIfAbsent(key, value) 方法 相当于 redis客户端的setnx 命令  返回一个布尔类型  可以设置返回true
        //如果 成功 返回true 表示已经锁住了
        if(redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }

        /**
         * 防止出现线程死锁的情况  能把死锁解开
         */
        //currentValue=A   这两个线程的value都是B  其中一个线程拿到锁
        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁是否超时  存储小于当前时间(锁过期)
        if (!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间  使用的是redis客户端的 getset命令  在java端方法为 getAndSet(key, value)
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            //如果旧的值不为空  旧的值和传进来的值相等  也是锁住了 返回true
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }

}
