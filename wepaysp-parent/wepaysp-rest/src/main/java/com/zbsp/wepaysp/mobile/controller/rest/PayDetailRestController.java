package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.pay.PayDetailsService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.paydetail.v1_0.QueryPayDetailRequest;
import com.zbsp.wepaysp.mo.paydetail.v1_0.QueryPayDetailResponse;
import com.zbsp.wepaysp.mo.paydetailprint.v1_0.QueryPrintPayDetailRequest;
import com.zbsp.wepaysp.mo.paydetailprint.v1_0.QueryPrintPayDetailResponse;
import com.zbsp.wepaysp.common.constant.EnumDefine;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;

@RestController
@RequestMapping("/paydetail/v1")
public class PayDetailRestController extends BaseController {
    
    private final static int PAGE_SIZE = 10;
    
	@Autowired
	private PayDetailsService payDetailsService; 
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
    public QueryPayDetailResponse query(@RequestBody QueryPayDetailRequest request) {
		String logPrefix = "处理查询支付明细请求 - ";
		if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info(logPrefix + "开始");
        logger.debug("request Data is {}", request.toString());
        QueryPayDetailResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new QueryPayDetailResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId()) || StringUtils.isBlank(request.getDealerEmployeeOid())) {
            response = new QueryPayDetailResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else if (!Validator.contains(QueryPayDetailRequest.QueryType.class, request.getQueryType())) {
        	response = new QueryPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(queryType)", responseId);
        } else if (request.getQueryType() == QueryPayDetailRequest.QueryType.bill.getValue() 
            && (StringUtils.isBlank(request.getTradeStatus()) || StringUtils.isBlank(request.getPayType()) || StringUtils.isBlank(request.getBeginTime()) || StringUtils.isBlank(request.getEndTime()))) {
        	response = new QueryPayDetailResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("outTradeNo", request.getOutTradeNo());
                paramMap.put("transactionId", request.getTransactionId());
                if (request.getQueryType() == QueryPayDetailRequest.QueryType.bill.getValue()) {
                	paramMap.put("beginTime", DateUtil.getDate(request.getBeginTime(), "yyyy-MM-dd HH:mm:ss"));
                	paramMap.put("endTime", DateUtil.getDate(request.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
                	paramMap.put("payType", request.getPayType());
                	paramMap.put("tradeStatus", request.getTradeStatus());
                	paramMap.put("totalFlag", true);
                } else {
                	// 当天
                	Date today = new Date();
                	paramMap.put("beginTime", TimeUtil.getDayStart(today));
                	paramMap.put("endTime", TimeUtil.getDayEnd(today));
                	paramMap.put("totalFlag", false);
                }
                
                int payNum = request.getPageNum();
                int paySize = PAGE_SIZE;
                if (StringUtils.isNotBlank(request.getPageSize())) {
                    paySize = Integer.valueOf(request.getPageSize());
                }
                response = payDetailsService.doJoinTransQueryPayDetails(request.getDealerEmployeeOid(), paramMap, (payNum - 1) * paySize, paySize);
                
                logger.info(logPrefix + "成功");
            } catch (IllegalArgumentException e) {
                response = new QueryPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.error(logPrefix + "异常：{}", e.getMessage(), e);
                response = new QueryPayDetailResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
    }
	
	@RequestMapping(value = "print", method = RequestMethod.POST)
	@ResponseBody
    public QueryPrintPayDetailResponse query(@RequestBody QueryPrintPayDetailRequest request) {
		String logPrefix = "处理查询打印支付明细请求 - ";
		if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info(logPrefix + "开始");
        logger.debug("request Data is {}", request.toString());
        QueryPrintPayDetailResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new QueryPrintPayDetailResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId()) || StringUtils.isBlank(request.getOutTradeNo())) {
            response = new QueryPrintPayDetailResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else if (!Validator.contains(EnumDefine.PayType.class, request.getPayType())) {
        	response = new QueryPrintPayDetailResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                response = payDetailsService.doJoinTransQueryPayDetail(request.getOutTradeNo(), request.getPayType());
                logger.info(logPrefix + "成功");
            } catch (IllegalArgumentException e) {
                response = new QueryPrintPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
            	logger.info(logPrefix + "异常：{}", e.getMessage(), e);
                response = new QueryPrintPayDetailResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
	}
	
}
