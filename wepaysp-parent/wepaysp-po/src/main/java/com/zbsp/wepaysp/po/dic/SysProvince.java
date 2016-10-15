package com.zbsp.wepaysp.po.dic;

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
@Table(name = "sys_province_t", uniqueConstraints = @UniqueConstraint(columnNames = "province_code"))
public class SysProvince implements Serializable {
    private static final long serialVersionUID = -6528668481706255462L;
    
    private String iwoid;
    private String provinceCode;
    private String provinceName;
    private String aliasName;
    private int dataType;
    private Integer municipalityType;
    private Integer busiType;
    private int pointHandleStatus;
    private Integer virtualFlag;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum DataType {
        /** 数据类别:福彩数据 */        welfare(0),
        /** 数据类别:银行数据 */        bank(1);

        private int value;

        public int getValue() {
            return value;
        }

        private DataType(int value) {
            this.value = value;
        }
    }
    
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
    
    public static enum BusiType {
        /** 开通省级业务标志:是 */        yes(0),
        /** 开通省级业务标志:否 */        no(1);

        private int value;

        public int getValue() {
            return value;
        }

        private BusiType(int value) {
            this.value = value;
        }
    }
    
    public static enum PointHandleStatus {
        /** 积分计算处理标志:否 */        no(0),
        /** 积分计算处理标志:是*/        yes(1);

        private int value;

        public int getValue() {
            return value;
        }

        private PointHandleStatus(int value) {
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
    
    public SysProvince() {
    }
    
    public SysProvince(String iwoid) {
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

    @Column(name = "province_code", unique = true, nullable = false, length = 6)
    public String getProvinceCode() {
        return this.provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Column(name = "province_name", nullable = false, length = 64)
    public String getProvinceName() {
        return this.provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Column(name = "alias_name", length = 32)
    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Column(name = "data_type", nullable = false)
    public int getDataType() {
        return this.dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Column(name = "municipality_type")
    public Integer getMunicipalityType() {
        return this.municipalityType;
    }

    public void setMunicipalityType(Integer municipalityType) {
        this.municipalityType = municipalityType;
    }

    @Column(name = "busi_type")
    public Integer getBusiType() {
        return this.busiType;
    }

    public void setBusiType(Integer busiType) {
        this.busiType = busiType;
    }

    @Column(name = "point_handle_status", nullable = false)
    public int getPointHandleStatus() {
        return this.pointHandleStatus;
    }

    public void setPointHandleStatus(int pointHandleStatus) {
        this.pointHandleStatus = pointHandleStatus;
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
