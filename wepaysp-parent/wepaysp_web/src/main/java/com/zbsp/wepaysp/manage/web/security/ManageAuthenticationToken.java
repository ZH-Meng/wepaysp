/*
 * ManageAuthenticationToken.java
 * 创建者：杨帆
 * 创建日期：2016年5月6日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 登录认证会话
 * 
 * @author 杨帆
 */
public class ManageAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -1536095561538442270L;
    
    private String loginIp;
    
    public ManageAuthenticationToken(String principal, String credentials, String loginIp) {
        super(principal, credentials);
        this.loginIp = loginIp;
    }
    
    public String getLoginIp() {
        return loginIp;
    }
    
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

}
