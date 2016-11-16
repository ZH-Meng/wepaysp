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
    
}
