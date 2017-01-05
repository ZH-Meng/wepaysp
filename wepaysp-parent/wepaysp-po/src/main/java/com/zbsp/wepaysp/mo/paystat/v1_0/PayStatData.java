package com.zbsp.wepaysp.mo.paystat.v1_0;

/**
 * 支付结算数据
 * */
public class PayStatData {
	
	/**支付方式*/
	private int payType;
	
	/**收款总金额，单位：分*/
	private long totalCollectionMoney;
	
	/**退款总金额，单位：分*/
	private long totalRefundMoney;
	
	/**净收款，单位：分*/
	private long totalNetCollectionMoney;
	
	/**交易笔数*/
	private long totalTradeCount;
	
	/**退款笔数*/
	private long totalRefundCount;
	
	public PayStatData() {
	}
	
	public PayStatData(int payType, long totalCollectionMoney, long totalRefundMoney, long totalNetCollectionMoney, long totalTradeCount, long totalRefundCount) {
		super();
		this.payType = payType;
		this.totalCollectionMoney = totalCollectionMoney;
		this.totalRefundMoney = totalRefundMoney;
		this.totalNetCollectionMoney = totalNetCollectionMoney;
		this.totalTradeCount = totalTradeCount;
		this.totalRefundCount = totalRefundCount;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public long getTotalCollectionMoney() {
		return totalCollectionMoney;
	}

	public void setTotalCollectionMoney(long totalCollectionMoney) {
		this.totalCollectionMoney = totalCollectionMoney;
	}

	public long getTotalRefundMoney() {
		return totalRefundMoney;
	}

	public void setTotalRefundMoney(long totalRefundMoney) {
		this.totalRefundMoney = totalRefundMoney;
	}

	public long getTotalNetCollectionMoney() {
		return totalNetCollectionMoney;
	}

	public void setTotalNetCollectionMoney(long totalNetCollectionMoney) {
		this.totalNetCollectionMoney = totalNetCollectionMoney;
	}

	public long getTotalTradeCount() {
		return totalTradeCount;
	}

	public void setTotalTradeCount(long totalTradeCount) {
		this.totalTradeCount = totalTradeCount;
	}

	public long getTotalRefundCount() {
		return totalRefundCount;
	}

	public void setTotalRefundCount(long totalRefundCount) {
		this.totalRefundCount = totalRefundCount;
	}
	
}
