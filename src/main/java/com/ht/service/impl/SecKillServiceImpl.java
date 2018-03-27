package com.ht.service.impl;

import com.ht.exception.SellException;
import com.ht.redis.RedisLock;
import com.ht.service.SecKillService;
import com.ht.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @auth Qiu
 * @time 2018/3/18
 **/
@Service
@Transactional
public class SecKillServiceImpl implements SecKillService{
    private static final int TIMEOUT = 10 * 1000; //超时时间 10s

    @Autowired
    private RedisLock redisLock;

    /**
     * 国庆活动，皮蛋粥特价，限量100000份
     */
    static Map<String,Integer> products;
    static Map<String,Integer> stock;
    static Map<String,String> orders;
    static
    {
        /**
         * 模拟多个表，商品信息表，库存表，秒杀成功订单表
         */
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();
        products.put("123456", 100000);
        stock.put("123456", 100000);
    }

    private String queryMap(String productId)
    {
        return "国庆活动，皮蛋粥特价，限量份"
                + products.get(productId)
                +" 还剩：" + stock.get(productId)+" 份"
                +" 该商品成功下单用户数目："
                +  orders.size() +" 人" ;
    }

    @Override
    public String querySecKillProductInfo(String productId)
    {
        return this.queryMap(productId);
    }


    /**
     * 模拟秒杀商品的方法
     * 当并发量大的时候  该方法会被多个线程访问 可能会造成一些问题
     *
     * 第一种解决方案是加锁 synchronized 这样可以解决一些并发问题
     * 但是缺点也很多: 1.程序变的很慢  2.无法做到细粒度的控制 3.只适合单点的情况  集群就不使用了
     *
     * 第二种解决方案 如下代码  保证部分代码使用单线程来访问 --->先给要单线程访问的代码 加锁  在访问结束后解锁
     * 优点:1.可以支持分布式 2.可以支持更细粒度的控制  --->多台机器上的多个进程对一个数据进行操作的互斥
     * 使用redis(单线程) 用商品的id productId作为key
     *
     * @param productId
     */
    @Override
    public  void orderProductMockDiffUser(String productId)
    {

        //定义一个时间  --->当前时间+超时时间
        long time=System.currentTimeMillis()+TIMEOUT;
        //加锁
        boolean status=redisLock.lock(productId,String.valueOf(time));

        //如果被锁住了  就会执行下面的代码
        if(!status){//如果不为true 就是没上到锁  就抛出异常 不让进入
            throw new SellException(101,"人太多了 请稍后再试");
        }


        //1.查询该商品库存，为0则活动结束。
        int stockNum = stock.get(productId);
        if(stockNum == 0) {
            throw new SellException(100,"活动结束");
        }else {
            //2.下单(模拟不同用户openid不同)
            orders.put(KeyUtil.getUniqueKey(),productId);
            //3.减库存
            stockNum =stockNum-1;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stock.put(productId,stockNum);
        }

        //解锁   执行完秒杀后 解锁这个线程的访问 让下一个线程进来
        redisLock.unlock(productId,String.valueOf(time));

    }
}
