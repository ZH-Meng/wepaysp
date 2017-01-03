package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.zbsp.wepaysp.mobile.common.CommonResultCode;
import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginResponse;

@RestController
@RequestMapping("/user/v1/")
public class UserRestController
    extends BaseController {
    
    private final String key = "11111111111111111111111111111111";// FIXME
    
    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
    	//FIXME 模拟设置sign
    	request.build(key);
    	
        logger.info("处理用户登录请求 - 开始");
        logger.debug("request Data is " + request.toString());
        UserLoginResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, key)) {
            response = new UserLoginResponse(CommonResultCode.parseError.getValue(), "数据包解析错误", responseId);
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd()) || StringUtils.isBlank(request.getRequestId())) {
            response = new UserLoginResponse(CommonResultCode.verifyError.getValue(), "数据包不完整", responseId);
        } else {
            try {
                response = new UserLoginResponse(CommonResultCode.success.getValue(), "登陆成功", responseId);
                
                Map<String, String> resultMap = sysUserService.doTransUserLogin4Client(request.getUserId(), DigestHelper.sha512Hex(request.getPasswd()));
                
                response.setDealerCompany(MapUtils.getString(resultMap, "dealerCompany"));
                response.setStoreName(MapUtils.getString(resultMap, "storeName"));
                response.setDealerEmployeeName(MapUtils.getString(resultMap, "dealerEmployeeName"));
                response.setDealerEmployeeId(MapUtils.getString(resultMap, "dealerEmployeeId"));
                response.setDealerEmployeeOid(MapUtils.getString(resultMap, "dealerEmployeeOid"));
                
                logger.info("用户登录 - 成功");
            } catch (IllegalAccessException | IllegalStateException e) {
            	logger.warn("用户登录失败：" + e.getMessage());
            	response = new UserLoginResponse(CommonResultCode.sysError.getValue(), "登陆失败", responseId);
            } catch (NotExistsException e) {
            	logger.warn("用户登录失败："+e.getMessage());
            	response = new UserLoginResponse(CommonResultCode.sysError.getValue(), "登陆失败", responseId);
            } catch (Exception e) {
                logger.error("处理用户登录请求 - 错误");
                logger.error(e.getMessage(), e);
                response = new UserLoginResponse(CommonResultCode.sysError.getValue(), "系統錯誤", responseId);
            }
        }
        response = response.build(key);
        logger.debug("response Data is " + response.toString());
        logger.info("处理用户登录请求 - 结束");
        return response;
    }
    
    public static void main(String[] args) {
        String key = "11111111111111111111";
        UserLoginRequest request = new UserLoginRequest();
        request.setUserId("dealerE1");
        request.setPasswd("111111");
        String reqId = Generator.generateIwoid();
        request.setRequestId(reqId);
        System.out.println(reqId);
        request.setAppType(MobileRequest.AppType.pc.getValue());
        try {
            request.setSignature(Signature.getSign(request, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(request.toString());
        
        UserLoginResponse response = null;
        String responseId = Generator.generateIwoid();
        if (!Signature.checkIsSignValidFromRequest(request, key)) {
            response = new UserLoginResponse(CommonResultCode.parseError.getValue(), "数据包解析错误", responseId);
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd())) {
            response = new UserLoginResponse(CommonResultCode.verifyError.getValue(), "数据包不完整", responseId);
        } else {
            response = new UserLoginResponse(CommonResultCode.success.getValue(), "处理成功", responseId);
        }
        response = response.build(key);
        System.out.println("response Data is " + response.toString());
    }

}
