package com.zbsp.wepaysp.mobile.model.vo;

/**蚂蚁开放平台授权回调VO*/
public class AliPayCallBackVO {

    private String app_id;
    private String source;
    
    /**商户应用授权回调请求参数，同意授权后返回*/
    private String app_auth_code;// 支付宝返回
    private String dealerOid;// 授权回调地址指定的商户识别码
    
    /**用户信息授权回调请求参数，同意授权后返回*/
    private String auth_code;
    private String scope;
    
    public String getApp_id() {
        return app_id;
    }
    
    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getApp_auth_code() {
        return app_auth_code;
    }
    
    public void setApp_auth_code(String app_auth_code) {
        this.app_auth_code = app_auth_code;
    }
    
    public String getAuth_code() {
        return auth_code;
    }
    
    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public String getDealerOid() {
        return dealerOid;
    }
    
    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }
    
}
