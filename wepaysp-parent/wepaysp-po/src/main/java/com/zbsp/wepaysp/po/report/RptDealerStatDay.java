package com.zbsp.wepaysp.po.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rpt_dealer_stat_day_t")
public class RptDealerStatDay
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3211080843107584522L;
    private String iwoid;
    private String dealerOid;
    private String dealerEmployeeOid;
    private String storeOid;
    private String partnerOid;
    private String partnerEmployeeOid;
    private Timestamp startTime;
    private String dealerId;
    private String dealerName;
    private String storeId;
    private String storeName;
    private String dealerEmployeeId;
    private String dealerEmployeeName;
    private String partnerId;
    private String partnerName;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String partnerEmployeeId;
    private String partnerEmployeeName;
    private Long totalAmount;
    private Long totalMoney;
    private BigDecimal partnerBonus;
    private BigDecimal partnerEmployeeBonus;
    private BigDecimal totalBonus;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public RptDealerStatDay() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "DEALER_OID", nullable = false)
    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    @Column(name = "DEALER_EMPLOYEE_OID", nullable = false)
    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    @Column(name = "STORE_OID", nullable = false)
    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    @Column(name = "PARTNER_OID", nullable = false)
    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    @Column(name = "PARTNER_EMPLOYEE_OID", nullable = false)
    public String getPartnerEmployeeOid() {
        return partnerEmployeeOid;
    }

    public void setPartnerEmployeeOid(String partnerEmployeeOid) {
        this.partnerEmployeeOid = partnerEmployeeOid;
    }

    @Column(name = "START_TIME", nullable = false, length = 0)
    public Timestamp getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Column(name = "DEALER_ID", length = 32)
    public String getDealerId() {
        return this.dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    @Column(name = "DEALER_NAME", nullable = false, length = 256)
    public String getDealerName() {
        return this.dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    @Column(name = "STORE_ID", length = 32)
    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Column(name = "STORE_NAME", length = 256)
    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Column(name = "DEALER_EMPLOYEE_ID", length = 32)
    public String getDealerEmployeeId() {
        return this.dealerEmployeeId;
    }

    public void setDealerEmployeeId(String dealerEmployeeId) {
        this.dealerEmployeeId = dealerEmployeeId;
    }

    @Column(name = "DEALER_EMPLOYEE_NAME", length = 64)
    public String getDealerEmployeeName() {
        return this.dealerEmployeeName;
    }

    public void setDealerEmployeeName(String dealerEmployeeName) {
        this.dealerEmployeeName = dealerEmployeeName;
    }

    @Column(name = "PARTNER_ID", length = 32)
    public String getPartnerId() {
        return this.partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @Column(name = "PARTNER_NAME", nullable = false, length = 256)
    public String getPartnerName() {
        return this.partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
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

    @Column(name = "PARTNER_EMPLOYEE_ID", length = 32)
    public String getPartnerEmployeeId() {
        return this.partnerEmployeeId;
    }

    public void setPartnerEmployeeId(String partnerEmployeeId) {
        this.partnerEmployeeId = partnerEmployeeId;
    }

    @Column(name = "PARTNER_EMPLOYEE_NAME", length = 64)
    public String getPartnerEmployeeName() {
        return this.partnerEmployeeName;
    }

    public void setPartnerEmployeeName(String partnerEmployeeName) {
        this.partnerEmployeeName = partnerEmployeeName;
    }

    @Column(name = "TOTAL_AMOUNT", precision = 16, scale = 0)
    public Long getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "TOTAL_MONEY", precision = 20, scale = 0)
    public Long getTotalMoney() {
        return this.totalMoney;
    }

    public void setTotalMoney(Long totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name = "partner_bonus", precision = 20, scale = 4)
    public BigDecimal getPartnerBonus() {
        return partnerBonus;
    }

    public void setPartnerBonus(BigDecimal partnerBonus) {
        this.partnerBonus = partnerBonus;
    }
    
    @Column(name = "partner_employee_bonus", precision = 20, scale = 4)
    public BigDecimal getPartnerEmployeeBonus() {
        return partnerEmployeeBonus;
    }

    public void setPartnerEmployeeBonus(BigDecimal partnerEmployeeBonus) {
        this.partnerEmployeeBonus = partnerEmployeeBonus;
    }

    @Column(name = "TOTAL_BONUS", precision = 20, scale = 4)
    public BigDecimal getTotalBonus() {
        return this.totalBonus;
    }

    public void setTotalBonus(BigDecimal totalBonus) {
        this.totalBonus = totalBonus;
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
