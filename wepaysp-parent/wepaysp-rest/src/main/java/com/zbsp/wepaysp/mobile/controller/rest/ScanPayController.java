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

import com.zbsp.wepaysp.api.service.main.pay.AliPayDetailsMainService;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.base.MobileRequest;
import com.zbsp.wepaysp.mo.pay.v1_0.ScanPayRequest;
import com.zbsp.wepaysp.mo.pay.v1_0.ScanPayResponse;
import com.zbsp.wepaysp.mo.refund.v1_0.ScanRefundRequest;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.SysEnums;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.WxEnums.WxPayResult;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.mobile.result.PayResult;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 被扫支付（微信刷卡支付/支付宝当面付-条码支付）、退款控制器
 * 
 * @author 孟郑宏
 */
@RestController
@RequestMapping("/pay/v1")
public class ScanPayController extends BaseController {
    
	@Autowired
	private WeixinPayDetailsMainService weixinPayDetailsMainService;
	
	@Autowired
	private AliPayDetailsMainService aliPayDetailsMainService;
	
	@RequestMapping(value = "scanPay", method = RequestMethod.POST)
	@ResponseBody
    public ScanPayResponse scanPay(@RequestBody ScanPayRequest request) {
		String logPrefix = "处理智慧扫码支付明细请求 - ";
		if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info(logPrefix + "开始");
        logger.info("request Data is {}", request.toString());
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
                    logger.info("检测支付方式：微信-刷卡支付, 授权码：{}", request.getAuthCode());
                    // 保存交易明细
                    WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
                    payDetailsVO.setPayType(SysEnums.PayType.WEIXIN_MICROPAY.getValue() + "");
                    payDetailsVO.setDealerEmployeeOid(request.getDealerEmployeeOid());
                    payDetailsVO.setTotalFee(new Long(request.getPayMoney()).intValue());
                    payDetailsVO.setAuthCode(request.getAuthCode());
                    
                    // 刷卡支付
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
                    // 收银客户端直接展示支付结果，所以支付成功失败都判断非空
                    if (payDetailsVO == null) {
                        throw new RuntimeException("wexinPayDetailsVO不能为空");
                    }
                    response.setCollectionMoney(payDetailsVO.getTotalFee());// 总金额实际收款金额
                    response.setOutTradeNo(payDetailsVO.getOutTradeNo());
                    response.setPayType(Integer.valueOf(payDetailsVO.getPayType()));
                    response.setTradeStatus(payDetailsVO.getTradeStatus());
                    response.setTransTime(DateUtil.getDate(payDetailsVO.getTransBeginTime(), SysEnvKey.TIME_PATTERN_YMD_SLASH_HMS_COLON));
                    
                } else if (request.getAuthCode().startsWith("28")) {// 支付宝-当面付-条码支付
                    logger.info("检测支付方式：支付宝-当面付-条码支付, 授权码：{}", request.getAuthCode());
                    response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(authCode)", responseId);
                    AliPayDetailsVO payDetailsVO = new AliPayDetailsVO();
                    
                    payDetailsVO.setPayType(SysEnums.PayType.ALI_FACE_BAR.getValue() + "");
                    payDetailsVO.setDealerEmployeeOid(request.getDealerEmployeeOid());
                    payDetailsVO.setTotalAmount(new Long(request.getPayMoney()).intValue());
                    payDetailsVO.setAuthCode(request.getAuthCode());
                    
                    // 当面付-条码支付
                    Map<String, Object> resultMap = aliPayDetailsMainService.face2FaceBarPay(payDetailsVO);
                    String resCode = MapUtils.getString(resultMap, "resultCode");
                    String resDesc = MapUtils.getString(resultMap, "resultDesc");
                    payDetailsVO = (AliPayDetailsVO) MapUtils.getObject(resultMap, "aliPayDetailsVO");
                    
                    if (!StringUtils.equalsIgnoreCase(AliPayResult.SUCCESS.getCode(), resCode)) {// 支付失败
                        logger.warn(logPrefix + "支付宝条码支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                        response = new ScanPayResponse(PayResult.PAY_FAIL.getCode(), PayResult.PAY_FAIL.getDesc(), responseId);
                    } else {
                        logger.info(logPrefix + "支付宝条码支付成功");
                        response = new ScanPayResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), responseId);
                    }
                    
                    // FIXME 收银客户端直接展示支付结果，所以支付成功失败都判断非空
                    if (payDetailsVO == null) {
                        throw new RuntimeException("aliPayDetailsVO不能为空");
                    }
                    
                    response.setCollectionMoney(payDetailsVO.getTotalAmount());
                    response.setOutTradeNo(payDetailsVO.getOutTradeNo());
                    response.setPayType(Integer.valueOf(payDetailsVO.getPayType()));
                    response.setTradeStatus(payDetailsVO.getTradeStatus());
                    response.setTransTime(DateUtil.getDate(payDetailsVO.getTransBeginTime(), SysEnvKey.TIME_PATTERN_YMD_SLASH_HMS_COLON));
                } else {
                    logger.warn(logPrefix + "警告：{}, 授权码：{}", "发现不能识别的支付请求", request.getAuthCode());
                    response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(authCode)", responseId);
                }
            } catch (IllegalArgumentException e) {
                logger.warn(logPrefix + "警告：{}", e.getMessage());
                response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
            	logger.error(logPrefix + "异常：{}", e.getMessage(), e);
                response = new ScanPayResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.info("response Data is {}", response.toString());
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
        logger.info("request Data is {}", request.toString());
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
                
            } catch (IllegalArgumentException e) {
                logger.warn(logPrefix + "警告：{}", e.getMessage());
                response = new ScanPayResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.error(logPrefix + "异常：{}", e.getMessage(), e);
                response = new ScanPayResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.info("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
    }
	
}
