/*
 * ManagePermissionTag.java
 * 创建者：杨帆
 * 创建日期：2015年6月12日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security.taglib.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.SpringContextUtil;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.api.service.manage.SysFunctionService;
import com.zbsp.wepaysp.api.service.manage.SysPermissionService;

/**
 * 权限控制验证标签.
 * 
 * @author 杨帆
 */
public class ManagePermissionTag
    extends TagSupport {

    private static final long serialVersionUID = 1044236668214598715L;

    private Log logger = LogFactory.getLog(getClass());

    private String validateUrl;

    private boolean validate;

    @Override
    public int doStartTag()
        throws JspException {
        validate = false;
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<GrantedAuthority> authorites = manageUser.getAuthorities();

        List<SysRole> functionRoleList = null;
        try {
            SysFunctionService sysFunctionService = (SysFunctionService) SpringContextUtil.getBean("sysFunctionService");
            SysFunction sysFunction = sysFunctionService.doJoinTransIsSysFunction(validateUrl);

            if (sysFunction != null) {
                Map<String, Object> queryMap = new HashMap<String, Object>();
                queryMap.put("functionUrl", validateUrl);
                queryMap.put("roleState", SysRole.State.normal.getValue());
                SysPermissionService sysPermissionService = (SysPermissionService) SpringContextUtil.getBean("sysPermissionService");
                functionRoleList = sysPermissionService.doJoinTransQueryFunctionRoleList(queryMap);

            }
            if (sysFunction != null) {
                if (functionRoleList != null && !functionRoleList.isEmpty()) {
                    for (SysRole sysRole : functionRoleList) {
                        String sysRoleOid = sysRole.getIwoid();

                        for (GrantedAuthority grantedAuthority : authorites) {
                            if (grantedAuthority.getAuthority().equals(sysRoleOid)) {
                                validate = true;
                                return EVAL_PAGE;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询功能项权限错误:" + e.getMessage());
        }

        return EVAL_PAGE;
    }

    public String getValidateUrl() {
        return validateUrl;
    }

    public void setValidateUrl(String validateUrl) {
        this.validateUrl = validateUrl;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
