package com.zbsp.wepaysp.common.constant;


/**
 * 枚举定义类
 * 
 * @author 孟郑宏
 */
public class EnumDefine {

    /**
     * 通信标识 
     */
    public static enum ReturnCode {
        SUCCESS, FAIL;
    }
    
    /**
     * 交易标识 
     */
    public static enum ResultCode {
        SUCCESS, FAIL;
    }
    
    /**
     * 微信支付交易类型
     */
    public static enum TradeType {
        /** 统一下单接口trade_type -- 公众号支付*/           JSAPI,
        /** 统一下单接口trade_type -- 原生扫码支付*/       NATIVE,
        /** 统一下单接口trade_type -- app支付*/             APP, 

        /** 刷卡支付支付接口 --刷卡支付*/                       MICROPAY;
    }
    
    /**
     * 支付交易状态 
     */
    public static enum TradeState {
        /** 支付成功 */                                                 SUCCESS,
        /** 转入退款 */                                                REFUND,
        /** 未支付 */                                                  NOTPAY,
        /** 已关闭 */                                                  CLOSED,
        /** 已撤销 */                                                  REVOKED,
        /** 用户支付中 */                                            USERPAYING,
        /** 未支付(确认支付超时) */                             NOPAY,
        /** 支付失败(其他原因，如银行返回失败) */        PAYERROR;
    }
    
    /**
     * 微信支付结果
     */
    public static enum WxPayResult {
        ERROR("error", "支付错误", null, null),
        SUCCESS("success", "支付成功", null, null),
        FAIL("fail", "支付失败", null, null),
        SYSTEMERROR("systemerror", "接口返回错误", "支付结果未知", "系统超时"),
        PARAM_ERROR("param_error", "参数错误", "支付确认失败", "请求参数未按指引进行填写"),
        ORDERPAID("orderpaid", "订单已支付", "支付确认失败", "订单号重复 "),
        NOAUTH("noauth", "商户无权限", "支付确认失败", "商户没有开通被扫支付权限 "),
        AUTHCODEEXPIRE("authcodeexpire", "二维码已过期，请用户在微信上刷新后再试", "支付确认失败", "用户的条码已经过期"),
        NOTENOUGH("notenough", "余额不足", "支付确认失败", "用户的零钱余额不足"),
        NOTSUPORTCARD("notsuportcard", "不支持卡类型", " 支付确认失败 ", "用户使用卡种不支持当前支付形式"),
        ORDERCLOSED ("orderclosed", "订单已关闭", "支付确认失败", "该订单已关"),
        ORDERREVERSED("orderreversed", "订单已撤销", "支付确认失败", "当前订单已经被撤销"),
        BANKERROR("bankerror", "银行系统异常", "支付结果未知", "银行端超时"),
        USERPAYING("userpaying", "用户支付中，需要输入密码", "支付结果未知", "该笔交易因为业务规则要求，需要用户输入支付密码。"),
        AUTH_CODE_ERROR("auth_code_error", "授权码参数错误", "支付确认失败", "请求参数未按指引进行填写"),
        AUTH_CODE_INVALID("auth_code_invalid", "授权码检验错误", "支付确认失败", "收银员扫描的不是微信支付的条码"),        
        XML_FORMAT_ERROR("xml_format_error", "XML格式错误", "支付确认失败", "XML格式错误"),
        REQUIRE_POST_METHOD("require_post_method", "请使用post方法", "支付确认失败", "未使用post传递参数"),
        SIGNERROR("signerror", "签名错误", "支付确认失败", "参数签名结果不正确"),
        LACK_PARAMS("lack_params", "缺少参数", "支付确认失败", "缺少必要的请求参数"),
        NOT_UTF8("not_utf8", "编码格式错误", "支付确认失败", "未使用指定编码格式"),
        BUYER_MISMATCH("buyer_mismatch", "支付帐号错误", "支付确认失败", "暂不支持同一笔订单更换支付方"),
        APPID_NOT_EXIST("appid_not_exist", "APPID不存在", "支付确认失败", "参数中缺少APPID"),
        MCHID_NOT_EXIST("mchid_not_exist", "MCHID不存在", "支付确认失败", "参数中缺少MCHID"),
        OUT_TRADE_NO_USED("out_trade_no_used", "商户订单号重复", "支付确认失败", "同一笔交易不能多次提交"),
        APPID_MCHID_NOT_MATCH("appid_mchid_not_match", "appid和mch_id不匹配", "支付确认失败", "appid和mch_id不匹配");
       
