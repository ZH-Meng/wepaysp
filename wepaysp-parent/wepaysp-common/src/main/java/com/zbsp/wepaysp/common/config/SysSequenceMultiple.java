package com.zbsp.wepaysp.common.config;

/**
 * 系统自定义序列 主键（序列名）的位数（1的整数倍数）
 * 
 * @author 孟郑宏
 */
public class SysSequenceMultiple {

    /** 服务商序列名中1的整数倍数-> partner表 partnerId */
    public static int PARTNER = 100;

    /** 商户序列名中1的整数倍数-> dealer表 dealerId */
    public static int DEALER = 1000;

    /** 门店序列名中1的整数倍数-> store表 storeId */
    public static int STORE = 1000;

    /** 服务商员工（业务员）序列名中1的整数倍数 -> partner_employee表 partnerEmployeeId */
    public static int PARTNER_EMPLOYEE = 10000;

    /** 商户员工（收银员） 序列名中1的整数倍数 -> dealer_employee表 dealerEmployeeId */
    public static int DEALER_EMPLOYEE = 100000;
    
    /** 支付订单ID 序列名中1的整数倍数 -> weixin_pay_details表 outTradeNo */
    public static long PAY_ORDER = 10000000000L;
    
    /** 退款订单ID 序列名中1的整数倍数  -> weixin_refund_details表 outTradeNo */
    public static long REFUND_ORDER = 10000000000L;

}
