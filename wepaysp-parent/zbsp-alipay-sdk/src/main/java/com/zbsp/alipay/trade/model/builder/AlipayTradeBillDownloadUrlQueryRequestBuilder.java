package com.zbsp.alipay.trade.model.builder;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

public class AlipayTradeBillDownloadUrlQueryRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.billDate)) {
            throw new NullPointerException("billDate should not be NULL!");
        }
        if (StringUtils.isEmpty(bizContent.billType)) {
            throw new NullPointerException("billType should not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeBillDownloadUrlQueryRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeBillDownloadUrlQueryRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeBillDownloadUrlQueryRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    public String getBillType() {
        return bizContent.billType;
    }

    public AlipayTradeBillDownloadUrlQueryRequestBuilder setBillType(String billType) {
        bizContent.billType = billType;
        return this;
    }

    public String getBillDate() {
        return bizContent.billDate;
    }

    public AlipayTradeBillDownloadUrlQueryRequestBuilder setBillDate(String billDate) {
        bizContent.billDate = billDate;
        return this;
    }

    public static class BizContent {
        // 商户订单号，通过此商户订单号撤销当面付的交易状态
        @SerializedName("bill_type")
        private String billType;
        
        @SerializedName("bill_date")
        private String billDate;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("billType='").append(billType).append('\'');
            sb.append("billDate='").append(billDate).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
