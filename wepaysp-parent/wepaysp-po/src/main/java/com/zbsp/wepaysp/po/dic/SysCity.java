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
@Table(name = "sys_city_t", uniqueConstraints = @UniqueConstraint(columnNames = "city_code"))
public class SysCity implements Serializable {
    private static final long serialVersionUID = -4232930126902161188L;
    
    private String iwoid;
    private SysProvince sysProvince;
    private String cityCode;
    private String cityName;
    private String aliasName;
    private Integer dataType;
    private Integer municipalityType;
    private Integer virtualFlag;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum MunicipalityType {
        /** 直辖市类型:是 */        yes(0),
        /** 直辖市类型:否 */        no(1);

        private int value;

        public int getValue() {
            return value;
        }

        private MunicipalityType(int value) {
            this.value = value;
        }
    }
    
    public static enum VirtualFlag {
        /** 虚拟标识：不是 */    no(0),
        /** 虚拟标识：是 */      yes(1);

        private VirtualFlag(int value) {
            this.value = value;
        }
        
        private int value;

        public int getValue() {
            return value;
        }
    }
    
    public SysCity() {
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
    @JoinColumn(name = "sys_province_oid", nullable = false)
    public SysProvince getSysProvince() {
        return this.sysProvince;
    }

    public void setSysProvince(SysProvince sysProvince) {
        this.sysProvince = sysProvince;
    }

    @Column(name = "city_code", unique = true, nullable = false, length = 6)
    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Column(name = "city_name", nullable = false, length = 64)
    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Column(name = "alias_name", length = 32)
    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Column(name = "data_type", nullable = false)
    public Integer getDataType() {
        return this.dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @Column(name = "municipality_type", nullable = false)
    public Integer getMunicipalityType() {
        return this.municipalityType;
    }

    public void setMunicipalityType(Integer municipalityType) {
        this.municipalityType = municipalityType;
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
