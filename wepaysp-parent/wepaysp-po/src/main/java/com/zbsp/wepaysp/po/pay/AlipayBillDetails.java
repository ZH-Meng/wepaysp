package com.zbsp.wepaysp.po.pay;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alipay_bill_details_t")
public class AlipayBillDetails
    implements java.io.Serializable {

    private static final long serialVersionUID = 4143866961366877263L;
    private String iwoid;
    private String tradeNo;
    private String outTradeNo;
    private String billType;
    private String subject;
    private Date gmtCreate;
    private Date gmtClose;
    private String operatorId;
    private String storeId;
    private String terminalId;
    private String storeName;
    private String buyerLogon;
    private Integer totalAmount;
    private Integer receiptAmount;
    private Integer alipayRedEnvelopAmount;
    private Integer pointAmount;
    private Integer alipayDiscountableAmount;
    private Integer dealerDiscountableAmount;
    private Integer couponAmount;
    private String couponName;
    private Integer dealerRedEnvelopAmount;
    private Integer cardAmount;
    private String refundNo;
    private Integer serviceAmount;
    private Integer profitAmount;
    private String billRemark;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;
    private String sourceId;

    /**明细来源*/
    public static enum SourceId {
        /**本系统 1*/ self("1"),
        /**外部 2*/ outer("2");
        
        private String value;
        public String getValue() {
            return this.value;
        }
        
        private SourceId(String value) {
            this.value = value;
        }
    }
    
    public AlipayBillDetails() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "TRADE_NO", length = 64)
    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "OUT_TRADE_NO", length = 64)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "BILL_TYPE", length = 1)
    public String getBillType() {
        return this.billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    @Column(name = "SUBJECT", length = 256)
    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "GMT_CREATE", length = 0)
    public Date getGmtCreate() {
        return this.gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Column(name = "GMT_CLOSE", length = 0)
    public Date getGmtClose() {
        return this.gmtClose;
    }

    public void setGmtClose(Date gmtClose) {
        this.gmtClose = gmtClose;
    }

    @Column(name = "OPERATOR_ID", length = 28)
    public String getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Column(name = "STORE_ID", length = 32)
    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Column(name = "TERMINAL_ID", length = 32)
    public String getTerminalId() {
        return this.terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Column(name = "STORE_NAME", length = 512)
    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Column(name = "BUYER_LOGON", length = 100)
    public String getBuyerLogon() {
        return this.buyerLogon;
    }

    public void setBuyerLogon(String buyerLogon) {
        this.buyerLogon = buyerLogon;
    }

    @Column(name = "TOTAL_AMOUNT")
    public Integer getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "RECEIPT_AMOUNT")
    public Integer getReceiptAmount() {
        return this.receiptAmount;
    }

    public void setReceiptAmount(Integer receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    @Column(name = "ALIPAY_RED_ENVELOP_AMOUNT")
    public Integer getAlipayRedEnvelopAmount() {
        return this.alipayRedEnvelopAmount;
    }

    public void setAlipayRedEnvelopAmount(Integer alipayRedEnvelopAmount) {
        this.alipayRedEnvelopAmount = alipayRedEnvelopAmount;
    }

    @Column(name = "POINT_AMOUNT")
    public Integer getPointAmount() {
        return this.pointAmount;
    }

    public void setPointAmount(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }

    @Column(name = "ALIPAY_DISCOUNTABLE_AMOUNT")
    public Integer getAlipayDiscountableAmount() {
        return this.alipayDiscountableAmount;
    }

    public void setAlipayDiscountableAmount(Integer alipayDiscountableAmount) {
        this.alipayDiscountableAmount = alipayDiscountableAmount;
    }

    @Column(name = "DEALER_DISCOUNTABLE_AMOUNT")
    public Integer getDealerDiscountableAmount() {
        return this.dealerDiscountableAmount;
    }

    public void setDealerDiscountableAmount(Integer dealerDiscountableAmount) {
        this.dealerDiscountableAmount = dealerDiscountableAmount;
    }

    @Column(name = "COUPON_AMOUNT")
    public Integer getCouponAmount() {
        return this.couponAmount;
    }

    public void setCouponAmount(Integer couponAmount) {
        this.couponAmount = couponAmount;
    }

    @Column(name = "COUPON_NAME", length = 32)
    public String getCouponName() {
        return this.couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    @Column(name = "DEALER_RED_ENVELOP_AMOUNT")
    public Integer getDealerRedEnvelopAmount() {
        return this.dealerRedEnvelopAmount;
    }

    public void setDealerRedEnvelopAmount(Integer dealerRedEnvelopAmount) {
        this.dealerRedEnvelopAmount = dealerRedEnvelopAmount;
    }

    @Column(name = "CARD_AMOUNT")
    public Integer getCardAmount() {
        return this.cardAmount;
    }

    public void setCardAmount(Integer cardAmount) {
        this.cardAmount = cardAmount;
    }

    @Column(name = "REFUND_NO", length = 64)
    public String getRefundNo() {
        return this.refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    @Column(name = "SERVICE_AMOUNT")
    public Integer getServiceAmount() {
        return this.serviceAmount;
    }

    public void setServiceAmount(Integer serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    @Column(name = "PROFIT_AMOUNT")
    public Integer getProfitAmount() {
        return this.profitAmount;
    }

    public void setProfitAmount(Integer profitAmount) {
        this.profitAmount = profitAmount;
    }

    @Column(name = "BILL_REMARK", length = 256)
    public String getBillRemark() {
        return this.billRemark;
    }

    public void setBillRemark(String billRemark) {
        this.billRemark = billRemark;
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
    
    @Column(name = "SOURCE_ID", length = 1)
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

}
