/*
 * SysPermissionServiceImpl.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.manage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysPermission;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.manage.SysPermissionService;

/**
 * @author 杨帆
 */
public class SysPermissionServiceImpl extends BaseService implements SysPermissionService {

    private SysLogService sysLogService;

    @Override
    public List<SysRole> doJoinTransQueryFunctionRoleList(Map<String, Object> paramMap) {
        String functionUrl = MapUtils.getString(paramMap, "functionUrl");
        Integer roleState = MapUtils.getInteger(paramMap, "roleState");

        String sql = "select p.sysRole from SysPermission p where 1= 1";

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(functionUrl)) {
            sql += " and p.sysFunction.url = :URL";
            queryMap.put("URL", functionUrl);
        }

        if (roleState != null) {
            sql += " and p.sysRole.state = :ROLESTATE";
            queryMap.put("ROLESTATE", roleState);
        }

        return commonDAO.findObjectList(sql, queryMap, false);
    }

    @Override
    public List<SysFunction> doJoinTransQueryRoleFunctionList(Map<String, Object> paramMap) {
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer functionState = MapUtils.getInteger(paramMap, "functionState");
        Integer functionType = MapUtils.getInteger(paramMap, "functionType");

        StringBuilder sql = new StringBuilder("select distinct p.sysFunction, p.sysFunction.displayOrder from SysPermission p where 1=1");

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and p.sysRole.iwoid = :ROLEOID");
            queryMap.put("ROLEOID", roleOid);
        }

        if (functionState != null) {
            sql.append(" and p.sysFunction.state = :FUNCTIONSTATE");
            queryMap.put("FUNCTIONSTATE", functionState);
        }

        if (functionType != null) {
            sql.append(" and p.sysFunction.functionType = :FUNCTIONTYPE");
            queryMap.put("FUNCTIONTYPE", functionType);
        }

        sql.append(" order by p.sysFunction.displayOrder asc");

        List<Object> list = (List<Object>) commonDAO.findObjectList(sql.toString(), queryMap, false);
        List<SysFunction> sysFunctions = new ArrayList<SysFunction>();

        if (list != null && !list.isEmpty()) {
            for (Object object : list) {
                Object[] obj = (Object[]) object;
                sysFunctions.add((SysFunction) obj[0]);
            }
        }

        return sysFunctions;
    }

    @Override
    public List<SysFunction> doJoinTransQueryUserFunctionList(Map<String, Object> paramMap) {
        String userOid = MapUtils.getString(paramMap, "userOid");
        Integer functionState = MapUtils.getInteger(paramMap, "functionState");
        Integer functionType = MapUtils.getInteger(paramMap, "functionType");

        StringBuilder sql = new StringBuilder(
            "select distinct p.sysFunction, p.sysFunction.displayOrder from SysPermission p");

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(userOid)) {
            sql.append(", SysAuthority a where p.sysRole.iwoid = a.sysRole.iwoid and a.sysUser.iwoid = :USEROID");
            queryMap.put("USEROID", userOid);
        } else {
            sql.append(" where 1=1");
        }

        if (functionState != null) {
            sql.append(" and p.sysFunction.state = :FUNCTIONSTATE");
            queryMap.put("FUNCTIONSTATE", functionState);
        }

        if (functionType != null) {
            sql.append(" and p.sysFunction.functionType = :FUNCTIONTYPE");
            queryMap.put("FUNCTIONTYPE", functionType);
        }

        sql.append(" order by p.sysFunction.displayOrder asc");

        List<Object> list = (List<Object>) commonDAO.findObjectList(sql.toString(), queryMap, false);
        List<SysFunction> sysFunctions = new ArrayList<SysFunction>();

        if (list != null && !list.isEmpty()) {
            for (Object object : list) {
                Object[] obj = (Object[]) object;
                sysFunctions.add((SysFunction) obj[0]);
            }
        }
        return sysFunctions;
    }

    @Override
    public void doTransAssignRoleFunction(String sysRoleOid, List<String> functionOidList, String operatorOid,
        String logFunctionOid) throws IllegalAccessException {
        Validator.checkArgument(StringUtils.isBlank(sysRoleOid), "要修改的角色Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录功能项Oid不能为空");

        SysRole sysRole = commonDAO.findObject(SysRole.class, sysRoleOid);

        if (sysRole == null) {
            throw new IllegalArgumentException("未找到角色信息");
        }

        if (sysRole.getBuildType() == SysUser.BuildType.internal.getValue()) {
            throw new IllegalAccessException("内置角色不可修改");
        }

        String sql = "delete from SysPermission p where p.sysRole.iwoid = :ROLEOID";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ROLEOID", sysRoleOid);

        commonDAO.executeBatch(sql, paramMap, false);

        if (functionOidList != null && !functionOidList.isEmpty()) {
            sql = "select f from SysFunction f where f.parentFunctionOid is null";

            SysFunction rootFunction = commonDAO.findObject(sql, null, false);

            if (rootFunction == null) {
                throw new IllegalArgumentException("未找到根节点数据");
            }

            SysPermission rootPermission = new SysPermission();
            rootPermission.setIwoid(Generator.generateIwoid());
            rootPermission.setSysFunction(rootFunction);
            rootPermission.setSysRole(sysRole);

            commonDAO.save(rootPermission, false);

            for (String functionOid : functionOidList) {
                SysPermission sysPermission = new SysPermission();
                sysPermission.setIwoid(Generator.generateIwoid());
                sysPermission.setSysFunction(new SysFunction(functionOid));
                sysPermission.setSysRole(sysRole);

                commonDAO.save(sysPermission, false);
            }
        }
    }

    public SysLogService getSysLogService() {
        return sysLogService;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
}
