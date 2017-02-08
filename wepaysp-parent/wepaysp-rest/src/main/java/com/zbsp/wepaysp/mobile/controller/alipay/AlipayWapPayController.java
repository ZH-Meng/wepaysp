package com.zbsp.wepaysp.mobile.controller.alipay;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.pay.AliPayDetailsMainService;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.vo.AlipayWapPayVO;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.DealerVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/**
 * 支付宝（蚂蚁开放平台）手机网站支付控制器
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/alipay/wappay/")
public class AlipayWapPayController
    extends BaseController {

    @Autowired
    private AliPayDetailsMainService aliPayDetailsMainService;
    
    @Autowired
    private DealerService dealerService;
    
    @Autowired
    private StoreService storeService;
    
    @Autowired    
    private DealerEmployeeService dealerEmployeeService;
    
    /**
     * 下单入口
     */
    @RequestMapping("index")
    public ModelAndView index(AlipayWapPayVO indexVO) {
        String logPrefix = "访问支付宝手机网站支付下单入口页面 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        ErrResult errResult = null;
        
        // 校验参数
        boolean checkFlag = false;
        try {
            if (indexVO == null || StringUtils.isBlank(indexVO.getDealerOid()) || StringUtils.isBlank(indexVO.getStoreOid())) {
                logger.warn(logPrefix + "参数检查 - 失败：{}", "商户Oid和门店Oid都不能为空！");
                errResult = new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc());
            } else {
                // 获取并校验商户信息，回显到页面
                DealerVO accessDealer = dealerService.doJoinTransQueryDealerByOid(indexVO.getDealerOid());
                if (accessDealer == null) {
                    logger.warn(logPrefix +"参数检查 - 失败：{}, dealerOid：{}", "访问的商户不存在", indexVO.getDealerOid());
                    errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc());
                } else {
                    //checkResutMap.put("dealerName", accessDealer.getCompany());
                    indexVO.setDealerName(accessDealer.getCompany());
                    checkFlag = true;
                    
                    // 获取门店信息，一商户一码模式屏蔽，门店Oid应为必填，暂不控制
                    if (StringUtils.isNotBlank(indexVO.getStoreOid())) {
                        StoreVO accessStore = storeService.doJoinTransQueryStoreByOid(indexVO.getStoreOid());
                        if (accessStore == null) {
                            logger.warn(logPrefix +"参数检查 - 失败：{}, storeOid：{}", "访问的门店不存在", indexVO.getStoreOid());
                            errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc());
                            checkFlag = false;
                        } else {
                            indexVO.setStoreName(accessStore.getStoreName());
                            if (StringUtils.isNotBlank(indexVO.getDealerEmployeeOid())) {
                                DealerEmployeeVO accessDE = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(indexVO.getDealerEmployeeOid());
                                if (accessDE == null) {
                                    logger.warn(logPrefix +"参数检查 - 失败：{}, dealerEmloyeeOid：{}", "访问的商户员工不存在", indexVO.getDealerEmployeeOid());
                                    errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc());
                                    checkFlag = false;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(logPrefix + "参数检查 - 异常：{}", e.getMessage(), e);
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "参数检查 - 结束");
            if (!checkFlag) {
                errResult.setTitleDesc("下单失败");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView;
            }
        }
        
        ModelMap model=new ModelMap();
        model.addAttribute("indexVO", indexVO);
        
        modelAndView = new ModelAndView("alipay/wapPayIndex", model);
        logger.info(logPrefix + "结束");
        return modelAndView;
    }
    
    /**
     * 下单
     */
    @RequestMapping("createOrder")
    public ModelAndView createOrder(AlipayWapPayVO createOrderVO) {
        String logPrefix = "处理支付宝手机网站支付下单请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        ErrResult errResult = null;
        
        logger.info(logPrefix + "参数检查 - 开始");
        boolean checkFlag = false;
        try {
            if (createOrderVO == null || StringUtils.isBlank(createOrderVO.getDealerOid()) || StringUtils.isBlank(createOrderVO.getStoreOid())) {
                logger.warn(logPrefix + "参数检查 - 失败：{}", "商户Oid和门店Oid都不能为空！");
                errResult = new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc());
            } else if (StringUtils.isBlank(createOrderVO.getMoney())) {
                logger.warn(logPrefix + "参数检查 - 失败：{}", "金额不能为空！");
                errResult = new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc() + "金额");
            } else if (!NumberUtils.isCreatable(createOrderVO.getMoney()) || !Pattern.matches(SysEnvKey.REGEX_￥_POSITIVE_FLOAT_2BIT, createOrderVO.getMoney())) {// 正确金额：例如：0.01/200/201.99
                logger.warn(logPrefix + "参数检查 - 失败：{}", "金额无效" + createOrderVO.getMoney());
                errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc() + "金额");
            } else if (StringUtils.isBlank(SysConfig.alipayWapPayNotifyURL) || StringUtils.isBlank(SysConfig.alipayWapPayReturnURL)) {
                logger.error("系统配置异常：alipayWapPayNotifyURL/alipayWapPayReturnURL不能为空！");
                errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
            } else {
                logger.info(logPrefix + "参数检查 - 通过");
                checkFlag = true;
            }
        } catch (Exception e) {
            logger.error(logPrefix + "参数检查 - 异常：{}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "参数检查 - 结束");
            if (!checkFlag) {
                errResult.setTitleDesc("下单失败");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
                return modelAndView;
            }
        }
        
        AliPayDetailsVO payDetailsVO = new AliPayDetailsVO();
        payDetailsVO.setPayType(PayType.ALI_H5.getValue());// 手机网站支付
        payDetailsVO.setDealerOid(createOrderVO.getDealerOid());
        payDetailsVO.setStoreOid(createOrderVO.getStoreOid());// 一店一码时，不为空
        payDetailsVO.setDealerEmployeeOid(createOrderVO.getDealerEmployeeOid());// 一收银员一码时，不为空

        BigDecimal orderMoney = new BigDecimal(createOrderVO.getMoney());// 订单金额
        payDetailsVO.setTotalAmount(orderMoney.multiply(new BigDecimal(100)).intValue());// 元转化为分
        
        logger.info(logPrefix + "下单 - 开始");
        boolean flag = false;
        try {
            Map<String, Object> resultMap = aliPayDetailsMainService.wapPayCreateOrder(payDetailsVO);
            String resCode = MapUtils.getString(resultMap, "resultCode");
            String resDesc = MapUtils.getString(resultMap, "resultDesc");
            payDetailsVO = (AliPayDetailsVO) MapUtils.getObject(resultMap, "aliPayDetailsVO");
            String wapPayRespBody = MapUtils.getString(resultMap, "wapPayRespBody");// 手机网站支付响应body

            if (!StringUtils.equalsIgnoreCase(AliPayResult.SUCCESS.getCode(), resCode)) {// 手机网站支付下单失败
                logger.warn(logPrefix  + "下单 - 失败，错误码：{}, 错误描述：{}", resCode, resDesc);
            } else {
                logger.info(logPrefix  + "下单 - 成功，跳转支付宝收银台页面");
                modelAndView = new ModelAndView("alipay/wapJumpAlipayCashier", "wapPayRespBody", wapPayRespBody);
                flag = true;
            }
        } catch (InvalidValueException e) {
            logger.warn(logPrefix + "下单 - 警告：{}", e.getMessage());
            errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc());
        } catch (IllegalArgumentException e) {
            logger.warn(logPrefix + "下单 - 警告：{}", e.getMessage());
            errResult = new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc());
        } catch (NotExistsException e) {
            logger.warn(logPrefix + "下单 - 警告：{}", e.getMessage());
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), "下单失败");
        } catch (Exception e) {
            logger.error(logPrefix + "下单 - 异常：{}", e.getMessage(), e);
            errResult = new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc());
        } finally {
            logger.info(logPrefix + "下单 -结束");
        }
        
        logger.info(logPrefix + "结束");
        
        if (!flag) {
            errResult.setTitleDesc("下单失败");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", errResult);
        }
        
        return modelAndView;
    }
    
    /**
     * 下单支付后前台回跳
     * 由于前台回跳的不可靠性，前台回跳只能作为商户支付结果页的入口，最终支付结果必须以异步通知或查询接口返回为准，不能依赖前台回跳。
     * h5ReturnVO 包含业务参数：out_trade_no、trade_no、total_amount、seller_id，公共参数暂不处理
     */
    @RequestMapping("h5Return")
    public ModelAndView h5Return(AlipayWapPayVO h5ReturnVO) {
        String logPrefix = "处理支付宝手机网站支付完成后前台回跳请求 - ";
        logger.info(logPrefix + "开始");
        logger.info("前台回跳请求参数：{}", h5ReturnVO == null ? null : JSONUtil.toJSONString(h5ReturnVO, true));
         
        // 根据参数查询交易状态
        ModelAndView modelAndView;
        try {
            Map<String, Object> resultMap = aliPayDetailsMainService.h5ReturnQueryPayResult(h5ReturnVO.getOut_trade_no(), h5ReturnVO.getTrade_no(), h5ReturnVO.getTotal_amount(), h5ReturnVO.getSeller_id());

            // 返回交易状态、交易信息
            ModelMap model = new ModelMap("tradeStatus", resultMap.get("tradeStatus"));
            model.put("aliPayDetailsVO", resultMap.get("aliPayDetailsVO"));
            
            modelAndView = new ModelAndView("alipay/wapPayResult", model);
        } catch (Exception e) {
            logger.error(logPrefix + "异常 : {}", e.getMessage(), e);
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.SYS_ERROR.getCode(), H5CommonResult.SYS_ERROR.getDesc()).setTitleDesc("支付结果未知"));
        }
        
        logger.info(logPrefix + "结束");
        return modelAndView;
    }

    /**
     * 异步通知（包含支付成功或者交易已经成功并且已经超过可退款期限），处理成功，返回success
     * @param createOrderVO
     * @return
     */
    @RequestMapping("asynNotify")
    @ResponseBody
    public String asynNotify(HttpServletRequest httpRequest) {
        String logPrefix = "处理支付宝手机网站支付异步通知请求 - ";
        logger.info(logPrefix + "开始");
        
        @SuppressWarnings("rawtypes")
        Map paramMap = httpRequest.getParameterMap();
        // 获取所有请求参数
        logger.info("异步通知请求参数：{}", JSONUtil.toJSONString(paramMap, true));
        //logger.info("异步通知请求参数：{}", asynNotifyVO == null ? null : JSONUtil.toJSONString(asynNotifyVO, true));
        //@SuppressWarnings("unchecked") paramMap = BeanMap.create(asynNotifyVO);
        
        String result;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = aliPayDetailsMainService.handleAsynNotify(paramMap);
            result = MapUtils.getString(resultMap, "result");
            logger.info(logPrefix + "处理结果为({})", result);
        } catch (Exception e) {
            logger.error(logPrefix + "异常 : {}", e.getMessage(), e);
            result = "sys_error";
        }
        
        logger.info(logPrefix + "结束");
        return result;// 返回非 success 也无意义，支付宝有重发机制
    }

}
