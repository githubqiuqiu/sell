package com.ht;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auth Qiu
 * @time 2018/3/8
 **/

//@RunWith：这个是指定使用的单元测试执行类
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LoggerTest {
    //引入一个日志类  (参数一定要写当前类)
    private final Logger logger=LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void test1(){

        String uname="root";
        String password="1234";
        //在日志中输出变量 下面有2种方法
        logger.info("uname: "+uname+", password:"+password);
        logger.info("uname: {} , password: {}",uname,password);

        //只会输出比自己等级高的日志 默认是info
        logger.trace("trace...");
        logger.debug("debug...");
        logger.info("info...");
        logger.warn("warn...");
        logger.error("error...");

    }



}
