package com.zbsp.wepaysp.common.constant;

/**
 * 微信API URL
 * 
 * @author 孟郑宏
 */
public class WxApiUrl {

    /**
     * 静默授权网页授权URL，公众号链接或者二维码对应链接，获取code，需要替换参数 APPID、REDIRECT_URI、STATE（可以不传，设置为""）<br>
     */
    public static final String JSAPI_AUTH_SNSAPI_BASE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

    /**
     * 弹出授权网页授权URL，公众号链接或者二维码对应链接，获取code，需要替换参数 APPID、REDIRECT_URI、STATE（可以不传，设置为""）<br>
     */
    public static final String JSAPI_AUTH_SNSAPI_USERINFO = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    
    /**
     * 通过code换取网页授权access_token，get方式请求，需要替换参数 APPID、SECRET、CODE（用户统一授权后得到）、grant_type=authorization_code；
     */
    public static final String JSAPI_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    
    /**
     * 刷新access_token，get方式请求，需要替换参数 APPID、REFRESH_TOKEN（通过code换取网页授权access_token得到的）
     */
    public static final String JSAPI_REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
 
    /**设置行业可在MP中完成，每月可修改行业1次，账号仅可使用所属行业中相关的模板，为方便第三方开发者，提供通过接口调用的方式来修改账号所属行业*/
    public static final String TEMPLATE_SET_INDUSTRY = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
    
    /**获得模板ID*/
    public static final String TEMPLATE_GET_ID = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
    
    /**发送模板消息*/
    public static final String TEMPLATE_SEND = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
}
