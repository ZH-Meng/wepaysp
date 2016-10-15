/*
 * ManageLogoutHandler.java
 * 创建者：杨帆
 * 创建日期：2015年7月2日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.zbsp.wepaysp.service.manage.SysUserService;

/**
 * 用户退出处理器
 * 
 * @author 杨帆
 */
public class ManageLogoutHandler
    implements LogoutHandler {

    private Log logger = LogFactory.getLog(getClass());

    private SysUserService sysUserService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null) {
            ManageUser manageUser = (ManageUser) authentication.getPrincipal();

            try {
                sysUserService.doTransUserLogout(manageUser.getIwoid());
                HttpSession session = request.getSession();
                session.setAttribute("loginState", "0");
                logger.info("用户:" + manageUser.getUserId() + "退出");
            } catch (Exception e) {
                logger.error("用户退出错误:" + e.getMessage());
            }
        }
    }

    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

}
