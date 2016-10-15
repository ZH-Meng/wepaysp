/*
 * LogInfoVo.java
 * 创建者：杨帆
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.vo.manage;

import java.io.Serializable;
import java.util.Date;

import com.zbsp.wepaysp.po.manage.SysUser;

/**
 * 管理员操作日志记录VO.
 * 
 * @author 杨帆
 */
public class SysLogVo implements Serializable {

    private static final long serialVersionUID = -7471535851769318283L;
    
    private String iwoid;
    private Integer logType;
    private SysUser sysUser;
    // 管理员角色名称（只记录一个）
    private String roleName;
    private String logAbstract;
    private Date processTimeBegin;
    private Date processTimeEnd;
    private Integer state;
    private String dataBefore;
    private String dataAfter;
    private String recordOid;
    private String functionOid;
    // 功能名称
    private String functionName;
    private Integer actionType;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    private String logTypeName;
    private String actionTypeName;
    private String stateName;
    private String userId;
    private String userName;
    
    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLogAbstract() {
        return logAbstract;
    }

    public void setLogAbstract(String logAbstract) {
        this.logAbstract = logAbstract;
    }

    public Date getProcessTimeBegin() {
        return processTimeBegin;
    }

    public void setProcessTimeBegin(Date processTimeBegin) {
        this.processTimeBegin = processTimeBegin;
    }

    public Date getProcessTimeEnd() {
        return processTimeEnd;
    }

    public void setProcessTimeEnd(Date processTimeEnd) {
        this.processTimeEnd = processTimeEnd;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getDataBefore() {
        return dataBefore;
    }

    public void setDataBefore(String dataBefore) {
        this.dataBefore = dataBefore;
    }

    public String getDataAfter() {
        return dataAfter;
    }

    public void setDataAfter(String dataAfter) {
        this.dataAfter = dataAfter;
    }

    public String getRecordOid() {
        return recordOid;
    }

    public void setRecordOid(String recordOid) {
        this.recordOid = recordOid;
    }

    public String getFunctionOid() {
        return functionOid;
    }

    public void setFunctionOid(String functionOid) {
        this.functionOid = functionOid;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getLogTypeName() {
		return logTypeName;
	}

	public void setLogTypeName(String logTypeName) {
		this.logTypeName = logTypeName;
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
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

}
