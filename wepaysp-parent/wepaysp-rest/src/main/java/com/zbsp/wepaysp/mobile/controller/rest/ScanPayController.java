package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.main.pay.WeixinRefundDetailsMainService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.base.MobileRequest;
import com.zbsp.wepaysp.mo.pay.v1_0.ScanPayRequest;
import com.zbsp.wepaysp.mo.pay.v1_0.ScanPayResponse;
import com.zbsp.wepaysp.mo.refund.v1_0.ScanRefundRequest;
import com.zbsp.wepaysp.common.constant.EnumDefine;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.mobile.result.PayResult;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

@RestController
@RequestMapping("/pay/v1")
public class ScanPayController extends BaseController {
    
	@Autowired
	private WeixinPayDetailsMainService weixinPayDetailsMainService;
	
	private WeixinRefundDetailsMainService weixinRefundDetailsMainService;
	
	@RequestMapping(value = "scanPay", method = RequestMethod.POST)
	@ResponseBody
    public ScanPayResponse scanPay(@RequestBody ScanPayRequest request) {
		String logPrefix = "处理智慧扫码支付明细请求 - ";
		if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info(logPrefix + "开始");
        logger.debug("request Data is {}", request.toString());
        ScanPayResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new ScanPayResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (!Validator.contains(MobileRequest.AppType.class, request.getAppType())) {
            response = new ScanPayResponse(CommonResult.INVALID_APPTYPE.getCode(), CommonResult.INVALID_APPTYPE.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId()) || StringUtils.isBlank(request.getDealerEmployeeOid()) || StringUtils.isBlank(request.getAuthCode())) {
            response = new ScanPayResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else if (request.getPayMoney() <= 0 ) {// 支付金额，单位：分
        	response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(payMoney)", responseId);
        } else {
            try {
            	// 根据AuthCode 判断微信或者支付宝支付
                // 微信刷卡授权码以13开头，例：130772863047391648
            	// 支付宝授权码以28开头，   例：280409337332958977
                if (request.getAuthCode().startsWith("13")) {// 微信-刷卡支付
                    // 保存交易明细
                    WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
                    payDetailsVO.setPayType(EnumDefine.PayType.WEIXIN_MICROPAY.getValue() + "");
                    payDetailsVO.setDealerEmployeeOid(request.getDealerEmployeeOid());
                    payDetailsVO.setTotalFee(new Long(request.getPayMoney()).intValue());
                    payDetailsVO.setAuthCode(request.getAuthCode());
                    
                    Map<String, Object> resultMap = weixinPayDetailsMainService.createPayAndInvokeWxPay(payDetailsVO, null, null, null);
                    String resCode = MapUtils.getString(resultMap, "resultCode");
                    String resDesc = MapUtils.getString(resultMap, "resultDesc");
                    payDetailsVO = (WeixinPayDetailsVO) MapUtils.getObject(resultMap, "wexinPayDetailsVO");
                    
                    if (!StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), resCode)) {// 支付失败
                        logger.warn(logPrefix + "微信刷卡支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                        response = new ScanPayResponse(PayResult.PAY_FAIL.getCode(), PayResult.PAY_FAIL.getDesc(), responseId);
                    } else {
                        logger.info(logPrefix + "微信刷卡支付成功");
                        response = new ScanPayResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), responseId);
                    }
                    if (payDetailsVO == null) {
                        throw new RuntimeException("wexinPayDetailsVO不能为空");
                    }
                    response.setCollectionMoney(payDetailsVO.getTotalFee());// 总金额实际收款金额
                    response.setOutTradeNo(payDetailsVO.getOutTradeNo());
                    response.setPayType(Integer.valueOf(payDetailsVO.getPayType()));
                    response.setTradeStatus(payDetailsVO.getTradeStatus());
                    response.setTransTime(DateUtil.getDate(payDetailsVO.getTransBeginTime(), SysEnvKey.TIME_PATTERN_YMD_SLASH_HMS_COLON));
                } else if (request.getAuthCode().startsWith("28")) {// 支付宝-当面付-条码支付
                    response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(authCode)", responseId);
                } else {
                    response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(authCode)", responseId);
                }
            } catch (IllegalArgumentException e) {
                response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
            	logger.info(logPrefix + "异常：{}", e.getMessage(), e);
                response = new ScanPayResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
    }
	
	@RequestMapping(value = "refund", method = RequestMethod.POST)
    @ResponseBody
    public ScanPayResponse scanRefund(@RequestBody ScanRefundRequest request) {
        String logPrefix = "处理扫码退款明细请求 - ";
        if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info(logPrefix + "开始");
        logger.debug("request Data is {}", request.toString());
        ScanPayResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new ScanPayResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (!Validator.contains(MobileRequest.AppType.class, request.getAppType())) {
            response = new ScanPayResponse(CommonResult.INVALID_APPTYPE.getCode(), CommonResult.INVALID_APPTYPE.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId()) || StringUtils.isBlank(request.getOutTradeNo())) {
            response = new ScanPayResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                // 根据AuthCode 判断微信或者支付宝支付
                // 微信刷卡授权码以13开头，例：130772863047391648
            } catch (IllegalArgumentException e) {
                response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.info(logPrefix + "异常：{}", e.getMessage(), e);
                response = new ScanPayResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
    }
	
}
