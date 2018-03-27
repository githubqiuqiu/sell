package com.ht.controller;

import com.ht.config.ProjectUrlConfig;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

/**
 * 微信的网页授权controller
 * @auth Qiu
 * @time 2018/3/14
 **/
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    /**
     * 引入微信 公众平台开发的 API的Service
     */
    @Autowired
    private WxMpService wxMpService;

    /**
     * 引入项目的路径配置
     */
    @Autowired
    private ProjectUrlConfig projectUrlConfig;


    /**
     * 引入微信 开放平台开发的 API的Service
     */
    @Autowired
    private WxMpService wxOpenService;


    /**
     * 获取微信 公众平台的code 并且跳转到获取access_token的页面获取openid
     * @param returnUrl
     * @return
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        //1. 配置
        //2. 调用方法 url为回调方法的路径
        String url = projectUrlConfig.getWechatMpAuthorize() + "/sell/wechat/userInfo";
        //第一个参数是回调的url地址  第二个参数是scope的选值  第三个参数是state的值 state参数可以设置为获取了openid后重定向到的页面URL  可见微信开公众平台的发文档
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl));
        //重定向到获取code的方法 成功后会重定向到该redirectUrl里面的redirect_uri参数上的url 也就是上面自己定义的url 跳转到获取用户openid的方法
         return "redirect:" + redirectUrl;
    }

    /**
     * 微信 公众平台  拿到code后回调的方法  获取用户的openid
     * @param code  code是 authorize回调到userInfo方法时获取的
     * @param returnUrl returnUrl是 authorize方法时传入的 也就是获取到openid后需要跳转的页面
     * @return
     */
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            //获取用户的access_token  openid也在里面
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            log.info("【获取的微信access_token】{}",wxMpOAuth2AccessToken);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }

        //获取openid
        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("【用户的微信openId】{}",openId);
        //获取到用户的openid后  重定向到一个页面
        return "redirect:" + returnUrl + "?openid=" + openId;
    }


    /**
     * 获取微信 开放平台的code 并且跳转到获取access_token的页面获取openid
     * @param returnUrl
     * @return
     */
    @GetMapping("/qrAuthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl) {
        String url = projectUrlConfig.getWechatOpenAuthorize() + "/sell/wechat/qrUserInfo";
        /**
         * @param redirectURI 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
         * @param scope       应用授权作用域，拥有多个作用域用逗号（,）分隔，网页应用目前仅填写snsapi_login即可
         * @param state       非必填，用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
         */
        String redirectUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, URLEncoder.encode(returnUrl));
        //重定向到微信服务器后  微信会帮我们重定向到下面的方法 获取openid
        return "redirect:" + redirectUrl;
    }


    /**
     * 微信 开放平台  拿到code后回调的方法  获取用户的openid
     * @param code
     * @param returnUrl
     * @return
     */
    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String returnUrl) {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }
        log.info("wxMpOAuth2AccessToken={}", wxMpOAuth2AccessToken);
        String openId = wxMpOAuth2AccessToken.getOpenId();

        return "redirect:" + returnUrl + "?openid=" + openId;
    }

}
