package com.zbsp.wepaysp.manage.web.action.appid;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.tencent.WXPay;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenReqData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoReqData;
import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoResData;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.EnumDefine.GrantType;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.vo.appid.PayNoticeBindResult;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

/**
 * 支付通知绑定（门店/收银员）-不能登录拦截
 * 
 * @author 孟郑宏
 */
public class PayNoticeBindAction
    extends BaseAction {

    private static final long serialVersionUID = 1309080322428502169L;

    /** -----授权回调回传-系统业务参数-------- */
    private String partnerOid;
    private String storeOid;
    private String dealerEmployeeOid;

    /** -----授权回调回传-用户同意才有值------ */
    private String code;
    //private String state;

    /** 用户在公众号的唯一标识 */
    private String openid;

    /** 绑定结果 */
    private PayNoticeBindResult bindResult;
    private PayNoticeBindWeixinVO payNoticeBindWeixinVO;
    private String bindType;
    private DealerEmployeeService dealerEmployeeService;
    private StoreService storeService;
    private SysConfigService sysConfigService;
    private PayNoticeBindWeixinService  payNoticeBindWeixinService;

    /**
     * 微信扫码后，点击确认登录（同意授权）微信回调地址
     * 
     * @return
     */
    public String bindWxIDCallBack() {
        logger.info("微信支付通知绑定微信账户微信回调成功");
        String result = "payNoticeBindResult";
        if (!checkCallBackParam()) {
            return result;
        }
        
        // 根据partnerOid查找APPID、SECRET
        Map<String, String> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(partnerOid);
        if (partnerMap == null || partnerMap.isEmpty()) {
            logger.warn("微信支付通知绑定微信账户微信回调访问的服务商不存在，partnerOid：" + partnerOid);
            bindResult = new PayNoticeBindResult("partner_invalid", "服务商信息无效");
            return result;
        }
        
        // 通过code换取网页授权access_token 和 openid
        GetAuthAccessTokenReqData authReqData = new GetAuthAccessTokenReqData(GrantType.AUTHORIZATION_CODE.getValue(), 
            MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID), MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET), code, null);
        
        GetAuthAccessTokenResData authResult = null;
        try {
            logger.info("开始获取网页授权access_token 和 openid");
            
            String jsonResult = WXPay.requestGetAuthAccessTokenService(authReqData, 
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.CERT_PASSWORD));
            authResult = JSONUtil.parseObject(jsonResult, GetAuthAccessTokenResData.class);
            
            // 校验获取access_token
            if (checkAccessTokenResult(authResult)) {
                openid = authResult.getOpenid();
                logger.info("auth_access_token：" + authResult.getAccess_token() + "，expires_in：" + authResult.getExpires_in() + ",openid = " + openid);
                // TODO 设置过期时间
                /*由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
                refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
                如果需要定期同步用户的昵称，则需要考虑刷新access_token*/
                
            } else {
                logger.warn("获取网页授权Access_token失败，错误码：" + authResult.getErrcode() + "，错误描述：" + authResult.getErrmsg());
                bindResult = new PayNoticeBindResult("wx_auth_error", "授权失败，请重试");
                return result;
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(),
                "获取网页授权Access_token失败", "，异常信息：" + e.getMessage()));
            logger.error(e.getMessage(), e);
            bindResult = new PayNoticeBindResult("sys_error", "系统异常");
            return result;
        }
        
        // 拉取用户信息(scope为 snsapi_userinfo)
        GetUserinfoReqData userinfoReqData = new GetUserinfoReqData(openid, authResult.getAccess_token());
        
        GetUserinfoResData userinfoResult = null;
        try {
            logger.info("开始拉取用户信息");
            String jsonResult = WXPay.requestGetUserinfoService(userinfoReqData,
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.CERT_PASSWORD));
            userinfoResult = JSONUtil.parseObject(jsonResult, GetUserinfoResData.class);
            
            // 校验获取access_token
            if (checkUserinfoResult(userinfoResult)) {
                if (!openid.equals(userinfoResult.getOpenid())) {
                    logger.warn("网页授权获取openid和获取用户信息响应openid不一致，抛弃此结果");
                    bindResult = new PayNoticeBindResult("wx_auth_error", "授权失败，请重试");
                    return result;
                }
                
                logger.info("微信支付通知绑定微信账户开始");
                String toRelateOid = PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindType) ? dealerEmployeeOid : storeOid;
                
                // 绑定，前台页面根据绑定结果展示相应的门店/收银员信息
                payNoticeBindWeixinVO = payNoticeBindWeixinService.doTransAddPayNoticeBindWeixin(bindType, toRelateOid, userinfoResult);
                bindResult = new PayNoticeBindResult("success", "绑定成功");
            } else {
                logger.warn("获取用户信息失败，错误码：" + userinfoResult.getErrcode() + "，错误描述：" + userinfoResult.getErrmsg());
                bindResult = new PayNoticeBindResult("wx_auth_error", "授权失败，请重试");
                return result;
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(),
                "获取网页授权Access_token失败", "，异常信息：" + e.getMessage()));
            logger.error(e.getMessage(), e);
            bindResult = new PayNoticeBindResult("sys_error", "系统异常");
            return result;
        }

        logger.info("微信支付通知绑定微信账户微信回调处理完毕");
        return result;
    }
    
    /**
     * 校验http get 拉取用户信息的结果
     * 
     * @param getUserinfoResData
     * @return
     */
    private boolean checkUserinfoResult(GetUserinfoResData getUserinfoResData) {
        boolean result = false;
        if (getUserinfoResData == null) {
            logger.warn("getUserinfoResData为空");
        } else {
            logger.debug(getUserinfoResData.toString());
        }
        if (StringUtils.isNotBlank(getUserinfoResData.getOpenid())) {
            result = true;
        } else if (StringUtils.isNotBlank(getUserinfoResData.getErrcode())) {
            result = false;
        } else {
            logger.warn("get userinfo result invalid");
        }
        return result;
    }
    
    /**
     * 校验http get 获取access_token的结果
     * 
     * @param getAuthAccessTokenResData
     * @return
     */
    private boolean checkAccessTokenResult(GetAuthAccessTokenResData getAuthAccessTokenResData) {
        boolean result = false;
        if (getAuthAccessTokenResData == null) {
            logger.warn("getBaseAccessTokenResData为空");
        } else {
            logger.debug(getAuthAccessTokenResData.toString());
        }
        if (StringUtils.isNotBlank(getAuthAccessTokenResData.getAccess_token()) && StringUtils.isNotBlank(getAuthAccessTokenResData.getOpenid())) {
            result = true;
        } else if (StringUtils.isNotBlank(getAuthAccessTokenResData.getErrcode())) {
            result = false;
        } else {
            logger.warn("get auth access_token result invalid");
        }
        return result;
    }

    /**
     * 检查绑定时微信网页授权回调的系统URL中参数是否完整和正确
     * 
     * @return
     */
    private boolean checkCallBackParam() {
        if (StringUtils.isBlank(code)) {
            logger.error("用户绑定微信支付通知时，选择了禁止授权.");
            return false;
        }
        
        // 校验门店/收银员等参数
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {// 绑定收银员级别支付通知
            bindType = PayNoticeBindWeixin.Type.dealerEmployee.getValue();
            DealerEmployeeVO  dealerEmployeeVO = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(dealerEmployeeOid);
            if (dealerEmployeeVO == null) {
                logger.warn("支付通知绑定的收银员不存在，storeOid：" + storeOid);
                bindResult = new PayNoticeBindResult("cashier_invalid", "收银员信息无效");
                return false;
            }
        } else if (StringUtils.isNotBlank(storeOid)) {
            bindType = PayNoticeBindWeixin.Type.store.getValue();
            StoreVO storeVO = storeService.doJoinTransQueryStoreByOid(storeOid);
            if (storeVO == null) {
                logger.warn("支付通知绑定的门店不存在，storeOid：" + storeOid);
                bindResult = new PayNoticeBindResult("store_invalid", "门店信息无效");
                return false;
            }
        } else {
            logger.warn("授权链接有误或者回调错误，导致参数缺失storeOid和dealerEmployeeOid");
            bindResult = new PayNoticeBindResult("param_miss", "绑定参数（门店/收银员）缺失");
            return false;
        }
        
        if (StringUtils.isBlank(partnerOid)) {
            logger.warn("授权链接有误或者回调错误，导致参数缺失partnerOid");
            bindResult = new PayNoticeBindResult("param_miss", "收银员信息无效");
            return false;
        }

        return true;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public PayNoticeBindResult getBindResult() {
        return bindResult;
    }
    
    public String getBindType() {
        return bindType;
    }

    public PayNoticeBindWeixinVO getPayNoticeBindWeixinVO() {
        return payNoticeBindWeixinVO;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDealerEmployeeService(DealerEmployeeService dealerEmployeeService) {
        this.dealerEmployeeService = dealerEmployeeService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
    
    public void setSysConfigService(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    public void setPayNoticeBindWeixinService(PayNoticeBindWeixinService payNoticeBindWeixinService) {
        this.payNoticeBindWeixinService = payNoticeBindWeixinService;
    }
    
}
