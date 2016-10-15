package com.zbsp.wepaysp.po.manage;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sys_log_t")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 340051465370163640L;

    private String iwoid;
    private SysUser sysUser;
    private SysFunction sysFunction;
    private Integer logType;
    private String logAbstract;
    private Date processTimeBegin;
    private Date processTimeEnd;
    private String dataBefore;
    private String dataAfter;
    private Integer state;
    private String recordOid;
    private Integer actionType;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum LogType {
        /** 日志类型：用户操作 */                       userOperate(0),
        /** 日志类型：用户登录操作 */                 userLogin(1),
        /** 日志类型：用户退出操作 */                 userLogout(2),
    	/** 日志类型：用户处理 */                 userProcess(3);

        private int value;

        public int getValue() {
            return value;
        }

        private LogType(int value) {
            this.value = value;
        }
    }
    
    public static enum State {
        /** 状态：成功 */        success(1),
        /** 受理中 */              inHand(0),
        /** 受理失败 */           fail(2);

        private int value;

        public int getValue() {
            return value;
        }

        private State(int value) {
            this.value = value;
        }
    }
    
    public static enum ActionType {
        /** 操作类型：新建 */           create(0),
        /** 操作类型：修改 */           modify(1),
        /** 操作类型：导出 */           export(2),
        /** 操作类型：删除 */           delete(3),
        /** 操作类型：重置密码 */     resetPwd(4),
        /** 操作类型：充值 */           charge(5),
        /** 操作类型：批量导入 */     batchImport(6),
        /** 操作类型：打印 */     print(7);

        private int value;

        public int getValue() {
            return value;
        }

        private ActionType(int value) {
            this.value = value;
        }
    }
    
    public SysLog() {
    }

    @Id
    @Column(name = "iwoid", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_user_oid")
    public SysUser getSysUser() {
        return this.sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_function_oid")
    public SysFunction getSysFunction() {
        return this.sysFunction;
    }

    public void setSysFunction(SysFunction sysFunction) {
        this.sysFunction = sysFunction;
    }

    @Column(name = "log_type", nullable = false)
    public Integer getLogType() {
        return this.logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    @Column(name = "log_abstract", length = 2000)
    public String getLogAbstract() {
        return this.logAbstract;
    }

    public void setLogAbstract(String logAbstract) {
        this.logAbstract = logAbstract;
    }

    @Column(name = "process_time_begin", nullable = false, length = 29)
    public Date getProcessTimeBegin() {
        return this.processTimeBegin;
    }

    public void setProcessTimeBegin(Date processTimeBegin) {
        this.processTimeBegin = processTimeBegin;
    }

    @Column(name = "process_time_end", length = 29)
    public Date getProcessTimeEnd() {
        return this.processTimeEnd;
    }

    public void setProcessTimeEnd(Date processTimeEnd) {
        this.processTimeEnd = processTimeEnd;
    }

    @Column(name = "data_before", length = 2000)
    public String getDataBefore() {
        return this.dataBefore;
    }

    public void setDataBefore(String dataBefore) {
        this.dataBefore = dataBefore;
    }

    @Column(name = "data_after", length = 2000)
    public String getDataAfter() {
        return this.dataAfter;
    }

    public void setDataAfter(String dataAfter) {
        this.dataAfter = dataAfter;
    }

    @Column(name = "state", nullable = false)
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Column(name = "record_oid", length = 32)
    public String getRecordOid() {
        return this.recordOid;
    }

    public void setRecordOid(String recordOid) {
        this.recordOid = recordOid;
    }

    @Column(name = "action_type")
    public Integer getActionType() {
        return this.actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    @Column(name = "creator", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modifier", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time", length = 29)
    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "remark", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
