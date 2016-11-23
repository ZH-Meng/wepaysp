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
    public static final String JSAPI_AUTH_SNSAPI_USERINFO = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SNSAPI_USERINFO &state=STATE#wechat_redirect";
    
    /**
     * 通过code换取网页授权access_token，get方式请求，需要替换参数 APPID、SECRET、CODE（用户统一授权后得到）、grant_type=authorization_code；
     */
    public static final String JSAPI_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    
    /**
     * 刷新access_token，get方式请求，需要替换参数 APPID、REFRESH_TOKEN（通过code换取网页授权access_token得到的）
     */
    public static final String JSAPI_REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    
}
