package com.zbsp.wepaysp.mobile.controller.appid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.api.util.SysUserUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.po.manage.SysUser;

/**
 * 公众号功能入口控制器
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/index")
public class AppIdIndexController
    extends BaseController {

    @Autowired
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
    @Autowired
    private StoreService storeService;

    @RequestMapping(value = "{function}")
    public ModelAndView index(@PathVariable String function, String openid, String dealerOid, String storeOid, String dealerEmployeeOid) {
        String logPrefix = "处理微信公众号入口请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;

        if (!"collection".equals(function) && !"stat".equals(function) && !"more".equals(function)) {
            logger.warn(logPrefix + "function：{}非法，忽略处理", function);
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ILLEGAL_REQUEST.getCode(), H5CommonResult.ILLEGAL_REQUEST.getDesc()));
        } else {
            logger.info(logPrefix + "参数openid：{}, dealerOid：{}, storeOid：{}, dealerEmployeeOid：{}", openid, dealerOid, storeOid, dealerEmployeeOid);

            modelAndView = new ModelAndView("appid/appidIndex");
            // 激活收款列表面板
            if ("collection".equals(function)) {
                logger.info(logPrefix + "收款列表");
                modelAndView.addObject("function", "collection-list");
            } else if ("stat".equals(function)) {
                logger.info(logPrefix + "收款汇总");
                modelAndView.addObject("function", "stat-list");
            } else if ("more".equals(function)) {
                logger.info(logPrefix + "更多功能");
                modelAndView.addObject("function", "more");
            }

            // 检查参数
            if (StringUtils.isBlank(openid)) {
                logger.warn(logPrefix + "openid为空");
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
            } else {
                // 根据openid 查找绑定商户、商户员工，并返回商户级别
                SysUser user = payNoticeBindWeixinService.doJoinTransQueryBindUser(openid);
                if (user == null) {
                    logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
                    modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.OPENID_UNKOWN.getCode(), H5CommonResult.OPENID_UNKOWN.getDesc()));
                } else {
                    if (SysUserUtil.isDealer(user) || SysUserUtil.isStoreManager(user) || SysUserUtil.isDealerEmployee(user)) {// 商户、商户员工有权查看
                        if (SysUserUtil.isDealer(user)) {
                            // 查找商户门店
                            Map<String, Object> paramMap = new HashMap<String, Object>();
                            paramMap.put("dealerOid", user.getDealer().getIwoid());
                            modelAndView.addObject("storeList", storeService.doJoinTransQueryStoreList(paramMap, 0, -1));
                        }
                        modelAndView.addObject("openid", openid);
                        modelAndView.addObject("dealerOid", dealerOid);
                        modelAndView.addObject("storeOid", storeOid);
                        modelAndView.addObject("dealerEmployeeOid", dealerEmployeeOid);
                        modelAndView.addObject("today", DateUtil.getDate(new Date(), "yyyy-MM-dd"));
                    } else {
                        modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.PERMISSION_DENIED.getCode(), H5CommonResult.PERMISSION_DENIED.getDesc()));
                        logger.warn(logPrefix + "权限不足，openid：{}", openid);
                    }
                }
            }
        }
        logger.info(logPrefix + "结束");
        return modelAndView;
    }

}
