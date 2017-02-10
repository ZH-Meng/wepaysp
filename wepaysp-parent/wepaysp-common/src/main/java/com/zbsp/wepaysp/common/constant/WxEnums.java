package com.zbsp.wepaysp.common.constant;

/**
 * 微信支付相关枚举定义类
 * 
 * @author 孟郑宏
 */
public class WxEnums {
    
    /**
     * 微信支付-通信标识 
     */
    public static enum ReturnCode {
        SUCCESS, FAIL;
    }
    
    /**
     * 微信支付-交易标识 
     */
    public static enum ResultCode {
        SUCCESS, FAIL;
    }
    
    /**
     * 微信支付-交易类型
     */
    public static enum TradeType {
        /** 统一下单接口trade_type -- 公众号支付*/           JSAPI,
        /** 统一下单接口trade_type -- 原生扫码支付*/       NATIVE,
        /** 统一下单接口trade_type -- app支付*/             APP, 

        /** 刷卡支付支付接口 --刷卡支付*/                       MICROPAY;
    }
    
    /**
     * 微信支付-交易状态 
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
     * 微信支付-支付结果
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

        public String getValue() {
            return code;
        }
        
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
     * 微信支付-发送模板消息结果码
     * 
     * @author 孟郑宏
     */
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
     * 微信支付-订单查询错误码
     * 
     * @author 孟郑宏
     */
    public static enum OrderQueryErr {
        /**订单不存在*/                      ORDERNOTEXIST,
        /**系统错误，建议重新查询*/    SYSTEMERROR;
    }
    
    /**
     * 微信支付-订单关闭错误码
     * 
     * @author 孟郑宏
     */
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
        XML_FORMAT_ERROR,
        /** 订单状态错误，官方文档没有次错误码，程序调试或运行阶段好像出现过*/
        TRADE_STATE_ERROR,
        /** 用户支付中 支付锁定，扣款和撤销间隔要在10s以上，官方文档没有次错误码，程序调试或运行阶段出现过*/
        USERPAYING;
    }
    
}
