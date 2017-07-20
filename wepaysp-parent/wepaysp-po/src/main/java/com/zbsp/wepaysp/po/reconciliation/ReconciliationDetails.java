package com.zbsp.wepaysp.po.reconciliation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the reconciliation_details_t database table.
 * 
 */
@Entity
@Table(name="reconciliation_details_t")
public class ReconciliationDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="BILL_RESULT_CODE")
	private String billResultCode;
	
	@Column(name="BILL_TOTAL_FEE")
	private int billTotalFee;

	@Column(name="CREATE_TIME")
	private Date createTime;

	@Column(name="CREATOR")
	private String creator;
	
	@Id
	@Column(name = "IWOID", unique = true, nullable = false, length = 32)
	private String iwoid;

	@Column(name="MODIFIER")
	private String modifier;

	@Column(name="MODIFY_TIME")
	private Date modifyTime;

	@Column(name="OUT_TRADE_NO")
	private String outTradeNo;

	@Column(name="PAY_PLATFORM")
	private int payPlatform;

	@Column(name="RECONCILIATION_RESULT")
	private int reconciliationResult;

	private String remark;

	@Column(name="RESULT_CODE")
	private String resultCode;

	@Column(name="TOTAL_FEE")
	private int totalFee;

	@Column(name="TRADE_STATUS")
	private int tradeStatus;

	@Column(name="TRADE_TIME")
	private Date tradeTime;

	@Column(name="TRANSACTION_ID")
	private String transactionId;

	public ReconciliationDetails() {
	}

	public String getBillResultCode() {
		return this.billResultCode;
	}

	public int getBillTotalFee() {
		return this.billTotalFee;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public String getCreator() {
		return this.creator;
	}

	public String getIwoid() {
		return this.iwoid;
	}

	public String getModifier() {
		return this.modifier;
	}

	public Date getModifyTime() {
		return this.modifyTime;
	}

	public String getOutTradeNo() {
		return this.outTradeNo;
	}

	public int getPayPlatform() {
		return this.payPlatform;
	}

	public int getReconciliationResult() {
		return this.reconciliationResult;
	}

	public String getRemark() {
		return this.remark;
	}

	public String getResultCode() {
		return this.resultCode;
	}

	public int getTotalFee() {
		return this.totalFee;
	}

	public int getTradeStatus() {
		return this.tradeStatus;
	}

	public Date getTradeTime() {
		return this.tradeTime;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setBillResultCode(String billResultCode) {
		this.billResultCode = billResultCode;
	}

	public void setBillTotalFee(int billTotalFee) {
		this.billTotalFee = billTotalFee;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setIwoid(String iwoid) {
		this.iwoid = iwoid;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public void setPayPlatform(int payPlatform) {
		this.payPlatform = payPlatform;
	}

	public void setReconciliationResult(int reconciliationResult) {
		this.reconciliationResult = reconciliationResult;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}