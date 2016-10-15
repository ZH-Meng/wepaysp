/*
 * LogServiceImpl.java
 * 创建者：马宗旺
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.vo.manage.SysLogVo;

/**
 * @author mazw
 */
public class SysLogServiceImpl extends BaseService implements SysLogService {

    @Override
    public SysLog doTransSaveSysLog(Integer logType, String operationUserOid, String logAbstract, Date processBeginTime,
        Date processEndTime, String dataBefore, String dataAfter, Integer logState, String recordOid,
        String functionOid, Integer actionType) {
        Validator.checkArgument(!Validator.contains(SysLog.LogType.class, logType), "日志类型不正确");
//        Validator.checkArgument(StringUtils.isBlank(operationUserOid), "操作用户主键不能为空");
        //Validator.checkArgument(StringUtils.isBlank(logAbstract), "日志描述不能为空");
        Validator.checkArgument(processBeginTime == null, "操作开始时间不能为空");
        Validator.checkArgument(processEndTime == null, "操作结束时间不能为空");
        Validator.checkArgument(!Validator.contains(SysLog.State.class, logState), "日志执行状态不正确");
        if (actionType != null) {
            Validator.checkArgument(!Validator.contains(SysLog.ActionType.class, actionType), "日志操作类型不正确");
        }

        SysLog sysLog = new SysLog();
        sysLog.setIwoid(Generator.generateIwoid());
        if(StringUtils.isNotBlank(operationUserOid)){
        	sysLog.setSysUser(new SysUser(operationUserOid));
        }        
        sysLog.setActionType(actionType);
        sysLog.setLogAbstract(logAbstract);
        sysLog.setLogType(logType);
        sysLog.setState(logState);

        if (!StringUtils.isBlank(functionOid)) {
            sysLog.setSysFunction(new SysFunction(functionOid));
        }

        sysLog.setRecordOid(recordOid);
        sysLog.setProcessTimeBegin(processBeginTime);
        sysLog.setProcessTimeEnd(processEndTime);
        sysLog.setDataAfter(dataAfter);
        sysLog.setDataBefore(dataBefore);

        commonDAO.save(sysLog, false);

        return sysLog;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysLogVo> doJoinTransQuerySysLogList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer logType = MapUtils.getInteger(paramMap, "logType");
        String functionOid = MapUtils.getString(paramMap, "functionOid");
        Integer actionType = MapUtils.getInteger(paramMap, "actionType");
        Date processBeginTime = (Date) MapUtils.getObject(paramMap, "processBeginTime");
        Date processEndTime = (Date) MapUtils.getObject(paramMap, "processEndTime");

        StringBuffer sql = new StringBuffer("from SysLog l left join fetch l.sysUser where 1=1");

        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and l.sysUser.iwoid in (select s.sysUser.iwoid from SysAuthority s where s.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
        }

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and l.sysUser.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
        }

        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and l.sysUser.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
        }

        if (logType != null) {
            sql.append(" and l.logType = :LOGTYPE");
            sqlMap.put("LOGTYPE", logType);
        }

        if (actionType != null) {
            sql.append(" and l.actionType = :ACTIONTYPE");
            sqlMap.put("ACTIONTYPE", actionType);
        }

        if (StringUtils.isNotBlank(functionOid)) {
            sql.append(" and l.sysFunction.iwoid = :FUNCTIONOID");
            sqlMap.put("FUNCTIONOID", functionOid);
        }

        if (processBeginTime != null) {
            sql.append(" and l.processTimeBegin >= :PROCESSBEGINTIME");
            sqlMap.put("PROCESSBEGINTIME", processBeginTime);
        }

        if (processEndTime != null) {
            sql.append(" and l.processTimeBegin < :PROCESSENDTIME");
            sqlMap.put("PROCESSENDTIME", processEndTime);
        }

        sql.append(" order by l.processTimeBegin desc, l.sysUser.userId");

        List<SysLogVo> resultList = new ArrayList<SysLogVo>();
        List<SysLog> sysLogList = (List<SysLog>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (sysLogList != null && !sysLogList.isEmpty()) {
            Set<String> userOidSet = new HashSet<String>();

            for (SysLog sysLog : sysLogList) {
                if (sysLog.getSysUser() != null) {
                    userOidSet.add(sysLog.getSysUser().getIwoid());
                }
            }

            Map<String, String> userRoleNameMap = new HashMap<String, String>();

            String roleNameSql = "select s.sysUser.iwoid, s.sysRole.roleName from SysAuthority s where s.sysUser.iwoid in (:USEROIDLIST)";

            sqlMap.clear();
            sqlMap.put("USEROIDLIST", userOidSet);

            Object[] userRoleNameArray = commonDAO.findObjectArray(roleNameSql, sqlMap, false);

            if (userRoleNameArray != null && userRoleNameArray.length > 0) {
                for (Object userRoleNameRow : userRoleNameArray) {
                    Object[] userRoleNameRowArr = (Object[]) userRoleNameRow;
                    userRoleNameMap.put(String.valueOf(userRoleNameRowArr[0]), String.valueOf(userRoleNameRowArr[1]));
                }
            }

            for (SysLog sysLog : sysLogList) {
                SysLogVo vo = new SysLogVo();

                vo.setActionType(sysLog.getActionType());
                vo.setCreator(sysLog.getCreator());
                vo.setCreateTime(sysLog.getCreateTime());
                vo.setDataAfter(sysLog.getDataAfter());
                vo.setDataBefore(sysLog.getDataBefore());

                if (sysLog.getSysFunction() != null) {
                    vo.setFunctionName(sysLog.getSysFunction().getFunctionName());
                    vo.setFunctionOid(sysLog.getSysFunction().getIwoid());
                }

                vo.setIwoid(sysLog.getIwoid());
                vo.setLogAbstract(sysLog.getLogAbstract());
                vo.setLogType(sysLog.getLogType());
                vo.setModifier(sysLog.getModifier());
                vo.setModifyTime(sysLog.getModifyTime());
                vo.setProcessTimeBegin(sysLog.getProcessTimeBegin());
                vo.setProcessTimeEnd(sysLog.getProcessTimeEnd());
                vo.setRecordOid(sysLog.getRecordOid());
                vo.setRemark(sysLog.getRemark());
                vo.setState(sysLog.getState());
                vo.setSysUser(sysLog.getSysUser());

                if (vo.getSysUser() != null) {
                    vo.setRoleName(userRoleNameMap.get(vo.getSysUser().getIwoid()));
                }

                resultList.add(vo);
            }
        }
        
        return resultList;
    }

    @Override
    public int doJoinTransQuerySysLogCount(Map<String, Object> paramMap) {
        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer logType = MapUtils.getInteger(paramMap, "logType");
        String functionOid = MapUtils.getString(paramMap, "functionOid");
        Integer actionType = MapUtils.getInteger(paramMap, "actionType");
        Date processBeginTime = (Date) MapUtils.getObject(paramMap, "processBeginTime");
        Date processEndTime = (Date) MapUtils.getObject(paramMap, "processEndTime");

        StringBuffer sql = new StringBuffer("select count(l.iwoid) from SysLog l where 1=1");

        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and l.sysUser.iwoid in (select s.sysUser.iwoid from SysAuthority s where s.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
        }

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and l.sysUser.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
        }

        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and l.sysUser.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
        }

        if (logType != null) {
            sql.append(" and l.logType = :LOGTYPE");
            sqlMap.put("LOGTYPE", logType);
        }

        if (actionType != null) {
            sql.append(" and l.actionType =:ACTIONTYPE");
            sqlMap.put("ACTIONTYPE", actionType);
        }

        if (StringUtils.isNotBlank(functionOid)) {
            sql.append(" and l.sysFunction.iwoid = :FUNCTIONOID");
            sqlMap.put("FUNCTIONOID", functionOid);
        }

        if (processBeginTime != null) {
            sql.append(" and l.processTimeBegin >= :PROCESSBEGINTIME");
            sqlMap.put("PROCESSBEGINTIME", processBeginTime);
        }

        if (processEndTime != null) {
            sql.append(" and l.processTimeBegin < :PROCESSENDTIME ");
            sqlMap.put("PROCESSENDTIME", processEndTime);
        }

        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysLogVo> doTransExportSysLogList(Map<String, Object> paramMap, String operationUserOid, String funOid) {
        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer logType = MapUtils.getInteger(paramMap, "logType");
        String functionOid = MapUtils.getString(paramMap, "functionOid");
        Integer actionType = MapUtils.getInteger(paramMap, "actionType");
        Date processBeginTime = (Date) MapUtils.getObject(paramMap, "processBeginTime");
        Date processEndTime = (Date) MapUtils.getObject(paramMap, "processEndTime");

        StringBuffer sql = new StringBuffer("from SysLog l left join fetch l.sysUser where 1=1");

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer logAbstract = new StringBuffer("查询条件：");

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and l.sysUser.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
            logAbstract.append("[登录名 ：").append(userId).append("]");
        }

        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and l.sysUser.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
            logAbstract.append("[真实姓名 ：").append(userName).append("]");
        }
        
        logAbstract.append("[角色：");
        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and l.sysUser.iwoid in (select s.sysUser.iwoid from SysAuthority s where s.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
            SysRole sysRole = commonDAO.findObject(SysRole.class, roleOid);
            logAbstract.append(sysRole.getRoleName());
        } else {
            logAbstract.append("全部");
        }
        logAbstract.append("]");

        logAbstract.append("[日志类型 ：");
        if (logType != null) {
            sql.append(" and l.logType = :LOGTYPE");
            sqlMap.put("LOGTYPE", logType);
            
            if (logType == SysLog.LogType.userLogin.getValue()) {
                logAbstract.append("用户登录操作");
            } else if (logType == SysLog.LogType.userLogout.getValue()) {
                logAbstract.append("用户退出操作");
            } else if (logType == SysLog.LogType.userOperate.getValue()) {
                logAbstract.append("用户操作");
            }
        } else {
            logAbstract.append("全部");
        }
        logAbstract.append("]");

        logAbstract.append("[功能名称：");
        if (StringUtils.isNotBlank(functionOid)) {
            sql.append(" and l.sysFunction.iwoid = :FUNCTIONOID");
            sqlMap.put("FUNCTIONOID", functionOid);

            SysFunction function = commonDAO.findObject(SysFunction.class, functionOid);
            logAbstract.append(function.getAliasName());
        } else {
            logAbstract.append("全部");
        }
        logAbstract.append("]");

        logAbstract.append("[操作名称 ：");
        if (actionType != null) {
            sql.append(" and l.actionType = :ACTIONTYPE");
            sqlMap.put("ACTIONTYPE", actionType);
            
            if (actionType == SysLog.ActionType.create.getValue()) {
                logAbstract.append("创建");
            } else if (actionType == SysLog.ActionType.export.getValue()) {
                logAbstract.append("导出");
            } else if (actionType == SysLog.ActionType.modify.getValue()) {
                logAbstract.append("修改");
            } else if (actionType == SysLog.ActionType.delete.getValue()) {
                logAbstract.append("删除");
            } else if (actionType == SysLog.ActionType.resetPwd.getValue()) {
                logAbstract.append("重置密码");
            } else if (actionType == SysLog.ActionType.charge.getValue()) {
                logAbstract.append("充值");
            } else if (actionType == SysLog.ActionType.batchImport.getValue()) {
                logAbstract.append("批量导入");
            }
        } else {
            logAbstract.append("全部");
        }
        logAbstract.append("]");
        
        if (processBeginTime != null) {
            sql.append(" and l.processTimeBegin >= :PROCESSBEGINTIME");
            sqlMap.put("PROCESSBEGINTIME", processBeginTime);

            logAbstract.append("[操作开始日期：").append(new SimpleDateFormat("yyyy-MM-dd").format(processBeginTime)).append("]");
        }

        if (processEndTime != null) {
            sql.append(" and l.processTimeBegin < :PROCESSENDTIME");
            sqlMap.put("PROCESSENDTIME", processEndTime);

            logAbstract.append("[操作结束日期 ：").append(new SimpleDateFormat("yyyy-MM-dd").format(new DateTime(processEndTime).plusDays(-1).toDate())).append("]");
        }

        sql.append(" order by l.processTimeBegin desc, l.sysUser.userId");

        List<SysLogVo> resultList = new ArrayList<SysLogVo>();
        List<SysLog> sysLogList = (List<SysLog>) commonDAO.findObjectList(sql.toString(), sqlMap, false);

        if (sysLogList != null && !sysLogList.isEmpty()) {
            Set<String> userOidSet = new HashSet<String>();

            for (SysLog sysLog : sysLogList) {
                if (sysLog.getSysUser() != null) {
                    userOidSet.add(sysLog.getSysUser().getIwoid());
                }
            }

            Map<String, String> userRoleNameMap = new HashMap<String, String>();

            String roleNameSql = "select s.sysUser.iwoid, s.sysRole.roleName from SysAuthority s where s.sysUser.iwoid in (:USEROIDLIST)";

            sqlMap.clear();
            sqlMap.put("USEROIDLIST", userOidSet);

            Object[] userRoleNameArray = commonDAO.findObjectArray(roleNameSql, sqlMap, false);

            if (userRoleNameArray != null && userRoleNameArray.length > 0) {
                for (Object userRoleNameRow : userRoleNameArray) {
                    Object[] userRoleNameRowArr = (Object[]) userRoleNameRow;
                    userRoleNameMap.put(String.valueOf(userRoleNameRowArr[0]), String.valueOf(userRoleNameRowArr[1]));
                }
            }

            for (SysLog sysLog : sysLogList) {
                SysLogVo vo = new SysLogVo();

                vo.setActionType(sysLog.getActionType());
                vo.setCreator(sysLog.getCreator());
                vo.setCreateTime(sysLog.getCreateTime());
                vo.setDataAfter(sysLog.getDataAfter());
                vo.setDataBefore(sysLog.getDataBefore());

                if (sysLog.getSysFunction() != null) {
                    vo.setFunctionName(sysLog.getSysFunction().getFunctionName());
                    vo.setFunctionOid(sysLog.getSysFunction().getIwoid());
                }

                vo.setIwoid(sysLog.getIwoid());
                vo.setLogAbstract(sysLog.getLogAbstract());
                vo.setLogType(sysLog.getLogType());
                vo.setModifier(sysLog.getModifier());
                vo.setModifyTime(sysLog.getModifyTime());
                vo.setProcessTimeBegin(sysLog.getProcessTimeBegin());
                vo.setProcessTimeEnd(sysLog.getProcessTimeEnd());
                vo.setRecordOid(sysLog.getRecordOid());
                vo.setRemark(sysLog.getRemark());
                vo.setState(sysLog.getState());
                vo.setSysUser(sysLog.getSysUser());
                vo.setUserId(sysLog.getSysUser().getUserId());
                vo.setUserName(sysLog.getSysUser().getUserName());
                
                if (vo.getSysUser() != null) {
                    vo.setRoleName(userRoleNameMap.get(vo.getSysUser().getIwoid()));
                }

                if (sysLog.getLogType() != null) {
                	if (sysLog.getLogType() == SysLog.LogType.userLogin.getValue()) {
                        vo.setLogTypeName("用户登录操作");
                    } else if (sysLog.getLogType() == SysLog.LogType.userLogout.getValue()) {
                    	vo.setLogTypeName("用户退出操作");
                    } else if (sysLog.getLogType() == SysLog.LogType.userOperate.getValue()) {
                    	vo.setLogTypeName("用户操作");
                    } else if (sysLog.getLogType() == SysLog.LogType.userProcess.getValue()) {
                    	vo.setLogTypeName("用户处理");
                    }
                }
                
                if (sysLog.getActionType() != null) {
                    if (sysLog.getActionType() == SysLog.ActionType.create.getValue()) {
                        vo.setActionTypeName("创建");
                    } else if (sysLog.getActionType() == SysLog.ActionType.export.getValue()) {
                        vo.setActionTypeName("导出");
                    } else if (sysLog.getActionType() == SysLog.ActionType.modify.getValue()) {
                        vo.setActionTypeName("修改");
                    } else if (sysLog.getActionType() == SysLog.ActionType.delete.getValue()) {
                        vo.setActionTypeName("删除");
                    } else if (sysLog.getActionType() == SysLog.ActionType.resetPwd.getValue()) {
                        vo.setActionTypeName("重置密码");
                    } else if (sysLog.getActionType() == SysLog.ActionType.charge.getValue()) {
                        vo.setActionTypeName("充值");
                    } else if (sysLog.getActionType() == SysLog.ActionType.batchImport.getValue()) {
                        vo.setActionTypeName("批量导入");
                    }
                }
                
                if (sysLog.getState() == 0) {
                	vo.setStateName("受理中");
                } else if (sysLog.getState() == 1) {
                	vo.setStateName("成功");
                } if (sysLog.getState() == 2) {
                	vo.setStateName("受理失败");
                }
                resultList.add(vo);
            }
        }

        doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operationUserOid, logAbstract.toString(), new Date(), new Date(), null,
            null, SysLog.State.success.getValue(), null, funOid, SysLog.ActionType.export.getValue());
        
        return resultList;
    }
}
