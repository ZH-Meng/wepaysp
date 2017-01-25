package com.zbsp.wepaysp.mobile.controller.alipay;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppRequestBuilder;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.vo.AliPayCallBackVO;

/**
 * 支付宝（蚂蚁开放平台）授权回调请求处理控制器，处理商户应用授权和用户信息授权两种回调
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/alipay/")
public class AlipayOpenAuthController extends BaseController {
    
    @Autowired
    private DealerService dealerService;
    
    /**
     * 客户端授权（商户应用授权/用户信息授权）后重定向 Get请求
     * 
     * @param callBackVO 回调参数自动封装成 AliPayCallBackVO
     * @return 返回H5授权结果页
     */
    @RequestMapping("auth/callback")
    /*public String appAuthCallBack(@RequestParam(value = "app_id", required = true) String appId, 
    @RequestParam("app_auth_code") String appAuthCode, @RequestParam("auth_code") String authCode) {*/
    public String appAuthCallBack(AliPayCallBackVO callBackVO) {
        String logPrefix = "处理蚂蚁开放平台商户应用授权后回调请求 - ";
        logger.info(logPrefix + "开始");
        if (callBackVO == null || StringUtils.isBlank(callBackVO.getApp_id())) {
            logger.warn(logPrefix + "callBackVO=null，无效授权回调请求");
            return "";
        }
        if (StringUtils.isNotBlank(callBackVO.getApp_auth_code())) {
            logger.info(logPrefix + "同意授权，类型为商户应用授权回调，app_id : {}, app_auth_code : {}", callBackVO.getApp_id(), callBackVO.getApp_auth_code());
            
            //  使用app_auth_code换取app_auth_token
            AlipayOpenAuthTokenAppResponse authResponse = AliPayUtil.authTokenApp(
                new AlipayOpenAuthTokenAppRequestBuilder().setCode(callBackVO.getApp_auth_code()).setGrantType(Constants.GRANT_TYPE_AUTHORIZATION_CODE));
            if (authResponse != null && Constants.SUCCESS.equals(authResponse.getCode())) {
                logger.info("换取app_auth_token成功，response : {}", JSONUtil.toJSONString(authResponse, true));
                
                // 目前更新 Dealer信息中的 app_auth_token，auth_app_id（授权商户的AppId（如果有服务窗，则为服务窗的AppId）），
                // FIXME 因为令牌有有效期，且可能一个商户多个app_id，商户也可随时取消授权，可以考虑关联表维护商户和支付宝应用和授权令牌关联记录来控制是否可以代商户发起开发平台相关接口请求
                
                //dealerService.doTransUpdateAlipayAppAuthInfo();
            } else {
                logger.error(AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "换取app_auth_token失败");
            }
            
        } else if (StringUtils.isNotBlank(callBackVO.getAuth_code())) {
            logger.info(logPrefix + "同意授权，类型为用户信息授权回调，app_id : {}, auth_code : {}", callBackVO.getApp_id(), callBackVO.getAuth_code());
            // TODO 暂不实现，如果有服务窗应用则可以考虑实现支付宝用户关注功能，或者有别的授权需要。
        } else {
            logger.warn(logPrefix + "app_auth_code 和 auth_code 都为null，用户/商户未同意授权，无法确定授权回调类型");
            return "";
        }
        
        logger.info(logPrefix + "结束");
        return "";
    }
    
}
