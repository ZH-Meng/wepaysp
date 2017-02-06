package com.zbsp.wepaysp.vo.alipay;

import java.util.Date;

/**
 * 第三方应用授权记录VO
 */
public class AlipayAppAuthDetailsVO {

    private String appId;
    private String appAuthToken;
    private String appRefreshToken;
    private Integer expiresIn;
    private Integer reExpiresIn;
    private String authUserId;
    /** 授权商户的AppId（如果有服务窗，则为服务窗的AppId） */
    private String authAppId;
    private String authMethods;
    private Date authStart;
    private Date authEnd;
    private String status;

    /**系统商户名称，授权成功后展示*/
    private String dealerName;
    /**系统保存蚂蚁平台应用名称，授权成功后展示*/
    private String appName;
    /**支付宝商户授权应用回调时回传*/
    private String dealerOid;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppAuthToken() {
        return appAuthToken;
    }

    public void setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
    }

    public String getAppRefreshToken() {
        return appRefreshToken;
    }

    public void setAppRefreshToken(String appRefreshToken) {
        this.appRefreshToken = appRefreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Integer getReExpiresIn() {
        return reExpiresIn;
    }

    public void setReExpiresIn(Integer reExpiresIn) {
        this.reExpiresIn = reExpiresIn;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public String getAuthAppId() {
        return authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId;
    }

    public String getAuthMethods() {
        return authMethods;
    }

    public void setAuthMethods(String authMethods) {
        this.authMethods = authMethods;
    }

    public Date getAuthStart() {
        return authStart;
    }

    public void setAuthStart(Date authStart) {
        this.authStart = authStart;
    }

    public Date getAuthEnd() {
        return authEnd;
    }

    public void setAuthEnd(Date authEnd) {
        this.authEnd = authEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDealerOid() {
        return dealerOid;
    }
    
    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }
    
}
