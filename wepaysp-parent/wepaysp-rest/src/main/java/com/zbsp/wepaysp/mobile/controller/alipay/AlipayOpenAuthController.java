package com.zbsp.wepaysp.mobile.controller.alipay;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zbsp.wepaysp.mobile.controller.BaseController;

@Controller
@RequestMapping("/alipay/")
public class AlipayOpenAuthController extends BaseController {
    
    @RequestMapping("auth/app/callback")
    public String appAuthCallBack(@RequestParam(value = "app_id", required = true) String appId, @RequestParam("app_auth_code") String appAuthCode) {
        String logPrefix = "处理蚂蚁开放平台商户应用授权后回调请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "授权响应，app_id : {}, app_auth_code : {}", appId, appAuthCode);
        
        if (StringUtils.isNotBlank(appAuthCode)) {
            logger.info("商户同意授权");
        }
        
        //  使用app_auth_code换取app_auth_token
        
        
        logger.info(logPrefix + "结束");
        return "";
    }
    
}
