package com.zbsp.wepaysp.common.config;

/**
 * 系统内置的应用角色编码，便于创建服务商、商户等用户时为其关联固定的角色
 * 
 * @author mengzh
 */
public class SysNestedRoleCode {

    /** 服务商 */
    public static String PARTNER1 = "partner1";

    /** 一级代理商 */
    public static String PARTNER2 = "partner2";

    /** 二级代理商 */
    public static String PARTNER3 = "partner3";

    /** 业务员 partnerEmployee */
    public static String SALESMAN = "salesman";

    /** 商户 */
    public static String DEALER = "dealer";

    /** 收银员 dealerEmployee */
    public static String CASHIER = "cashier";
    
    /** 分店店长 dealerEmployee */
    public static String STORE_MANAGER = "storeManager";

}
