package com.zbsp.wepaysp.common.constant;

/**
 * 访问微信参数名常量
 * 
 * @author 孟郑宏
 */
public class WxParam {

    /** 公众号的唯一标识 */
    public static final String APP_ID = "appid";
    /** 公众号的appsecret */
    public static final String APP__SECRET = "secret";
    /** 用户统一授权获取的code */
    public static final String CODE = "code";
    /** 通过code换取网页授权access_token时，值为 authorization_code；刷新access_token时值为 refresh_token； */
    public static final String GRANT_TYPE = "grant_type";
    /** 通过access_token获取到的refresh_token参数 */
    public static final String REFRESH_TOKEN = "refresh_token";

    /** 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同 */
    public static final String ACCESS_TOKEN = "access_token";
    /** 用户的唯一标识 */
    public static final String OPEN_ID = "openid";

    /** 用户授权时使用，必填，授权后重定向的回调链接地址，请使用urlencode对链接进行处理 */
    public static final String REDIRECT_URI = "redirect_uri";
    /** 用户授权时使用，必填，返回类型，请填写code */
    public static final String RESPONSE_TYPE = "response_type";

    /**
     * 用户授权时使用，必填，应用授权作用域：
     * 
     * <pre>
     * snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）;<br>
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地，<br>
     * 并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
     * 
     * <pre>
     */
    public static final String SCOPE = "scope";
    /** 用户授权时使用，非必填，重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节 */
    public static final String STATE = "state";
    /**用户授权时使用，非必填，无论直接打开还是做页面302重定向时候，必须带此参数*/
    public static final String WECHAT_REDIRECT = "#wechat_redirect";

}
