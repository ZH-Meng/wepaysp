package com.zbsp.wepaysp.po.dic;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sys_county_t", uniqueConstraints = @UniqueConstraint(columnNames = "county_code"))
public class SysCounty implements Serializable {
    private static final long serialVersionUID = 1890982938940254043L;
    private String iwoid;
    private SysCity sysCity;
    private String countyCode;
    private String countyName;
    private Integer dataType;
    private Integer virtualFlag;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public SysCounty() {
    }

    public SysCounty(String iwoid) {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_city_oid", nullable = false)
    public SysCity getSysCity() {
        return this.sysCity;
    }

    public void setSysCity(SysCity sysCity) {
        this.sysCity = sysCity;
    }

    @Column(name = "county_code", unique = true, nullable = false, length = 6)
    public String getCountyCode() {
        return this.countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    @Column(name = "county_name", nullable = false, length = 64)
    public String getCountyName() {
        return this.countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    @Column(name = "data_type", nullable = false)
    public Integer getDataType() {
        return this.dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @Column(name = "virtual_flag")
    public Integer getVirtualFlag() {
        return this.virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    @Column(name = "creator", nullable = false, length = 64)
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

    @Column(name = "modifier", length = 64)
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
