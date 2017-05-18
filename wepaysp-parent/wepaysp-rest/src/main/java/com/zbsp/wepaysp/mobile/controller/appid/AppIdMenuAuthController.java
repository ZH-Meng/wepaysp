package com.zbsp.wepaysp.mobile.controller.appid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.vo.WxCallBackVO;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.zbsp.wepaysp.api.util.WeixinUtil;

/**
 * 微信公众号菜单授权控制器
 * <pre>
 *      微信浏览器授权后回调；
 *      根据菜单ID跳转不同地址；
 * <pre>
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/menu/")
public class AppIdMenuAuthController extends BaseController {
    
    @RequestMapping(value="auth/{menuid}")
    public ModelAndView auth(WxCallBackVO callBackVO, @PathVariable String menuid, RedirectAttributes redirectAttributes) {
        String logPrefix = "处理微信公众号菜单授权请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        
        if (callBackVO == null) {
            logger.warn(logPrefix + "callBack is null");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else if (StringUtils.isBlank(callBackVO.getCode())) {
            logger.warn(logPrefix + "code is null");
            // 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else if (StringUtils.isBlank(callBackVO.getPartnerOid())) {
			logger.warn(logPrefix + "partnerOid is null");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else if (StringUtils.isBlank(menuid)) {// FIXME 可以跳转到公众号网站默认页
			logger.warn(logPrefix + "menuid is null");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else {
        	logger.info(logPrefix + "menuid : {}", menuid);
        	// 通过code换取网页授权access_token 和 openid
            GetAuthAccessTokenResData authResult = null;
            authResult = WeixinUtil.getAuthAccessToken(callBackVO.getCode(), callBackVO.getPartnerOid());
            if (authResult == null) {
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ACCESS_TOKEN_FAIL.getCode(), H5CommonResult.ACCESS_TOKEN_FAIL.getDesc()));
            } else {
                if ("collectionStat".equalsIgnoreCase(menuid)) {// 收款汇总
                	redirectAttributes.addAttribute("openid", authResult.getOpenid());
                	modelAndView = new ModelAndView("redirect:/appid/index/stat");
                } else {
        			logger.warn(logPrefix + "menuid invalid : {}", menuid);// FIXME 可以跳转到公众号网站默认页
                    modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
                }
            }
        }
        logger.info(logPrefix + "结束");
        return modelAndView;
    }

}
