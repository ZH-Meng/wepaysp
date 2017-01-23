package com.zbsp.alipay.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 第三方应用授权请求构建器
 * 
 * @author 孟郑宏
 */
public class AlipayOpenAuthTokenAppRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.grantType)) {
            throw new NullPointerException("grant_type should not be NULL!");
        } else {
            if (!bizContent.grantType.equals("authorization_code") && !bizContent.grantType.equals("refresh_token")) {
                throw new IllegalStateException("invalid grant_type!");
            } else if (bizContent.grantType.equals("authorization_code") && StringUtils.isEmpty(bizContent.code)) {
                throw new NullPointerException("grant_type = authorization_code, code should not be NULL!");
            } else if (bizContent.grantType.equals("refresh_token") && StringUtils.isEmpty(bizContent.refreshToken)) {
                throw new NullPointerException("grant_type = refresh_token, refresh_token should not be NULL!");
            }
        }
        return true;
    }
    
    public String getGrantType() {
        return bizContent.grantType;
    }

    public AlipayOpenAuthTokenAppRequestBuilder setGrantType(String grantType) {
        bizContent.grantType = grantType;
        return this;
    }

    public String getCode() {
        return bizContent.code;
    }

    public AlipayOpenAuthTokenAppRequestBuilder setCode(String code) {
        bizContent.code = code;
        return this;
    }

    public String getRefreshToken() {
        return bizContent.refreshToken;
    }

    public AlipayOpenAuthTokenAppRequestBuilder setRefreshToken(String refreshToken) {
        bizContent.refreshToken = refreshToken;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayOpenAuthTokenAppRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }
    
    public static class BizContent {
        // 如果使用app_auth_code换取token，则为authorization_code，如果使用refresh_token换取新的token，则为refresh_token
        @SerializedName("grant_type")
        private String grantType;
        // 与refresh_token二选一，用户对应用授权后得到，即第一步中开发者获取到的app_auth_code值
        private String code;

        // 与code二选一，可为空，刷新令牌时使用
        @SerializedName("refresh_token")
        private String refreshToken;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("grantType='").append(grantType).append('\'');
            sb.append(", code='").append(code).append('\'');
            sb.append(", refreshToken='").append(refreshToken).append('\'');
            sb.append('}');
            return sb.toString();
        }
        
    }

}
