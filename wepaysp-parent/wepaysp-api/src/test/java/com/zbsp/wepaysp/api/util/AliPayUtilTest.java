package com.zbsp.wepaysp.api.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.response.AlipayOpenAuthTokenAppQueryResponse;
import com.zbsp.alipay.trade.config.Configs;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppQueryRequestBuilder;
import com.zbsp.wepaysp.common.util.JSONUtil;

public class AliPayUtilTest {
 
    @BeforeClass
    public static void init() {
        Configs.init("zfbinfo.properties");
        
        AliPayUtil.client = new DefaultAlipayClient(Configs.getOpenApiDomain(), Configs.getAppid(), Configs.getPrivateKey(),
            "json", "utf-8", Configs.getAlipayPublicKey(), Configs.getSignType());
    }
    
    @Test
    public void appAuthQuery() {
        // 查询授权信息
        AlipayOpenAuthTokenAppQueryResponse authQueryResponse= AliPayUtil.authTokenAppQuery(
            new AlipayOpenAuthTokenAppQueryRequestBuilder().setAppAuthToken("201702BBf5d53c08827c4dea9b6ff6323ce42E31"));
        Assert.assertEquals("令牌有效", "invalid", authQueryResponse.getStatus());
        Assert.assertNotNull("授权接口List为Null", authQueryResponse.getAuthMethods());
        String authMethods = authQueryResponse.getAuthMethods().toString();
        System.out.println(authQueryResponse.getAuthMethods().toString());
        System.out.println(authQueryResponse.getAuthMethods().size());
        System.out.println(authMethods.substring(1, authMethods.length()-1));
        System.out.println(JSONUtil.toJSONString(authQueryResponse, true));
    }
}
