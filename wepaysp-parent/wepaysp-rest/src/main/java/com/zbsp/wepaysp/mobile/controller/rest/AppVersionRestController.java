package com.zbsp.wepaysp.mobile.controller.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.app.AppManageService;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.version.v1_0.CheckVersionResponse;
import com.zbsp.wepaysp.mobile.result.CommonResult;
import com.zbsp.wepaysp.po.app.AppManage;

@RestController
@RequestMapping("/version/v1")
public class AppVersionRestController extends BaseController {

    @Autowired
    private AppManageService appManageService;
    
    @RequestMapping(value = "check", method = RequestMethod.POST)
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
                response = new CheckVersionResponse(CommonResult.SUCCESS.getCode(), "处理成功", responseId);
                AppManage appManage = appManageService.doJoinTransQueryLatestApp(request.getAppType());
                response.setVersionName(appManage.getVersionName());
                response.setVersionNumber(appManage.getVersionNumber());
                response.setDescription(appManage.getVersionDesc());
                response.setDownloadUrl(appManage.getDownloadUrl());
                response.setMinNumber(appManage.getSupportMinVersionNumber());
                response.setMinVersion(appManage.getSupportMinVersionName());
                response.setTipType(appManage.getTipType());
                response.setFileSize(appManage.getFileSize());

                logger.info("获取版本请求 - 成功");
            } catch (IllegalArgumentException e) {
                response = new CheckVersionResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (NotExistsException e) {
                response = new CheckVersionResponse(CommonResult.DATA_NOT_EXIST.getCode(), "版本信息" + CommonResult.DATA_NOT_EXIST.getDesc(), responseId);
            } catch (Exception e) {
                logger.error("获取版本请求 - 异常：\n {}", e.getMessage(), e);
                response = new CheckVersionResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info("处理获取版本请求 - 结束");
        return response;
    }
    
}
