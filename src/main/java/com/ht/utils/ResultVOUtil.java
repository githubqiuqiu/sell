package com.ht.utils;

import com.ht.VO.ResultVO;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
public class ResultVOUtil {

    public  static ResultVO success(Object object){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        resultVO.setData(object);
        return resultVO;
    }

    public static ResultVO success(){
        //调用上面的success 方法
        return  success(null);
    }

    public static ResultVO error(Integer code,String msg){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }


}
