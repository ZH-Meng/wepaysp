/*
 * SysAuthorityServiceImpl.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysAuthorityService;

/**
 * @author 杨帆
 */
public class SysAuthorityServiceImpl extends BaseService implements SysAuthorityService {

    @SuppressWarnings("unchecked")
    @Override
    public List<SysRole> doJoinTransQueryUserRoleList(Map<String, Object> paramMap) {
        String sysUserOid = MapUtils.getString(paramMap, "sysUserOid");
        Integer roleState = MapUtils.getInteger(paramMap, "roleState");
        
        String sql = "select a.sysRole from SysAuthority a where 1=1 ";
        
        Map<String, Object> queryMap = new HashMap<String, Object>();
        
        if (StringUtils.isNotBlank(sysUserOid)) {
            sql += " and a.sysUser.iwoid = :SYSUSEROID ";
            queryMap.put("SYSUSEROID", sysUserOid);
        }
        
        if (roleState != null) {
            sql += " and a.sysRole.state = :ROLESTATE ";
            queryMap.put("ROLESTATE", roleState);
        }
        
        return (List<SysRole>) commonDAO.findObjectList(sql, queryMap, false);
    }

}
