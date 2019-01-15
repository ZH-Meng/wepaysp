/*
 * SysRoleServiceImpl.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.manage.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.manage.SysRoleService;

/**
 * @author 杨帆
 */
public class SysRoleServiceImpl extends BaseService implements SysRoleService {

    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
    @Override
    public List<SysRole> doJoinTransQuerySysRoleList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Integer state = MapUtils.getInteger(paramMap, "state");
        String roleName = MapUtils.getString(paramMap, "roleName");
        Integer roleLevel = MapUtils.getInteger(paramMap, "roleLevel");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");
        List<Integer> stateList = (List<Integer>) MapUtils.getObject(paramMap, "stateList");
        
        String sql = "select r from SysRole r where 1=1 ";

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (state != null) {
            sql += " and r.state = :STATE ";
            queryMap.put("STATE", state);
        }

        if (!StringUtils.isBlank(roleName)) {
            sql += " and r.roleName = :ROLENAME ";
            queryMap.put("ROLENAME", roleName);
        }

        if (roleLevel != null) {
            sql += " and r.roleLevel = :ROLELEVEL ";
            queryMap.put("ROLELEVEL", roleLevel);
        }

        if (buildType != null) {
            sql += " and r.buildType = :BUILDTYPE";
            queryMap.put("BUILDTYPE", buildType);
        }
        
        if (stateList != null && !stateList.isEmpty()) {
            sql += " and r.state in (:STATELIST) ";
            queryMap.put("STATELIST", stateList);
        }

        sql += " order by r.modifyTime desc";

