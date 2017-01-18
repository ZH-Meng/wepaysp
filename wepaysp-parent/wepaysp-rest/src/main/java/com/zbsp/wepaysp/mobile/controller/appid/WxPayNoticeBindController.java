package com.zbsp.wepaysp.mobile.controller.appid;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tencent.WXPay;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenReqData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoReqData;
import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoResData;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.WxEnums.GrantType;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.result.PayNoticeBindResult;
import com.zbsp.wepaysp.mobile.model.vo.WxCallBackVO;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

/**
 * 支付通知绑定（门店/收银员）控制器<br> 
 * 后台管理（不能登录拦截）迁移至rest
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/pay/notice")
public class WxPayNoticeBindController extends BaseController {
    
    @Autowired
    private StoreService storeService;
    
    @Autowired
    private DealerEmployeeService dealerEmployeeService;
    
    @Autowired
    private PayNoticeBindWeixinService  payNoticeBindWeixinService;

    /**
     * 微信支付通知绑定<br>
     * 微信扫码后，点击确认登录（同意授权）微信回调地址
     * 
     * @return
     */
    @RequestMapping(value="bindWxIDCallBack", method=RequestMethod.GET)
    public ModelAndView bindWxIDCallBack(WxCallBackVO callBackVO) {
        String logPrefix = "处理微信扫码绑定支付通知回调请求 - ";
        logger.info(logPrefix + "开始");
        
        ModelAndView modelAndView = null;
        ErrResult errResult = new ErrResult();
        String bindType = "";
        
        logger.info(logPrefix + "参数检查 - 开始");
        Map<String, Object> partnerMap = null;
        boolean flag = false;
        try {
            // 检查参数
            Map<String, Object> checkResultMap = checkScanBindCallBackParam(callBackVO);
            
            if (!MapUtils.getBooleanValue(checkResultMap, "result", false)) {
                errResult = (ErrResult) MapUtils.getObject(checkResultMap, "errResult");
            } else {
                // 从内存中获取服务商配置信息
                partnerMap = SysConfig.partnerConfigMap.get(callBackVO.getPartnerOid());
                if (partnerMap == null || partnerMap.isEmpty()) {
                    logger.error(logPrefix + "参数检查 - 失败：{}, partnerOid：{}", "服务商不存在", callBackVO.getPartnerOid());
                    errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc() + "(partner)");
                } else {
                    flag  = true;
                    bindType = MapUtils.getString(checkResultMap, "bindType");
                }
            }
            logger.info(logPrefix + "参数检查 - 通过");
        } catch (Exception e) {
            logger.error(logPrefix + "参数检查 - 异常：{}", e.getMessage(), e);
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "参数检查 - 结束");
            if (!flag) {
                errResult.setTitleDesc("微信支付通知绑定");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView; 
            }
        }
        
        logger.info(logPrefix + "获取网页授权access_token 和 openid - 开始");
        GetAuthAccessTokenResData authResult = null;
        flag = false;
        try {
            // 通过code换取网页授权access_token 和 openid
            GetAuthAccessTokenReqData authReqData = new GetAuthAccessTokenReqData(GrantType.AUTHORIZATION_CODE.getValue(), 
                MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID), MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET), callBackVO.getCode(), null);
            logger.info(logPrefix + "获取网页授权access_token 和 openid，request Data : {}", authReqData.toString());
            
            
            String jsonResult = WXPay.requestGetAuthAccessTokenService(authReqData, 
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
            authResult = JSONUtil.parseObject(jsonResult, GetAuthAccessTokenResData.class);
            logger.info(logPrefix + "获取网页授权access_token 和 openid，response Data : {}", authResult.toString());
            
            // 校验获取access_token
            if (WeixinUtil.checkAuthAccessTokenResult(authResult)) {
                logger.info(logPrefix + "获取网页授权access_token 和 openid - 成功, auth_access_token：{}, expires_in：{} " + "auth_access_token：{}, openid：{}", authResult.getAccess_token(), authResult.getExpires_in(), authResult.getOpenid());
                flag = true;
                // TODO 设置过期时间
                /*由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
                refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
                如果需要定期同步用户的昵称，则需要考虑刷新access_token*/
            } else {
                logger.warn(logPrefix + "获取网页授权access_token 和 openid - 失败，错误码：" + authResult.getErrcode() + "，错误描述：" + authResult.getErrmsg());
                errResult = new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc());
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), logPrefix, "获取网页授权Access_token失败", "，异常信息：{}"), e.getMessage(), e);
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "获取网页授权access_token 和 openid - 结束");
            if (!flag) {
                errResult.setTitleDesc("微信支付通知绑定");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView; 
            }
        }
        
        logger.info(logPrefix + "拉取用户信息 - 开始");// 拉取用户信息(scope为 snsapi_userinfo)
        GetUserinfoResData userinfoResult = null;
        flag = false;
        try {
            GetUserinfoReqData userinfoReqData = new GetUserinfoReqData(authResult.getOpenid(), authResult.getAccess_token());
            logger.info(logPrefix + "拉取用户信息，request Data : {}", userinfoReqData.toString());
            
            String jsonResult = WXPay.requestGetUserinfoService(userinfoReqData,
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
            userinfoResult = JSONUtil.parseObject(jsonResult, GetUserinfoResData.class);
            logger.info(logPrefix + "拉取用户信息，response Data : {}", userinfoResult.toString());
            
            // 校验获取用户信息结果
            if (WeixinUtil.checkUserinfoResult(userinfoResult)) {
                if (!authResult.getOpenid().equals(userinfoResult.getOpenid())) {
                    logger.warn(logPrefix + "拉取用户信息 - 网页授权获取openid和获取用户信息响应openid不一致，抛弃此结果");
                    errResult = new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc());
                } else {
                    flag = true;
                }
            } else {
                logger.warn(logPrefix + "拉取用户信息 - 失败，错误码：" + userinfoResult.getErrcode() + "，错误描述：" + userinfoResult.getErrmsg());
                errResult = new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc());
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), logPrefix, "拉取用户信息 - 失败，异常信息：" + e.getMessage()), e);
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "拉取用户信息 - 结束");
            if (!flag) {
                errResult.setTitleDesc("微信支付通知绑定");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView; 
            }
        }
        
        // 绑定，前台页面根据绑定结果展示相应的门店/收银员信息
        logger.info(logPrefix + "微信支付通知绑定，微信账户绑定门店/收银员信息 - 开始");
        PayNoticeBindResult bindResult = null;
        try {
            String toRelateOid = PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindType) ? callBackVO.getDealerEmployeeOid() : callBackVO.getStoreOid();
            PayNoticeBindWeixinVO payNoticeBindWeixinVO = payNoticeBindWeixinService.doTransAddPayNoticeBindWeixin(bindType, toRelateOid, userinfoResult);
            bindResult = new PayNoticeBindResult("success", "绑定成功");//FIXME

            ModelMap model = new ModelMap();
            model.put("bindResult", bindResult);
            model.put("bindType", bindType);
            model.put("payNoticeBindWeixinVO", payNoticeBindWeixinVO);
            modelAndView = new ModelAndView("appid/payNoticeBindResult", model);
            logger.info(logPrefix + "微信支付通知绑定，微信账户绑定门店/收银员信息 - 成功");
        } catch (AlreadyExistsException e) {
            logger.warn(logPrefix + "微信支付通知绑定，微信账户绑定门店/收银员信息 - {}", e.getMessage());
            bindResult = new PayNoticeBindResult("bound", "已绑定过并且当前有效");//FIXME
            // TODO 跳转门店/收银员公众号展示资金统计
        } catch (Exception e) {
            logger.error(logPrefix + "微信支付通知绑定，微信账户绑定门店/收银员信息 - 异常：{}", e.getMessage(), e);
            bindResult = new PayNoticeBindResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "微信支付通知绑定，微信账户绑定门店/收银员信息 - 结束");
            if (modelAndView == null) {
                modelAndView = new ModelAndView("appid/payNoticeBindResult", "bindResult", bindResult);
            }
        }
        
        logger.info(logPrefix + "结束");
        return modelAndView;
    }
    
    /**
     * 检查扫码绑定支付通知时微信网页授权回调的系统URL中参数是否完整和正确
     * 
     * @return
     */
    private Map<String, Object> checkScanBindCallBackParam(WxCallBackVO callBack) {
        Map<String, Object> checkResutMap = new HashMap<String, Object>();
        boolean result = false;
        if (callBack == null) {
            logger.error("微信网页授权回调 - 参数检查 - 失败：{}", "callBack is null");
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else if (StringUtils.isBlank(callBack.getCode())) {
            logger.warn("微信网页授权回调 - 参数检查 - 失败：{}", "用户访问公众号，选择禁止授权.");
            // 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc() + "(网页授权失败)"));
        } else if (StringUtils.isBlank(callBack.getPartnerOid())) {
            logger.warn("微信网页授权回调 - 参数检查 - 失败：{}", "参数缺失(partnerOid)");
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else {
            // 校验门店/收银员等参数
            if (StringUtils.isNotBlank(callBack.getDealerEmployeeOid())) {// 绑定收银员级别支付通知
                checkResutMap.put("bindType", PayNoticeBindWeixin.Type.dealerEmployee.getValue());
                DealerEmployeeVO  dealerEmployeeVO = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(callBack.getDealerEmployeeOid());
                if (dealerEmployeeVO == null) {
                    logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, dealerEmployeeOid：{}", "访问的收银员不存在", callBack.getDealerEmployeeOid());
                    checkResutMap.put("errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()+"(cashier)"));
                } else {
                    result = true;
                }
            } else if (StringUtils.isNotBlank(callBack.getStoreOid())) {
                checkResutMap.put("bindType", PayNoticeBindWeixin.Type.store.getValue());
                StoreVO storeVO = storeService.doJoinTransQueryStoreByOid(callBack.getStoreOid());
                if (storeVO == null) {
                    logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, storeOid：{}", "访问的门店不存在", callBack.getStoreOid());
                    checkResutMap.put("errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()+"(store)"));
                } else {
                    result = true;
                }
            } else {
                logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, (storeOid：{}, dealerEmployeeOid：{})", "参数缺失", callBack.getStoreOid(), callBack.getDealerEmployeeOid());
                checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc())+"store/cashier");
            }
            
        }
        
        checkResutMap.put("result", result);
        return checkResutMap;
    }
    
}
