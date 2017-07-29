package com.zbsp.wepaysp.api.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.WXPay;
import com.tencent.protocol.appid.base_access_token_protocol.GetBaseAccessTokenReqData;
import com.tencent.protocol.appid.base_access_token_protocol.GetBaseAccessTokenResData;
import com.tencent.protocol.appid.send_template_msg_protocol.SendTemplateMsgReqData;
import com.tencent.protocol.appid.send_template_msg_protocol.SendTemplateMsgResData;
import com.tencent.protocol.appid.send_template_msg_protocol.TemplateData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenReqData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoResData;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.WxEnums.GrantType;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.Validator;

/**
 * 微信接口工具类
 * 
 * @author 孟郑宏
 */
public final class WeixinUtil {
    
    /**日志对象*/
    private static final Logger logger = LogManager.getLogger(WeixinUtil.class);
    
    private final static int refreshLimitErrorCount = 3;
	
    /**FIXME 可以在数据库中灵活配置需要通知的业务和模版ID的对应关系，以及模版ID的启用停用*/
    private static String TEMPLATE_ID_PAY_SUCCESS = "nJWFUU8wDvd7elT4znZivLLHmMYl_ajID6cd4OujHa0";
    
    /**
     * 发送订单支付成功通知，使用微信模版库模版（编号：OPENTM406213232，templateId=nJWFUU8wDvd7elT4znZivLLHmMYl_ajID6cd4OujHa0）
     * 
     * <pre>
     * 构造模版消息包；
     * 调用模版消息发送接口；
     * 返回消息发送结果（不能确定是否下发至微信用户，需要查看事件推送结果）；
     * </pre>
     * 
     * @param touser 微信用户在此公众号的唯一标识openID
     * @param certLocalPath
     * @param certPassword
     * @param accessToken 基础支持的接口accessToken
     * @return 发送消息结果（不能判断是否下发至微信用户）
     * @throws Exception
     */
    public static SendTemplateMsgResData sendPaySuccessNotice(Map<String, Object> payResultMap, String touser, String certLocalPath, String certPassword, String accessToken, String messageUrl) throws Exception {
        Validator.checkArgument(payResultMap == null, "发送支付通知payResultMap不能为空");
        Validator.checkArgument(StringUtils.isBlank(touser),"发送支付通知touser不能为空");
        Validator.checkArgument(StringUtils.isBlank(certLocalPath),"发送支付通知certLocalPath不能为空");
        Validator.checkArgument(StringUtils.isBlank(certPassword),"发送支付通知certPassword不能为空");
        Validator.checkArgument(StringUtils.isBlank(accessToken),"发送支付通知accessToken不能为空");
        String outTradeNo = MapUtils.getString(payResultMap, "outTradeNo");
        Integer totalFee = MapUtils.getInteger(payResultMap, "totalFee");
        if (totalFee== null) {
    		totalFee = MapUtils.getInteger(payResultMap, "totalAmount");
		}
		Object transBeginTime = MapUtils.getObject(payResultMap, "transBeginTime"); // FIXME
        Validator.checkArgument(StringUtils.isBlank(outTradeNo),"发送支付通知系统订单号不能为空");
        Validator.checkArgument(totalFee == null, "发送支付通知totalFee不能为空");
        Validator.checkArgument(transBeginTime == null, "发送支付通知transBeginTime不能为空");
        String dealerName = MapUtils.getString(payResultMap, "dealerName");
        String storeName = MapUtils.getString(payResultMap, "storeName");
        Date transTime = null;
        if (transBeginTime instanceof Date) {
        	transTime = (Date) transBeginTime;
        } else if (transBeginTime instanceof Timestamp) { 
        	transTime = new Date(((Timestamp) transBeginTime).getTime());
        }
        
        // 构造模版数据
        Map<String, TemplateData> dataMap = new HashMap<String,TemplateData>();
        /*
         * {{first.DATA}} 订单编号：{{keyword1.DATA}} 订单金额：{{keyword2.DATA}} 实付金额：{{keyword3.DATA}} 消费地点：{{keyword4.DATA}} 消费时间：{{keyword5.DATA}} {{remark.DATA}}
         */
        TemplateData first = new TemplateData("收到一笔支付通知，订单信息如下：", "#000000");
        dataMap.put("first", first);
      	DecimalFormat myformat = new DecimalFormat();
    	myformat.applyPattern("###,###,##0.00");
    	
        TemplateData keyword1 = new TemplateData(outTradeNo, "#000000");
        TemplateData keyword2 = new TemplateData(myformat.format(new BigDecimal(totalFee).divide(new BigDecimal(100))) + "元", "#000000");
        TemplateData keyword3 = new TemplateData(myformat.format(new BigDecimal(totalFee).divide(new BigDecimal(100))) + "元", "#000000");
        TemplateData keyword4 = new TemplateData(dealerName + "-" + storeName, "#000000");
        TemplateData keyword5 = new TemplateData(DateUtil.getDate(transTime, "yyyy-MM-dd HH:mm:ss"));
        dataMap.put("keyword1", keyword1);
        dataMap.put("keyword2", keyword2);
        dataMap.put("keyword3", keyword3);
        dataMap.put("keyword4", keyword4);
        dataMap.put("keyword5", keyword5);
        // 构造模版消息包
        SendTemplateMsgReqData templateMsg = new SendTemplateMsgReqData(touser, null, TEMPLATE_ID_PAY_SUCCESS, messageUrl, dataMap);
        // 调用模版消息发送接口
        String jsonResult = WXPay.requestSendTemplateMsgService(templateMsg, accessToken, certLocalPath, certPassword);
        // 返回消息发送结果（不能确定是否下发至微信用户，需要查看事件推送结果）
        return JSONUtil.parseObject(jsonResult, SendTemplateMsgResData.class);
    }
    
