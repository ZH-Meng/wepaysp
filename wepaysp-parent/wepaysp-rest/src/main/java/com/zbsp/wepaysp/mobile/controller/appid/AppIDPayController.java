package com.zbsp.wepaysp.mobile.controller.appid;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.constant.WxEnums.GrantType;
import com.zbsp.wepaysp.common.constant.WxEnums.WxPayResult;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.CreateOrderResult;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.vo.WxCallBackVO;
import com.tencent.WXPay;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenReqData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.tencent.protocol.unified_order_protocol.JSPayReqData;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.DealerVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信公众号支付控制器
 * <pre>
 *      微信浏览器授权后回调；
 *      公众号下单；
 *      微信支付异步结果通知；
 *      Ajax获取后台系统支付结果；
 * <pre>
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/pay/")
public class AppIDPayController extends BaseController {
    
    @Autowired
    private DealerService dealerService;
    
    @Autowired
    private StoreService storeService;
    
    @Autowired    
    private DealerEmployeeService dealerEmployeeService;
    
    @Autowired
    private WeixinPayDetailsMainService weixinPayDetailsMainService;
    
    /**
     * 微信浏览器授权后回调，目前使用公众号支付不需要access_token，只是下单时需要access_token<br>
     * 其他：微信公众平台开发（其中有一个支付接口）会用access_token（有时效性，调用API有限），需要考虑 access_token缓存或刷新方案
     * 
     * @return
     */
    @SuppressWarnings("finally")
    @RequestMapping(value="wxCallBack", method=RequestMethod.GET)
    public ModelAndView wxCallBack(WxCallBackVO callBackVO) {
        String logPrefix = "处理微信扫码公众号下单网页授权回调请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        
        logger.info(logPrefix + "参数检查 - 开始");
        Map<String, Object> partnerMap = null;
        boolean checkFlag = false;
        try {
            // 检查参数
            Map<String, Object> checkResultMap = checkScanCreateOrderCallBackParam(callBackVO);
            
            if (!MapUtils.getBooleanValue(checkResultMap, "result", false)) {
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", MapUtils.getObject(checkResultMap, "errResult"));
            } else {
                // 从内存中获取服务商配置信息
                partnerMap = SysConfig.partnerConfigMap.get(callBackVO.getPartnerOid());
                if (partnerMap == null || partnerMap.isEmpty()) {
                    logger.error(logPrefix + "参数检查 - 失败：{}, partnerOid：{}", "服务商不存在", callBackVO.getPartnerOid());
                    modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc() + "(partner)"));
                } else {
                    checkFlag = true;
                }
            }
            logger.info(logPrefix + "参数检查 - 通过");
        } catch (Exception e) {
            logger.error(logPrefix + "参数检查 - 异常：{}", e.getMessage(), e);
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc()));
        } finally {
            logger.info(logPrefix + "参数检查 - 结束");
            if (!checkFlag) {
                return modelAndView; 
            }
        }
        
        
        logger.info(logPrefix + "获取网页授权access_token 和 openid - 开始");
        try {
            // 通过code换取网页授权access_token 和 openid
            GetAuthAccessTokenReqData authReqData = new GetAuthAccessTokenReqData(GrantType.AUTHORIZATION_CODE.getValue(), 
                MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID), MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET), callBackVO.getCode(), null);
            
            logger.info(logPrefix + "获取网页授权access_token 和 openid，request Data : {}", authReqData.toString());
            GetAuthAccessTokenResData authResult = null;
            
            String jsonResult = WXPay.requestGetAuthAccessTokenService(authReqData, MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
            authResult = JSONUtil.parseObject(jsonResult, GetAuthAccessTokenResData.class);
            logger.info(logPrefix + "获取网页授权access_token 和 openid，response Data : {}", authResult.toString());
            
            // 校验获取access_token
            if (WeixinUtil.checkAuthAccessTokenResult(authResult)) {
                logger.info(logPrefix + "获取网页授权access_token 和 openid - 成功, auth_access_token：{}, expires_in：{} " + ",refresh_token：{}, openid：{}", authResult.getAccess_token(), authResult.getExpires_in(), authResult.getRefresh_token(), authResult.getOpenid());
            	// 设置openid，下单时需要，暂通过request及前台隐藏于传递给下单请求
                callBackVO.setOpenid(authResult.getOpenid());
                // TODO 设置过期时间
               /* 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
                refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
                如果需要定期同步用户的昵称，则需要考虑刷新access_token*/
                
                ModelMap model=new ModelMap();
                model.addAttribute("callBackVO", callBackVO);
                modelAndView = new ModelAndView("appid/wxCallBack", model);
                logger.info(logPrefix + "成功");
            } else {
                logger.warn(logPrefix + "获取网页授权access_token 和 openid - 失败，错误码：" + authResult.getErrcode() + "，错误描述：" + authResult.getErrmsg());
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc()));
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), logPrefix, "获取网页授权access_token 和 openid - 异常：{}"), e.getMessage(), e);
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc()));
        } finally {
            logger.info(logPrefix + "结束");
            return modelAndView;
        }
        //return "appid/wxCallBack";
    }
    
    /**
     * 创建订单->微信下单->更新订单->跳转字符
     * 
     * @return CreateOrderResult下单结果
     */
    @RequestMapping(value="createOrder", method=RequestMethod.POST)
    @ResponseBody
    public CreateOrderResult createOrder(WxCallBackVO callBackVO, String money) {
        String logPrefix = "处理微信公众号下单请求 - ";
        logger.info(logPrefix + "开始");
        CreateOrderResult result = null;
        
        logger.info(logPrefix + "参数检查 - 开始");
        boolean checkFlag = false;
        try {
            if (StringUtils.isBlank(money) || !NumberUtils.isCreatable(money) || !Pattern.matches(SysEnvKey.REGEX_￥_POSITIVE_FLOAT_2BIT, money)) {// 正确金额：例如：0.01/200/201.99
                logger.warn(logPrefix + "参数检查 - 失败：{}", "金额无效，请重新输入，单位为元！");
                result = new CreateOrderResult(H5CommonResult.INVALID_ARGUMENT.getCode(), "金额无效，请重新输入，单位为元！", null, null);
            } else if (callBackVO == null || StringUtils.isBlank(callBackVO.getDealerOid()) || StringUtils.isBlank(callBackVO.getOpenid())) {
                logger.warn(logPrefix + "参数检查 - 失败：{}", "商户Oid和openid都不能为空！");
                result = new CreateOrderResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc(), null, null);
            } else if (StringUtils.isBlank(SysConfig.wxPayNotifyURL)) {
                logger.error("系统配置异常：微信支付统一下单通知URL为空！");
                result = new CreateOrderResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc(), null, null);
            } else {
                logger.info(logPrefix + "参数检查 - 通过");
                checkFlag = true;
            }
        } catch (Exception e) {
            logger.error(logPrefix + "参数检查 - 异常：{}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "参数检查 - 结束");
            if (!checkFlag) {
                return result;
            }
        }

        // 保存交易明细
        WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
        payDetailsVO.setPayType(PayType.WEIXIN_JSAPI.getValue());// 公众号支付
        payDetailsVO.setDealerOid(callBackVO.getDealerOid());
        payDetailsVO.setOpenid(callBackVO.getOpenid());
        payDetailsVO.setStoreOid(callBackVO.getStoreOid());// 一店一码时，不为空
        payDetailsVO.setDealerEmployeeOid(callBackVO.getDealerEmployeeOid());// 一收银员一码时，不为空
        payDetailsVO.setNotifyUrl(SysConfig.wxPayNotifyURL);// 通知地址

        BigDecimal orderMoney = new BigDecimal(money);// 订单金额
        payDetailsVO.setTotalFee(orderMoney.multiply(new BigDecimal(100)).intValue());// 元转化为分
        logger.info(logPrefix + "下单 - 开始");
        try {
            Map<String, Object> resultMap = weixinPayDetailsMainService.createPayAndInvokeWxPay(payDetailsVO, callBackVO.getOpenid(), null, null);
            String resCode = MapUtils.getString(resultMap, "resultCode");
            String resDesc = MapUtils.getString(resultMap, "resultDesc");
            payDetailsVO = (WeixinPayDetailsVO) MapUtils.getObject(resultMap, "wexinPayDetailsVO");
            
            String weixinPayDetailOid = payDetailsVO != null ? payDetailsVO.getIwoid() : "";// 支付订单明细Oid
            JSPayReqData jsPayReqData = (JSPayReqData) MapUtils.getObject(resultMap, "jsPayReqData");// JS支付接口请求参数包

            if (!StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), resCode)) {// 公众号下单失败
                logger.warn(logPrefix  + "下单 - 失败，错误码：{}, 错误描述：{}", resCode, resDesc);
                result = new CreateOrderResult(resCode, resDesc, null, null);
            } else {
                logger.info(logPrefix  + "下单 - 成功");
                result = new CreateOrderResult(H5CommonResult.SUCCESS.getCode(), "下单成功", jsPayReqData, weixinPayDetailOid);
            }
        } catch (InvalidValueException e) {
            logger.warn(logPrefix + "警告：{}", e.getMessage());
            result = new CreateOrderResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc(), null, null);
        } catch (IllegalArgumentException e) {
            logger.warn(logPrefix + "警告：{}", e.getMessage());
            result = new CreateOrderResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc(), null, null);
        } catch (NotExistsException e) {
            logger.warn(logPrefix + "警告：{}", e.getMessage());
            result = new CreateOrderResult(H5CommonResult.SYS_ERROR.getCode(), "下单失败", null, null);
        } catch (Exception e) {
            logger.error(logPrefix + "异常：{}", e.getMessage(), e);
            result = new CreateOrderResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc(), null, null);
        }
        
        logger.info(logPrefix + "结束");
        return result;
    }
    
    
    /**
     * 微信异步通知支付结果
     */
    @RequestMapping(value="wxPayNotify")
    public String wxPayNotify(HttpServletRequest request) {
        String logPrefix = "处理微信异步通知支付结果请求 - ";
        logger.info(logPrefix + "开始");
        StringBuffer xmlStr = new StringBuffer();
        String resultXML = null;
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }
            logger.info("微信异步通知支付结果内容：" + xmlStr.toString());
            
            resultXML = weixinPayDetailsMainService.handleWxPayNotify(xmlStr.toString());
            logger.info("系统处理微信异步通知支付结果的响应内容：" + resultXML);
        } catch (Exception e) {
            logger.error(logPrefix + "异常：{}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "结束");
        }
        return resultXML;
    }
    
    /**
     * H5支付结果返回后检查真实交易状态
     * 
     * @return 
     */
    @RequestMapping(value="jsPayResult", method=RequestMethod.GET)
    public ModelAndView jsPayResult(String payResult, String weixinPayDetailOid) {
        String logPrefix = "微信公众号支付同步获取后台系统的支付结果 - ";
        logger.info(logPrefix + "开始");

        ModelAndView modelAndView = null;
        ModelMap model=new ModelMap();
        try {
            // 根据 weixinPayDetailOid 查询系统真实结果
            Map<String, Object> resultMap = weixinPayDetailsMainService.checkPayResult(payResult, weixinPayDetailOid);
            
            model.addAttribute("tradeStatus", MapUtils.getString(resultMap, "tradeStatus"));
            model.addAttribute("weixinPayDetailsVO", MapUtils.getObject(resultMap, "weixinPayDetailsVO"));
        } catch (Exception e) {
            logger.error(logPrefix + "异常：{}", e.getMessage(), e);
        } finally {
            modelAndView = new ModelAndView("appid/jsPayResult", model);
            logger.info(logPrefix + "结束");
        }
        return modelAndView;
    }
    
    /**
     * 检查微信扫码下单时微信网页授权回调的系统URL中参数是否完整和正确
     * 
     * @return
     */
    private Map<String, Object> checkScanCreateOrderCallBackParam(WxCallBackVO callBack) {
        Map<String, Object> checkResutMap = new HashMap<String, Object>();
        boolean result = false;
        if (callBack == null) {
            logger.error("微信网页授权回调 - 参数检查 - 失败：{}", "callBack is null");
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else if (StringUtils.isBlank(callBack.getCode())) {
            logger.warn("微信网页授权回调 - 参数检查 - 失败：{}", "用户访问公众号，选择禁止授权.");
            // 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc() + "(网页授权失败)"));
        } else if (StringUtils.isBlank(callBack.getPartnerOid()) || StringUtils.isBlank(callBack.getDealerOid())) {// 校验商户等参数
            logger.warn("微信网页授权回调 - 参数检查 - 失败：{}", "参数缺失（partnerOid：" + callBack.getPartnerOid() + ",dealerOid：" + callBack.getDealerOid() + ")");
            checkResutMap.put("errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else {
            // 获取并校验商户信息，回显到页面
            DealerVO accessDealer = dealerService.doJoinTransQueryDealerByOid(callBack.getDealerOid());
            if (accessDealer == null) {
                logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, dealerOid：{}", "访问的商户不存在", callBack.getDealerOid());
                checkResutMap.put("errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()));
            } else {
                //checkResutMap.put("dealerName", accessDealer.getCompany());
                callBack.setDealerName(accessDealer.getCompany());
                result = true;
                
                // 获取门店信息，一商户一码模式屏蔽，门店Oid应为必填，暂不控制
                if (StringUtils.isNotBlank(callBack.getStoreOid())) {
                    StoreVO accessStore = storeService.doJoinTransQueryStoreByOid(callBack.getStoreOid());
                    if (accessStore == null) {
                        logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, storeOid：{}", "访问的门店不存在", callBack.getStoreOid());
                        checkResutMap.put("errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()));
                        result = false;
                    } else {
                        if (StringUtils.isNotBlank(callBack.getDealerEmployeeOid())) {
                            DealerEmployeeVO accessDE = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(callBack.getDealerEmployeeOid());
                            if (accessDE == null) {
                                logger.warn("微信网页授权回调 - 参数检查 - 失败：{}, dealerEmloyeeOid：{}", "访问的商户员工不存在", callBack.getDealerEmployeeOid());
                                checkResutMap.put("errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()));
                                result = false;
                            }
                        }
                    }
                }
            }
            
        }
        checkResutMap.put("result", result);
        return checkResutMap;
    }

}
