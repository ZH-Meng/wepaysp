package com.zbsp.wepaysp.common.constant;

import java.math.BigDecimal;

/**
 * 系统环境变量Key，保证唯一，Map、session等作用域存值都可使用
 * 
 * @author 孟郑宏
 */
public class SysEnvKey {
    
    public static final String WX_APP_ID = "wx_appid";
    public static final String WX_MCH_ID = "wx_mch_id";
    public static final String WX_SECRET = "wx_secret";
    public static final String WX_CERT_LOCAL_PATH = "wx_cert_local_path";
    public static final String WX_CERT_PASSWORD = "cert_password";
    public static final String WX_KEY = "wx_key";
    
    public static final String WX_BASE_ACCESS_TOKEN = "wx_base_access_token";
    public static final String WX_BASE_EXPIRE_TIME = "wx_base_expire_time"; 
    public static final String WX_AUTH_ACCESS_TOKEN = "wx_auth_access_token";
    public static final String WX_AUTH_REFRESH_TOKEN = "wx_auth_refresh_token";
    public static final String WX_OPEN_ID = "wx_open_id";
    public static final String WX_CODE = "wx_code";

    /**支付宝沙箱应用id*/
    public static final String ALIPAY_SANDBOXIE_APPID = "alipay_sandboxie_appid";
    /**支付宝普通应用id*/
    public static final String ALIPAY_ORDINARYE__APPID = "alipay_ordinary_appid";
    /**支付宝服务窗应用id*/
    public static final String ALIPAY_SERVICE_WINDOW__APPID = "alipay_service_window_appid";
    /**应用信息：支付宝应用ID*/
    public static final String ALIPAY_APP_ID = "alipay_app_id";    
    /**应用信息：支付宝公钥*/
    public static final String ALIPAY_PUBLIC_KEY = "alipay_public_key";
    /**应用信息：签名类型*/
    public static final String ALIPAY_APP_SIGN_TYPE = "sign_type";
    /**应用信息：RSA/RSA2公钥*/
    public static final String ALIPAY_APP_PUBLIC_KEY = "public_key";
    /**应用信息：RSA/RSA2私钥*/
    public static final String ALIPAY_APP_PRIVATE_KEY = "private_key";
    /**应用信息：最大查询次数*/
    public static final String ALIPAY_APP_MAX_QUERY_RETRY = "max_query_retry";
    /**应用信息：查询间隔（毫秒）*/
    public static final String ALIPAY_APP_QUERY_DURATION = "query_duration";
    /**应用信息：最大撤销次数*/
    public static final String ALIPAY_APP_MAX_CANCEL_RETRY = "max_cancel_retry";
    /**应用信息：撤销间隔（毫秒）*/
    public static final String ALIPAY_APP_CANCEL_DURATION = "cancel_duration";
    /**应用信息：应用类型*/
    public static final String ALIPAY_APP_TYPE = "alipay_app_type";    
    
    /** 时间格式：yyyy/MM/dd HH:mm:ss */
    public static final String TIME_PATTERN_YMD_SLASH_HMS_COLON  = "yyyy/MM/dd HH:mm:ss";
    
    /** 时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String TIME_PATTERN_YMD_HYPHEN_HMS_COLON  = "yyyy-MM-dd HH:mm:ss";
    
    /**金额：分和元的倍数*/
    public static final BigDecimal BIG_100 = new BigDecimal(100);
    
    /**支付宝支付超时时间 1分钟*/
    public static final String EXPIRE_TIME_ALI_PAY_1M = "1m";
    public static final String EXPIRE_TIME_ALI_PAY_30M = "30m";
    
    /** 蚂蚁开放平台网关-正式 */
    public static final String ANT_OPEN_API_DOMAIN = "https://openapi.alipay.com/gateway.do";
    /** 蚂蚁开放平台网关-开发 */
    public static final String ANT_OPEN_API_DOMAIN_DEV = "https://openapi.alipaydev.com/gateway.do";
    /** 云监控网关 */
    public static final String ANT_CLOUD_MONITOR_DOMAIN = "http://mcloudmonitor.com/gateway.do"; 
    
    
    /**蚂蚁开放平台第三方应用授权URL，服务窗链接或者二维码对应链接，获取app_auth_code，需要替换参数 APPID、REDIRECT_URI*/
    public static final String ALIPAY_AUTH_APP_URL= "https://openauth.alipay.com/oauth2/appToAppAuth.htm?app_id=APPID&redirect_uri=REDIRECT_URI";
    
    /**沙箱-蚂蚁开放平台第三方应用授权URL，服务窗链接或者二维码对应链接，获取app_auth_code，需要替换参数 APPID、REDIRECT_URI*/
    public static final String ALIPAY_AUTH_APP_URL_DEV= "https://openauth.alipaydev.com/oauth2/appToAppAuth.htm?app_id=APPID&redirect_uri=REDIRECT_URI";

    /**正则，金额，大于0的整数或者小数，小数时 两位有效数字，例如：0.01或者200*/
    public static final String REGEX_￥_POSITIVE_FLOAT_2BIT= "^((0{1}(\\.((0{1}[1-9]{1})|([1-9]{1}\\d?)))?)|([1-9]{1}\\d*(\\.\\d{1,2})?))$";
    
    /**正则，金额，整数*/
    public static final String REGEX_￥_INTEGER="^\\d+$";
    
    /**微信刷卡支付过期秒数 120*/    
    public final static int WX_MICROPAY_EXPIRE_SECS = 120;
    
}
