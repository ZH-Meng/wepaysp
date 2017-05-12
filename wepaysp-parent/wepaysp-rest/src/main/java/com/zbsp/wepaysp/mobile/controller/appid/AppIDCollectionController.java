package com.zbsp.wepaysp.mobile.controller.appid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.pay.PayDetailsService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.api.util.SysUserUtil;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.po.manage.SysUser;

/**
 * 公众号收款控制器
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/collection")
public class AppIDCollectionController extends BaseController {

    @Autowired
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
    @Autowired
    private PayDetailsService payDetailsService;
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView list(String openid, String dealerOid, String storeOid, String dealerEmployeeOid, String queryStoreOid) {
        String logPrefix = "处理微信公众号收款列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, dealerOid：{}, storeOid：{}, dealerEmployeeOid：{}", openid, dealerEmployeeOid, storeOid, dealerEmployeeOid);
        ModelAndView modelAndView = null;

        // 检查参数
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(dealerOid)) {
            logger.warn(logPrefix + "openid或dealerOid为空");
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
        } else {
            // 根据openid 查找绑定商户、商户员工，并返回商户级别
            SysUser user = payNoticeBindWeixinService.doJoinTransQueryBindUser(openid);
            if (user == null) {
                logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.OPENID_UNKOWN.getCode(), H5CommonResult.OPENID_UNKOWN.getDesc()));
            } else {
                // 根据级别查询不同收款记录
                if (SysUserUtil.isDealer(user)) {// 商户查看所有门店的数据
                    //payDetailsService.
                } else if (SysUserUtil.isStoreManager(user)) {// 店长查看门店所有数据

                } else if (SysUserUtil.isDealerEmployee(user)) {// 店长查看门店所有数据

                }
            }
        }
        return modelAndView;
    }
    
}
