package com.zbsp.wepaysp.po.partner;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "partner_employee_t")
public class PartnerEmployee
    implements java.io.Serializable {
    
    private static final long serialVersionUID = 254410044039304852L;
    private String iwoid;
    private Partner partner;
    private String partnerEmployeeId;
    private String employeeName;
    private String moblieNumber;
    private String state;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public PartnerEmployee() {
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

    @Column(name = "PARTNER_EMPLOYEE_ID", length = 32)
    public String getPartnerEmployeeId() {
        return this.partnerEmployeeId;
    }

    public void setPartnerEmployeeId(String partnerEmployeeId) {
        this.partnerEmployeeId = partnerEmployeeId;
    }

    @Column(name = "EMPLOYEE_NAME", length = 64)
    public String getEmployeeName() {
        return this.employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Column(name = "MOBLIE_NUMBER", length = 32)
    public String getMoblieNumber() {
        return this.moblieNumber;
    }

    public void setMoblieNumber(String moblieNumber) {
        this.moblieNumber = moblieNumber;
    }

    @Column(name = "STATE", length = 1)
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "CREATOR", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATE_TIME", nullable = false, length = 0)
    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
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
    public Timestamp getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
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
