package com.zbsp.wepaysp.po.edu;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alipay_edu_bill_t")
public class AlipayEduBill
    implements java.io.Serializable {

    private static final long serialVersionUID = 4441187631184247787L;
    private String iwoid;
    private String alipayEduTotalBillOid;
    private String partnerOid;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String partnerEmployeeOid;
    private String appId;
    private String appAuthToken;
    private Date sendTime;
    private Integer lineNum;
    private String userMobile;
    private String userName;
    private String userRelation;
    private String userChangeMobile;
    private String schoolPid;
    private String schoolNo;
    private String childName;
    private String grade;
    private String classIn;
    private String studentCode;
    private String studentIdentify;
    private String status;
    private String outTradeNo;
    private String k12OrderNo;
    private String chargeBillTitle;
    private String chargeItem;
    private Integer amount;
    private String gmtEnd;
    private String endEnable;
    private String isvPartnerId;
    private String orderStatus;
    private String studentNo;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;
    private Date gmtPayment;
    private String buyerLogonId;
    private String tradeNo;

    /** 账单状态 */
    public enum OrderStatus {
        INIT("INIT", "待发送"), // 新建
        NOT_PAY("NOT_PAY", "待缴费"), // 待缴费 （订单发送后状态）
        PAYING("PAYING", "支付中"), // 支付中
        PAY_SUCCESS("PAY_SUCCESS", "支付成功，处理中"), // 支付成功，处理中 （收到支付成功的异步通知后状态）
        BILLING_SUCCESS("BILLING_SUCCESS", "缴费成功"), // 缴费成功 （同步教育接口状态成功后）
        TIMEOUT_CLOSED("TIMEOUT_CLOSED", "逾期关闭账单"), // 逾期关闭账单
        ISV_CLOSED("ISV_CLOSED", "账单关闭");// 账单关闭 （同步教育接口订单主动关闭成功后状态）
        
        private String value;
        private String desc;
        
        public String getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }
        
        private  OrderStatus(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }
        
    }
    
    public AlipayEduBill() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "ALIPAY_EDU_TOTAL_BILL_OID", length = 32)
    public String getAlipayEduTotalBillOid() {
        return this.alipayEduTotalBillOid;
    }

    public void setAlipayEduTotalBillOid(String alipayEduTotalBillOid) {
        this.alipayEduTotalBillOid = alipayEduTotalBillOid;
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

    @Column(name = "APP_ID", length = 40)
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

    @Column(name = "SEND_TIME", length = 0)
    public Date getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(name = "LINE_NUM")
    public Integer getLineNum() {
        return this.lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    @Column(name = "USER_MOBILE", length = 16)
    public String getUserMobile() {
        return this.userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    @Column(name = "USER_NAME", length = 32)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "USER_RELATION", length = 2)
    public String getUserRelation() {
        return this.userRelation;
    }

    public void setUserRelation(String userRelation) {
        this.userRelation = userRelation;
    }

    @Column(name = "USER_CHANGE_MOBILE", length = 16)
    public String getUserChangeMobile() {
        return this.userChangeMobile;
    }

    public void setUserChangeMobile(String userChangeMobile) {
        this.userChangeMobile = userChangeMobile;
    }

    @Column(name = "SCHOOL_PID", length = 128)
    public String getSchoolPid() {
        return this.schoolPid;
    }

    public void setSchoolPid(String schoolPid) {
        this.schoolPid = schoolPid;
    }

    @Column(name = "SCHOOL_NO", length = 16)
    public String getSchoolNo() {
        return this.schoolNo;
    }

    public void setSchoolNo(String schoolNo) {
        this.schoolNo = schoolNo;
    }

    @Column(name = "CHILD_NAME", length = 32)
    public String getChildName() {
        return this.childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    @Column(name = "GRADE", length = 32)
    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Column(name = "CLASS_IN", length = 32)
    public String getClassIn() {
        return this.classIn;
    }

    public void setClassIn(String classIn) {
        this.classIn = classIn;
    }

    @Column(name = "STUDENT_CODE", length = 32)
    public String getStudentCode() {
        return this.studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    @Column(name = "STUDENT_IDENTIFY", length = 18)
    public String getStudentIdentify() {
        return this.studentIdentify;
    }

    public void setStudentIdentify(String studentIdentify) {
        this.studentIdentify = studentIdentify;
    }

    @Column(name = "STATUS", length = 2)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "OUT_TRADE_NO", length = 128)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "CHARGE_BILL_TITLE", length = 512)
    public String getChargeBillTitle() {
        return this.chargeBillTitle;
    }

    public void setChargeBillTitle(String chargeBillTitle) {
        this.chargeBillTitle = chargeBillTitle;
    }

    @Column(name = "CHARGE_ITEM", length = 2048)
    public String getChargeItem() {
        return this.chargeItem;
    }

    public void setChargeItem(String chargeItem) {
        this.chargeItem = chargeItem;
    }

    @Column(name = "AMOUNT")
    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Column(name = "GMT_END", length = 19)
    public String getGmtEnd() {
        return this.gmtEnd;
    }

    public void setGmtEnd(String gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    @Column(name = "END_ENABLE", length = 2)
    public String getEndEnable() {
        return this.endEnable;
    }

    public void setEndEnable(String endEnable) {
        this.endEnable = endEnable;
    }

    @Column(name = "ISV_PARTNER_ID", length = 32)
    public String getIsvPartnerId() {
        return this.isvPartnerId;
    }

    public void setIsvPartnerId(String isvPartnerId) {
        this.isvPartnerId = isvPartnerId;
    }

    @Column(name = "ORDER_STATUS", length = 64)
    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Column(name = "STUDENT_NO", length = 128)
    public String getStudentNo() {
        return this.studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
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
    
    @Column(name = "K12_ORDER_NO", length = 12)
    public String getK12OrderNo() {
        return k12OrderNo;
    }
    
    public void setK12OrderNo(String k12OrderNo) {
        this.k12OrderNo = k12OrderNo;
    }

    @Column(name = "GMT_PAYMENT", length = 0)
    public Date getGmtPayment() {
        return this.gmtPayment;
    }

    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
    }
    
    @Column(name = "BUYER_LOGON_ID", length = 100)
    public String getBuyerLogonId() {
        return this.buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }
    
    @Column(name = "TRADE_NO", length = 64)
    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

}
