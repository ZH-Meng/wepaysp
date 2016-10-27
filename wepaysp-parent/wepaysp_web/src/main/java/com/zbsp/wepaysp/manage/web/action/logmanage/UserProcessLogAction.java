package com.zbsp.wepaysp.manage.web.action.logmanage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.service.manage.SysFunctionService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.manage.SysRoleService;
import com.zbsp.wepaysp.vo.manage.SysLogVo;

/**
 * 用户操作日志Action
 * 
 * @author 侯建玮
 */
public class UserProcessLogAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 5835306657247528558L;

    private Map<String, Object> session;
    private List<SysLogVo> sysLogVoList;
    private List<SysRole> sysRoleAll;
    private List<SysFunction> sysFunctionAll;
    private String userId;
    private String userName;
    private String roleOid;
    private Integer logType;
    private String functionOid;
    private Integer actionType;
    private String processBeginTime;
    private String processEndTime;

    private String conditionRoleName;
    private String conditionLogTypeName;
    private String conditionFunctionName;
    private String conditionActionTypeName;

    private String downFileName;

    private SysLogService sysLogService;
    private SysRoleService sysRoleService;
    private SysFunctionService sysFunctionService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("userName", userName);
        paramMap.put("logTypeLevel", logType);
        paramMap.put("functionOid", functionOid);
        paramMap.put("actionType", actionType);
        paramMap.put("processBeginTime", getProcessBeginTime());
        paramMap.put("processEndTime", new DateTime(getProcessEndTime()).plusDays(1).toDate());
        paramMap.put("roleOid", roleOid);

        try {
            int logCount = 0;

            sysLogVoList = sysLogService.doJoinTransQuerySysLogList(paramMap, start, size);
            logCount = sysLogService.doJoinTransQuerySysLogCount(paramMap);

            List<Integer> roleStateList = new ArrayList<Integer>();
            roleStateList.add(SysRole.State.normal.getValue());
            roleStateList.add(SysRole.State.frozen.getValue());

            paramMap.clear();
            paramMap.put("stateList", roleStateList);

            sysRoleAll = sysRoleService.doJoinTransQuerySysRoleList(paramMap, 0, -1);

            paramMap.clear();
            paramMap.put("functionType", SysFunction.FunctionType.menu.getValue());
            sysFunctionAll = sysFunctionService.doJoinTransQueryLogSysFunctionList(paramMap);

            rowCount = logCount;
        } catch (Exception e) {
            logger.error("系统用户操作日志查询列表错误：" + e.getMessage());
            setAlertMessage("系统用户操作日志查询列表错误：" + e.getMessage());
        }

        return "userProcessLog";
    }

    public String exportFile() {
        return SUCCESS;
    }

    public InputStream getDownFile() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        InputStream inputStream = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("userName", userName);
        paramMap.put("roleOid", roleOid);
        paramMap.put("logTypeLevel", logType);
        paramMap.put("functionOid", functionOid);
        paramMap.put("actionType", actionType);
        paramMap.put("processBeginTime", getProcessBeginTime());
        paramMap.put("processEndTime", new DateTime(getProcessEndTime()).plusDays(1).toDate());

        try {
            sysLogVoList = sysLogService.doTransExportSysLogList(paramMap, manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            StringBuffer queryParam = new StringBuffer();
            if (StringUtils.isNotBlank(userId)) {
                queryParam.append(" 登录名：").append(userId);
            }

            if (StringUtils.isNotBlank(userName)) {
                queryParam.append(" 用户名:").append(userName);
            }

            queryParam.append(" 角色：").append(conditionRoleName);
            queryParam.append(" 日志类型：").append(conditionLogTypeName);
            queryParam.append(" 功能名称：").append(conditionFunctionName);
            queryParam.append(" 操作名称：").append(conditionActionTypeName);
            queryParam.append(" 操作开始日期：").append(processBeginTime);
            queryParam.append(" 操作结束日期：").append(processEndTime);

            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("param", queryParam.toString());

            String path = UserProcessLogAction.class.getResource("userProcessLogExport.xlsx").getPath();
            ExcelUtil excelUtil = new ExcelUtil();
            inputStream = excelUtil.writeData(sysLogVoList, path, dataMap, false);

            String fileName = "系统用户操作日志-" + DateUtil.getDate(new Date(), "yyyyMMdd") + ".xlsx";
            downFileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("系统用户操作日志导出列表错误：" + e.getMessage());
        }

        return inputStream;
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public List<SysLogVo> getSysLogVoList() {
        return sysLogVoList;
    }

    public void setLogInfoVoList(List<SysLogVo> sysLogVoList) {
        this.sysLogVoList = sysLogVoList;
    }

    public List<SysRole> getSysRoleAll() {
        return sysRoleAll;
    }

    public void setSysRoleAll(List<SysRole> sysRoleAll) {
        this.sysRoleAll = sysRoleAll;
    }

    public List<SysFunction> getSysFunctionAll() {
        return sysFunctionAll;
    }

    public void setSysFunctionAll(List<SysFunction> sysFunctionAll) {
        this.sysFunctionAll = sysFunctionAll;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleOid() {
        return roleOid;
    }

    public void setRoleOid(String roleOid) {
        this.roleOid = roleOid;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getFunctionOid() {
        return functionOid;
    }

    public void setFunctionOid(String functionOid) {
        this.functionOid = functionOid;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Date getProcessBeginTime() {
        Date date = null;
        if (StringUtils.isBlank(processBeginTime)) {
            processBeginTime = DateTime.now().minusDays(30).toString("yyyy-MM-dd");
        } else {
            date = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(processBeginTime).toDate();
        }
        return date;
    }

    public void setProcessBeginTime(String processBeginTime) {
        this.processBeginTime = processBeginTime;
    }

    public Date getProcessEndTime() {
        Date date = null;
        if (StringUtils.isBlank(processEndTime)) {
            date = new Date();
            processEndTime = DateTime.now().toString("yyyy-MM-dd");
        } else {
            date = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(processEndTime).toDate();
        }

        return date;
    }

    public void setProcessEndTime(String processEndTime) {
        this.processEndTime = processEndTime;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public String getConditionRoleName() {
        return conditionRoleName;
    }

    public void setConditionRoleName(String conditionRoleName) {
        this.conditionRoleName = conditionRoleName;
    }

    public String getConditionLogTypeName() {
        return conditionLogTypeName;
    }

    public void setConditionLogTypeName(String conditionLogTypeName) {
        this.conditionLogTypeName = conditionLogTypeName;
    }

    public String getConditionFunctionName() {
        return conditionFunctionName;
    }

    public void setConditionFunctionName(String conditionFunctionName) {
        this.conditionFunctionName = conditionFunctionName;
    }

    public String getConditionActionTypeName() {
        return conditionActionTypeName;
    }

    public void setConditionActionTypeName(String conditionActionTypeName) {
        this.conditionActionTypeName = conditionActionTypeName;
    }

    public String getDownFileName() {
        return downFileName;
    }

    public void setDownFileName(String downFileName) {
        this.downFileName = downFileName;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    public void setSysFunctionService(SysFunctionService sysFunctionService) {
        this.sysFunctionService = sysFunctionService;
    }

}
