package com.zbsp.wepaysp.api.util;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayOpenAuthTokenAppQueryRequest;
import com.alipay.api.request.AlipayOpenAuthTokenAppRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayOpenAuthTokenAppQueryResponse;
import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.zbsp.alipay.trade.config.Configs;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.alipay.trade.model.TradeStatus;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppQueryRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradeWapPayRequestBuilder;
import com.zbsp.alipay.trade.model.result.AlipayF2FPayResult;
import com.zbsp.alipay.trade.model.result.AlipayF2FQueryResult;
import com.zbsp.alipay.trade.service.AlipayMonitorService;
import com.zbsp.alipay.trade.service.AlipayTradeService;
import com.zbsp.alipay.trade.service.impl.AlipayMonitorServiceImpl;
import com.zbsp.alipay.trade.service.impl.AlipayTradeServiceImpl;
import com.zbsp.alipay.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/**
 * 支付宝支付接口工具类
 * 
 * @author 孟郑宏
 */
public class AliPayUtil {
    protected static final Logger logger = LogManager.getLogger(AliPayUtil.class);
    
    public static AlipayClient client;
    
    /**支付宝当面付2.0服务*/
    public static AlipayTradeService tradeService;

    /**支付宝当面付2.0服务（集成了交易保障接口逻辑）*/
    public static AlipayTradeService tradeWithHBService;

    /**支付宝交易保障接口服务*/
    public static AlipayMonitorService monitorService;
    
