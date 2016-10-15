package com.zbsp.wepaysp.po.manage;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sys_function_t", uniqueConstraints = @UniqueConstraint(columnNames = "url"))
public class SysFunction implements Serializable {

    private static final long serialVersionUID = -7389420968405521041L;
    
    private String iwoid;
    private String parentFunctionOid;
    private String logFunctionOid;
    private String functionName;
    private String aliasName;
    private String description;
    private String url;
    private Integer functionLevel;
    private int displayOrder;
    private Integer functionType;
    private Integer buildType;
    private Integer state;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum FunctionType {
        /** 功能类型:菜单 */        menu(0),
        /** 功能类型:操作 */        action(1);

        private int value;

        public int getValue() {
            return value;
        }

        private FunctionType(int value) {
            this.value = value;
        }
    }
    
    public static enum State {
        /** 状态:正常 */        normal(0),
        /** 状态:冻结 */        frozen(1),
        /** 状态:注销 */        canceled(2);

        private int value;

        public int getValue() {
            return value;
        }

        private State(int value) {
            this.value = value;
        }
    }
    
    public SysFunction() {
    }
    
    public SysFunction(String iwoid) {
        this.iwoid = iwoid;
    }

    @Id
    @Column(name = "iwoid", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "PARENT_FUNCTION_OID", length = 32)
    public String getParentFunctionOid() {
        return this.parentFunctionOid;
    }

    public void setParentFunctionOid(String parentFunctionOid) {
        this.parentFunctionOid = parentFunctionOid;
    }

    @Column(name = "LOG_FUNCTION_OID", length = 32)
    public String getLogFunctionOid() {
        return this.logFunctionOid;
    }

    public void setLogFunctionOid(String logFunctionOid) {
        this.logFunctionOid = logFunctionOid;
    }

    @Column(name = "function_name", nullable = false, length = 50)
    public String getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Column(name = "alias_name", nullable = false, length = 60)
    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Column(name = "description", length = 300)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "url", unique = true, length = 300)
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "function_level", nullable = false)
    public Integer getFunctionLevel() {
        return this.functionLevel;
    }

    public void setFunctionLevel(Integer functionLevel) {
        this.functionLevel = functionLevel;
    }

    @Column(name = "display_order", nullable = false)
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Column(name = "function_type", nullable = false)
    public Integer getFunctionType() {
        return this.functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    @Column(name = "build_type", nullable = false)
    public Integer getBuildType() {
        return this.buildType;
    }

    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }

    @Column(name = "state", nullable = false)
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[功能名称=");
        builder.append(functionName);
        builder.append(", 功能别名=");
        builder.append(aliasName);
        builder.append(", 功能描述=");
        builder.append(description);
        builder.append(", 功能地址=");
        builder.append(url);
        builder.append(", 功能级别=");
        builder.append(functionLevel);
        builder.append(", 排序权重=");
        builder.append(displayOrder);
        
        builder.append(", 功能类型=");
        
        if (functionType != null) {
            if (functionType == FunctionType.menu.getValue()) {
                builder.append("菜单");
            } else if (functionType == FunctionType.action.getValue()) {
                builder.append("操作");
            }
        }
        
        builder.append(", 状态=");
        
        if (state != null) {
            if (state == State.normal.getValue()) {
                builder.append("正常");
            } else if (state == State.frozen.getValue()) {
                builder.append("冻结");
            } else if (state == State.canceled.getValue()) {
                builder.append("注销");
            }
        }
        
        builder.append("]");
        return builder.toString();
    }
}
