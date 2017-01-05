package com.zbsp.wepaysp.mobile.controller.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.app.AppManageService;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.mo.base.MobileRequest;
import com.zbsp.wepaysp.mo.version.v1_0.CheckVersionResponse;
import com.zbsp.wepaysp.mobile.controller.BaseController;

@RestController
@RequestMapping("/version/v1")
public class AppVersionRestController extends BaseController {

    @Autowired
    private AppManageService appManageService;
    
    @RequestMapping(value = "check", method = RequestMethod.POST)
    @ResponseBody
    public CheckVersionResponse checkVersion(@RequestBody MobileRequest request) {
        if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info("处理获取版本请求 - 开始");
        logger.debug("request Data is {}", request.toString());
        CheckVersionResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new CheckVersionResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getRequestId())) {
            response = new CheckVersionResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                response= appManageService.doJoinTransQueryLatestApp(request.getAppType());
            } catch (IllegalArgumentException e) {
                response = new CheckVersionResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.error("获取版本请求 - 异常：\n {}", e.getMessage(), e);
                response = new CheckVersionResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        
        if (response.getResult() == CommonResult.SUCCESS.getCode()) {
            logger.info("获取版本请求 - 成功");
        } else {
            logger.info("获取版本请求 - 失败，原因：{}", response.getMessage());
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info("处理获取版本请求 - 结束");
        return response;
    }
    
}
