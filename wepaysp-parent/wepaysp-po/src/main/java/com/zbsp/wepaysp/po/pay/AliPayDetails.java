package com.zbsp.wepaysp.po.pay;

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
@Table(name = "ali_pay_details_t")
public class AliPayDetails
    implements java.io.Serializable {

    private static final long serialVersionUID = 6012675304878197859L;

    private String iwoid;
    private Dealer dealer;
    private DealerEmployee dealerEmployee;
    private Store store;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private String payType;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
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
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public static void main(String[] args) {
        System.out.println(Scene.BAR_CODE.name());
    }

    /** 支付场景 */
    public static enum Scene {
        /** 条码支付 */
        BAR_CODE,
        /** 声波支付 */
        WAVE_CODE
    }

    public AliPayDetails() {
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
    @JoinColumn(name = "DEALER_EMPLOYEE_OID")
    public DealerEmployee getDealerEmployee() {
        return this.dealerEmployee;
    }

    public void setDealerEmployee(DealerEmployee dealerEmployee) {
        this.dealerEmployee = dealerEmployee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_OID")
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
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

    @Column(name = "PAY_TYPE", length = 1)
    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    @Column(name = "isv_partner_id", length = 16)
    public String getIsvPartnerId() {
        return this.isvPartnerId;
    }

    public void setIsvPartnerId(String isvPartnerId) {
        this.isvPartnerId = isvPartnerId;
    }

    @Column(name = "APP_ID", length = 32)
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "APP_AUTH_TOKEN", length = 40)
    public String getAppAuthToken() {
        return this.appAuthToken;
    }

    public void setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
    }

    @Column(name = "OUT_TRADE_NO", length = 64)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "SCENE", length = 32)
    public String getScene() {
        return this.scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    @Column(name = "AUTH_CODE", length = 32)
    public String getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Column(name = "SUBJECT", length = 256)
    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "SELLER_ID", length = 28)
    public String getSellerId() {
        return this.sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Column(name = "TOTAL_AMOUNT")
    public Integer getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "DISCOUNTABLE_AMOUNT")
    public Integer getDiscountableAmount() {
        return this.discountableAmount;
    }

    public void setDiscountableAmount(Integer discountableAmount) {
        this.discountableAmount = discountableAmount;
    }

    @Column(name = "UNDISCOUNTABLE_AMOUNT")
    public Integer getUndiscountableAmount() {
        return this.undiscountableAmount;
    }

    public void setUndiscountableAmount(Integer undiscountableAmount) {
        this.undiscountableAmount = undiscountableAmount;
    }

    @Column(name = "BODY", length = 128)
    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
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

    @Column(name = "ALIPAY_STORE_ID", length = 32)
    public String getAlipayStoreId() {
        return this.alipayStoreId;
    }

    public void setAlipayStoreId(String alipayStoreId) {
        this.alipayStoreId = alipayStoreId;
    }

    @Column(name = "CODE", length = 16)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "MSG", length = 128)
    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Column(name = "SUB_CODE", length = 64)
    public String getSubCode() {
        return this.subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Column(name = "SUB_MSG", length = 128)
    public String getSubMsg() {
        return this.subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    @Column(name = "SIGN", length = 256)
    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Column(name = "TRADE_NO", length = 64)
    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "BUYER_LOGON_ID", length = 100)
    public String getBuyerLogonId() {
        return this.buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    @Column(name = "RECEIPT_AMOUNT")
    public Integer getReceiptAmount() {
        return this.receiptAmount;
    }

    public void setReceiptAmount(Integer receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    @Column(name = "BUYER_PAY_AMOUNT")
    public Integer getBuyerPayAmount() {
        return this.buyerPayAmount;
    }

    public void setBuyerPayAmount(Integer buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }

    @Column(name = "POINT_AMOUNT")
    public Integer getPointAmount() {
        return this.pointAmount;
    }

    public void setPointAmount(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }

    @Column(name = "INVOICE_AMOUNT")
    public Integer getInvoiceAmount() {
        return this.invoiceAmount;
    }

    public void setInvoiceAmount(Integer invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @Column(name = "GMT_PAYMENT", length = 0)
    public Timestamp getGmtPayment() {
        return this.gmtPayment;
    }

    public void setGmtPayment(Timestamp gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    @Column(name = "CARD_BALANCE")
    public Long getCardBalance() {
        return this.cardBalance;
    }

    public void setCardBalance(Long cardBalance) {
        this.cardBalance = cardBalance;
    }

    @Column(name = "STORE_NAME", length = 512)
    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Column(name = "BUYER_USER_ID", length = 28)
    public String getBuyerUserId() {
        return this.buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    @Column(name = "DISCOUNT_GOODS_DETAIL", length = 512)
    public String getDiscountGoodsDetail() {
        return this.discountGoodsDetail;
    }

    public void setDiscountGoodsDetail(String discountGoodsDetail) {
        this.discountGoodsDetail = discountGoodsDetail;
    }

    @Column(name = "TRANS_BEGIN_TIME", nullable = false, length = 0)
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

    @Column(name = "REFUND_FEE")
    public Integer getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    @Column(name = "TRADE_STATUS")
    public Integer getTradeStatus() {
        return this.tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "notify_time", nullable = false, length = 0)
    public Timestamp getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Timestamp notifyTime) {
        this.notifyTime = notifyTime;
    }

    @Column(name = "notify_id", length = 128)
    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    @Column(name = "gmt_refund", nullable = false, length = 0)
    public Timestamp getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(Timestamp gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    @Column(name = "gmt_close", nullable = false, length = 0)
    public Timestamp getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(Timestamp gmtClose) {
        this.gmtClose = gmtClose;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AliPayDetails [");
        if (iwoid != null)
            builder.append("iwoid=").append(iwoid).append(", ");
        if (dealer != null)
            builder.append("dealerOid=").append(dealer.getIwoid()).append(", ");
        if (dealerEmployee != null)
            builder.append("dealerEmployeeOid=").append(dealerEmployee.getIwoid()).append(", ");
        if (store != null)
            builder.append("storeOid=").append(store.getIwoid()).append(", ");
        if (partner != null)
            builder.append("partnerOid=").append(partner.getIwoid()).append(", ");
        if (partnerEmployee != null)
            builder.append("partnerEmployeeOid=").append(partnerEmployee.getIwoid()).append(", ");
        if (payType != null)
            builder.append("payType=").append(payType).append(", ");
        if (partnerLevel != null)
            builder.append("partnerLevel=").append(partnerLevel).append(", ");
        if (partner1Oid != null)
            builder.append("partner1Oid=").append(partner1Oid).append(", ");
        if (partner2Oid != null)
            builder.append("partner2Oid=").append(partner2Oid).append(", ");
        if (partner3Oid != null)
            builder.append("partner3Oid=").append(partner3Oid).append(", ");
        if (isvPartnerId != null)
            builder.append("isvPartnerId=").append(isvPartnerId).append(", ");
        if (appAuthToken != null)
            builder.append("appAuthToken=").append(appAuthToken).append(", ");
        if (outTradeNo != null)
            builder.append("outTradeNo=").append(outTradeNo).append(", ");
        if (scene != null)
            builder.append("scene=").append(scene).append(", ");
        if (authCode != null)
            builder.append("authCode=").append(authCode).append(", ");
        if (subject != null)
            builder.append("subject=").append(subject).append(", ");
        if (sellerId != null)
            builder.append("sellerId=").append(sellerId).append(", ");
        if (totalAmount != null)
            builder.append("totalAmount=").append(totalAmount).append(", ");
        if (discountableAmount != null)
            builder.append("discountableAmount=").append(discountableAmount).append(", ");
        if (undiscountableAmount != null)
            builder.append("undiscountableAmount=").append(undiscountableAmount).append(", ");
        if (body != null)
            builder.append("body=").append(body).append(", ");
        if (operatorId != null)
            builder.append("operatorId=").append(operatorId).append(", ");
        if (storeId != null)
            builder.append("storeId=").append(storeId).append(", ");
        if (terminalId != null)
            builder.append("terminalId=").append(terminalId).append(", ");
        if (alipayStoreId != null)
            builder.append("alipayStoreId=").append(alipayStoreId).append(", ");
        if (code != null)
            builder.append("code=").append(code).append(", ");
        if (msg != null)
            builder.append("msg=").append(msg).append(", ");
        if (subCode != null)
            builder.append("subCode=").append(subCode).append(", ");
        if (subMsg != null)
            builder.append("subMsg=").append(subMsg).append(", ");
        if (sign != null)
            builder.append("sign=").append(sign).append(", ");
        if (tradeNo != null)
            builder.append("tradeNo=").append(tradeNo).append(", ");
        if (buyerLogonId != null)
            builder.append("buyerLogonId=").append(buyerLogonId).append(", ");
        if (receiptAmount != null)
            builder.append("receiptAmount=").append(receiptAmount).append(", ");
        if (buyerPayAmount != null)
            builder.append("buyerPayAmount=").append(buyerPayAmount).append(", ");
        if (pointAmount != null)
            builder.append("pointAmount=").append(pointAmount).append(", ");
        if (invoiceAmount != null)
            builder.append("invoiceAmount=").append(invoiceAmount).append(", ");
        if (gmtPayment != null)
            builder.append("gmtPayment=").append(gmtPayment).append(", ");
        if (cardBalance != null)
            builder.append("cardBalance=").append(cardBalance).append(", ");
        if (storeName != null)
            builder.append("storeName=").append(storeName).append(", ");
        if (buyerUserId != null)
            builder.append("buyerUserId=").append(buyerUserId).append(", ");
        if (discountGoodsDetail != null)
            builder.append("discountGoodsDetail=").append(discountGoodsDetail).append(", ");
        if (transBeginTime != null)
            builder.append("transBeginTime=").append(transBeginTime).append(", ");
        if (transEndTime != null)
            builder.append("transEndTime=").append(transEndTime).append(", ");
        if (refundFee != null)
            builder.append("refundFee=").append(refundFee).append(", ");
        if (tradeStatus != null)
            builder.append("tradeStatus=").append(tradeStatus).append(", ");
        if (notifyTime != null)
            builder.append("notifyTime=").append(notifyTime).append(", ");
        if (notifyId != null)
            builder.append("notifyId=").append(notifyId).append(", ");
        if (gmtRefund != null)
            builder.append("gmtRefund=").append(gmtRefund).append(", ");
        if (gmtClose != null)
            builder.append("gmtClose=").append(gmtClose).append(", ");
        if (creator != null)
            builder.append("creator=").append(creator).append(", ");
        if (createTime != null)
            builder.append("createTime=").append(createTime).append(", ");
        if (modifier != null)
            builder.append("modifier=").append(modifier).append(", ");
        if (modifyTime != null)
            builder.append("modifyTime=").append(modifyTime).append(", ");
        if (remark != null)
            builder.append("remark=").append(remark);
        builder.append("]");
        return builder.toString();
    }

}