        private String code;
        private String desc;
        private String state;
        private String cause;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        private WxPayResult(String code, String desc, String state, String cause) {
            this.code = code;
            this.desc = desc;
            this.state = state;
            this.cause = cause;
        }

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
    
    /**微信订单查询错误码*/
    public static enum OrderQueryErr {
        /**订单不存在*/                      ORDERNOTEXIST,
        /**系统错误，建议重新查询*/    SYSTEMERROR;
    }
    
    /**微信订单关闭错误码*/
    public static enum OrderClosedErr {
        /** 订单已支付，不能发起关单，请当作已支付的正常交易 */
        ORDERPAID,
        /** 系统异常，请重新调用该API */
        SYSTEMERROR,
        /** 订单不存在，不需要关单，当作未提交的支付的订单 */
        ORDERNOTEXIST,
        /** 订单已关闭，无法重复关闭 */
        ORDERCLOSED,
        /** 参数签名结果不正确 */
        SIGNERROR,
        /** 未使用post传递参数 */
        REQUIRE_POST_METHOD,
        /** XML格式错误 */
        XML_FORMAT_ERROR;
    }
    
    /**发送模板消息结果码*/
    public static enum SendTempMsgErr {
        /**成功*/                      SUCCESS("0");
    	private String value;
    	
    	public String getValue() {
    		return value;
    	}
    	
    	private SendTempMsgErr(String value) {
    		this.value = value;
		}
    }
    
    /**
     * 二维码类型
     * @author mengzh
     *
     */
    public static enum QRCodeType {
    	/** 支付二维码*/ 				PAY(1),
    	/** 绑定支付通知二维码*/ 	BIND_PAY_NOTICE(2);
    	
    	private int value;
    	
    	public int getValue() {
    		return value;
    	}
    	
    	private QRCodeType(int value) {
    		this.value = value;
		}
    }
    
    /**
     * 公众号授权类型
     * 
     * @author mengzh
     *
     */
    public static enum GrantType {
        /** 获取网页授权access_token*/         AUTHORIZATION_CODE("authorization_code"),
        /** 刷新网页授权access_token*/        REFRESH_TOKEN("refresh_token"),
        /** 获取基础支持的access_token*/     client_credential("CLIENT_CREDENTIAL");
        
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private GrantType(String value) {
            this.value = value;
        }
    }

    /**
     * 支付客户端
     * 
     * @author 孟郑宏
     */
    public static enum PayClientType {
        /** 1 微信APP（浏览器）*/                                  APP_WEIXIN("1"),
        /** 2 支付宝APP */                                             APP_ALI("2"),
        /** 3 百度APP */                                                APP_BAIDU("3"),        
        /** 4 系统业务APP */                                          APP_SELF("4"),
        /** 5 未知（不同浏览器以及其他扫码客户端）*/       UN_KNOWN("5");
        
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private PayClientType(String value) {
            this.value = value;
        }
    }
    
    /**
     * 支付类型
     */
    public static enum PayType {
        /** 1：微信-刷卡支付*/                                  WEIXIN_MICROPAY(1),
        /** 2：微信-公众号支付 */								 WEIXIN_JSAPI(2),
        /** 3：微信-扫码支付 */								 WEIXIN_NATIVE(3),
        /** 4：微信-微信买单 */								 WEIXIN_PAY(4),
        
        /** 6：支付宝-当面付 */                                  ALI_FACE(6),
        /** 7：支付宝-扫码付 */								  ALI_SCAN(7),
        /** 8：支付宝-手机网站支付 */						  ALI_H5(8);
        
        private int value;
        
        
        public int getValue() {
            return value;
        }
        
        private PayType(int value) {
            this.value = value;
        }
        
    }
    
    /**
     * 交易状态
     */
    public static enum TradeStatus {
        /** 0：交易中 */        TRADEING(0),
        /** 1：交易成功 */     TRADE_SUCCESS(1),
        /** 2：交易失败 */     TRADE_FAIL(2),
        /** 3：交易撤销 */     TRADE_REVERSED(3),
        /** 4：交易关闭 */     TRADE_CLOSED(4),
        /** 5：交易待关闭 */     TRADE_TO_BE_CLOSED(5);
        
        private int value;

        public int getValue() {
            return value;
        }

        private TradeStatus(int value) {
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
