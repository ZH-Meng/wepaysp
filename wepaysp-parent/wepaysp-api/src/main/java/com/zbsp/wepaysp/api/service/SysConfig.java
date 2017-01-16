package com.zbsp.wepaysp.api.service;

import java.util.HashMap;
import java.util.Map;

import com.zbsp.alipay.trade.service.AlipayMonitorService;
import com.zbsp.alipay.trade.service.AlipayTradeService;

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
    
    /**支付宝当面付2.0服务*/
    public static AlipayTradeService tradeService;

    /**支付宝当面付2.0服务（集成了交易保障接口逻辑）*/
    public static AlipayTradeService tradeWithHBService;

    /**支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt*/
    public static AlipayMonitorService monitorService;

}
