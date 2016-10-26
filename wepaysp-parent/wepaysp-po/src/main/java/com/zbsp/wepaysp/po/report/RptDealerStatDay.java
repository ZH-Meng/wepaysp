package com.zbsp.wepaysp.po.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;

@Entity
@Table(name = "rpt_dealer_stat_day_t")
public class RptDealerStatDay
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3211080843107584522L;
    private String iwoid;
    private Dealer dealer;
    private DealerEmployee dealerEmployee;
    private Store store;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private Timestamp startTime;
    private String dealerId;
    private String dealerName;
    private String storeId;
    private String storeName;
    private String dealerEmployeeId;
    private String dealerEmployeeName;
    private String partneId;
    private String partneName;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String partnerEmployeeId;
    private String partnerEmployeeName;
    private Long totalAmount;
    private BigDecimal totalMoney;
    private Double totalBonus;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_OID", nullable = false)
    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_EMPLOYEE_OID")
    public DealerEmployee getDealerEmployee() {
        return this.dealerEmployee;
    }

    public void setDealerEmployee(DealerEmployee dealerEmployee) {
        this.dealerEmployee = dealerEmployee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOTRE_OID")
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_OID", nullable = false)
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

    @Column(name = "PARTNE_ID", length = 32)
    public String getPartneId() {
        return this.partneId;
    }

    public void setPartneId(String partneId) {
        this.partneId = partneId;
    }

    @Column(name = "PARTNE_NAME", nullable = false, length = 256)
    public String getPartneName() {
        return this.partneName;
    }

    public void setPartneName(String partneName) {
        this.partneName = partneName;
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
    public BigDecimal getTotalMoney() {
        return this.totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name = "TOTAL_BONUS", precision = 20, scale = 4)
    public Double getTotalBonus() {
        return this.totalBonus;
    }

    public void setTotalBonus(Double totalBonus) {
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
