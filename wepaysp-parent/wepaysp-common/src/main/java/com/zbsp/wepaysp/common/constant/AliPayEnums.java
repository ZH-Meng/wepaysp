package com.zbsp.wepaysp.common.constant;


/**
 * 支付宝相关枚举定义类
 * 
 * @author 孟郑宏
 */
public class AliPayEnums {
    
    /**
     * 支付宝支付网关响应
     */
    public static enum GateWayResponse {
        
        /** 10000 接口调用成功 */     SUCCESS("10000", "接口调用成功"),
        /** 10003 用户支付中*/         ORDER_SUCCESS_PAY_INPROCESS("10003", "用户支付中"),
        /** 20000 服务不可用 */        UNKNOW("20000", "服务不可用"),
        /** 20001 授权权限不足 */     AUTH_INVALID("20001", "授权权限不足"),
        /** 40001 缺少必选参数 */     PARAM_MISSING("40001", "缺少必选参数"),
        /** 40002 非法的参数 */        PARAM_INVALID("40002", "非法的参数"),
        /** 40004 业务处理失败 */     FAIL("40004", "业务处理失败"),
        /** 40006 权限不足 */           PERMISSIONS_INSUFFICIENT("40006", "权限不足");
        
        private String code;
        private String desc;
        
        private GateWayResponse(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }
        
    }
    
    /**支付宝支付结果*/
    public static enum AliPayResult {
        SUCCESS("SUCCESS", "支付成功", null),
        ERROR("ERROR", "支付错误", "商户系统支付错误，请查看日志"),
        FAIL("FAIL", "支付失败", "商户系统支付失败，请查看日志"),
        
        SYSTEM_ERROR("SYSTEM_ERROR","接口返回错误", "请立即调用查询订单API，查询当前订单的状态，并根据订单状态决定下一步的操作，如果多次调用依然报此错误码，请联系支付宝客服."),
        INVALID_PARAMETER("INVALID_PARAMETER", "参数无效", "检查请求参数，修改后重新发起请求"),
        ACCESS_FORBIDDEN("ACCESS_FORBIDDEN", "无权限使用接口", "未签约条码支付或者合同已到期"),
        EXIST_FORBIDDEN_WORD("EXIST_FORBIDDEN_WORD", "订单信息中包含违禁词", "修改订单信息后，重新发起请求"),
        PARTNER_ERROR("PARTNER_ERROR", "应用APP_ID填写错误", "联系支付宝小二，确认APP_ID的状态"),
        TOTAL_FEE_EXCEED("TOTAL_FEE_EXCEED", "订单总金额超过限额", "修改订单金额再发起请求"),
        PAYMENT_AUTH_CODE_INVALID("PAYMENT_AUTH_CODE_INVALID", "支付授权码无效","用户刷新条码后，重新扫码发起请求"),
        CONTEXT_INCONSISTENT("CONTEXT_INCONSISTENT", "交易信息被篡改", "更换商家订单号后，重新发起请求"),
        TRADE_HAS_SUCCESS("TRADE_HAS_SUCCESS", "交易已被支付", "确认该笔交易信息是否为当前买家的，如果是则认为交易付款成功，如果不是则更换商家订单号后，重新发起请求"),
        TRADE_HAS_CLOSE("TRADE_HAS_CLOSE", "交易已经关闭", "更换商家订单号后，重新发起请求"),
        BUYER_BALANCE_NOT_ENOUGH("BUYER_BALANCE_NOT_ENOUGH", "买家余额不足", "买家绑定新的银行卡或者支付宝余额有钱后再发起支付"),
        BUYER_BANKCARD_BALANCE_NOT_ENOUGH("BUYER_BANKCARD_BALANCE_NOT_ENOUGH", "用户银行卡余额不足", "建议买家更换支付宝进行支付或者更换其它付款方式"),
        ERROR_BALANCE_PAYMENT_DISABLE("ERROR_BALANCE_PAYMENT_DISABLE", "余额支付功能关闭", "用户打开余额支付开关后，再重新进行支付"),
        BUYER_SELLER_EQUAL("BUYER_SELLER_EQUAL", "买卖家不能相同", "更换买家重新付款"),
        TRADE_BUYER_NOT_MATCH("TRADE_BUYER_NOT_MATCH", "交易买家不匹配", "更换商家订单号后，重新发起请求"),
        BUYER_ENABLE_STATUS_FORBID("BUYER_ENABLE_STATUS_FORBID", "买家状态非法", "用户联系支付宝小二，确认买家状态为什么非法"),
        PULL_MOBILE_CASHIER_FAIL("PULL_MOBILE_CASHIER_FAIL", "唤起移动收银台失败", "用户刷新条码后，重新扫码发起请求"),
        MOBILE_PAYMENT_SWITCH_OFF("MOBILE_PAYMENT_SWITCH_OFF", "用户的无线支付开关关闭", "用户在PC上打开无线支付开关后，再重新发起支付"),
        PAYMENT_FAIL("PAYMENT_FAIL", "支付失败", "用户刷新条码后，重新发起请求，如果重试一次后仍未成功，更换其它方式付款"),
        BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR("BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR", " 买家付款日限额超限", "更换买家进行支付"),
        BEYOND_PAY_RESTRICTION("BEYOND_PAY_RESTRICTION", "商户收款额度超限", "联系支付宝小二提高限额"),
        BEYOND_PER_RECEIPT_RESTRICTION("BEYOND_PER_RECEIPT_RESTRICTION", "商户收款金额超过月限额", "联系支付宝小二提高限额"),
        BUYER_PAYMENT_AMOUNT_MONTH_LIMIT_ERROR("BUYER_PAYMENT_AMOUNT_MONTH_LIMIT_ERROR", "买家付款月额度超限", "让买家更换账号后，重新付款或者更换其它付款方式"),
        SELLER_BEEN_BLOCKED("SELLER_BEEN_BLOCKED", "商家账号被冻结", "联系支付宝小二，解冻账号"),
        ERROR_BUYER_CERTIFY_LEVEL_LIMIT("ERROR_BUYER_CERTIFY_LEVEL_LIMIT", "买家未通过人行认证", "让用户联系支付宝小二并更换其它付款方式"),
        PAYMENT_REQUEST_HAS_RISK("PAYMENT_REQUEST_HAS_RISK", "支付有风险", "更换其它付款方式"),
        NO_PAYMENT_INSTRUMENTS_AVAILABLE("NO_PAYMENT_INSTRUMENTS_AVAILABLE", "没用可用的支付工具", "更换其它付款方式"),
        USER_FACE_PAYMENT_SWITCH_OFF("USER_FACE_PAYMENT_SWITCH_OFF", "用户当面付付款开关关闭", "让用户在手机上打开当面付付款开关"),
        INVALID_STORE_ID("INVALID_STORE_ID", "商户门店编号无效", "检查传入的门店编号是否有效");
        
