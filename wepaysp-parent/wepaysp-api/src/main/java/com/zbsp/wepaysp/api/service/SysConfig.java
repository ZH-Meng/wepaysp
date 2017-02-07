package com.zbsp.wepaysp.api.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统静态配置，由SysConfigService 接口将配置初始化
 * 
 * @author 孟郑宏
 */
public class SysConfig {
    
    /**支付客户端检查地址*/
    public static String payClientCheckURL;
    
    /**公众号支付：扫码后微信回调系统地址*/
    public static String wxPayCallBackURL;
    
    /**微信支付统一下单通知URL*/
    public static String wxPayNotifyURL;
    
    /**收银员绑定微信支付通知：扫码后微信回调系统地址*/
    public static String bindCallBackURL;
    
    /**系统生成二维码存放路径*/
    public static String qRCodeRootPath;
    
    /**公众号二维码存放路径*/
    public static String appidQrCodePath;
    
	/**
	 * 系统支持多个服务商配置，key=partnerOid，value为服务商配置Map 
	 * 服务商配置Map key ：
	 * <pre>
	 * SysEnvKey.WX_APPID<br>
	 * SysEnvKey.WX_MCH_ID<br>
	 * SysEnvKey.WX_SECRET<br>
	 * SysEnvKey.WX_CERT_LOCAL_PATH<br>
	 * SysEnvKey.CERT_PASSWORD<br>
	 * SysEnvKey.WX_KEY<br>
	 * SysEnvKey.WX_BASE_ACCESS_TOKEN<br>
	 * SysEnvKey.WX_BASE_EXPIRE_TIME<br>
	 * </pre>
	 */
    public static Map<String, Map<String, Object>> partnerConfigMap = new HashMap<String, Map<String, Object>>();
    
	/**
	 * 系统支持多个服务商配置，key=appid，value为服务商配置Map 
	 * 服务商配置Map key ：
	 * <pre>
	 * SysEnvKey.WX_APPID<br>
	 * SysEnvKey.WX_MCH_ID<br>
	 * SysEnvKey.WX_SECRET<br>
	 * SysEnvKey.WX_CERT_LOCAL_PATH<br>
	 * SysEnvKey.CERT_PASSWORD<br>
	 * SysEnvKey.WX_KEY<br>
	 * SysEnvKey.WX_BASE_ACCESS_TOKEN<br>
	 * SysEnvKey.WX_BASE_EXPIRE_TIME<br>
	 * </pre>
	 */
    public static Map<String, Map<String, Object>> partnerConfigMap2 = new HashMap<String, Map<String, Object>>();
    
    /***
     * 支付当面付功能的蚂蚁平台应用ID，需要在 alipay_app_t 表中存在
     */
    public static String appId4Face2FacePay;
    
    /**支付宝授权回调系统地址*/
    public static String alipayAuthCallBackURL;
    
    /**开发模式标志，为1时为开发，其他为线上模式*/
    public static String devMode;
    
    /**支付宝手机网站支付支付成功回调系统地址*/
    public static String alipayWapPayReturnURL;
    
    /**支付宝手机网站支付异步通知地址*/
    public static String alipayWapPayNotifyURL;

}
