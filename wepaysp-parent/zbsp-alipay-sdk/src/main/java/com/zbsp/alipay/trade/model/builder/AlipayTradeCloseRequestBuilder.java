package com.zbsp.alipay.trade.model.builder;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

public class AlipayTradeCloseRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.outTradeNo)) {
            throw new NullPointerException("out_trade_no should not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeCancelRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeCloseRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeCloseRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeCloseRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeCloseRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayTradeCloseRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public String getTradeNo() {
        return bizContent.tradeNo;
    }

    public AlipayTradeCloseRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }
    
    public String getOperatorId() {
        return bizContent.operatorId;
    }

    public AlipayTradeCloseRequestBuilder setOperatorId(String operatorId) {
        bizContent.operatorId = operatorId;
        return this;
    }

    public static class BizContent {
        // 商户订单号，通过此商户订单号撤销当面付的交易状态
        @SerializedName("out_trade_no")
        private String outTradeNo;
        
        @SerializedName("trade_no")
        private String tradeNo;
        
        @SerializedName("operator_id")
        private String operatorId;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("outTradeNo='").append(outTradeNo).append('\'');
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append("operatorId='").append(operatorId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
