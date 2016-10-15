/*
 * ManageAccessDecisionManager.java
 * 创建者：杨帆
 * 创建日期：2015年6月7日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 访问决策器.
 * 
 * @author 杨帆
 */
public class ManageAccessDecisionManager implements AccessDecisionManager {

    //private Log logger = LogFactory.getLog(getClass());

    /**
     * 是否保护没有显示定义的资源.
     */
    private boolean allowIfAllAbstainDecisions = false;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, 
            InsufficientAuthenticationException {
        if (configAttributes == null) {// 功能项未在库中定义
            if (!this.isAllowIfAllAbstainDecisions()) {
                throw new AccessDeniedException("访问被拒绝");
            } else {
                return;
            }
        }

        if (configAttributes.isEmpty()) {// 功能项已在库中定义，但未授权给任何角色
            throw new AccessDeniedException("访问被拒绝");
        }

        /*Map<String, Object> paramMap = new HashMap<String, Object>();
        ManageUser manageUser = (ManageUser) authentication.getPrincipal();
        paramMap.put("sysUserOid", manageUser.getIwoid());

        GeneralMessageBody requestMsgBody = new GeneralMessageBody();
        requestMsgBody.setProcessKey("refreshLoginToken");
        requestMsgBody.setProcessData(paramMap);

        GeneralMessageBody responseMsgBody = (GeneralMessageBody) WebAgent.getInstance().process("generalSecurityProcessor", 
            requestMsgBody, BusinessTime.NORMAL);

        if (responseMsgBody == null) {
            logger.warn("用户刷新登录令牌处理超时");
        } else {
            if (!"SUCC".equals(responseMsgBody.getProcessKey())) {
                logger.error("用户刷新登录令牌错误:" + responseMsgBody.getProcessMessage());
            }
        }*/

        Iterator<ConfigAttribute> ite = configAttributes.iterator();

        while (ite.hasNext()) {
            ConfigAttribute ca = ite.next();
            String needRole = ((SecurityConfig) ca).getAttribute();

            // ga 为用户所被赋予的权限， needRole为访问相应的资源应该具有的权限。
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.trim().equals(ga.getAuthority().trim())) {
                    return;
                }
            }
        }

        throw new AccessDeniedException("访问被拒绝");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    public boolean isAllowIfAllAbstainDecisions() {
        return allowIfAllAbstainDecisions;
    }

    public void setAllowIfAllAbstainDecisions(boolean allowIfAllAbstainDecisions) {
        this.allowIfAllAbstainDecisions = allowIfAllAbstainDecisions;
    }

}
