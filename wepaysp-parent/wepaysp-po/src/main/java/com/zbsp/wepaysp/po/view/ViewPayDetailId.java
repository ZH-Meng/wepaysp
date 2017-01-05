package com.zbsp.wepaysp.po.view;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ViewPayDetailId
    implements java.io.Serializable {

    private static final long serialVersionUID = -4253970957465990507L;
    private String partnerOid;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String partnerEmployeeOid;
    private String dealerOid;
    private String storeOid;
    private String dealerEmployeeOid;
    private String payType;
    private String transactionId;
    private String outTradeNo;
    private Timestamp transBeginTime;
    private Timestamp transEndTime;
    private Integer tradeStatus;
    private Integer totalFee;
    private Long cashFee;
    private Integer couponFee;
    private Integer refundFee;

    public ViewPayDetailId() {
    }

    public ViewPayDetailId(String partnerOid, Integer partnerLevel, String partner1Oid, String partner2Oid,
        String partner3Oid, String partnerEmployeeOid, String dealerOid, String storeOid, String dealerEmployeeOid,
        String payType, String transactionId, String outTradeNo, Timestamp transBeginTime, Timestamp transEndTime,
        Integer tradeStatus, Integer totalFee, Long cashFee, Integer couponFee, Integer refundFee) {
        this.partnerOid = partnerOid;
        this.partnerLevel = partnerLevel;
        this.partner1Oid = partner1Oid;
        this.partner2Oid = partner2Oid;
        this.partner3Oid = partner3Oid;
        this.partnerEmployeeOid = partnerEmployeeOid;
        this.dealerOid = dealerOid;
        this.storeOid = storeOid;
        this.dealerEmployeeOid = dealerEmployeeOid;
        this.payType = payType;
        this.transactionId = transactionId;
        this.outTradeNo = outTradeNo;
        this.transBeginTime = transBeginTime;
        this.transEndTime = transEndTime;
        this.tradeStatus = tradeStatus;
        this.totalFee = totalFee;
        this.cashFee = cashFee;
        this.couponFee = couponFee;
        this.refundFee = refundFee;
    }

    @Column(name = "PARTNER_OID", length = 32)
    public String getPartnerOid() {
        return this.partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
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

    @Column(name = "PARTNER_EMPLOYEE_OID", length = 32)
    public String getPartnerEmployeeOid() {
        return this.partnerEmployeeOid;
    }

    public void setPartnerEmployeeOid(String partnerEmployeeOid) {
        this.partnerEmployeeOid = partnerEmployeeOid;
    }

    @Column(name = "DEALER_OID", length = 32)
    public String getDealerOid() {
        return this.dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    @Column(name = "STORE_OID", length = 32)
    public String getStoreOid() {
        return this.storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    @Column(name = "DEALER_EMPLOYEE_OID", length = 32)
    public String getDealerEmployeeOid() {
        return this.dealerEmployeeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    @Column(name = "PAY_TYPE", length = 1)
    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Column(name = "TRANSACTION_ID", length = 32)
    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "OUT_TRADE_NO", length = 32)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "TRANS_BEGIN_TIME", length = 0)
    public Timestamp getTransBeginTime() {
        return this.transBeginTime;
    }

    public void setTransBeginTime(Timestamp transBeginTime) {
        this.transBeginTime = transBeginTime;
    }

    @Column(name = "TRANS_END_TIME", length = 0)
    public Timestamp getTransEndTime() {
        return this.transEndTime;
    }

    public void setTransEndTime(Timestamp transEndTime) {
        this.transEndTime = transEndTime;
    }

    @Column(name = "TRADE_STATUS")
    public Integer getTradeStatus() {
        return this.tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "TOTAL_FEE")
    public Integer getTotalFee() {
        return this.totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    @Column(name = "CASH_FEE")
    public Long getCashFee() {
        return this.cashFee;
    }

    public void setCashFee(Long cashFee) {
        this.cashFee = cashFee;
    }

    @Column(name = "COUPON_FEE")
    public Integer getCouponFee() {
        return this.couponFee;
    }

    public void setCouponFee(Integer couponFee) {
        this.couponFee = couponFee;
    }

    @Column(name = "REFUND_FEE")
    public Integer getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof ViewPayDetailId))
            return false;
        ViewPayDetailId castOther = (ViewPayDetailId) other;

        return ((this.getPartnerOid() == castOther.getPartnerOid()) || (this.getPartnerOid() != null
            && castOther.getPartnerOid() != null && this.getPartnerOid().equals(castOther.getPartnerOid())))
            && ((this.getPartnerLevel() == castOther.getPartnerLevel()) || (this.getPartnerLevel() != null
                && castOther.getPartnerLevel() != null && this.getPartnerLevel().equals(castOther.getPartnerLevel())))
            && ((this.getPartner1Oid() == castOther.getPartner1Oid()) || (this.getPartner1Oid() != null
                && castOther.getPartner1Oid() != null && this.getPartner1Oid().equals(castOther.getPartner1Oid())))
            && ((this.getPartner2Oid() == castOther.getPartner2Oid()) || (this.getPartner2Oid() != null
                && castOther.getPartner2Oid() != null && this.getPartner2Oid().equals(castOther.getPartner2Oid())))
            && ((this.getPartner3Oid() == castOther.getPartner3Oid()) || (this.getPartner3Oid() != null
                && castOther.getPartner3Oid() != null && this.getPartner3Oid().equals(castOther.getPartner3Oid())))
            && ((this.getPartnerEmployeeOid() == castOther.getPartnerEmployeeOid()) || (this.getPartnerEmployeeOid() != null
                && castOther.getPartnerEmployeeOid() != null && this.getPartnerEmployeeOid().equals(
                castOther.getPartnerEmployeeOid())))
            && ((this.getDealerOid() == castOther.getDealerOid()) || (this.getDealerOid() != null
                && castOther.getDealerOid() != null && this.getDealerOid().equals(castOther.getDealerOid())))
            && ((this.getStoreOid() == castOther.getStoreOid()) || (this.getStoreOid() != null
                && castOther.getStoreOid() != null && this.getStoreOid().equals(castOther.getStoreOid())))
            && ((this.getDealerEmployeeOid() == castOther.getDealerEmployeeOid()) || (this.getDealerEmployeeOid() != null
                && castOther.getDealerEmployeeOid() != null && this.getDealerEmployeeOid().equals(
                castOther.getDealerEmployeeOid())))
            && ((this.getPayType() == castOther.getPayType()) || (this.getPayType() != null
                && castOther.getPayType() != null && this.getPayType().equals(castOther.getPayType())))
            && ((this.getTransactionId() == castOther.getTransactionId()) || (this.getTransactionId() != null
                && castOther.getTransactionId() != null && this.getTransactionId().equals(castOther.getTransactionId())))
            && ((this.getOutTradeNo() == castOther.getOutTradeNo()) || (this.getOutTradeNo() != null
                && castOther.getOutTradeNo() != null && this.getOutTradeNo().equals(castOther.getOutTradeNo())))
            && ((this.getTransBeginTime() == castOther.getTransBeginTime()) || (this.getTransBeginTime() != null
                && castOther.getTransBeginTime() != null && this.getTransBeginTime().equals(
                castOther.getTransBeginTime())))
            && ((this.getTransEndTime() == castOther.getTransEndTime()) || (this.getTransEndTime() != null
                && castOther.getTransEndTime() != null && this.getTransEndTime().equals(castOther.getTransEndTime())))
            && ((this.getTradeStatus() == castOther.getTradeStatus()) || (this.getTradeStatus() != null
                && castOther.getTradeStatus() != null && this.getTradeStatus().equals(castOther.getTradeStatus())))
            && ((this.getTotalFee() == castOther.getTotalFee()) || (this.getTotalFee() != null
                && castOther.getTotalFee() != null && this.getTotalFee().equals(castOther.getTotalFee())))
            && ((this.getCashFee() == castOther.getCashFee()) || (this.getCashFee() != null
                && castOther.getCashFee() != null && this.getCashFee().equals(castOther.getCashFee())))
            && ((this.getCouponFee() == castOther.getCouponFee()) || (this.getCouponFee() != null
                && castOther.getCouponFee() != null && this.getCouponFee().equals(castOther.getCouponFee())))
            && ((this.getRefundFee() == castOther.getRefundFee()) || (this.getRefundFee() != null
                && castOther.getRefundFee() != null && this.getRefundFee().equals(castOther.getRefundFee())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + (getPartnerOid() == null ? 0 : this.getPartnerOid().hashCode());
        result = 37 * result + (getPartnerLevel() == null ? 0 : this.getPartnerLevel().hashCode());
        result = 37 * result + (getPartner1Oid() == null ? 0 : this.getPartner1Oid().hashCode());
        result = 37 * result + (getPartner2Oid() == null ? 0 : this.getPartner2Oid().hashCode());
        result = 37 * result + (getPartner3Oid() == null ? 0 : this.getPartner3Oid().hashCode());
        result = 37 * result + (getPartnerEmployeeOid() == null ? 0 : this.getPartnerEmployeeOid().hashCode());
        result = 37 * result + (getDealerOid() == null ? 0 : this.getDealerOid().hashCode());
        result = 37 * result + (getStoreOid() == null ? 0 : this.getStoreOid().hashCode());
        result = 37 * result + (getDealerEmployeeOid() == null ? 0 : this.getDealerEmployeeOid().hashCode());
        result = 37 * result + (getPayType() == null ? 0 : this.getPayType().hashCode());
        result = 37 * result + (getTransactionId() == null ? 0 : this.getTransactionId().hashCode());
        result = 37 * result + (getOutTradeNo() == null ? 0 : this.getOutTradeNo().hashCode());
        result = 37 * result + (getTransBeginTime() == null ? 0 : this.getTransBeginTime().hashCode());
        result = 37 * result + (getTransEndTime() == null ? 0 : this.getTransEndTime().hashCode());
        result = 37 * result + (getTradeStatus() == null ? 0 : this.getTradeStatus().hashCode());
        result = 37 * result + (getTotalFee() == null ? 0 : this.getTotalFee().hashCode());
        result = 37 * result + (getCashFee() == null ? 0 : this.getCashFee().hashCode());
        result = 37 * result + (getCouponFee() == null ? 0 : this.getCouponFee().hashCode());
        result = 37 * result + (getRefundFee() == null ? 0 : this.getRefundFee().hashCode());
        return result;
    }

}
