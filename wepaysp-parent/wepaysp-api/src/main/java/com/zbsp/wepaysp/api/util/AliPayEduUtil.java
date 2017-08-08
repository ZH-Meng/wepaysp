package com.zbsp.wepaysp.api.util;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayEcoEduKtBillingSendRequest;
import com.alipay.api.response.AlipayEcoEduKtBillingSendResponse;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingSendRequestBuilder;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;

public class AliPayEduUtil {

    protected static final Logger logger = LogManager.getLogger(AliPayEduUtil.class);
    
    /** 教育缴费应用访问客户端*/
    public static AlipayClient eduClient;
    
    /**
     * 发送账单
     * @throws AlipayApiException 
     */
    public static AlipayEcoEduKtBillingSendResponse billSend(AlipayEduBillVO alipayEduBillVO) throws AlipayApiException {
        Validator.checkArgument(alipayEduBillVO == null, "alipayEduBillVO为空");
        
        AlipayEcoEduKtBillingSendRequestBuilder builder = AliPayPackConverter.alipayEduBillVO2AlipayEcoEduKtBillingSendRequestBuilder(alipayEduBillVO);
        
        AlipayEcoEduKtBillingSendRequest request = new AlipayEcoEduKtBillingSendRequest();
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayEcoEduKtBillingSendRequest bizContent:" + request.getBizContent());
        
        return eduClient.execute(request);
    }
    
    /**初始化教育缴费配置*/
    public static void init(Map<String, Object> app) {
		String openApiDomain = SysConfig.onlineFlag ? SysEnvKey.ANT_OPEN_API_DOMAIN : SysEnvKey.ANT_OPEN_API_DOMAIN_DEV;
		String appid = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_ID);
		String alipayPublicKey = MapUtils.getString(app, SysEnvKey.ALIPAY_PUBLIC_KEY);
		String signType = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_SIGN_TYPE);
		String privateKey = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_PRIVATE_KEY);
		eduClient = new DefaultAlipayClient(openApiDomain, appid, privateKey, "json", "utf-8", alipayPublicKey, signType);
    }
}
