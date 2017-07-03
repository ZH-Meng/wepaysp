package com.zbsp.wepaysp.mobile.controller.alipay;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppRequestBuilder;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppAuthDetailsService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.vo.AliPayCallBackVO;
import com.zbsp.wepaysp.vo.alipay.AlipayAppAuthDetailsVO;

/**
 * 支付宝（蚂蚁开放平台）授权回调请求处理控制器，处理商户应用授权和用户信息授权两种回调
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/alipay/auth/")
public class AlipayOpenAuthController
    extends BaseController {

    @Autowired
    private AlipayAppAuthDetailsService alipayAppAuthDetailsService;

    /**
     * 客户端授权（商户应用授权/用户信息授权）后重定向 Get请求
     * 
     * @param callBackVO
     *            回调参数自动封装成 AliPayCallBackVO
     * @return 返回H5授权结果页
     */
    @RequestMapping("callback")
    public ModelAndView appAuthCallBack(AliPayCallBackVO callBackVO) {
        String logPrefix = "处理蚂蚁开放平台商户应用授权后回调请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        ErrResult errResult = null;
        String resultTitle = "支付宝授权结果";
        if (callBackVO == null || StringUtils.isBlank(callBackVO.getApp_id())) {
            logger.warn(logPrefix + "callBackVO=null，无效授权回调请求");
            errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()).setTitleDesc(resultTitle);
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
            return modelAndView;
        }
        
        boolean flag = false;        
        if (StringUtils.isNotBlank(callBackVO.getApp_auth_code())) {
            logger.info(logPrefix + "同意授权，类型为商户应用授权回调，app_id : {}, app_auth_code : {}", callBackVO.getApp_id(), callBackVO.getApp_auth_code());
            if (StringUtils.isBlank(callBackVO.getDealerOid())) {
                logger.warn(logPrefix + "商户同意授权，但是回调地址参数缺失：dealerOid");
                errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc() + ("dealerOid")).setTitleDesc(resultTitle);
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView;
            }

            // 使用app_auth_code换取app_auth_token
            AlipayAppAuthDetailsVO appAuthDetailsVO = AliPayUtil.getOrRefreshAppAuthToken(
                new AlipayOpenAuthTokenAppRequestBuilder().setCode(callBackVO.getApp_auth_code()).setGrantType(Constants.GRANT_TYPE_AUTHORIZATION_CODE));
            if (appAuthDetailsVO != null) {
                logger.info("(appid={})换取app_auth_token - 成功", callBackVO.getApp_id());

                appAuthDetailsVO.setDealerOid(callBackVO.getDealerOid());// 商户
                appAuthDetailsVO.setAppId(callBackVO.getApp_id());// 应用
                
                try {
                    logger.info("保存商户授权应用令牌信息 - 开始");
                    appAuthDetailsVO = alipayAppAuthDetailsService.doTransCreateAppAuthDetail(appAuthDetailsVO);
                    modelAndView = new ModelAndView("alipay/authAppCallBack", "appAuthDetailsVO", appAuthDetailsVO);
                    flag = true;
                    logger.info("保存商户授权应用令牌信息 - 成功");
                } catch (Exception e) {
                    logger.error("保存商户授权应用令牌信息 - 异常 : {}", e.getMessage(), e);
                } finally {
                    logger.info("保存商户授权应用令牌信息 - 结束");
                }
            } else {
                logger.error(AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "(appid={})换取app_auth_token - 失败", callBackVO.getApp_id());
            }
        } else if (StringUtils.isNotBlank(callBackVO.getAuth_code())) {
            logger.info(logPrefix + "同意授权，类型为用户信息授权回调，app_id : {}, auth_code : {}", callBackVO.getApp_id(), callBackVO.getAuth_code());
            // TODO 暂不实现，如果有服务窗应用则可以考虑实现支付宝用户关注功能，或者有别的授权需要。
            errResult = new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), "暂不支持用户关注/授权回调").setTitleDesc(resultTitle);
        } else {
            logger.warn(logPrefix + "app_auth_code 和 auth_code 都为null，用户/商户未同意授权，无法确定授权回调类型");
            errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc() + ("app_auth_code/auth_code")).setTitleDesc(resultTitle);
        }

        logger.info(logPrefix + "结束");
        if (!flag) {
            if (errResult == null) {
                errResult = new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc()).setTitleDesc(resultTitle);
            }
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
        }
        return modelAndView;
    }

}
