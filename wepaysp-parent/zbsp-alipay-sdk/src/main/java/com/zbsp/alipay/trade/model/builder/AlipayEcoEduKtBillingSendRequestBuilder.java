package com.zbsp.alipay.trade.model.builder;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;
import com.zbsp.alipay.trade.model.ChargeItems;
import com.zbsp.alipay.trade.model.UserDetails;

public class AlipayEcoEduKtBillingSendRequestBuilder
    extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public boolean validate() {
        if (StringUtils.isBlank(bizContent.schoolPid))
            throw new NullPointerException("schoolPid should not be NULL!");
        if (StringUtils.isBlank(bizContent.schoolNo))
            throw new NullPointerException("schoolNo should not be NULL!");
        if (StringUtils.isBlank(bizContent.childName))
            throw new NullPointerException("childName should not be NULL!");
        if (StringUtils.isBlank(bizContent.classIn))
            throw new NullPointerException("classIn should not be NULL!");
        if (StringUtils.isBlank(bizContent.outTradeNo))
            throw new NullPointerException("outTradeNo should not be NULL!");
        if (StringUtils.isBlank(bizContent.chargeBillTitle))
            throw new NullPointerException("chargeBillTitle should not be NULL!");
        if (StringUtils.isBlank(bizContent.amount))
            throw new NullPointerException("amount should not be NULL!");
        if (StringUtils.isBlank(bizContent.gmtEnd))
            throw new NullPointerException("gmtEnd should not be NULL!");
        if (StringUtils.isBlank(bizContent.endEnable))
            throw new NullPointerException("endEnable should not be NULL!");
        if (StringUtils.isBlank(bizContent.partnerId))
            throw new NullPointerException("partnerId should not be NULL!");
        if (StringUtils.isBlank(bizContent.studentCode) && StringUtils.isBlank(bizContent.studentIdentify) && (bizContent.users == null || bizContent.users.isEmpty()))
            throw new NullPointerException("studentCode、studentIdentify、users should not all be NULL!");

        return true;
    }

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    public List<UserDetails> getUsers() {
        return bizContent.users;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setUsers(List<UserDetails> users) {
        bizContent.users = users;
        return this;
    }

    public String getSchoolPid() {
        return bizContent.schoolPid;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setSchoolPid(String schoolPid) {
        bizContent.schoolPid = schoolPid;
        return this;
    }

    public String getSchoolNo() {
        return bizContent.schoolNo;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setSchoolNo(String schoolNo) {
        bizContent.schoolNo = schoolNo;
        return this;
    }

    public String getChildName() {
        return bizContent.childName;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setChildName(String childName) {
        bizContent.childName = childName;
        return this;
    }

    public String getGrade() {
        return bizContent.grade;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setGrade(String grade) {
        bizContent.grade = grade;
        return this;
    }

    public String getClassIn() {
        return bizContent.classIn;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setClassIn(String classIn) {
        bizContent.classIn = classIn;
        return this;
    }

    public String getStudentCode() {
        return bizContent.studentCode;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setStudentCode(String studentCode) {
        bizContent.studentCode = studentCode;
        return this;
    }

    public String getStudentIdentify() {
        return bizContent.studentIdentify;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setStudentIdentify(String studentIdentify) {
        bizContent.studentIdentify = studentIdentify;
        return this;
    }

    public String getStatus() {
        return bizContent.status;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setStatus(String status) {
        bizContent.status = status;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public String getChargeBillTitle() {
        return bizContent.chargeBillTitle;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setChargeBillTitle(String chargeBillTitle) {
        bizContent.chargeBillTitle = chargeBillTitle;
        return this;
    }

    public List<ChargeItems> getChargeItem() {
        return bizContent.chargeItem;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setChargeItem(List<ChargeItems> chargeItem) {
        bizContent.chargeItem = chargeItem;
        return this;
    }

    public String getAmount() {
        return bizContent.amount;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setAmount(String amount) {
        bizContent.amount = amount;
        return this;
    }

    public String getGmtEnd() {
        return bizContent.gmtEnd;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setGmtEnd(String gmtEnd) {
        bizContent.gmtEnd = gmtEnd;
        return this;
    }

    public String getEndEnable() {
        return bizContent.endEnable;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setEndEnable(String endEnable) {
        bizContent.endEnable = endEnable;
        return this;
    }

    public String getPartnerId() {
        return bizContent.partnerId;
    }

    public AlipayEcoEduKtBillingSendRequestBuilder setPartnerId(String partnerId) {
        bizContent.partnerId = partnerId;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayEcoEduKtBillingSendRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    public static class BizContent {

        /**  特殊可选，孩子的家长信息，最多一次输入20个家长，此字段做为识别家长的孩子用，与student_identify、student_code至少选一个 */
        @SerializedName("users")
        private List<UserDetails> users;

        /** 必填 学校支付宝pid */
        @SerializedName("school_pid")
        private String schoolPid;

        /** 必填 学校编码，录入学校接口返回的参数 */
        @SerializedName("school_no")
        private String schoolNo;

        /** 必填 孩子名字 */
        @SerializedName("child_name")
        private String childName;

        /** 可选，孩子所在年级 */
        private String grade;

        /** 必填，孩子所在班级 */
        @SerializedName("class_in")
        private String classIn;

        /** 特殊可选，学生的学号，一般以教育局学号为准，作为学生的唯一标识。此字段与student_identify、家长user_mobile至少选一个 */
        @SerializedName("student_code")
        private String studentCode;

        /** 特殊可选，学生的身份证号，如果ISV有学生身份证号，则同步身份证号作为学生唯一标识。此字段与student_code、家长user_mobile至少选一个 */
        @SerializedName("student_identify")
        private String studentIdentify;

        /** 可选，用于删除孩子，状态为“D”，表示删除孩子，状态“U”表示孩子信息添加或更新。为空则不更新孩子信息 */
        private String status;

        /** 必填，ISV端的缴费账单编号 */
        @SerializedName("out_trade_no")
        private String outTradeNo;

        /** 必填，缴费账单名称 */
        @SerializedName("charge_bill_title")
        private String chargeBillTitle;

        /** 可选，缴费详情：输入json格式字符串。Json定义：key填写缴费项名称，value填写缴费项金额，金额保留2位小数。至少输入一个 */
        @SerializedName("charge_item")
        private List<ChargeItems> chargeItem;

        /** 必填，总金额,保留两位小数。总金额= charge_item各子项的金额总和 */
        private String amount;

        /** 必填，学校发布缴费信息，家长可支付的截止时间，格式"yyyy-MM-dd HH:mm:ss" */
        @SerializedName("gmt_end")
        private String gmtEnd;

        /** 必填，截止日期是否生效，与gmt_end_time发布配合使用,N为gmt_end_time不生效，用户过期后仍可以缴费；Y为gmt_end_time生效，用户过期后，不能再缴费。 */
        @SerializedName("end_enable")
        private String endEnable;

        /** 必填，Isv支付宝pid。 */
        @SerializedName("partner_id")
        private String partnerId;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("BizContent [users=").append(users).append(", schoolPid=").append(schoolPid).append(", schoolNo=").append(schoolNo).append(", childName=").append(childName)
                .append(", grade=").append(grade).append(", classIn=").append(classIn).append(", studentCode=").append(studentCode).append(", studentIdentify=").append(studentIdentify)
                .append(", status=").append(status).append(", outTradeNo=").append(outTradeNo).append(", chargeBillTitle=").append(chargeBillTitle).append(", chargeItem=").append(chargeItem)
                .append(", amount=").append(amount).append(", gmtEnd=").append(gmtEnd).append(", endEnable=").append(endEnable).append(", partnerId=").append(partnerId).append("]");
            return builder.toString();
        }

    }

}
