package com.zbsp.wepaysp.vo.pay;

import java.io.Serializable;
import java.sql.Timestamp;

public class AliPayDetailsVO
    implements Serializable {

    private static final long serialVersionUID = 3368103692208601283L;

    private String iwoid;
    private String dealerOid;
    private String storeOid;
    private String dealerEmployeeOid;
    private String payType;

    /** 返佣必填项，支付宝分配的系统商编号 SysServiceProviderId */
    private String isvPartnerId;
    private String appId;
    private String appAuthToken;
    private String outTradeNo;
    private String scene;
    private String authCode;
    private String subject;
    private String sellerId;
    private Integer totalAmount;
    private Integer discountableAmount;
    private Integer undiscountableAmount;
    private String body;
    private String operatorId;
    private String storeId;
    private String terminalId;
    private String alipayStoreId;
    private String code;
    private String msg;
    private String subCode;
    private String subMsg;
    private String sign;
    private String tradeNo;
    private String buyerLogonId;
    private Integer receiptAmount;
    private Integer buyerPayAmount;
    private Integer pointAmount;
    private Integer invoiceAmount;
    private Timestamp gmtPayment;
    private Long cardBalance;
    private String storeName;
    private String buyerUserId;
    private String discountGoodsDetail;
    private Timestamp transBeginTime;
    private Timestamp transEndTime;
    private Integer refundFee;
    private Integer tradeStatus;
    private Timestamp notifyTime;
    private String notifyId;
    private Timestamp gmtRefund;
    private Timestamp gmtClose;
    private String remark;

    /** 后台管理查询和展示需要的参数 */
    private String dealerName;
    // private String storeName;
    private String dealerEmployeeName;
    private String dealerEmployeeId;
    
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String partnerEmployeeId;
    private String dealerId;
    private String partnerId;
    private String partnerName;
    private String partnerEmployeeName;

    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getIsvPartnerId() {
        return isvPartnerId;
    }

    public void setIsvPartnerId(String isvPartnerId) {
        this.isvPartnerId = isvPartnerId;
    }

    public String getAppAuthToken() {
        return appAuthToken;
    }

    public void setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getDiscountableAmount() {
        return discountableAmount;
    }

    public void setDiscountableAmount(Integer discountableAmount) {
        this.discountableAmount = discountableAmount;
    }

    public Integer getUndiscountableAmount() {
        return undiscountableAmount;
    }

    public void setUndiscountableAmount(Integer undiscountableAmount) {
        this.undiscountableAmount = undiscountableAmount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getAlipayStoreId() {
        return alipayStoreId;
    }

    public void setAlipayStoreId(String alipayStoreId) {
        this.alipayStoreId = alipayStoreId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    public Integer getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(Integer receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public Integer getBuyerPayAmount() {
        return buyerPayAmount;
    }

    public void setBuyerPayAmount(Integer buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }

    public Integer getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }

    public Integer getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Integer invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Timestamp getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(Timestamp gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    public Long getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(Long cardBalance) {
        this.cardBalance = cardBalance;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getDiscountGoodsDetail() {
        return discountGoodsDetail;
    }

    public void setDiscountGoodsDetail(String discountGoodsDetail) {
        this.discountGoodsDetail = discountGoodsDetail;
    }

    public Timestamp getTransBeginTime() {
        return transBeginTime;
    }

    public void setTransBeginTime(Timestamp transBeginTime) {
        this.transBeginTime = transBeginTime;
    }

    public Timestamp getTransEndTime() {
        return transEndTime;
    }

    public void setTransEndTime(Timestamp transEndTime) {
        this.transEndTime = transEndTime;
    }

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerEmployeeName() {
        return dealerEmployeeName;
    }

    public void setDealerEmployeeName(String dealerEmployeeName) {
        this.dealerEmployeeName = dealerEmployeeName;
    }

    public String getDealerEmployeeId() {
        return dealerEmployeeId;
    }

    public void setDealerEmployeeId(String dealerEmployeeId) {
        this.dealerEmployeeId = dealerEmployeeId;
    }

    public String getPartner2Oid() {
        return partner2Oid;
    }

    public void setPartner2Oid(String partner2Oid) {
        this.partner2Oid = partner2Oid;
    }

    public String getPartner3Oid() {
        return partner3Oid;
    }

    public void setPartner3Oid(String partner3Oid) {
        this.partner3Oid = partner3Oid;
    }

    public String getPartnerEmployeeId() {
        return partnerEmployeeId;
    }

    public void setPartnerEmployeeId(String partnerEmployeeId) {
        this.partnerEmployeeId = partnerEmployeeId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmployeeName() {
        return partnerEmployeeName;
    }

    public void setPartnerEmployeeName(String partnerEmployeeName) {
        this.partnerEmployeeName = partnerEmployeeName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Timestamp getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Timestamp notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public Timestamp getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(Timestamp gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    public Timestamp getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(Timestamp gmtClose) {
        this.gmtClose = gmtClose;
    }

    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getPartner1Oid() {
		return partner1Oid;
	}

	public void setPartner1Oid(String partner1Oid) {
		this.partner1Oid = partner1Oid;
	}

}