    /**
     * 获取Base_access_token，如果过期则先刷新再返回
     * @param partner1Oid
     * @return
     */
    public static synchronized String getBaseAccessToken(String partner1Oid) {
    	Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partner1Oid); 
    	String accessToken = MapUtils.getString(partnerMap, SysEnvKey.WX_BASE_ACCESS_TOKEN);
		Long expireTime = MapUtils.getLong(partnerMap, SysEnvKey.WX_BASE_EXPIRE_TIME);
		String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);
		// 检查和设置开关
		boolean flag = false;
    	if (StringUtils.isBlank(accessToken)) {// 首次启动
    	    logger.info("系统启动，服务商（"+ appid +"）首次获取access_token");
    		flag = true;
        } else if (expireTime == null || TimeUtil.plusSeconds(120).getTime() >= expireTime.longValue()) {// access_token过期 提前两分钟刷新
            logger.info("服务商（"+ appid +"）access_token过期，准备刷新");
    		flag = true;
    	}
    	if (flag) {
    		return refreshBaseAccessToken(partner1Oid);
    	} else {
    		return accessToken;
    	}
    }
    
    /**
     * 刷新Base_access_token
     * @param partner1Oid 服务商Oid
     */
    public static String refreshBaseAccessToken(String partner1Oid) {
    	Validator.checkArgument(StringUtils.isBlank(partner1Oid), "partner1Oid不能为空");
    	Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partner1Oid); 
    	String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);
    	logger.info("服务商（"+ appid +"）开始调用刷新access_token接口");
		
		String secret= MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET);
		String certLocalPath= MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
		String certPassword= MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
		for (int i = 1; i <= refreshLimitErrorCount; i++) {
		    try {
	            String jsonResult = WXPay.requestGetBaseAccessTokenService(new GetBaseAccessTokenReqData(appid, secret), certLocalPath, certPassword);
	            // 转化JSON结果
	            GetBaseAccessTokenResData accessTokenResult = JSONUtil.parseObject(jsonResult, GetBaseAccessTokenResData.class);
	            // 校验获取access_token
	            if (checkBaseAccessTokenResult(accessTokenResult)) {
	                logger.info("服务商（"+ appid  +"）获取/刷新Access_token成功，access_token：" + accessTokenResult.getAccess_token() + "，expires_in：" + accessTokenResult.getExpires_in());
	                // 缓存access_token 和 expire_time
	                partnerMap.put(SysEnvKey.WX_BASE_ACCESS_TOKEN, accessTokenResult.getAccess_token());
	                partnerMap.put(SysEnvKey.WX_BASE_EXPIRE_TIME, new Date().getTime() + accessTokenResult.getExpires_in() * 1000);
	                logger.info("服务商（"+ appid +"）分别更新以partner1Oid、appid为key的Map配置");
	                SysConfig.partnerConfigMap.put(partner1Oid, partnerMap);
	                SysConfig.partnerConfigMap2.put(appid, partnerMap);
	                break;
	            } else {
	                logger.error("服务商（"+ appid  +"）获取/刷新Access_token失败第" + i + "次，错误码：" + accessTokenResult.getErrcode() + "，错误描述：" + accessTokenResult.getErrmsg());
	                try {
	                    Thread.sleep(3000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
            } catch (Exception e) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), "获取/刷新Access_token失败", "，异常信息：" + e.getMessage()), e);
            }
		}
		return MapUtils.getString(partnerMap, SysEnvKey.WX_BASE_ACCESS_TOKEN);
    }
    
    /**
     * 校验http get获取基础接口access_token的结果
     * 
     * @param getBaseAccessTokenResData
     * @return
     */
    public static boolean checkBaseAccessTokenResult(GetBaseAccessTokenResData getBaseAccessTokenResData) {
        boolean result = false;
        if (getBaseAccessTokenResData == null) {
            logger.warn("getBaseAccessTokenResData为空");
        } else {
            logger.info(getBaseAccessTokenResData.toString());
            
            if (StringUtils.isNotBlank(getBaseAccessTokenResData.getAccess_token())) {
                result = true;
            } else if (StringUtils.isNotBlank(getBaseAccessTokenResData.getErrcode())) {
                result = false;
            } else {
                logger.warn("get or refresh base access_token result invalid");
            }
        }
        return result;
    }
    
    /**
     * 校验http get获取网页授权access_token的结果
     * 
     * @param getAuthAccessTokenResData
     * @return
     */
    public static boolean checkAuthAccessTokenResult(GetAuthAccessTokenResData getAuthAccessTokenResData) {
        boolean result = false;
        if (getAuthAccessTokenResData == null) {
            logger.warn("getAuthAccessTokenResData为空");
        } else {
            logger.info(getAuthAccessTokenResData.toString());
            
            if (StringUtils.isNotBlank(getAuthAccessTokenResData.getAccess_token()) && StringUtils.isNotBlank(getAuthAccessTokenResData.getOpenid())) {
                result = true;
            } else if (StringUtils.isNotBlank(getAuthAccessTokenResData.getErrcode())) {
                result = false;
            } else {
                logger.warn("get auth access_token result invalid");
            }
        }
        return result;
    }
    
    /**
     * 校验http get 网页授权通过后拉取用户信息的结果
     * 
     * @param getUserinfoResData
     * @return
     */
    public static boolean checkUserinfoResult(GetUserinfoResData getUserinfoResData) {
        boolean result = false;
        if (getUserinfoResData == null) {
            logger.warn("getUserinfoResData为空");
        } else {
            logger.info(getUserinfoResData.toString());
            
            if (StringUtils.isNotBlank(getUserinfoResData.getOpenid())) {
                result = true;
            } else if (StringUtils.isNotBlank(getUserinfoResData.getErrcode())) {
                result = false;
            } else {
                logger.warn("get userinfo result invalid");
            }
        }
        return result;
    }
    
    /**
     * 通过code换取网页授权access_token 和 openid
     * @param authCode 微信公众号授权后回调返回的code
     * @return 获取token和openid的响应GetAuthAccessTokenResData，如果null代表授权失败
     */
    public static GetAuthAccessTokenResData getAuthAccessToken(String authCode, String topPartnerOid) {
        logger.info("获取网页授权access_token 和 openid - 开始");
        GetAuthAccessTokenResData authResult = null;
        try {
            Validator.checkArgument(StringUtils.isBlank(authCode), "authCode不能空");
            Validator.checkArgument(StringUtils.isBlank(topPartnerOid), "topPartnerOid不能空");
            // 从内存中获取服务商配置信息
            Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(topPartnerOid);
            if (partnerMap == null || partnerMap.isEmpty()) {
                throw new RuntimeException("服务商配置不存在，topPartnerOid=" + topPartnerOid);
            }
            GetAuthAccessTokenReqData authReqData = new GetAuthAccessTokenReqData(GrantType.AUTHORIZATION_CODE.getValue(), MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID),
                MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET), authCode, null);
            logger.info("获取网页授权access_token 和 openid，request Data : {}", authReqData.toString());

            String jsonResult = WXPay.requestGetAuthAccessTokenService(authReqData, MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH),
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
            authResult = JSONUtil.parseObject(jsonResult, GetAuthAccessTokenResData.class);
            logger.info("获取网页授权access_token 和 openid，response Data : {}", authResult.toString());

            // 校验获取access_token
            if (WeixinUtil.checkAuthAccessTokenResult(authResult)) {
                logger.info("获取网页授权access_token 和 openid - 成功, auth_access_token：{}, expires_in：{} " + ",refresh_token：{}, openid：{}", authResult.getAccess_token(), authResult.getExpires_in(),
                    authResult.getRefresh_token(), authResult.getOpenid());
                // TODO 设置过期时间
                /*由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
                refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
                如果需要定期同步用户的昵称，则需要考虑刷新access_token*/
            } else {
                logger.warn("获取网页授权access_token 和 openid - 失败，" + (authResult != null ? (" 错误码：" + authResult.getErrcode() + "，错误描述：" + authResult.getErrmsg()) : null));
                authResult = null;
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), "获取网页授权access_token 和 openid", "，异常信息：{}"), e.getMessage(), e);
            authResult = null;
        } finally {
            logger.info("获取网页授权access_token 和 openid - 结束");
        }
        return authResult;
    }
    
}
