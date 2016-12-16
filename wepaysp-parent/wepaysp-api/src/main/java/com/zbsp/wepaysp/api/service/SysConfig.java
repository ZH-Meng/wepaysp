package com.zbsp.wepaysp.api.service;

/**
 * 系统静态配置，由SysConfigService 接口将配置初始化
 * 
 * @author 孟郑宏
 */
public class SysConfig {
    
    /**公众号支付：扫码后微信回调系统地址*/
    public static String payCallBackURL;
    
    /**微信支付统一下单通知URL*/
    public static String wxPayNotifyURL;
    
    /**收银员绑定微信支付通知：扫码后微信回调系统地址*/
    public static String bindCallBackURL;
    
    
    /**系统生成二维码存放路径*/
    public static String qRCodeRootPath;
    
}
