package com.zbsp.wepaysp.common.config;

/**
 * 数据库自定义序列 主键（序列名）
 * 
 * @author 孟郑宏
 */
public class SysSequenceCode {

    /** 服务商序列名 -> partner表 partnerId */
    public static String PARTNER = "sequence_partner";

    /** 商户序列名 -> dealer表 dealerId */
    public static String DEALER = "sequence_dealer";

    /** 门店序列名 -> store表 storeId */
    public static String STORE = "sequence_store";

    /** 服务商员工（业务员）序列名 -> partner_employee表 partnerEmployeeId */
    public static String PARTNER_EMPLOYEE = "sequence_partner_employee";

    /** 商户员工（收银员） 序列名 -> dealer_employee表 dealerEmployeeId */
    public static String DEALER_EMPLOYEE = "sequence_dealer_employee";

}
