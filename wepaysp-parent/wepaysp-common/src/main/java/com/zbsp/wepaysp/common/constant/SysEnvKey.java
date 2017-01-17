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
    
    /** 时间格式：yyyy/MM/dd HH:mm:ss */
    public static final String TIME_PATTERN_YMD_SLASH_HMS_COLON  = "yyyy/MM/dd HH:mm:ss";
    
    /** 时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String TIME_PATTERN_YMD_HYPHEN_HMS_COLON  = "yyyy-MM-dd HH:mm:ss";
    
    /**金额：分和元的倍数*/
    public static final BigDecimal BIG_100 = new BigDecimal(100);
    
}
