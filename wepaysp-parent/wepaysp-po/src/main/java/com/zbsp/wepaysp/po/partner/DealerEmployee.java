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
@Table(name = "dealer_employee_t")
public class DealerEmployee
    implements java.io.Serializable {

    private static final long serialVersionUID = -4466002587882099690L;
    private String iwoid;
    private Dealer dealer;
    private Store store;
    private String dealerEmployeeId;
    private String employeeName;
    private String moblieNumber;
    private String state;
    private String refundPassword;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public DealerEmployee() {
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
    @JoinColumn(name = "DEALER_OID")
    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_OID")
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Column(name = "DEALER_EMPLOYEE_ID", length = 32)
    public String getDealerEmployeeId() {
        return this.dealerEmployeeId;
    }

    public void setDealerEmployeeId(String dealerEmployeeId) {
        this.dealerEmployeeId = dealerEmployeeId;
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

    @Column(name = "REFUND_PASSWORD", length = 32)
    public String getRefundPassword() {
        return this.refundPassword;
    }

    public void setRefundPassword(String refundPassword) {
        this.refundPassword = refundPassword;
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