        private String code;
        private String desc;
        private String todo;
        
        private AliPayResult(String code, String desc, String todo) {
            this.code = code;
            this.desc = desc;
            this.todo = todo;
        }
        
        public String getValue() {
        	return code;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }

        public String getTodo() {
            return todo;
        }
        
    }
    
    /**
     * 支付宝支付交易状态
     */
    public static enum TradeState4AliPay {
        /** 交易创建，等待买家付款 */                                             WAIT_BUYER_PAY,
        /** 未付款交易超时关闭，或支付完成后全额退款 */                  TRADE_CLOSED,
        /** 交易支付成功 */                                                            TRADE_SUCCESS,
        /** 交易结束，不可退款 */                                                   TRADE_FINISHED;
    }
    
    /**支付宝异步通知，系统处理返回结果*/
    public static enum AsynNotifyHandleResult {
        SUCCESS, FAILURE;
    }
    
    /**支付宝撤销交易业务结果 */
    public static enum TradeCancelResult{
        SYSTEM_ERROR("AQC.SYSTEM_ERROR", "系统错误", "请使用相同的参数再次调用"),
        INVALID_PARAMETER("ACQ.INVALID_PARAMETER", "参数无效", "请求参数有错，重新检查请求后，再调用撤销"),
        SELLER_BALANCE_NOT_ENOUGH("ACQ.SELLER_BALANCE_NOT_ENOUGH", "商户的支付宝账户中无足够的资金进行撤销", "商户支付宝账户充值后重新发起撤销即可"),
        REASON_TRADE_BEEN_FREEZEN("ACQ.REASON_TRADE_BEEN_FREEZEN", "当前交易被冻结，不允许进行撤销", "联系支付宝小二，确认该笔交易的具体情况"),
        TRADE_CANCEL_TIME_OUT("ACQ.TRADE_CANCEL_TIME_OUT", "交易超过了撤销的时间范围", "官方未说明，经测试关闭订单可以");

        private String code;
        private String desc;
        private String todo;
        
        private TradeCancelResult(String code, String desc, String todo) {
            this.code = code;
            this.desc = desc;
            this.todo = todo;
        }
        
        public String getValue() {
            return code;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }

        public String getTodo() {
            return todo;
        }
    }
    
    /**支付宝撤销触发的交易动作 */
    public static enum TradeCancelAction {
        close,// 关闭交易，无退款  
        refund;// 产生了退款
    }
    
}
