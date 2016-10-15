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
@Table(name = "sys_role_t", uniqueConstraints = @UniqueConstraint(columnNames = "role_id"))
public class SysRole implements Serializable, Comparable<SysRole> {

    private static final long serialVersionUID = -857885691491163476L;

    private String iwoid;
    private String roleId;
    private String roleName;
    private String description;
    private Integer roleLevel;
    private Integer buildType;
    private String roleIndex;
    private Integer state;
    private Integer useState;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum Level {
        /** 级别:应用 */        normal(0),
        /** 级别:管理 */        manage(1);

        private int value;

        public int getValue() {
            return value;
        }

        private Level(int value) {
            this.value = value;
        }
    }
    
    public static enum UseState {
        /** 使用状态:未使用 */        notUsed(0),
        /** 使用状态:已使用 */        used(1);

        private int value;

        public int getValue() {
            return value;
        }

        private UseState(int value) {
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
    
    public SysRole() {
    }
    
    public SysRole(String iwoid) {
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

    @Column(name = "role_id", unique = true, nullable = false, length = 20)
    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name = "role_name", nullable = false, length = 50)
    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name = "description", length = 300)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "role_level", nullable = false)
    public Integer getRoleLevel() {
        return this.roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    @Column(name = "build_type", nullable = false)
    public Integer getBuildType() {
        return this.buildType;
    }

    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }

    @Column(name = "role_index", length = 100)
    public String getRoleIndex() {
        return this.roleIndex;
    }

    public void setRoleIndex(String roleIndex) {
        this.roleIndex = roleIndex;
    }
    
    @Column(name = "state", nullable = false)
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Column(name = "use_state", nullable = false)
    public Integer getUseState() {
        return this.useState;
    }

    public void setUseState(Integer useState) {
        this.useState = useState;
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
        builder.append("[角色Id=");
        builder.append(roleId);
        builder.append(", 角色名称=");
        builder.append(roleName);
        builder.append(", 角色描述=");
        builder.append(description);
        builder.append(", 角色级别=");
        
        if (roleLevel != null) {
            if (roleLevel == Level.normal.getValue()) {
                builder.append("应用");
            } else if (roleLevel == Level.manage.getValue()) {
                builder.append("管理");
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
        builder.append(", 角色首页=");
        if (null != roleIndex) {
        	builder.append(roleIndex);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int compareTo(SysRole o) {
        if (roleId == null || o == null || o.getRoleId() == null) {
            return -1;
        }
        
        return roleId.compareTo(o.getRoleId());
    }
}
