package com.ht.handler;

import com.ht.VO.ResultVO;
import com.ht.config.ProjectUrlConfig;
import com.ht.exception.ResponseBankException;
import com.ht.exception.SellException;
import com.ht.exception.SellerAuthorizeException;
import com.ht.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @auth Qiu
 * @time 2018/3/17
 **/
//@ControllerAdvice 处理全局异常，返回固定格式Json
@ControllerAdvice
public class SellExceptionHandler {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    /**
     * @ExceptionHandler：统一处理某一类异常，从而能够减少代码重复率和复杂度
     * @ControllerAdvice：异常集中处理，更好的使业务逻辑与异常处理剥离开
     * @ResponseStatus：可以将某种异常映射为HTTP状态码
     * @return
     */

    //拦截登录异常
    //注意 这里设置的是跳转到微信的扫描登入页面
    //如果要测试  因为没有openid 所以可以不要跳到这个页面 直接跳到登录页面 带上openid
    //http://20nz264316.imwork.net/sell/wechat/qrAuthorize?returnUrl=http://20nz264316.imwork.net/sell/seller/login
    @ExceptionHandler(value = SellerAuthorizeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handlerAuthorizeException() {
        //concat 字符串拼接 跳到微信扫描的页面  跳到验证商家微信扫描的页面
        return new ModelAndView("redirect:"
                .concat(projectUrlConfig.getWechatOpenAuthorize())
                .concat("/sell/wechat/qrAuthorize")
                .concat("?returnUrl=")
                .concat(projectUrlConfig.getSell())
                .concat("/sell/seller/login"));
    }

    /**
     * 商品异常 对返回数据的处理
     * 因为返回的数据格式是 json  所以 加上@ResponseBody注解
     * 处理对商品类信息失败时的数据返回格式
     * @param e
     * @return
     */
    @ExceptionHandler(value = SellException.class)
    @ResponseBody
    public ResultVO handlerSellerException(SellException e) {
        return ResultVOUtil.error(e.getCode(), e.getMessage());
    }

    /**
     * 假设有返回银行信息的异常  可以添加一个 捕获ResponseBankException 异常的方法
     *  @ResponseStatus(HttpStatus.FORBIDDEN) 返回的状态码  FORBIDDEN相当于403错误
     */
    @ExceptionHandler(value = ResponseBankException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleResponseBankException() {

    }

}
