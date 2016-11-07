/*
 * ManageAuthenticationProvider.java
 * 创建者：杨帆
 * 创建日期：2015年6月7日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.manage.SysAuthorityService;
import com.zbsp.wepaysp.service.manage.SysUserService;

/**
 * 用户认证（登录）.
 * 
 * @author 杨帆
 */
public class ManageAuthenticationProvider
    extends AbstractUserDetailsAuthenticationProvider {

    private SysUserService sysUserService;
    private SysAuthorityService sysAuthorityService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("用户密码为空:" + userDetails);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        UserDetails loadedUser = null;

        ManageAuthenticationToken manageToken = (ManageAuthenticationToken) authentication;

        String password = (String) manageToken.getCredentials();
        String loginIp = (String) manageToken.getLoginIp();

        try {

            Map<String, Object> resultMap = sysUserService.doTransUserLogin(username, password, loginIp);

            SysUser sysUser = (SysUser) resultMap.get("sysUser");
            // SysUserLoginToken loginToken = (SysUserLoginToken) resultMap.get("loginToken");
            List<SysRole> userRoleList = null;

            if (sysUser != null) {
                Map<String, Object> queryMap = new HashMap<String, Object>();
                queryMap.put("sysUserOid", sysUser.getIwoid());
                queryMap.put("roleState", SysRole.State.normal.getValue());

                userRoleList = sysAuthorityService.doJoinTransQueryUserRoleList(queryMap);

                if (null == userRoleList || 0 == userRoleList.size()) {
                    throw new IllegalStateException("该用户所属角色状态异常，不能进行该操作！");
                }
            }
            List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
            String roleIndex = "";
            for (SysRole sysRole : userRoleList) {
                GrantedAuthorityImpl authority = new GrantedAuthorityImpl(sysRole.getIwoid());
                auths.add(authority);
                roleIndex = sysRole.getRoleIndex();
            }

            loadedUser = new ManageUser(sysUser.getIwoid(), sysUser.getUserId(), sysUser.getUserName(), sysUser.getLoginPwd(), null, sysUser.getUserLevel(),
                sysUser.getDataPermisionType(), sysUser.getPartner(), sysUser.getDealer(), sysUser.getPartnerEmployee(), sysUser.getDealerEmployee(), sysUser.getLastLoginIp(), sysUser.getLastLoginTime(), roleIndex, true, true, true, true, auths);

        } catch (IllegalStateException e) {
            throw new AuthenticationServiceException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new AuthenticationServiceException("用户名或密码错误！");
        } catch (AlreadyExistsException e) {
            throw new AuthenticationServiceException(e.getMessage());
        } catch (Exception e) {
            logger.error("用户登录验证错误:" + e.getMessage());
            throw new AuthenticationServiceException(e.getMessage());
        }

        return loadedUser;
    }

    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
    
    public void setSysAuthorityService(SysAuthorityService sysAuthorityService) {
        this.sysAuthorityService = sysAuthorityService;
    }
    
}
