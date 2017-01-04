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

import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginResponse;
import com.zbsp.wepaysp.mobile.result.CommonResult;
import com.zbsp.wepaysp.mobile.result.LoginResult;

@RestController
@RequestMapping("/user/v1/")
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
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd()) || StringUtils.isBlank(request.getRequestId())) {
            response = new UserLoginResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            try {
                response = new UserLoginResponse(CommonResult.SUCCESS.getCode(), "登陆成功", responseId);

                Map<String, String> resultMap = sysUserService.doTransUserLogin4Client(request.getUserId(), DigestHelper.sha512Hex(request.getPasswd()));

                response.setDealerCompany(MapUtils.getString(resultMap, "dealerCompany"));
                response.setStoreName(MapUtils.getString(resultMap, "storeName"));
                response.setDealerEmployeeName(MapUtils.getString(resultMap, "dealerEmployeeName"));
                response.setDealerEmployeeId(MapUtils.getString(resultMap, "dealerEmployeeId"));
                response.setDealerEmployeeOid(MapUtils.getString(resultMap, "dealerEmployeeOid"));

                logger.info("用户( {} )登陆 - 成功", request.getUserId());
            } catch (IllegalAccessException | IllegalStateException | NotExistsException e) {
                logger.warn("用户登陆失败：" + e.getMessage());
                response = new UserLoginResponse(LoginResult.LOGIN_FAIL.getCode(), LoginResult.LOGIN_FAIL.getDesc(), responseId);
            } catch (Exception e) {
                logger.error("处理用户登陆请求 - 错误");
                logger.error(e.getMessage(), e);
                response = new UserLoginResponse(CommonResult.SYS_ERROR.getCode(), CommonResult.SYS_ERROR.getDesc(), responseId);
            }
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
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getRequestId())) {
            response = new MobileResponse(CommonResult.ARGUMENT_MISS.getCode(), CommonResult.ARGUMENT_MISS.getDesc(), responseId);
        } else {
            response = new MobileResponse(CommonResult.SUCCESS.getCode(), "登出成功", responseId);
            logger.info("用户( {} )登出 - 成功", request.getUserId());
        }
        response = response.build(KEY);
        logger.debug("response Data is {}", response.toString());
        logger.info("处理用户登出请求 - 结束");
        return response;
    }

}