        return commonDAO.findObjectList(sql, queryMap, false, startIndex, maxResult);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doJoinTransQuerySysRoleCount(Map<String, Object> paramMap) {
        Integer state = MapUtils.getInteger(paramMap, "state");
        String roleName = MapUtils.getString(paramMap, "roleName");
        Integer roleLevel = MapUtils.getInteger(paramMap, "roleLevel");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");
        List<Integer> stateList = (List<Integer>) MapUtils.getObject(paramMap, "stateList");

        String sql = "select count(r.iwoid) from SysRole r where 1=1 ";

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (state != null) {
            sql += " and r.state = :STATE";
            queryMap.put("STATE", state);
        }

        if (!StringUtils.isBlank(roleName)) {
            sql += " and r.roleName = :ROLENAME";
            queryMap.put("ROLENAME", roleName);
        }

        if (roleLevel != null) {
            sql += " and r.roleLevel = :ROLELEVEL";
            queryMap.put("ROLELEVEL", roleLevel);
        }

        if (buildType != null) {
            sql += " and r.buildType = :BUILDTYPE";
            queryMap.put("BUILDTYPE", buildType);
        }
        
        if (stateList != null && !stateList.isEmpty()) {
            sql += " and r.state in (:STATELIST) ";
            queryMap.put("STATELIST", stateList);
        }

        return commonDAO.queryObjectCount(sql, queryMap, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysRole> doTransExportSysRoleList(Map<String, Object> paramMap, String operatorUserOid, String logFunctionOid) {
        Integer state = MapUtils.getInteger(paramMap, "state");
        String roleName = MapUtils.getString(paramMap, "roleName");
        Integer roleLevel = MapUtils.getInteger(paramMap, "roleLevel");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");
        List<Integer> stateList = (List<Integer>) MapUtils.getObject(paramMap, "stateList");

        String sql = "select r from SysRole r where 1=1 ";

        Map<String, Object> queryMap = new HashMap<String, Object>();

        StringBuilder queryConditionStr = new StringBuilder("查询条件：");

        queryConditionStr.append("[状态：");
        if (state != null) {
            sql += " and r.state = :STATE ";
            queryMap.put("STATE", state);

            if (SysRole.State.normal.getValue() == state) {
                queryConditionStr.append("正常");
            } else if (SysRole.State.frozen.getValue() == state) {
                queryConditionStr.append("冻结");
            } else if (SysRole.State.canceled.getValue() == state) {
                queryConditionStr.append("注销");
            }
        } else {
            queryConditionStr.append("全部");
        }
        queryConditionStr.append("]");

        queryConditionStr.append("[角色名称：");
        if (!StringUtils.isBlank(roleName)) {
            sql += " and r.roleName = :ROLENAME ";
            queryMap.put("ROLENAME", roleName);
            queryConditionStr.append(roleName);
        } else {
            queryConditionStr.append("全部");
        }
        queryConditionStr.append("]");

        queryConditionStr.append("[角色级别：");
        if (roleLevel != null) {
            sql += " and r.roleLevel = :ROLELEVEL ";
            queryMap.put("ROLELEVEL", roleLevel);
            if (SysRole.Level.normal.getValue() == roleLevel) {
                queryConditionStr.append("应用级别");
            } else if (SysRole.Level.manage.getValue() == roleLevel) {
                queryConditionStr.append("管理级别");
            }
        } else {
            queryConditionStr.append("全部");
        }
        queryConditionStr.append("]");

        if (buildType != null) {
            sql += " and r.buildType = :BUILDTYPE";
            queryMap.put("BUILDTYPE", buildType);
        }
        
        if (stateList != null && !stateList.isEmpty()) {
            sql += " and r.state in (:STATELIST) ";
            queryMap.put("STATELIST", stateList);
        }

        sql += " order by r.modifyTime desc";

        Date processBeginTime = new Date();

        List<SysRole> sysRoleList = commonDAO.findObjectList(sql, queryMap, false);

        Date processEndTime = new Date();

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, queryConditionStr.toString(),
            processBeginTime, processEndTime, null, null, SysLog.State.success.getValue(), null, logFunctionOid,
            SysLog.ActionType.export.getValue());

        return sysRoleList;
    }
    
    @Override
    public List<String> doJoinTransQueryUniqueRoleNameList(Map<String, Object> paramMap) {
        Integer state = MapUtils.getInteger(paramMap, "state");
        Integer roleLevel = MapUtils.getInteger(paramMap, "roleLevel");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");

        String sql = "select distinct(r.roleName) from SysRole r where 1=1 ";

        Map<String, Object> queryMap = new HashMap<String, Object>();

        if (state != null) {
            sql += " and r.state = :STATE ";
            queryMap.put("STATE", state);
        }

        if (roleLevel != null) {
            sql += " and r.roleLevel = :ROLELEVEL ";
            queryMap.put("ROLELEVEL", roleLevel);
        }

        if (buildType != null) {
            sql += " and r.buildType = :BUILDTYPE";
            queryMap.put("BUILDTYPE", buildType);
        }

        sql += " order by r.roleName";

        return commonDAO.findObjectList(sql, queryMap, false);
    }

    @Override
    public SysRole doTransAddRole(SysRole sysRole, String creator, String operatorUserOid, String logFunctionOid) throws AlreadyExistsException {
        Validator.checkArgument(StringUtils.isBlank(sysRole.getRoleId()), "角色代码不能为空");
        Validator.checkArgument(StringUtils.isBlank(sysRole.getRoleName()), "角色名称不能为空");
        Validator.checkArgument(!Validator.contains(SysRole.Level.class, sysRole.getRoleLevel()), "角色级别类型不正确");

        Date processBeginTime = new Date();
        // 查询角色代码 名称是否重复
        String sql = "select s from SysRole s where s.roleId = :ROLEID or (s.roleName = :ROLENAME and s.state <> :STATE)";

        Map<String, Object> mapParams = new HashMap<String, Object>();
        mapParams.put("ROLEID", sysRole.getRoleId());
        mapParams.put("ROLENAME", sysRole.getRoleName());
        mapParams.put("STATE", SysRole.State.canceled.getValue());

        List<SysRole> checkList = commonDAO.findObjectList(sql.toString(), mapParams, false);

        if (checkList != null && !checkList.isEmpty()) {
            SysRole moreRole = checkList.get(0);
            if (moreRole.getRoleId().equals(sysRole.getRoleId())) {
                throw new AlreadyExistsException("角色代码不允许重复！");
            } else {
                throw new AlreadyExistsException("角色名称不允许重复！");
            }
        }

        // 状态为空 设置默认正常
        if (sysRole.getState() == null) {
            sysRole.setState(SysRole.State.normal.getValue());
        }

        sysRole.setUseState(SysRole.UseState.notUsed.getValue());
        sysRole.setIwoid(Generator.generateIwoid());
        sysRole.setBuildType(SysUser.BuildType.create.getValue());
        sysRole.setCreator(creator);
        sysRole.setModifier(creator);
        sysRole.setModifyTime(new Date());
        commonDAO.save(sysRole, false);

        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
            "用户创建角色[角色代码：" + sysRole.getRoleId() + ", 角色名称：" + sysRole.getRoleName() + "]", processBeginTime,
            processEndTime, null, sysRole.toString(), SysLog.State.success.getValue(), sysRole.getIwoid(),
            logFunctionOid, SysLog.ActionType.create.getValue());

        return sysRole;
    }

