package com.zbsp.wepaysp.mobile.controller.appid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.pay.PayDetailsService;
import com.zbsp.wepaysp.api.service.report.RptDealerStatService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.api.util.SysUserUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
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
    @Autowired
    private RptDealerStatService rptDealerStatService;
    @Autowired
    private StoreService storeService;
    
    @RequestMapping(value = "list")
    public ModelAndView list(String openid, String dealerOid, String storeOid, String dealerEmployeeOid) {
        String logPrefix = "处理微信公众号收款列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, dealerOid：{}, storeOid：{}, dealerEmployeeOid：{}", openid, dealerOid, storeOid, dealerEmployeeOid);
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
                if (SysUserUtil.isDealer(user) || SysUserUtil.isStoreManager(user) || SysUserUtil.isDealerEmployee(user)) {// 商户、商户员工有权查看
                    modelAndView = new ModelAndView("appid/collectionList");
					if (SysUserUtil.isDealer(user)) {
						// 查找商户门店
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("dealerOid", user.getDealer().getIwoid());
						modelAndView.addObject("storeList", storeService.doJoinTransQueryStoreList(paramMap, 0, -1));
					}
					// 激活收款列表面板
					modelAndView.addObject("title", "收款列表");
					modelAndView.addObject("tabActive", "tab-collection-list");
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
        logger.info(logPrefix + "结束");
        return modelAndView;
    }
    
    /**
     * 公众号消息连接按日期分页查询收款列表
     * @param openid 微信用户唯一标识，判断用户在系统中的用户级别
     * @param dealerOid、storeOid、dealerEmployeeOid组成支付二维码的唯一标识 
     * @param queryStoreOid 商户选择某门店查看
     * @param queryDate 为空时代表默认当日
     * @param pageIndex 页码
     * @return
     */
    @RequestMapping(value = "list/{pageIndex}")
    @ResponseBody
    public Map<String, Object> page(String openid, String dealerOid, String storeOid, String dealerEmployeeOid, String queryStoreOid, String queryDate, @PathVariable String pageIndex, String pageSize) {
        String logPrefix = "处理微信公众号分页查询收款列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, dealerOid：{}, storeOid：{}, dealerEmployeeOid：{}", openid, dealerOid, storeOid, dealerEmployeeOid);
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	
    	Map<String, Object> paramMap = new HashMap<String, Object>();
		int startIndex = 0;
		int maxResult = 0;
		// 检查参数
		if (StringUtils.isBlank(openid)) {
			logger.warn(logPrefix + "openid为空");
		} else if (StringUtils.isBlank(dealerOid)) {
			logger.warn(logPrefix + "dealerOid为空");
		} else if (StringUtils.isBlank(storeOid)) {// 至少是1店一码
			logger.warn(logPrefix + "storeOid为空");			
		} else if (StringUtils.isBlank(pageIndex) && !NumberUtils.isCreatable(pageIndex)) {
			logger.warn(logPrefix + "pageIndex格式不正确");
		} else if (StringUtils.isBlank(pageSize) && !NumberUtils.isCreatable(pageSize)) {
			logger.warn(logPrefix + "pageSize格式不正确");
		} else {
			// 根据openid 查找绑定商户、商户员工，并返回商户级别
			SysUser user = payNoticeBindWeixinService.doJoinTransQueryBindUser(openid);
			if (user == null) {
				logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
			} else {
				maxResult = Integer.valueOf(pageSize);
				startIndex = maxResult * (Integer.valueOf(pageIndex) - 1);

				// 微信公众号查看收款列表-针对微信公众号支付
				paramMap.put("payType", PayType.WEIXIN_JSAPI.getValue());
				// 根据级别查询不同收款记录
				if (StringUtils.isBlank(queryDate)) {
					// 当天
					Date today = new Date();
					paramMap.put("beginTime", TimeUtil.getDayStart(today));
					paramMap.put("endTime", TimeUtil.getDayEnd(today));
				} else {
					Date date = DateUtil.getDate(queryDate, "yyyy-MM-dd");
					paramMap.put("beginTime", TimeUtil.getDayStart(date));
					paramMap.put("endTime", TimeUtil.getDayEnd(date));
				}

				if (SysUserUtil.isDealer(user)) {// 商户查看所有门店的数据
					paramMap.put("dealerOid", user.getDealer().getIwoid());
					paramMap.put("storeOid", queryStoreOid);
				} else if (SysUserUtil.isStoreManager(user)) {// 店长查看门店所有数据
					// paramMap.put("storeOid", user.getDealerEmployee().getStore().getIwoid());
					paramMap.put("storeOid", storeOid);
				} else if (SysUserUtil.isDealerEmployee(user)) {// 店员
					paramMap.put("storeOid", storeOid);
					if (StringUtils.isNotBlank(dealerEmployeeOid)) {// 一人一码
						paramMap.put("dealerEmployeeOid", dealerEmployeeOid);
					}
				} else {
					logger.warn(logPrefix + "权限不足，openid：{}", openid);
				}
				resultMap = payDetailsService.doJoinTransAppIdQueryList(paramMap, startIndex, maxResult);
			}
        }
        return resultMap;
    }
    
    @RequestMapping(value = "stat")
    public ModelAndView stat(String openid, String dealerOid, String storeOid, String dealerEmployeeOid, String queryStoreOid) {
        String logPrefix = "处理微信公众号收款汇总请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, dealerOid：{}, storeOid：{}, dealerEmployeeOid：{}, queryStoreOid：{}", openid, dealerOid, storeOid, dealerEmployeeOid, queryStoreOid);
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
                if (SysUserUtil.isDealer(user) || SysUserUtil.isStoreManager(user) || SysUserUtil.isDealerEmployee(user)) {// 商户、商户员工有权查看
                    modelAndView = new ModelAndView("appid/collectionList");
                    if (SysUserUtil.isDealer(user)) {
                        // 查找商户门店
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("dealerOid", user.getDealer().getIwoid());
                        modelAndView.addObject("storeList", storeService.doJoinTransQueryStoreList(paramMap, 0, -1));
                    }
                    
                    // 查询统计
                    
                    // 激活收款汇总面板
                    modelAndView.addObject("title", "收款汇总");
                    modelAndView.addObject("tabActive", "tab-stat-list");
                    modelAndView.addObject("openid", openid);
                    modelAndView.addObject("dealerOid", dealerOid);
                    modelAndView.addObject("storeOid", storeOid);
                    modelAndView.addObject("dealerEmployeeOid", dealerEmployeeOid);
                } else {
                    modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.PERMISSION_DENIED.getCode(), H5CommonResult.PERMISSION_DENIED.getDesc()));
                    logger.warn(logPrefix + "权限不足，openid：{}", openid);                  
                }
            }
        }
        logger.info(logPrefix + "结束");
        return modelAndView;
    }
    
}
