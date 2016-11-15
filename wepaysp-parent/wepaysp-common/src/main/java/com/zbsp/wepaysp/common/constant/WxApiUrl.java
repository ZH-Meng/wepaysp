package com.zbsp.wepaysp.common.constant;

/**
 * 微信API URL
 * 
 * @author 孟郑宏
 */
public class WxApiUrl {

    /**
     * 网页授权URL，公众号链接或者二维码对应链接，需要拼接参数 appid、redirect_uri、response_type=code、scope=snsapi_base，state、#wechat_redirect，参考如下官方示例：<br>
     * https://open.weixin.qq.com/connect/oauth2/authorize?<br>
     * appid=wx520c15f417810387&<br>
     * redirect_uri=https%3A%2F%2Fchong.qq.com%2Fphp%2Findex.php%3Fd%3D%26c%3DwxAdapter%26m%3DmobileDeal%26showwxpaytitle%3D1%26vb2ctag%3D4_2030_5_1194_60&<br>
     * response_type=code& <br>
     * scope=snsapi_base&state=123#wechat_redirect<br>
     * <p>
     * 注意：拼接参数固定
     * </p>
     */
    public static final String JSAPI_AUTH = "https://open.weixin.qq.com/connect/oauth2/authorize";

    /**
     * 获取access_token的URL： <br>
     * <p>
     * 用法①：通过code换取网页授权access_token，get方式请求，需要拼接参数 appid、secret、code（用户统一授权后得到）、grant_type=authorization_code；
     * </p>
     * <p>
     * 用法②：刷新access_token，get方式请求，需要拼接参数 appid、grant_type=refresh_token、refresh_token（通过code换取网页授权access_token得到的）、
     * </p>
     */
    public static final String JSAPI_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

}
