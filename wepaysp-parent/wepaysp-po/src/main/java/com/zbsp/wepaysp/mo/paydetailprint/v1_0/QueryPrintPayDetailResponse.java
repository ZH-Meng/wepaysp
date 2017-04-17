package com.zbsp.wepaysp.mo.paydetailprint.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileResponse;

/**
 * 打印支付明细查询响应
 */
public class QueryPrintPayDetailResponse extends MobileResponse {

	/** 商户公司名称 */
	private String dealerCompany;

	/** 商户编号 */
	private String dealerId;

	/** 终端编号（设备号） */
	private String deviceId;

	/** 收银员号 */
	private String dealerEmployeeId;

	/** 商户单号 */
	private String outTradeNo;

	/** 支付单号 */
	private String transactionId;

	/** 交易时间yyyy/MM/dd HH:mm:ss */
	private String tradeTime;

	/** 交易方式 */
	private int payType;

	/** 交易状态 */
	private int tradeStatus;

	/** 交易银行 */
	private String payBank;

	/** 金额，单位：分 */
	private long Money;

	/** 批次号 */
	private String batchNo;

	/** 打印标题 */
	private String printTitle;

	public QueryPrintPayDetailResponse() {
	}

	public QueryPrintPayDetailResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}

	public String getDealerCompany() {
		return dealerCompany;
	}

	public void setDealerCompany(String dealerCompany) {
		this.dealerCompany = dealerCompany;
	}

	public String getDealerId() {
		return dealerId;
	}

	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDealerEmployeeId() {
		return dealerEmployeeId;
	}

	public void setDealerEmployeeId(String dealerEmployeeId) {
		this.dealerEmployeeId = dealerEmployeeId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getPayBank() {
		return payBank;
	}

	public void setPayBank(String payBank) {
		this.payBank = payBank;
	}

	public long getMoney() {
		return Money;
	}

	public void setMoney(long money) {
		Money = money;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getPrintTitle() {
		return printTitle;
	}

	public void setPrintTitle(String printTitle) {
		this.printTitle = printTitle;
	}

	@Override
	public String toString() {
		return "QueryPrintPayDetailResponse [dealerCompany=" + dealerCompany + ", dealerId=" + dealerId + ", deviceId="
				+ deviceId + ", dealerEmployeeId=" + dealerEmployeeId + ", outTradeNo=" + outTradeNo
				+ ", transactionId=" + transactionId + ", tradeTime=" + tradeTime + ", payType=" + payType
				+ ", tradeStatus=" + tradeStatus + ", payBank=" + payBank + ", Money=" + Money + ", batchNo=" + batchNo
				+ ", printTitle=" + printTitle + ", " + super.toString() + " ]";
	}

	@Override
	public QueryPrintPayDetailResponse build(String key) {
		try {
			setSignature(Signature.getSign(this, key));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

}
