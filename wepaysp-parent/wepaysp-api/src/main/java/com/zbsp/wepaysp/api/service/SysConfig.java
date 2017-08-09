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
    
    /**微信公众号收款消息链接URL*/
    public static String wxPayMessageLinkURL;
    
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
    
    /**
     * 系统中的支付宝应用配置，key=appid，value为应用信息Map 
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
    public static Map<String, Map<String, Object>> alipayAppMap = new HashMap<String, Map<String, Object>>();
    
    /***
     * 支付当面付功能的蚂蚁平台应用ID，需要在 alipay_app_t 表中存在
     */
    public static String appId4Face2FacePay;
    
    /***
     * 支付宝教育缴费功能能的蚂蚁平台应用ID，需要在 alipay_app_t 表中存在
     */
    public static String appId4Edu;
    
    /**支付宝授权回调系统地址*/
    public static String alipayAuthCallBackURL;
    
    /**支付宝手机网站支付系统地址*/
    public static String alipayWapPayURL;
    
    /**支付宝手机网站支付支付成功回调系统地址*/
    public static String alipayWapPayReturnURL;
    
    /**支付宝手机网站支付异步通知地址*/
    public static String alipayWapPayNotifyURL;
    
    /**支付宝 - 接口调用效率上报标志，true，调用交易保障服务，false调用 非交易保障服务*/
    public static boolean alipayReportFlag = false;
    
    /**上线开关，为true时为线上模式，false为开发/测试模式*/
    public static boolean onlineFlag;
    
    /**支付宝教育缴费异步通知地址*/
    public static String alipayEduNotifyURL;

}
