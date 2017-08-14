package com.zbsp.alipay.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class AlipayEcoEduKtBillingQueryRequestBuilder
    extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public boolean validate() {
        if (StringUtils.isBlank(bizContent.isvPid))
            throw new NullPointerException("isvPid should not be NULL!");
        if (StringUtils.isBlank(bizContent.schoolPid))
            throw new NullPointerException("schoolPid should not be NULL!");
        if (StringUtils.isBlank(bizContent.outTradeNo))
            throw new NullPointerException("outTradeNo should not be NULL!");
        
        return true;
    }

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    public String getIsvPid() {
        return bizContent.isvPid;
    }

    public AlipayEcoEduKtBillingQueryRequestBuilder setIsvPid(String isvPid) {
        bizContent.isvPid = isvPid;
        return this;
    }

    public String getSchoolPid() {
        return bizContent.schoolPid;
    }

    public AlipayEcoEduKtBillingQueryRequestBuilder setSchoolPid(String schoolPid) {
        bizContent.schoolPid = schoolPid;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayEcoEduKtBillingQueryRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public static class BizContent {

        /** 必填 */
        @SerializedName("isv_pid")
        private String isvPid;

        /** 必填 学校支付宝pid */
        @SerializedName("school_pid")
        private String schoolPid;

        /** 必填 ISV调用发送账单接口，返回给商户的order_no */
        @SerializedName("out_trade_no")
        private String outTradeNo;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("BizContent [isvPid=").append(isvPid).append(", schoolPid=").append(schoolPid).append(", outTradeNo=").append(outTradeNo).append("]");
            return builder.toString();
        }

    }

}
