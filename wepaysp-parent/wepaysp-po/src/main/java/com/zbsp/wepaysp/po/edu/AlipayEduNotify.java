package com.zbsp.wepaysp.po.edu;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "alipay_edu_notify_t")
public class AlipayEduNotify
    implements java.io.Serializable {

    private static final long serialVersionUID = 3834489215020126909L;
    private String iwoid;
    private AlipayEduBill alipayEduBill;
    private String outTradeNo;
    private Date notifyTime;
    private String notifyType;
    private String tradeNo;
    private String outBizNo;
    private String buyerId;
    private String buyerLogonId;
    private String sellerId;
    private String sellerEmail;
    private String tradeStatus;
    private Integer totalAmoun;
    private Integer receiptAmount;
    private Integer invoiceAmount;
    private Integer buyerPayAmount;
    private Integer refundFee;
    private Integer pointAmount;
    private String subject;
    private String body;
    private Date gmtCreate;
    private Date gmtPayment;
    private Date gmtRefund;
    private Date gmtClose;
    private String fundBillList;
    private String voucherDetailList;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;
    private String notifyId;

    public AlipayEduNotify() {
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
    @JoinColumn(name = "ALIPAY_EDU_BILL_OID")
    public AlipayEduBill getAlipayEduBill() {
        return this.alipayEduBill;
    }

    public void setAlipayEduBill(AlipayEduBill alipayEduBill) {
        this.alipayEduBill = alipayEduBill;
    }

    @Column(name = "OUT_TRADE_NO", length = 128)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "NOTIFY_TIME", nullable = false, length = 0)
    public Date getNotifyTime() {
        return this.notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    @Column(name = "NOTIFY_TYPE", length = 64)
    public String getNotifyType() {
        return this.notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    @Column(name = "TRADE_NO", length = 64)
    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "OUT_BIZ_NO", length = 64)
    public String getOutBizNo() {
        return this.outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    @Column(name = "BUYER_ID", length = 16)
    public String getBuyerId() {
        return this.buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    @Column(name = "BUYER_LOGON_ID", length = 100)
    public String getBuyerLogonId() {
        return this.buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    @Column(name = "SELLER_ID", length = 30)
    public String getSellerId() {
        return this.sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Column(name = "SELLER_EMAIL", length = 100)
    public String getSellerEmail() {
        return this.sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    @Column(name = "TRADE_STATUS", length = 32)
    public String getTradeStatus() {
        return this.tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "TOTAL_AMOUN")
    public Integer getTotalAmoun() {
        return this.totalAmoun;
    }

    public void setTotalAmoun(Integer totalAmoun) {
        this.totalAmoun = totalAmoun;
    }

    @Column(name = "RECEIPT_AMOUNT")
    public Integer getReceiptAmount() {
        return this.receiptAmount;
    }

    public void setReceiptAmount(Integer receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    @Column(name = "INVOICE_AMOUNT")
    public Integer getInvoiceAmount() {
        return this.invoiceAmount;
    }

    public void setInvoiceAmount(Integer invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @Column(name = "BUYER_PAY_AMOUNT")
    public Integer getBuyerPayAmount() {
        return this.buyerPayAmount;
    }

    public void setBuyerPayAmount(Integer buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }

    @Column(name = "REFUND_FEE")
    public Integer getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    @Column(name = "POINT_AMOUNT")
    public Integer getPointAmount() {
        return this.pointAmount;
    }

    public void setPointAmount(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }

    @Column(name = "SUBJECT", length = 256)
    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "BODY", length = 400)
    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = "GMT_CREATE", length = 0)
    public Date getGmtCreate() {
        return this.gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Column(name = "GMT_PAYMENT", length = 0)
    public Date getGmtPayment() {
        return this.gmtPayment;
    }

    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    @Column(name = "GMT_REFUND", length = 0)
    public Date getGmtRefund() {
        return this.gmtRefund;
    }

    public void setGmtRefund(Date gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    @Column(name = "GMT_CLOSE", length = 0)
    public Date getGmtClose() {
        return this.gmtClose;
    }

    public void setGmtClose(Date gmtClose) {
        this.gmtClose = gmtClose;
    }

    @Column(name = "FUND_BILL_LIST", length = 512)
    public String getFundBillList() {
        return this.fundBillList;
    }

    public void setFundBillList(String fundBillList) {
        this.fundBillList = fundBillList;
    }

    @Column(name = "VOUCHER_DETAIL_LIST", length = 2048)
    public String getVoucherDetailList() {
        return this.voucherDetailList;
    }

    public void setVoucherDetailList(String voucherDetailList) {
        this.voucherDetailList = voucherDetailList;
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
    
    @Column(name = "notify_id", length = 128)
    public String getNotifyId() {
        return notifyId;
    }
    
    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

}
