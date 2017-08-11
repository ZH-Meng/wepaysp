package com.zbsp.alipay.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class AlipayEcoEduKtBillingModifyRequestBuilder
    extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public boolean validate() {
        if (StringUtils.isBlank(bizContent.outTradeNo))
            throw new NullPointerException("outTradeNo should not be NULL!");
        if (StringUtils.isBlank(bizContent.status))
            throw new NullPointerException("status should not be NULL!");

        return true;
    }

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    public String getTradeNo() {
        return bizContent.tradeNo;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public String getStatus() {
        return bizContent.status;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setStatus(String status) {
        bizContent.status = status;
        return this;
    }

    public String getBankuccess() {
        return bizContent.bankuccess;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setBankuccess(String bankuccess) {
        bizContent.bankuccess = bankuccess;
        return this;
    }

    public String getFundChange() {
        return bizContent.fundChange;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setFundChange(String fundChange) {
        bizContent.fundChange = fundChange;
        return this;
    }

    public String getRefundAmount() {
        return bizContent.refundAmount;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setRefundAmount(String refundAmount) {
        bizContent.refundAmount = refundAmount;
        return this;
    }

    public String getRefundReason() {
        return bizContent.refundReason;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setRefundReason(String refundReason) {
        bizContent.refundReason = refundReason;
        return this;
    }

    public String getOutRequestNo() {
        return bizContent.outRequestNo;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setOutRequestNo(String outRequestNo) {
        bizContent.outRequestNo = outRequestNo;
        return this;
    }

    public String getBuyerLogonId() {
        return bizContent.buyerLogonId;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setBuyerLogonId(String buyerLogonId) {
        bizContent.buyerLogonId = buyerLogonId;
        return this;
    }

    public String getGmtRefund() {
        return bizContent.gmtRefund;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setGmtRefund(String gmtRefund) {
        bizContent.gmtRefund = gmtRefund;
        return this;
    }

    public String getBuyerUserId() {
        return bizContent.buyerUserId;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setBuyerUserId(String buyerUserId) {
        bizContent.buyerUserId = buyerUserId;
        return this;
    }

    public String getRefundDetaiItemList() {
        return bizContent.refundDetaiItemList;
    }

    public AlipayEcoEduKtBillingModifyRequestBuilder setRefundDetaiItemList(String refundDetaiItemList) {
        bizContent.refundDetaiItemList = refundDetaiItemList;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayEcoEduKtBillingModifyRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    public static class BizContent {

        /** 可选， 支付宝返回的交易号 */
        @SerializedName("trade_no")
        private String tradeNo;

        /** 必填 ISV调用发送账单接口，返回给商户的order_no */
        @SerializedName("out_trade_no")
        private String outTradeNo;

        /**
         * 必填 状态：1:缴费成功； 2:关闭账单； 3、退费，需要填写fund_change, refund_amount, refund_reason, out_request_no, buyer_logon_id, gmt_refund, buyer_user_id, refund_detail_item_list; 
         * 4、同步网商返回的状态,如果是网商银行的账单，bank_success这个字段必填
         */
        private String status;

        /** 同步网商返回的状态,如果是网商银行的账单，bank_success这个字段必填，成功Y，失败N */
        @SerializedName("bank_success")
        private String bankuccess;

        /** 退款时填写，本次退款是否发生了资金变化 */
        @SerializedName("fund_change")
        private String fundChange;

        /** 退款时填写，需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数 */
        @SerializedName("refund_amount")
        private String refundAmount;

        /** 退款时填写，退款原因，商家根据客户实际退款原因填写（若退款时填写，则同步退款状态时也必须填写） */
        @SerializedName("refund_reason")
        private String refundReason;

        /** 退款时填写，标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。（若退款时填写，则同步退款状态时也必须填写） */
        @SerializedName("out_request_no")
        private String outRequestNo;

        /** 退款时填写，退款时，支付宝返回的用户的登录id */
        @SerializedName("buyer_logon_id")
        private String buyerLogonId;

        /** 退款时填写，支付宝返回的退款时间内，而不是商户退款申请的时间 */
        @SerializedName("gmt_refund")
        private String gmtRefund;

        /** 退款时填写，支付宝返回的买家支付宝用户id。 */
        @SerializedName("buyer_user_id")
        private String buyerUserId;

        /** 退款时填写，支付宝返回的退款资金渠道，json格式字符串。 */
        @SerializedName("refund_detail_item_list")
        private String refundDetaiItemList;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("BizContent [tradeNo=").append(tradeNo).append(", outTradeNo=").append(outTradeNo).append(", status=").append(status).append(", bankuccess=").append(bankuccess)
                .append(", fundChange=").append(fundChange).append(", refundAmount=").append(refundAmount).append(", refundReason=").append(refundReason).append(", outRequestNo=").append(outRequestNo)
                .append(", buyerLogonId=").append(buyerLogonId).append(", gmtRefund=").append(gmtRefund).append(", buyerUserId=").append(buyerUserId).append(", refundDetaiItemList=")
                .append(refundDetaiItemList).append("]");
            return builder.toString();
        }

    }

}
