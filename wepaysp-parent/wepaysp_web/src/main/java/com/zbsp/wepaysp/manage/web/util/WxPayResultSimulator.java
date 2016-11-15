package com.zbsp.wepaysp.manage.web.util;

import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.vo.wxauth.AccessTokenResultVO;

/**
 * 微信支付模拟请求结果
 * 
 * @author 孟郑宏
 */
public class WxPayResultSimulator {

    /**
     * 模拟微信网页授权，成功
     * 
     * @param apiURL
     *            eg：https://api.weixin.qq.com/sns/oauth2/access_token?appid=${APPID}&secret=${SECRET}&code=${CODE}&grant_type=authorization_code
     * @return
     */
    public static String jsAPIAuthSucc(String apiURL) {
        System.out.println("模拟微信网页授权，URL：" + apiURL);
        AccessTokenResultVO result = new AccessTokenResultVO();
        result.setAccess_token(Generator.generateIwoid());
        result.setExpires_in(new Long(7200));
        result.setRefresh_token(Generator.generateIwoid());
        result.setOpenid(Generator.generateIwoid());
        result.setScope("snsapi_base");
        result.setUnionid(Generator.generateIwoid());
        return JSONUtil.toJSONString(result, false);
    }

    public static String jsAPIAuthFail(String apiURL) {
        return "{\"errcode\":40029,\"errmsg\":\"invalid code\"}";
    }

    /**
     * 模拟微信刷新access_token
     * 
     * @param apiURL
     *            eg：https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=${APPID}&grant_type=refresh_token&refresh_token=${REFRESH_TOKEN}
     * @return
     */
    public static String jsAPIRefreshTokenSucc(String apiURL) {
        System.out.println("模拟微信刷新access_token，URL：" + apiURL);
        AccessTokenResultVO result = new AccessTokenResultVO();
        result.setAccess_token(Generator.generateIwoid());
        result.setExpires_in(new Long(7200));
        result.setRefresh_token(Generator.generateIwoid());
        result.setOpenid(Generator.generateIwoid());
        result.setScope("snsapi_base");
        return JSONUtil.toJSONString(result, false);
    }

}
