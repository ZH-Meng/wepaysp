/*
 * LoginFilter.java
 * 创建者：杨帆
 * 创建日期：2014年6月4日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zbsp.wepaysp.common.bean.MessageBean;



/**
 * 页面访问过滤器
 * 
 * @author 杨帆
 */
public class LoginFilter implements Filter {

    private Log logger = LogFactory.getLog(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        MessageBean messageBean = new MessageBean();
        messageBean.setAlertMessage("登录超时或您还没有登录，请重新登录。");
        if (httpRequest.getRequestURI().endsWith(".jsp")) {
            
            if (session == null || session.getAttribute("loginState") == null) {
                httpRequest.setAttribute("messageBean", messageBean);
                httpRequest.getRequestDispatcher("/index.jsp").forward(httpRequest, httpResponse);
                return;
            }                
        } else if (httpRequest.getRequestURI().indexOf("/nostate/") != -1) {//所有不需要登录拦截的请求
            
        } else {
        	if (session == null || session.getAttribute("loginState") == null) {
	        	String requestType = httpRequest.getHeader("X-Requested-With");
	
	        	if(StringUtils.isNotBlank(requestType) && requestType.equalsIgnoreCase("XMLHttpRequest")){
	        		httpResponse.setHeader("sessionstatus", "timeout");  
	        		httpResponse.sendError(518, "session timeout."); 
	        		return;
	        	}
        	}
        }

        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }

}
