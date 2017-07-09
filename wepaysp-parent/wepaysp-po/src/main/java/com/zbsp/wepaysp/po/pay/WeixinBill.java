package com.zbsp.wepaysp.po.pay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * The persistent class for the weixin_bill_t database table.
 * 
 */
@Entity
@Table(name="weixin_bill_t")
public class WeixinBill implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "IWOID", unique = true, nullable = false, length = 32)
	private String iwoid;
	
	@Column(name="APPID")
	private String appid;
	
	@Column(name="ATTACH")
	private String attach;

	@Column(name="BANK_TYPE")
	private String bankType;

	@Column(name="COUPON_FEE")
	private int couponFee;

	@Column(name="COUPON_REFUND_FEE")
	private int couponRefundFee;

	@Column(name="CREATE_TIME")
	private Date createTime;
	
	@Column(name="CREATOR")
	private String creator;

	@Column(name="DEVICE_INFO")
	private String deviceInfo;

	@Column(name="FEE_TYPE")
	private String feeType;

	@Column(name="GOODS_TAG")
	private String goodsTag;

	@Column(name="MCH_ID")
	private String mchId;

	@Column(name="MODIFIER")
	private String modifier;

	@Column(name="MODIFY_TIME")
	private Date modifyTime;

	@Column(name="OPENID")
	private String openid;

	@Column(name="OUT_REFUND_NO")
	private String outRefundNo;

	@Column(name="OUT_TRADE_NO")
	private String outTradeNo;

	@Column(name="PAY_TYPE")
	private String payType;
	
	@Column(name="POUNDAGE")
	private String poundage;
	
	@Column(name="RATE")
	private String rate;

	@Column(name="REFUND_CODE")
	private String refundCode;

	@Column(name="REFUND_ID")
	private String refundId;

	@Column(name="REFUND_TYPE")
	private String refundType;
	
	@Column(name="REMARK")
	private String remark;

	@Column(name="RESULT_CODE")
	private String resultCode;

	@Column(name="SETTLEMENT_REFUND_FEE")
	private int settlementRefundFee;

	@Column(name="SUB_APPID")
	private String subAppid;

	@Column(name="SUB_MCH_ID")
	private String subMchId;

	@Column(name="TOTAL_FEE")
	private int totalFee;

	@Column(name="TRADE_TIME")
	private Date tradeTime;

	@Column(name="TRANSACTION_ID")
	private String transactionId;

	public WeixinBill() {
	}
	
	public WeixinBill(String iwoid, String appid, String attach, String bankType, int couponFee, int couponRefundFee,
			Date createTime, String creator, String deviceInfo, String feeType, String goodsTag, String mchId,
			String modifier, Date modifyTime, String openid, String outRefundNo, String outTradeNo, String payType,
			String poundage, String rate, String refundCode, String refundId, String refundType, String remark,
			String resultCode, int settlementRefundFee, String subAppid, String subMchId, int totalFee,
			Date tradeTime, String transactionId) {
		super();
		this.iwoid = iwoid;
		this.appid = appid;
		this.attach = attach;
		this.bankType = bankType;
		this.couponFee = couponFee;
		this.couponRefundFee = couponRefundFee;
		this.createTime = createTime;
		this.creator = creator;
		this.deviceInfo = deviceInfo;
		this.feeType = feeType;
		this.goodsTag = goodsTag;
		this.mchId = mchId;
		this.modifier = modifier;
		this.modifyTime = modifyTime;
		this.openid = openid;
		this.outRefundNo = outRefundNo;
		this.outTradeNo = outTradeNo;
		this.payType = payType;
		this.poundage = poundage;
		this.rate = rate;
		this.refundCode = refundCode;
		this.refundId = refundId;
		this.refundType = refundType;
		this.remark = remark;
		this.resultCode = resultCode;
		this.settlementRefundFee = settlementRefundFee;
		this.subAppid = subAppid;
		this.subMchId = subMchId;
		this.totalFee = totalFee;
		this.tradeTime = tradeTime;
		this.transactionId = transactionId;
	}
	public String getIwoid() {
		return this.iwoid;
	}

	public void setIwoid(String iwoid) {
		this.iwoid = iwoid;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAttach() {
		return this.attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getBankType() {
		return this.bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public int getCouponFee() {
		return this.couponFee;
	}

	public void setCouponFee(int couponFee) {
		this.couponFee = couponFee;
	}

	public int getCouponRefundFee() {
		return this.couponRefundFee;
	}

	public void setCouponRefundFee(int couponRefundFee) {
		this.couponRefundFee = couponRefundFee;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDeviceInfo() {
		return this.deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getFeeType() {
		return this.feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getGoodsTag() {
		return this.goodsTag;
	}

	public void setGoodsTag(String goodsTag) {
		this.goodsTag = goodsTag;
	}

	public String getMchId() {
		return this.mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getModifier() {
		return this.modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getOpenid() {
		return this.openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getOutRefundNo() {
		return this.outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public String getOutTradeNo() {
		return this.outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getPayType() {
		return this.payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPoundage() {
		return this.poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getRate() {
		return this.rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getRefundCode() {
		return this.refundCode;
	}

	public void setRefundCode(String refundCode) {
		this.refundCode = refundCode;
	}

	public String getRefundId() {
		return this.refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getRefundType() {
		return this.refundType;
	}

	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResultCode() {
		return this.resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public int getSettlementRefundFee() {
		return this.settlementRefundFee;
	}

	public void setSettlementRefundFee(int settlementRefundFee) {
		this.settlementRefundFee = settlementRefundFee;
	}

	public String getSubAppid() {
		return this.subAppid;
	}

	public void setSubAppid(String subAppid) {
		this.subAppid = subAppid;
	}

	public String getSubMchId() {
		return this.subMchId;
	}

	public void setSubMchId(String subMchId) {
		this.subMchId = subMchId;
	}

	public int getTotalFee() {
		return this.totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public Date getTradeTime() {
		return this.tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}