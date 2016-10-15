package com.zbsp.wepaysp.vo.manage;

import java.io.Serializable;
import java.util.Date;

public class SysRoleVO implements Serializable, Comparable<SysRoleVO> {

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
    
    private String roleLevelName;
    private String stateName;

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
    
    public SysRoleVO() {
    }
    
    public SysRoleVO(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRoleLevel() {
        return this.roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Integer getBuildType() {
        return this.buildType;
    }

    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }

    public String getRoleIndex() {
        return this.roleIndex;
    }

    public void setRoleIndex(String roleIndex) {
        this.roleIndex = roleIndex;
    }
    
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getUseState() {
        return this.useState;
    }

    public void setUseState(Integer useState) {
        this.useState = useState;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRoleLevelName() {
		return roleLevelName;
	}

	public void setRoleLevelName(String roleLevelName) {
		this.roleLevelName = roleLevelName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
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
    public int compareTo(SysRoleVO o) {
        if (roleId == null || o == null || o.getRoleId() == null) {
            return -1;
        }
        
        return roleId.compareTo(o.getRoleId());
    }
}
