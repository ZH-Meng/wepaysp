/*
 * ManageLoginFilter.java
 * 创建者：杨帆
 * 创建日期：2015年6月7日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.zbsp.wepaysp.common.security.DigestHelper;

/**
 * 登录过滤器.
 * 
 * @author 杨帆
 */
public class ManageLoginFilter extends UsernamePasswordAuthenticationFilter {

    private String verifyCodeParameter = "verifycode";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String verifyCode = obtainVerifyCode(request);

        HttpSession session = request.getSession(true);

        String securityCode = (String) session.getAttribute("securityCode");

        if (securityCode == null || !securityCode.equalsIgnoreCase(verifyCode)) {
            throw new AuthenticationServiceException("验证码输入错误!");
        }

        ManageAuthenticationToken authRequest = new ManageAuthenticationToken(username, DigestHelper.sha512Hex(password), request.getRemoteAddr());
        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainVerifyCode(HttpServletRequest request) {
        return request.getParameter(verifyCodeParameter);
    }

    public String getVerifyCodeParameter() {
        return verifyCodeParameter;
    }

    public void setVerifyCodeParameter(String verifyCodeParameter) {
        this.verifyCodeParameter = verifyCodeParameter;
    }
}
