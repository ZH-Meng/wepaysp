package com.zbsp.alipay.trade.model.builder;

import com.google.gson.annotations.SerializedName;

public class AlipayEcoEduKtBillingSendRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }
    
    @Override
    public boolean validate() {
        return true;
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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append('}');
            return sb.toString();
        }
    }
    
}
