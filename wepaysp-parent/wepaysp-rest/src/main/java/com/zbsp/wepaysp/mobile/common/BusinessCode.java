package com.zbsp.wepaysp.mobile.common;

/**
 * 通讯业务识别码
 */
public enum BusinessCode {
    /** 客户端版本检查  */                       checkVersion("ZBSP_APP_CHECK_VERSION"),    
    /** 重置登录密码 */                         resetUserPwd("ZBSP_APP_RESET_USERPWD"),    
    /** 账户登录 */                            userLogin("ZBSP_APP_USER_LOGIN"),
    /** 获取账户信息 */                         getUserInfo("ZBSP_APP_GET_USERINFO"),
    /** 获取首页信息 */                         getIndex("ZBSP_APP_GET_INDEX"),
    /** 网络环境检查 */                         checkNet("ZBSP_APP_CHECK_NET"),
    
    /** 查询微信支付明细 */                         queryBetDetail("ZBSP_APP_QUERY_WX_PAY_DETAIL");
    
    private String value;

    public String getValue() {
        return value;
    }

    private BusinessCode(String value) {
        this.value = value;
    }
    
}
