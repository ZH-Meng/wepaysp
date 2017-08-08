package com.zbsp.wepaysp.vo.edu;

import java.util.Date;

public class AlipayEduTotalBillVO {
	private String iwoid;
	private String schoolNo;
	private String billName;
	private Date sendTime;
	private Date closeTime;
	private String orderStatus;
	private Integer totalCount;
	private Integer totalMoney;
	private Integer receiptCount;
	private Integer receiptMoney;
	private String excelPath;
	private String[] chargeItemHeaders;

	public String getIwoid() {
		return iwoid;
	}

	public void setIwoid(String iwoid) {
		this.iwoid = iwoid;
	}

	public String getSchoolNo() {
		return schoolNo;
	}

	public void setSchoolNo(String schoolNo) {
		this.schoolNo = schoolNo;
	}

	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Integer totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Integer getReceiptCount() {
		return receiptCount;
	}

	public void setReceiptCount(Integer receiptCount) {
		this.receiptCount = receiptCount;
	}

	public Integer getReceiptMoney() {
		return receiptMoney;
	}

	public void setReceiptMoney(Integer receiptMoney) {
		this.receiptMoney = receiptMoney;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String[] getChargeItemHeaders() {
		return chargeItemHeaders;
	}

	public void setChargeItemHeaders(String[] chargeItemHeaders) {
		this.chargeItemHeaders = chargeItemHeaders;
	}

}
