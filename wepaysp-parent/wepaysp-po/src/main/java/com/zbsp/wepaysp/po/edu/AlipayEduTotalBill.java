package com.zbsp.wepaysp.po.edu;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alipay_edu_total_bill_t")
public class AlipayEduTotalBill implements java.io.Serializable {

	private static final long serialVersionUID = -3673372787955879897L;
	private String iwoid;
	private String partnerOid;
	private Integer partnerLevel;
	private String partner1Oid;
	private String partner2Oid;
	private String partner3Oid;
	private String partnerEmployeeOid;
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
	private String creator;
	private Date createTime;
	private String modifier;
	private Date modifyTime;
	private String remark;
	private String chargeItemHeaders;

	/** 账单状态 */
	public enum OrderStatus {
		INIT, // 新建
		SEND_SUCCESS;// 发送成功
	}

	public AlipayEduTotalBill() {
	}

	@Id
	@Column(name = "IWOID", unique = true, nullable = false, length = 32)
	public String getIwoid() {
		return this.iwoid;
	}

	public void setIwoid(String iwoid) {
		this.iwoid = iwoid;
	}

	@Column(name = "PARTNER_OID", length = 32)
	public String getPartnerOid() {
		return this.partnerOid;
	}

	public void setPartnerOid(String partnerOid) {
		this.partnerOid = partnerOid;
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

	@Column(name = "PARTNER_EMPLOYEE_OID", length = 32)
	public String getPartnerEmployeeOid() {
		return this.partnerEmployeeOid;
	}

	public void setPartnerEmployeeOid(String partnerEmployeeOid) {
		this.partnerEmployeeOid = partnerEmployeeOid;
	}

	@Column(name = "SCHOOL_NO", length = 16)
	public String getSchoolNo() {
		return this.schoolNo;
	}

	public void setSchoolNo(String schoolNo) {
		this.schoolNo = schoolNo;
	}

	@Column(name = "BILL_NAME", length = 128)
	public String getBillName() {
		return this.billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	@Column(name = "SEND_TIME", length = 0)
	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Column(name = "CLOSE_TIME", length = 0)
	public Date getCloseTime() {
		return this.closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	@Column(name = "ORDER_STATUS", length = 64)
	public String getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name = "TOTAL_COUNT")
	public Integer getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@Column(name = "TOTAL_MONEY")
	public Integer getTotalMoney() {
		return this.totalMoney;
	}

	public void setTotalMoney(Integer totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Column(name = "RECEIPT_COUNT")
	public Integer getReceiptCount() {
		return this.receiptCount;
	}

	public void setReceiptCount(Integer receiptCount) {
		this.receiptCount = receiptCount;
	}

	@Column(name = "RECEIPT_MONEY")
	public Integer getReceiptMoney() {
		return this.receiptMoney;
	}

	public void setReceiptMoney(Integer receiptMoney) {
		this.receiptMoney = receiptMoney;
	}

	@Column(name = "EXCEL_PATH", length = 128)
	public String getExcelPath() {
		return this.excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	@Column(name = "CREATOR", nullable = false, length = 32)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "CREATE_TIME", nullable = false, length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
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
	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "REMARK", length = 256)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name = "CHARGE_ITEM_HEADERS", length = 1024)
	public String getChargeItemHeaders() {
		return chargeItemHeaders;
	}

	public void setChargeItemHeaders(String chargeItemHeaders) {
		this.chargeItemHeaders = chargeItemHeaders;
	}

}
