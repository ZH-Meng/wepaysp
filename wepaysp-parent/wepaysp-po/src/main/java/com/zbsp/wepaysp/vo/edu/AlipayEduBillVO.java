package com.zbsp.wepaysp.vo.edu;

import java.util.LinkedHashMap;

public class AlipayEduBillVO {
	private String outTradeNo;
	private String childName;
	private String userMobile;
	private String userName;
	private String classIn;
	private String orderStatus;
	private String chargeBillTitle;
	private String chargeItem;
	private Integer amount;
	private LinkedHashMap<String, Object> chargeItemMap;

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClassIn() {
		return classIn;
	}

	public void setClassIn(String classIn) {
		this.classIn = classIn;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getChargeBillTitle() {
		return chargeBillTitle;
	}

	public void setChargeBillTitle(String chargeBillTitle) {
		this.chargeBillTitle = chargeBillTitle;
	}

	public String getChargeItem() {
		return chargeItem;
	}

	public void setChargeItem(String chargeItem) {
		this.chargeItem = chargeItem;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
    
    public LinkedHashMap<String, Object> getChargeItemMap() {
        return chargeItemMap;
    }
    
    public void setChargeItemMap(LinkedHashMap<String, Object> chargeItemMap) {
        this.chargeItemMap = chargeItemMap;
    }

}
