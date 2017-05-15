package com.zbsp.wepaysp.vo.pay;

/**
 * 支付/收款明细VO
 * 
 * @author mengzh
 */
public class PayDetailVO {
    
    private int index;
    
	private String outTradeNo;
	/** 实收金额，格式化金额：元 */
	private String collectionMoney;
	private String transTime;
	private String storeName;

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getCollectionMoney() {
		return collectionMoney;
	}

	public void setCollectionMoney(String collectionMoney) {
		this.collectionMoney = collectionMoney;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }

}