    @Override
    public SysRole doTransUpdateRole(SysRole sysRole, String modifier, String operatorUserOid, String logFunctionOid) throws AlreadyExistsException, IllegalArgumentException {
        Validator.checkArgument(StringUtils.isBlank(sysRole.getRoleId()), "角色代码不能为空");
        Validator.checkArgument(StringUtils.isBlank(sysRole.getRoleName()), "角色名称不能为空");
        Validator.checkArgument(StringUtils.isBlank(sysRole.getIwoid()), "角色主键不能为空");
        Validator.checkArgument(!Validator.contains(SysRole.State.class, sysRole.getState()), "角色状态类型不正确");
        Validator.checkArgument(!Validator.contains(SysRole.Level.class, sysRole.getRoleLevel()), "角色级别类型不正确");

        Date processBeginTime = new Date();

        // 查找原角色
        SysRole oldSysRole = commonDAO.findObject(SysRole.class, sysRole.getIwoid());

        if (oldSysRole == null) {
            throw new AlreadyExistsException("未找到要修改的角色信息");
        } else if (SysRole.State.canceled.getValue() == oldSysRole.getState()) {
            throw new IllegalArgumentException("该角色已注销不允许修改");
        } else if (oldSysRole.getState() != sysRole.getState()
            && sysRole.getState() == SysRole.State.canceled.getValue()) {// 角色状态修改为注销 情况
            StringBuilder roleStateSql = new StringBuilder(
                "select count(*) from SysAuthority s where s.sysRole.iwoid = :ROLEIWOID and s.sysUser.state <> :STATE");

            Map<String, Object> roleParams = new HashMap<String, Object>();
            roleParams.put("ROLEIWOID", sysRole.getIwoid());
            roleParams.put("STATE", SysUser.State.canceled.getValue());

            Integer users = commonDAO.queryObjectCount(roleStateSql.toString(), roleParams, false);

            if (users > 0) {
                throw new AlreadyExistsException("角色下存在非注销的用户，不允许注销该角色");
            }
        }

        String oldSysRoleStr = oldSysRole.toString();

        // 查询角色代码 名称是否重复
        StringBuilder sql = new StringBuilder(
            "select s from SysRole s where (s.roleId = :ROLEID or (s.roleName = :ROLENAME and s.state <> :STATE)) and s.iwoid <> :ROLEOID");

        Map<String, Object> mapParams = new HashMap<String, Object>();
        mapParams.put("ROLEID", sysRole.getRoleId());
        mapParams.put("ROLENAME", sysRole.getRoleName());
        mapParams.put("STATE", SysRole.State.canceled.getValue());
        mapParams.put("ROLEOID", sysRole.getIwoid());

        List<SysRole> checkList = commonDAO.findObjectList(sql.toString(), mapParams, false);

        if (checkList != null && !checkList.isEmpty()) {
            for (SysRole moreRole : checkList) {
                if (moreRole.getRoleId().equals(sysRole.getRoleId()) && moreRole.getIwoid() != oldSysRole.getIwoid()) {
                    throw new AlreadyExistsException("角色代码不允许重复！");
                } else if (moreRole.getIwoid() != oldSysRole.getIwoid()) {
                    throw new AlreadyExistsException("角色名称不允许重复！");
                }
            }
        }

        // 设置值
        oldSysRole.setModifier(modifier);
        oldSysRole.setRoleName(sysRole.getRoleName());
        oldSysRole.setRoleLevel(sysRole.getRoleLevel());
        if (oldSysRole.getUseState() == SysRole.UseState.notUsed.getValue()) {
            oldSysRole.setRoleId(sysRole.getRoleId());
        }
        oldSysRole.setDescription(sysRole.getDescription());
        oldSysRole.setRemark(sysRole.getRemark());
        oldSysRole.setState(sysRole.getState());
        oldSysRole.setRoleIndex(sysRole.getRoleIndex());

        commonDAO.update(oldSysRole);

        String newSysRoleStr = oldSysRole.toString();

        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
            "用户修改角色[角色代码：" + oldSysRole.getRoleId() + ", 角色名称：" + oldSysRole.getRoleName() + "]", processBeginTime,
            processEndTime, oldSysRoleStr, newSysRoleStr, SysLog.State.success.getValue(), sysRole.getIwoid(),
            logFunctionOid, SysLog.ActionType.modify.getValue());

        return oldSysRole;
    }

    @Override
    public SysRole doJoinTransQuerySysRoleByOid(String sysRoleOid) {
        Validator.checkArgument(StringUtils.isBlank(sysRoleOid), "角色Oid不能为空");

        return commonDAO.findObject(SysRole.class, sysRoleOid);
    }

    public SysLogService getSysLogService() {
        return sysLogService;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
