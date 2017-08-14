package com.zbsp.wepaysp.api.util;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayEcoEduKtBillingModifyRequest;
import com.alipay.api.request.AlipayEcoEduKtBillingQueryRequest;
import com.alipay.api.request.AlipayEcoEduKtBillingSendRequest;
import com.alipay.api.request.AlipayEcoEduKtSchoolinfoModifyRequest;
import com.alipay.api.response.AlipayEcoEduKtBillingModifyResponse;
import com.alipay.api.response.AlipayEcoEduKtBillingQueryResponse;
import com.alipay.api.response.AlipayEcoEduKtBillingSendResponse;
import com.alipay.api.response.AlipayEcoEduKtSchoolinfoModifyResponse;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingModifyRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingQueryRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingSendRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtSchoolinfoModifyRequestBuilder;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.partner.School;

public class AliPayEduUtil {

    protected static final Logger logger = LogManager.getLogger(AliPayEduUtil.class);
    
    /** 教育缴费应用访问客户端*/
    public static AlipayClient eduClient;
    
    /**
     * 学校信息录入
     * @throws AlipayApiException 
     */
    public static AlipayEcoEduKtSchoolinfoModifyResponse schoolInfoModify(School school) throws AlipayApiException {
        Validator.checkArgument(school == null, "school 为空");
        
        AlipayEcoEduKtSchoolinfoModifyRequestBuilder builder = AliPayPackConverter.school2AlipayEcoEduKtSchoolinfoModifyRequestBuilder(school);
        
        AlipayEcoEduKtSchoolinfoModifyRequest request = new AlipayEcoEduKtSchoolinfoModifyRequest();
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayEcoEduKtSchoolinfoModifyRequest bizContent:" + request.getBizContent());
        
        return eduClient.execute(request);
    }
    
    /**
     * 发送账单
     * @throws AlipayApiException 
     */
    public static AlipayEcoEduKtBillingSendResponse billSend(AlipayEduBill alipayEduBill) throws AlipayApiException {
        Validator.checkArgument(alipayEduBill == null, "alipayEduBill为空");
        
        AlipayEcoEduKtBillingSendRequestBuilder builder = AliPayPackConverter.alipayEduBill2AlipayEcoEduKtBillingSendRequestBuilder(alipayEduBill);
        
        AlipayEcoEduKtBillingSendRequest request = new AlipayEcoEduKtBillingSendRequest();
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayEcoEduKtBillingSendRequest bizContent:" + request.getBizContent());
        
        return eduClient.execute(request);
    }
    
    /**
     * 同步账单状态，1：缴费成功，2关闭账单，3退费，4同步网商状态返回的状态
     * @throws AlipayApiException 
     */
    public static AlipayEcoEduKtBillingModifyResponse billModify(AlipayEduBill alipayEduBill, int status) throws AlipayApiException {
        Validator.checkArgument(alipayEduBill == null, "alipayEduBill为空");
        
        AlipayEcoEduKtBillingModifyRequestBuilder builder = AliPayPackConverter.alipayEduBill2AlipayEcoEduKtBillingModifyRequestBuilder(alipayEduBill);
        builder.setStatus(String.valueOf(status));
        
        AlipayEcoEduKtBillingModifyRequest request = new AlipayEcoEduKtBillingModifyRequest();
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayEcoEduKtBillingModifyRequest bizContent:" + request.getBizContent());
        
        return eduClient.execute(request);
    }
    
    /**
     * 账单查询
     * @throws AlipayApiException 
     */
    public static AlipayEcoEduKtBillingQueryResponse billQuery(AlipayEduBill alipayEduBill) throws AlipayApiException {
        Validator.checkArgument(alipayEduBill == null, "alipayEduBill为空");
        
        AlipayEcoEduKtBillingQueryRequestBuilder builder = AliPayPackConverter.alipayEduBill2AlipayEcoEduKtBillingQueryRequestBuilder(alipayEduBill);
        
        AlipayEcoEduKtBillingQueryRequest request = new AlipayEcoEduKtBillingQueryRequest();
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayEcoEduKtBillingQueryRequest bizContent:" + request.getBizContent());
        
        return eduClient.execute(request);
    }
    
    /**初始化教育缴费配置*/
    public static void init(Map<String, Object> app) {
		String openApiDomain = SysEnvKey.ANT_OPEN_API_DOMAIN ;
		String appid = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_ID);
		String alipayPublicKey = MapUtils.getString(app, SysEnvKey.ALIPAY_PUBLIC_KEY);
		String signType = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_SIGN_TYPE);
		String privateKey = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_PRIVATE_KEY);
		eduClient = new DefaultAlipayClient(openApiDomain, appid, privateKey, "json", "utf-8", alipayPublicKey, signType);
    }
}
