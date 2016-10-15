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
@Table(name = "sys_authority_t")
public class SysAuthority implements Serializable {

    private static final long serialVersionUID = 6501157102746341745L;

    private String iwoid;
    private SysRole sysRole;
    private SysUser sysUser;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public SysAuthority() {
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
    @JoinColumn(name = "sys_role_oid", nullable = false)
    public SysRole getSysRole() {
        return this.sysRole;
    }

    public void setSysRole(SysRole sysRole) {
        this.sysRole = sysRole;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_user_oid", nullable = false)
    public SysUser getSysUser() {
        return this.sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
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
