package com.zbsp.wepaysp.po.partner;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "school_t")
public class School
    implements java.io.Serializable {
    
    private static final long serialVersionUID = 1268923414523270973L;
    private String iwoid;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private String schoolNo;
    private String schoolPid;
    private String shcoolName;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String schoolIcon;
    private String schoolIconType;
    private String schoolStdcode;
    private String schoolType;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String isvName;
    private String isvNotifyUrl;
    private String isvPid;
    private String isvPhone;
    private String bankcardNo;
    private String bankUid;
    private String bankNotifyUrl;
    private String techSupportPerson;
    private String techSupportPhone;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public School() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_OID")
    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_EMPLOYEE_OID")
    public PartnerEmployee getPartnerEmployee() {
        return this.partnerEmployee;
    }

    public void setPartnerEmployee(PartnerEmployee partnerEmployee) {
        this.partnerEmployee = partnerEmployee;
    }

    @Column(name = "SCHOOL_NO", length = 32)
    public String getSchoolNo() {
        return this.schoolNo;
    }

    public void setSchoolNo(String schoolNo) {
        this.schoolNo = schoolNo;
    }

    @Column(name = "SCHOOL_PID", length = 128)
    public String getSchoolPid() {
        return this.schoolPid;
    }

    public void setSchoolPid(String schoolPid) {
        this.schoolPid = schoolPid;
    }

    @Column(name = "SHCOOL_NAME", length = 256)
    public String getShcoolName() {
        return this.shcoolName;
    }

    public void setShcoolName(String shcoolName) {
        this.shcoolName = shcoolName;
    }

    @Column(name = "PARTNER_LEVEL")
    public Integer getPartnerLevel() {
        return this.partnerLevel;
    }

    public void setPartnerLevel(Integer partnerLevel) {
        this.partnerLevel = partnerLevel;
    }

    @Column(name = "PARTNER1_OID", length = 32)
    public String getPartner1Oid() {
        return this.partner1Oid;
    }

    public void setPartner1Oid(String partner1Oid) {
        this.partner1Oid = partner1Oid;
    }

    @Column(name = "PARTNER2_OID", length = 32)
    public String getPartner2Oid() {
        return this.partner2Oid;
    }

    public void setPartner2Oid(String partner2Oid) {
        this.partner2Oid = partner2Oid;
    }

    @Column(name = "PARTNER3_OID", length = 32)
    public String getPartner3Oid() {
        return this.partner3Oid;
    }

    public void setPartner3Oid(String partner3Oid) {
        this.partner3Oid = partner3Oid;
    }

    @Column(name = "SCHOOL_ICON", length = 256)
    public String getSchoolIcon() {
        return this.schoolIcon;
    }

    public void setSchoolIcon(String schoolIcon) {
        this.schoolIcon = schoolIcon;
    }

    @Column(name = "SCHOOL_ICON_TYPE", length = 10)
    public String getSchoolIconType() {
        return this.schoolIconType;
    }

    public void setSchoolIconType(String schoolIconType) {
        this.schoolIconType = schoolIconType;
    }

    @Column(name = "SCHOOL_STDCODE", length = 16)
    public String getSchoolStdcode() {
        return this.schoolStdcode;
    }

    public void setSchoolStdcode(String schoolStdcode) {
        this.schoolStdcode = schoolStdcode;
    }

    @Column(name = "SCHOOL_TYPE", length = 6)
    public String getSchoolType() {
        return this.schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    @Column(name = "PROVINCE_CODE", length = 8)
    public String getProvinceCode() {
        return this.provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Column(name = "PROVINCE_NAME", length = 32)
    public String getProvinceName() {
        return this.provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Column(name = "CITY_CODE", length = 16)
    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Column(name = "CITY_NAME", length = 64)
    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Column(name = "DISTRICT_CODE", length = 16)
    public String getDistrictCode() {
        return this.districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    @Column(name = "DISTRICT_NAME", length = 64)
    public String getDistrictName() {
        return this.districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Column(name = "ISV_NAME", length = 256)
    public String getIsvName() {
        return this.isvName;
    }

    public void setIsvName(String isvName) {
        this.isvName = isvName;
    }

    @Column(name = "ISV_NOTIFY_URL", length = 256)
    public String getIsvNotifyUrl() {
        return this.isvNotifyUrl;
    }

    public void setIsvNotifyUrl(String isvNotifyUrl) {
        this.isvNotifyUrl = isvNotifyUrl;
    }

    @Column(name = "ISV_PID", length = 128)
    public String getIsvPid() {
        return this.isvPid;
    }

    public void setIsvPid(String isvPid) {
        this.isvPid = isvPid;
    }

    @Column(name = "ISV_PHONE", length = 20)
    public String getIsvPhone() {
        return this.isvPhone;
    }

    public void setIsvPhone(String isvPhone) {
        this.isvPhone = isvPhone;
    }

    @Column(name = "BANKCARD_NO", length = 32)
    public String getBankcardNo() {
        return this.bankcardNo;
    }

    public void setBankcardNo(String bankcardNo) {
        this.bankcardNo = bankcardNo;
    }

    @Column(name = "BANK_UID", length = 32)
    public String getBankUid() {
        return this.bankUid;
    }

    public void setBankUid(String bankUid) {
        this.bankUid = bankUid;
    }

    @Column(name = "BANK_NOTIFY_URL", length = 256)
    public String getBankNotifyUrl() {
        return this.bankNotifyUrl;
    }

    public void setBankNotifyUrl(String bankNotifyUrl) {
        this.bankNotifyUrl = bankNotifyUrl;
    }

    @Column(name = "TECH_SUPPORT_PERSON", length = 32)
    public String getTechSupportPerson() {
        return this.techSupportPerson;
    }

    public void setTechSupportPerson(String techSupportPerson) {
        this.techSupportPerson = techSupportPerson;
    }

    @Column(name = "TECH_SUPPORT_PHONE", length = 32)
    public String getTechSupportPhone() {
        return this.techSupportPhone;
    }

    public void setTechSupportPhone(String techSupportPhone) {
        this.techSupportPhone = techSupportPhone;
    }

    @Column(name = "CREATOR", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATE_TIME", nullable = false, length = 0)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "MODIFIER", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Column(name = "MODIFY_TIME", length = 0)
    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "REMARK", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
