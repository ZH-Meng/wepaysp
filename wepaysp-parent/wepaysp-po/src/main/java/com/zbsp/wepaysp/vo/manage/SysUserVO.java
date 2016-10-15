/*
 * SysUserVO.java
 * 创建者：杨帆
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.vo.manage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.zbsp.wepaysp.po.dic.SysCity;
import com.zbsp.wepaysp.po.dic.SysProvince;
import com.zbsp.wepaysp.po.manage.SysRole;

/**
 * 系统管理员VO
 * 
 * @author 杨帆
 */
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = -2467954569564498116L;
    
    private String iwoid;
    private String userId;
    private String loginPwd;
    private String userName;
    private Integer gender;
    private Integer age;
    private String department;
    private String position;
    private String lineTel;
    private String email;
    private Integer buildType;
    private Date lastLoginTime;
    private Integer state;
    private Integer dataPermisionType;
    private SysProvince dataPermisionProvince;
    private SysCity dataPermisionCity;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;
    
    private String genderName;
    private String stateName;
    private String dataPermisionTypeName;
    private String roleName;
    // 用户角色列表
    private List<SysRole> userRoleList;
    
    public String getIwoid() {
        return iwoid;
    }
    
    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getLoginPwd() {
        return loginPwd;
    }
    
    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getLineTel() {
        return lineTel;
    }
    
    public void setLineTel(String lineTel) {
        this.lineTel = lineTel;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getBuildType() {
        return buildType;
    }
    
    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }
    
    public Date getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public Integer getState() {
        return state;
    }
    
    public void setState(Integer state) {
        this.state = state;
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
    
    public List<SysRole> getUserRoleList() {
        return userRoleList;
    }
    
    public void setUserRoleList(List<SysRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

	public Integer getDataPermisionType() {
		return dataPermisionType;
	}

	public void setDataPermisionType(Integer dataPermisionType) {
		this.dataPermisionType = dataPermisionType;
	}

	public SysProvince getDataPermisionProvince() {
		return dataPermisionProvince;
	}

	public void setDataPermisionProvince(SysProvince dataPermisionProvince) {
		this.dataPermisionProvince = dataPermisionProvince;
	}

	public SysCity getDataPermisionCity() {
		return dataPermisionCity;
	}

	public void setDataPermisionCity(SysCity dataPermisionCity) {
		this.dataPermisionCity = dataPermisionCity;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getDataPermisionTypeName() {
		return dataPermisionTypeName;
	}

	public void setDataPermisionTypeName(String dataPermisionTypeName) {
		this.dataPermisionTypeName = dataPermisionTypeName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
