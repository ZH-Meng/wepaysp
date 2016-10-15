/*
 * ManageLoginInterceptor.java
 * 创建者：杨帆
 * 创建日期：2016年5月24日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 登录状态拦截器
 * 
 * @author 杨帆
 */
public class ManageLoginInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -3756813903883615047L;
    
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> session = ActionContext.getContext().getSession();
        
        if (null == session || StringUtils.isBlank((String) session.get("loginState")) || "0".equals(session.get("loginState"))) {
        	session.clear();
        	return getReturnResult(request);
        }
        
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (manageUser == null) {
            return getReturnResult(request);
        }
        return invocation.invoke();
    }
    
    private String getReturnResult(HttpServletRequest request) {
        HttpSession session = request.getSession();
        //"登录超时或还没有登录，请重新登录"
        SessionAuthenticationException sessionAuthenticationException = new SessionAuthenticationException("登录超时或还没有登录，请重新登录");
        
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", sessionAuthenticationException);
        
        return "login";
    }
}
