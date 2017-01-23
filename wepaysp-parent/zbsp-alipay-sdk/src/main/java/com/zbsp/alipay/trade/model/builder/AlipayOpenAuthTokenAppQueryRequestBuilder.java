package com.zbsp.alipay.trade.model.builder;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * 查询授权信息
 * 
 * @author 孟郑宏
 */
public class AlipayOpenAuthTokenAppQueryRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.appAuthToken)) {
            throw new IllegalStateException("appAuthToken can not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayOpenAuthTokenAppQueryRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }


    public String getAppAuthToken() {
        return bizContent.appAuthToken;
    }

    public AlipayOpenAuthTokenAppQueryRequestBuilder setAppAuthToken(String appAuthToken) {
        bizContent.appAuthToken = appAuthToken;
        return this;
    }

    public static class BizContent {
        // 商户授权令牌，通过该令牌来帮助商户发起请求，完成业务
        @SerializedName("app_auth_token")
        private String appAuthToken;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append(", appAuthToken='").append(appAuthToken).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
