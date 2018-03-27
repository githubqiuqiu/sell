package com.ht.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 微信Controller
 * @auth Qiu
 * @time 2018/3/13
 **/
@RestController
@RequestMapping("/weixin")
@Slf4j
public class WeiXinController {

    /**
     * 回调路径  拿到用户的 code 用来换取 access_token
     * @param code
     */
    @GetMapping("/auth")
    public void auth(@RequestParam("code") String code) {
        log.info("进入auth方法。。。");
        log.info("code={}", code);

        //返回的是一个json格式的字符串  其中有我们要的openid
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx66a696997d004341&secret=5bf53f840b676e1c08fc5e4c8102645d&code=" + code + "&grant_type=authorization_code";

        //借助 RestTemplate，Spring应用能够方便地使用REST资源
        RestTemplate restTemplate = new RestTemplate();
        //发送一个http get请求 返回的请求体将映射为一个对象
        //postForObject() POST 数据到一个URL，返回根据响应体匹配形成的对象
        String response = restTemplate.getForObject(url, String.class);
        log.info("response={}", response);
    }
}
