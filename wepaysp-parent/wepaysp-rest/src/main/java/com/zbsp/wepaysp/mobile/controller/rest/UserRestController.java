package com.zbsp.wepaysp.mobile.controller.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.base.MobileRequest;
import com.zbsp.wepaysp.mo.base.MobileResponse;
import com.zbsp.wepaysp.mo.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mo.userlogin.v1_0.UserLoginResponse;
import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;

@RestController
@RequestMapping("/user/v1")
public class UserRestController
    extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info("处理用户登陆请求 - 开始");
        logger.debug("request Data is {}", request.toString());
        UserLoginResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new UserLoginResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (!Validator.contains(MobileRequest.AppType.class, request.getAppType())) {
            response = new UserLoginResponse(CommonResult.INVALID_APPTYPE.getCode(), CommonResult.INVALID_APPTYPE.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd()) || StringUtils.isBlank(request.getRequestId())) {
            response = new UserLoginResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                response = sysUserService.doTransUserLogin4Client(request.getUserId(), DigestHelper.sha512Hex(request.getPasswd()));
            } catch (IllegalArgumentException e) {
                logger.warn("用户登陆失败：" + e.getMessage());
                response = new UserLoginResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), responseId);
            } catch (Exception e) {
                logger.error("处理用户登陆请求 - 错误");
                logger.error(e.getMessage(), e);
                response = new UserLoginResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
        }
        if (response.getResult() == CommonResult.SUCCESS.getCode()) {
            logger.info("用户( {} )登陆 - 成功", request.getUserId());
        } else {
            logger.info("用户( {} )登陆 - 失败，原因：{}", request.getUserId(), response.getMessage());
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info("处理用户登陆请求 - 结束");
        return response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public MobileResponse logout(@RequestBody UserLoginRequest request) {
        if (DEV_FLAG) {// 开发阶段：模拟设置sign
            request.build(KEY);
        }

        logger.info("处理用户登出请求 - 开始");
        logger.debug("request Data is {}", request.toString());
        MobileResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, KEY)) {
            response = new MobileResponse(CommonResult.PARSE_ERROR.getCode(), CommonResult.PARSE_ERROR.getDesc(), responseId);
        } else if (!Validator.contains(MobileRequest.AppType.class, request.getAppType())) {
            response = new UserLoginResponse(CommonResult.INVALID_APPTYPE.getCode(), CommonResult.INVALID_APPTYPE.getDesc(), responseId);
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getRequestId())) {
            response = new MobileResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            response = new MobileResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), responseId);
            logger.info("用户( {} )登出 - 成功", request.getUserId());
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info("处理用户登出请求 - 结束");
        return response;
    }

}
