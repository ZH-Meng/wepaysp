package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.zbsp.wepaysp.api.service.report.RptDealerStatService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.base.MobileRequest;
import com.zbsp.wepaysp.mo.paystat.v1_0.QueryPayStatRequest;
import com.zbsp.wepaysp.mo.paystat.v1_0.QueryPayStatResponse;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;

@RestController
@RequestMapping("/paystat/v1")
public class PayStatRestController extends BaseController {
    
	@Autowired
	private RptDealerStatService rptDealerStatService;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
    public QueryPayStatResponse query(@RequestBody QueryPayStatRequest request) {
		String logPrefix = "处理查询支付结算请求 - ";
		if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

		logger.info(logPrefix + "开始");
        logger.debug("request Data is {}", request.toString());
        QueryPayStatResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new QueryPayStatResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (!Validator.contains(MobileRequest.AppType.class, request.getAppType())) {
            response = new QueryPayStatResponse(CommonResult.INVALID_APPTYPE.getCode(), CommonResult.INVALID_APPTYPE.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId()) || StringUtils.isBlank(request.getDealerEmployeeOid())) {
            response = new QueryPayStatResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else if (!Validator.contains(QueryPayStatRequest.QueryType.class, request.getQueryType())) {
        	response = new QueryPayStatResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc() + "(queryType)", responseId);
        } else if (StringUtils.isBlank(request.getBeginTime()) || StringUtils.isBlank(request.getEndTime())) {
        	response = new QueryPayStatResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("beginTime", DateUtil.getDate(request.getBeginTime(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
                paramMap.put("endTime", DateUtil.getDate(request.getEndTime(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
                paramMap.put("queryType", request.getQueryType());

                response = rptDealerStatService.doJoinTransQueryPayStat4DealerE(request.getDealerEmployeeOid(), paramMap);
                
                logger.info(logPrefix + "成功");
            } catch (IllegalArgumentException e) {
                response = new QueryPayStatResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.error(logPrefix + "异常：{}", e.getMessage(), e);
                response = new QueryPayStatResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info(logPrefix + "结束");
        return response;
    }
	
}