    // 调用AlipayClient的execute方法，进行远程调用
    public  static <T extends AlipayResponse> T getResponse(AlipayClient client, AlipayRequest<T> request) {
        try {
            T response = client.execute(request);
            if (response != null) {
                logger.info(response.getBody());
            }
            return response;

        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /** 获取缺省AliPay交易服务，公共参数已通过配置文件构建*/
    public static AlipayTradeService getDefaultAlipayTradeService() {
        return tradeService;
    }
    
    /**使用app_auth_code换取app_auth_token，接口名称：alipay.open.auth.token.app<br>
     * 应用授权的app_auth_code唯一的；app_auth_code使用一次后失效，一天（从生成app_auth_code开始的24小时）未被使用自动过期； 
     * app_auth_token有效期为365天，刷新后重新计时。
     */
    public static AlipayOpenAuthTokenAppResponse authTokenApp(AlipayOpenAuthTokenAppRequestBuilder builder) {
        AlipayOpenAuthTokenAppRequest request = new AlipayOpenAuthTokenAppRequest();
        // 设置平台参数
        
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AuthTokenAppRequest bizContent:" + request.getBizContent());
        AlipayOpenAuthTokenAppResponse response = null;
        for (int i = 1; i < 3; i++) {
            response = getResponse(client, request);
            logger.info("AuthTokenAppResponse :" + response == null ? null : JSONUtil.toJSONString(response, true));
            if (response != null && Constants.SUCCESS.equals(response.getCode())) {
                logger.info("应用授权使用app_auth_code换取app_auth_token成功");
                break;
            } else if (response != null && Constants.FAILED.equals(response.getCode())) {
                logger.warn("应用授权使用app_auth_code换取app_auth_token失败");
                break;
            } else {
                logger.warn("应用授权使用app_auth_code换取app_auth_token异常");
            }
        }
        
        return response;
    }
    
    /**查询授权信息，接口名称：alipay.open.auth.token.app.query<br>
     * 当商户把服务窗、店铺等接口的权限授权给ISV之后，支付宝会给ISV颁发一个app_auth_token。
     * 如若授权成功之后，ISV想知道用户的授权信息，如授权者、授权接口列表等信息，可以调用本接口查询某个app_auth_token对应的授权信息。
     */
    public static AlipayOpenAuthTokenAppQueryResponse  authTokenAppQuery(AlipayOpenAuthTokenAppQueryRequestBuilder builder) {
        AlipayOpenAuthTokenAppQueryRequest request = new AlipayOpenAuthTokenAppQueryRequest();
        // 设置平台参数

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AuthTokenAppRequest bizContent:" + request.getBizContent());
        AlipayOpenAuthTokenAppQueryResponse response = null;
        for (int i = 1; i < 3; i++) {
            response = getResponse(client, request);
            logger.info("AuthTokenAppResponse :" + response == null ? null : JSONUtil.toJSONString(response, true));
            if (response != null && Constants.SUCCESS.equals(response.getCode())) {
                logger.info("查询授权信息（app_auth_token={}）成功", builder.getAppAuthToken());
                break;
            } else if (response != null && Constants.FAILED.equals(response.getCode())) {
                logger.warn("查询授权信息（app_auth_token={}）失败", builder.getAppAuthToken());
                break;
            } else {
                logger.warn("查询授权信息（app_auth_token={}）异常", builder.getAppAuthToken());
            }
        }
        
        return response;
    }
    
    /**
     * 当面付支付2.0
     * @param payDetailsVO
     * @return
     */
    public static AlipayF2FPayResult tradeF2FPay(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(payDetailsVO == null, "payDetailsVO为空");
        
        logger.info("支付明细转换当面付支付请求包构造器 - 开始");
        AlipayTradePayRequestBuilder builder = AliPayPackConverter.aliPayDetailsVO2AlipayTradePayRequestBuilder(payDetailsVO);
        logger.info("支付明细转换当面付支付请求包构造器 - 成功");
        
        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult payResult = null;
        if (SysConfig.alipayReportFlag) {
            payResult = AliPayUtil.tradeWithHBService.tradePay(builder);
        } else {
            payResult = AliPayUtil.tradeService.tradePay(builder);
        }
        return payResult;
    }
    
    /**
     * 手机网站支付下单(手机网站支付2.0)
     * @param payDetailsVO
     * @return
     */
    public static AlipayTradeWapPayResponse tradeWapPay(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(payDetailsVO == null, "payDetailsVO为空");
        
        logger.info("支付明细转换手机网站支付请求包构造器 - 开始");
        // 支付请求构造器
        AlipayTradeWapPayRequestBuilder builder = AliPayPackConverter.aliPayDetailsVO2AlipayTradeWapPayRequestBuilder(payDetailsVO);
        
        logger.info("支付明细转换手机网站支付请求包构造器  - 成功");
        
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        // 设置平台参数
        request.setNotifyUrl(SysConfig.alipayWapPayNotifyURL);
        request.setReturnUrl(SysConfig.alipayWapPayReturnURL);
        
        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        logger.info("AlipayTradeWapPayRequest bizContent:" + request.getBizContent());
        AlipayTradeWapPayResponse response = null;
        for (int i = 1; i < 3; i++) {
            try {
                response = client.pageExecute(request);
                logger.info("AlipayTradeWapPayResponse :" + response == null ? null : JSONUtil.toJSONString(response, true));
                if (response != null && StringUtils.isNotBlank(response.getBody())) {
                    logger.info("手机网站支付下单成功");
                    break;
                } else {
                    logger.warn("手机网站支付下单失败");
                    break;
                }
            } catch (AlipayApiException e) {
                logger.warn("手机网站支付下单异常");
                e.printStackTrace();
            }
        }
        
        return response;
    }
    
    /**
     * 支付宝交易查询
     * @param outTradeNo
     * @return
     */
    public static AlipayF2FQueryResult tradeQuery(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(payDetailsVO == null, "payDetailsVO为空");
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getOutTradeNo()), "payDetailsVO.outTradeNo为空");
        
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder();
        builder.setOutTradeNo(payDetailsVO.getOutTradeNo());
        builder.setTradeNo(payDetailsVO.getTradeNo());
        builder.setAppAuthToken(payDetailsVO.getAppAuthToken());
        
        request.setBizContent(builder.toJsonString());
        
        logger.info("AlipayTradeQueryRequest bizContent:" + request.getBizContent());
        AlipayF2FQueryResult queryTradeResult = null;
        for (int i = 1; i < 3; i++) {
            // 查询
            queryTradeResult = tradeService.queryTradeResult(builder);
            if (TradeStatus.UNKNOWN.equals(queryTradeResult.getTradeStatus())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
        
        logger.info("queryTradeResult : {}", JSONUtil.toJSONString(queryTradeResult, true));
        if (TradeStatus.SUCCESS.equals(queryTradeResult.getTradeStatus())) {
            logger.info("支付宝交易查询结果：交易成功（outTradeNo={}）", builder.getOutTradeNo());
        } else if (TradeStatus.UNKNOWN.equals(queryTradeResult.getTradeStatus())) {
            logger.warn("支付宝交易查询结果：交易状态未知（outTradeNo={}）", builder.getOutTradeNo());
        } else {
            logger.warn("支付宝交易查询结果：交易失败（outTradeNo={}）", builder.getOutTradeNo());
        }
        
        return queryTradeResult;
    }

    @Deprecated
    public static void init() {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");
        
        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        
        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
            .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
            .setFormat("json").build();
        
        /*if (StringUtils.isEmpty(Configs.getOpenApiDomain())) {
        throw new NullPointerException("gatewayUrl should not be NULL!");
        }
        if (StringUtils.isEmpty(Configs.getAppid())) {
            throw new NullPointerException("appid should not be NULL!");
        }
        if (StringUtils.isEmpty(Configs.getPrivateKey())) {
            throw new NullPointerException("privateKey should not be NULL!");
        }
        if (StringUtils.isEmpty(Configs.getAlipayPublicKey())) {
            throw new NullPointerException("alipayPublicKey should not be NULL!");
        }
        if (StringUtils.isEmpty(Configs.getSignType())) {
            throw new NullPointerException("signType should not be NULL!");
        }*/
        
        client = new DefaultAlipayClient(Configs.getOpenApiDomain(), Configs.getAppid(), Configs.getPrivateKey(),
            "json", "utf-8", Configs.getAlipayPublicKey(), Configs.getSignType());
    }

    public static void init(Map<String, Object> app) {
        String openApiDomain = SysConfig.onlineFlag ? SysEnvKey.ANT_OPEN_API_DOMAIN : SysEnvKey.ANT_OPEN_API_DOMAIN_DEV;
        String appid = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_ID);
        String alipayPublicKey = MapUtils.getString(app, SysEnvKey.ALIPAY_PUBLIC_KEY);
        String signType = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_SIGN_TYPE);
        String privateKey = MapUtils.getString(app, SysEnvKey.ALIPAY_APP_PRIVATE_KEY);
        
        tradeService = new AlipayTradeServiceImpl.ClientBuilder()
            .setGatewayUrl(openApiDomain)
            .setAppid(appid)
            .setAlipayPublicKey(alipayPublicKey)
            .setSignType(signType)
            .setPrivateKey(privateKey)
            .build();
        
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder()
            .setGatewayUrl(openApiDomain)
            .setAppid(appid)
            .setAlipayPublicKey(alipayPublicKey)
            .setSignType(signType)
            .setPrivateKey(privateKey)
            .build();

        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
            .setGatewayUrl(SysEnvKey.ANT_CLOUD_MONITOR_DOMAIN)
            .setAppid(appid)
            .setSignType(signType)
            .setPrivateKey(privateKey)
            //.setCharset("GBK")
            .setFormat("json")
            .build();
        
        client = new DefaultAlipayClient(Configs.getOpenApiDomain(), appid, privateKey, "json", "utf-8", alipayPublicKey, signType);
    }
    
}
