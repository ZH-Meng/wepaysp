package com.zbsp.wepaysp.common.constant;

/**
 * 系统枚举定义类
 * 
 * @author 孟郑宏
 */
public class SysEnums {
    
    /**
     * 支付类型
     */
    public static enum PayType {
        /** 1：微信-刷卡支付*/                                  WEIXIN_MICROPAY("1"),
        /** 2：微信-公众号支付 */                                WEIXIN_JSAPI("2"),
        /** 3：微信-扫码支付 */                                 WEIXIN_NATIVE("3"),
        /** 4：微信-微信买单 */                                 WEIXIN_PAY("4"),
        
        /** 6：支付宝-当面付-条码支付 */                    ALI_FACE_BAR("6"),
        /** 7：支付宝-扫码付 */                                  ALI_SCAN("7"),
        /** 8：支付宝-手机网站支付 */                       ALI_H5("8");
        
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private PayType(String value) {
            this.value = value;
        }
    }
    
    /**
     * 支付平台：适用用于汇总展示支付类型，PayType（1~5对应PayPlatformType=1，6~10对应PayPlatformType=2）
     */
    public static enum PayPlatform {
        /** 1 微信支付*/            WEIXIN("1", "微信支付"),
        /** 2 支付宝支付 */        ALI("2", "支付宝支付");
        
        private String value;
        private String desc;

        private PayPlatform(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }
        
        public String getValue() {
            return value;
        }
        
        public String getDesc() {
            return desc;
        }
    }
    
    /**
     * 交易状态
     */
    public static enum TradeStatus {
        /** 0：交易中 */                                        
        TRADEING(0),
        /** 1：交易成功 */
        TRADE_SUCCESS(1),
        /** 2：交易失败 */     
        TRADE_FAIL(2),
        /** 3：交易撤销 */
        TRADE_REVERSED(3),
        /** 4：未付款交易超时关闭，或支付完成后全额退款 */     
        TRADE_CLOSED(4),
        /** 5：交易待关闭 */  
        TRADE_TO_BE_CLOSED(5),
        
        /** 99 人工处理*/
        MANUAL_HANDLING(99);
        
        private int value;

        public int getValue() {
            return value;
        }

        private TradeStatus(int value) {
            this.value = value;
        }
    }
    
    /** 
     * 交易状态展示
     */
    public static enum TradeStatusShow {
        PAY_SUCCESS(1, "支付成功"),
        REFUND(2, "已申请退款");
        
        private int value;
        private String desc;

        private TradeStatusShow(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getDesc() {
            return desc;
        }
    }
    
    /**
     * 二维码类型
     */
    public static enum QRCodeType {
        /** 支付二维码*/                 PAY(1),
        /** 绑定支付通知二维码*/     BIND_PAY_NOTICE(2),
        /** 商户支付宝授权二维码*/  ALIPAY_APP_AUTH(3),
        /** 商户支付宝沙箱授权二维码*/  ALIPAY_APP_AUTH_DEV(4);
        
        private int value;
        
        public int getValue() {
            return value;
        }
        
        private QRCodeType(int value) {
            this.value = value;
        }
    }

    /**
     * 扫码客户端
     * 
     * @author 孟郑宏
     */
    public static enum ScanCodeClient {
        /** 1 微信APP（浏览器）*/                                  APP_WEIXIN("1"),
        /** 2 支付宝APP */                                             APP_ALI("2"),
        /** 3 百度APP */                                                APP_BAIDU("3"),        
        /** 4 系统业务APP */                                          APP_SELF("4"),
        /** 5 未知（不同浏览器以及其他扫码客户端）*/       UN_KNOWN("5");
        
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private ScanCodeClient(String value) {
            this.value = value;
        }
    }
    
    /**
     * 部署server类型，适用于server启动执行不同的配置及操作
     * 
     * @author 孟郑宏
     */
    public static enum ServerType {
        WEB_MANAGE, REST 
    }
    
    /**
     * 系统报警枚举
     * 
     * @author 孟郑宏
     */
    public enum AlarmLogPrefix {
        wxPayAPIMoneyException("【微信支付金额异常，系统报警】"),
        /** 调用微信支付API失败 */
        invokeWxPayAPIErr("【调用微信支付API失败，系统报警】"),
        /** 调用微信公众号API失败 */
        invokeWxJSAPIErr("【调用微信公众号接口API失败，系统报警】"),
        /** 发送微信支付请求失败 */
        sendWxPayRequestException("【发送微信支付请求失败，系统报警】"),
        /** 受理微信支付结果异常 */
        handleWxPayResultException("【处理微信支付结果异常，系统报警】"),
        /** 受理微信支付结果失败 */
        handleWxPayResultErr("【处理微信支付结果失败，系统报警】"),
        
        aliPayAPIMoneyException("【支付宝支付金额异常，系统报警】"),
        /** 调用支付宝支付API失败 */
        invokeAliPayAPIErr("【调用支付宝支付API失败，系统报警】"),
        /** 发送支付宝支付请求失败 */
        sendAliPayRequestException("【发送支付宝支付请求失败，系统报警】"),
        /** 受理支付宝支付结果异常 */
        handleAliPayResultException("【处理支付宝支付结果异常，系统报警】"),
        /** 受理支付宝支付结果失败 */
        handleAliPayResultErr("【处理支付宝支付结果失败，系统报警】");

        private String value;

        public String getValue() {
            return value;
        }

        private AlarmLogPrefix(String value) {
            this.value = value;
        }
    }
    
    /**
     * 是否可以退款标识
     */
    public static enum RefundFlag {
        YES,NO;
    }
    
    /**
     * 开发过程使用参数
     */
    public static enum DevParam {
        APPID("wx8a60a03a3b75acf7"),
        APPSECRET("0f7dbf1be06f76af581f5a17058d09d6"),
        MCHID("1337800201"),
        KEY("402881c6014672d801014672ef300001"),
        CERT_LOCAL_PATH ("C:/apiclient_cert.p12"),
        CERT_PASSWORD ("1337800201");
        
        private String value;
        
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private DevParam(String value) {
            this.value = value;
        }
    }
    
}
