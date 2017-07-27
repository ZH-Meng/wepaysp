package com.zbsp.wepaysp.mobile.controller.appid;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;

/**
 * 公众号更多功能控制器
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/more")
public class AppIdMoreController
    extends BaseController {

    @Autowired
    private PayNoticeBindWeixinService payNoticeBindWeixinService;

    @RequestMapping(value = "index")
    public ModelAndView index(String openid) {
        String logPrefix = "处理微信公众号入口请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        // 检查参数
        if (StringUtils.isBlank(openid)) {
            logger.warn(logPrefix + "openid为空");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else {
            // 根据openid 查找绑定商户、商户员工，并返回商户级别
            Map<String, Object> bindMap = payNoticeBindWeixinService.doJoinTransQueryBindInfo(openid);
            SysUser dealerUser = (SysUser) bindMap.get("dealerUser");
            SysUser cashierUser = (SysUser) bindMap.get("cashierUser");

            if (dealerUser == null && cashierUser == null) {
                logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.PERMISSION_DENIED.getCode(), H5CommonResult.PERMISSION_DENIED.getDesc()));
            } else {
                modelAndView = new ModelAndView("appid/appidMore");
                modelAndView.addObject("openid", openid);
                // 商户员工更多功能：选项：是否每日接收收款通知， 是否每日接收收款汇总通知
                // 商户 更多功能，选项：是否每日接收收款汇总通知，暂不考虑可能既是商户又是员工
                if (dealerUser != null) {
                    PayNoticeBindWeixin bindDealer = (PayNoticeBindWeixin) bindMap.get("bindDealer");
                    modelAndView.addObject("collectionNoticeState", PayNoticeBindWeixin.State.open.getValue().equals(bindDealer.getState()) ? "on" : "off");
                    modelAndView.addObject("bindCollnoticeOid", bindDealer.getIwoid());
                } else if (cashierUser != null) {
                    PayNoticeBindWeixin bindCashier = (PayNoticeBindWeixin) bindMap.get("bindCashier");
                    modelAndView.addObject("collectionNoticeState", PayNoticeBindWeixin.State.open.getValue().equals(bindCashier.getState()) ? "on" : "off");
                    modelAndView.addObject("bindCollnoticeOid", bindCashier.getIwoid());
                }
            }
        }
        logger.info(logPrefix + "结束");
        return modelAndView;
    }

    @RequestMapping(value = "collectionNotice/{operate}")
    @ResponseBody
    public Map<String, Object> collectionNotice(String bindOid, @PathVariable String operate) {
        String logPrefix = "处理微信公众号支付消息通知请求 - ";
        logger.info(logPrefix + "开始");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (!"open".equals(operate) && !"close".equals(operate)) {
            logger.warn(logPrefix + "operate：{}非法，忽略处理", operate);
            resultMap.put("result", "FAIL");
        } else {
            logger.info(logPrefix + "参数bindOid：{}, operate：{}", bindOid, operate);
            // 检查参数
            if (StringUtils.isBlank(bindOid)) {
                logger.warn(logPrefix + "bindOid为空");
                resultMap.put("result", "FAIL");
            } else {
                payNoticeBindWeixinService.doTransUpdateBindWeixinState(bindOid, "open".equals(operate) ? PayNoticeBindWeixin.State.open : PayNoticeBindWeixin.State.closed);
                resultMap.put("result", "SUCESS");
            }
        }

        logger.info(logPrefix + "结束");
        return resultMap;
    }

}
