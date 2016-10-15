/*
 * ManageSecurityMetadataSource.java
 * 创建者：杨帆
 * 创建日期：2015年6月7日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.service.manage.SysFunctionService;
import com.zbsp.wepaysp.service.manage.SysPermissionService;

/**
 * 资源权限定义数据源.
 * 
 * @author 杨帆
 */
public class ManageSecurityMetadataSource
    implements FilterInvocationSecurityMetadataSource {

    private Log logger = LogFactory.getLog(getClass());

    private SysFunctionService sysFunctionService;
    private SysPermissionService sysPermissionService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object)
        throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String url = filterInvocation.getRequestUrl();
        int firstQuestionMarkIndex = url.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            url = url.substring(0, firstQuestionMarkIndex);// 去掉URL中的参数
        }

        List<ConfigAttribute> result = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("url", url);

        SysFunction sysFunction = sysFunctionService.doJoinTransIsSysFunction(url);
        List<SysRole> functionRoleList = null;
        if (sysFunction != null) {
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("functionUrl", url);
            queryMap.put("roleState", SysRole.State.normal.getValue());

            functionRoleList = sysPermissionService.doJoinTransQueryFunctionRoleList(queryMap);

        }

        if (sysFunction != null) {
            HttpSession session = filterInvocation.getHttpRequest().getSession();
            // 设置当前访问的功能项对应的日志记录功能项Oid
            session.setAttribute("currentLogFunctionOid", sysFunction.getLogFunctionOid());

            // result为null时功能项未在库中定义，result为空list时，功能项已定义但未授权给任何角色
            result = new ArrayList<ConfigAttribute>();

            if (functionRoleList != null && !functionRoleList.isEmpty()) {
                for (SysRole sysRole : functionRoleList) {
                    ConfigAttribute conf = new SecurityConfig(sysRole.getIwoid());
                    result.add(conf);
                }
            }
        }

        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    public void setSysFunctionService(SysFunctionService sysFunctionService) {
        this.sysFunctionService = sysFunctionService;
    }

    public void setSysPermissionService(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

}
