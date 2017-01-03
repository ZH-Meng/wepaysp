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
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.mobile.common.CommonResultCode;
import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginResponse;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.DealerEmployee;

@RestController
@RequestMapping("/user")
public class UserRestController
    extends BaseController {
    
    private final String key = "11111111111111111111111111111111";// FIXME
    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        logger.info("处理用户登录请求 - 开始");
        logger.debug("request Data is " + request.toString());
        UserLoginResponse response = null;
        
        if (!Signature.checkIsSignValidFromRequest(request, key)) {
            response = new UserLoginResponse(CommonResultCode.parseError.getValue(), "数据包解析错误");
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd())) {
            response = new UserLoginResponse(CommonResultCode.verifyError.getValue(), "数据包不完整");
        } else {
            try {
                response = new UserLoginResponse(CommonResultCode.success.getValue(), "处理成功");
                
                Map<String, Object> resMap = sysUserService.doTransUserLogin(request.getUserId(), DigestHelper.sha512Hex(request.getPasswd()), httpRequest.getRemoteAddr());
                SysUser user = (SysUser) MapUtils.getObject(resMap, "sysUser");
                
                DealerEmployee dealerE = user.getDealerEmployee();
                if (user.getDealerEmployee() == null) {
                    return response;
                }
                response.setDealerEmployeeName(dealerE.getEmployeeName());
                response.setDealerEmployeeId(dealerE.getDealerEmployeeId());
                // FIXME
                response.setSignature(Signature.getSign(response, key));
                // response.setDealerCompany();
                
                logger.error("用户登录 - 成功");
            } catch (Exception e) {
                logger.error("处理用户登录请求 - 错误");
                logger.error(e.getMessage(), e);
                response = new UserLoginResponse(CommonResultCode.sysError.getValue(), "系統錯誤");
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
        if (!Signature.checkIsSignValidFromRequest(request, key)) {
            response = new UserLoginResponse(CommonResultCode.parseError.getValue(), "数据包解析错误");
        } else if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getPasswd())) {
            response = new UserLoginResponse(CommonResultCode.verifyError.getValue(), "数据包不完整");
        } else {
            response = new UserLoginResponse(CommonResultCode.success.getValue(), "处理成功");
        }
        response = response.build(key);
        System.out.println("response Data is " + response.toString());
    }

}
