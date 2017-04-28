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

	/** 收银员号 */
	private String dealerEmployeeId;

	/** 商户单号 */
	private String outTradeNo;

	/** 支付单号 */
	private String transactionId;

	/** 交易时间yyyy/MM/dd HH:mm:ss */
	private String tradeTime;

	/** 交易方式 */
	private String payType;

	/** 交易状态 */
	private String tradeStatus;

	/** 金额，单位：元 */
	private String money;

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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
	    this.money = money;
	}

	@Override
	public String toString() {
		return "QueryPrintPayDetailResponse [dealerCompany=" + dealerCompany + ", dealerId=" + dealerId + ", "
		        + "dealerEmployeeId=" + dealerEmployeeId + ", outTradeNo=" + outTradeNo
				+ ", transactionId=" + transactionId + ", tradeTime=" + tradeTime + ", payType=" + payType
				+ ", tradeStatus=" + tradeStatus + ", money=" + money + super.toString() + " ]";
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
