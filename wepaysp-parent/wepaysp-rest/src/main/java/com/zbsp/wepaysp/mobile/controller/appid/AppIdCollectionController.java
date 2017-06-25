package com.zbsp.wepaysp.mobile.controller.appid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.vo.report.AppidCollectionStatVO;

/**
 * 公众号收款控制器
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/appid/collection")
public class AppIdCollectionController extends BaseController {

    @Autowired
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
    @Autowired
    private PayDetailsService payDetailsService;
    @Autowired
    private RptDealerStatService rptDealerStatService;
    @Autowired
    private StoreService storeService;

    /**此方法可废掉，暂时兼顾历史消息*/
    @RequestMapping(value = "list")
    public ModelAndView list(String openid) {
        String logPrefix = "处理微信公众号收款列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}", openid);
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
                modelAndView = new ModelAndView("appid/appidIndex");
                if (dealerUser != null) {
                    // 查找商户门店
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("dealerOid", dealerUser.getDealer().getIwoid());
                    modelAndView.addObject("storeList", storeService.doJoinTransQueryStoreList(paramMap, 0, -1));
                }
                // 激活收款列表面板
                modelAndView.addObject("function", "collection-list");
                modelAndView.addObject("openid", openid);
                modelAndView.addObject("today", DateUtil.getDate(new Date(), "yyyy-MM-dd"));
            }
        }
        logger.info(logPrefix + "结束");
        return modelAndView;
    }
            
    /**
     * 公众号消息连接按日期分页查询收款列表
     * @param openid 微信用户唯一标识，判断用户在系统中的用户级别
     * @param queryStoreOid 商户选择某门店查看
     * @param queryDate 为空时代表默认当日
     * @param pageIndex 页码
     * @return
     */
    @RequestMapping(value = "list/{pageIndex}")
    @ResponseBody
    public Map<String, Object> page(String openid, String queryStoreOid, String queryDate, @PathVariable String pageIndex, String pageSize) {
        String logPrefix = "处理微信公众号分页查询收款列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, pageIndex：{}, pageSize：{}", openid, pageIndex, pageSize);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        int startIndex = 0;
        int maxResult = 0;
        // 检查参数
        if (StringUtils.isBlank(openid)) {
            logger.warn(logPrefix + "openid为空");
        } else if (StringUtils.isBlank(pageIndex) && !NumberUtils.isCreatable(pageIndex)) {
            logger.warn(logPrefix + "pageIndex格式不正确");
        } else if (StringUtils.isBlank(pageSize) && !NumberUtils.isCreatable(pageSize)) {
            logger.warn(logPrefix + "pageSize格式不正确");
        } else {
            // 根据openid 查找绑定商户、商户员工，并返回商户级别
            Map<String, Object> bindMap = payNoticeBindWeixinService.doJoinTransQueryBindInfo(openid);
            SysUser dealerUser = (SysUser) bindMap.get("dealerUser");
            SysUser cashierUser = (SysUser) bindMap.get("cashierUser");
            if (dealerUser == null && cashierUser == null) {
                logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
            } else {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                maxResult = Integer.valueOf(pageSize);
                startIndex = maxResult * (Integer.valueOf(pageIndex) - 1);

                if (dealerUser != null) {// 商户查看所有门店的数据
                    paramMap.put("dealerOid", dealerUser.getDealer().getIwoid());
                    paramMap.put("storeOid", queryStoreOid);
                } else if (SysUserUtil.isStoreManager(cashierUser)) {// 店长查看门店所有数据
                    paramMap.put("storeOid", cashierUser.getDealerEmployee().getStore().getIwoid());
                } else {
                    PayNoticeBindWeixin bindCashier = (PayNoticeBindWeixin) bindMap.get("bindCashier");
                    paramMap.put("storeOid", cashierUser.getDealerEmployee().getStore().getIwoid());
                    if (bindCashier.getPayDealerEmployee() != null) {// 一人一码
                        paramMap.put("dealerEmployeeOid", bindCashier.getPayDealerEmployee().getIwoid());
                    }
                }
                // 微信公众号查看收款列表-针对微信公众号支付
                // paramMap.put("payType", PayType.WEIXIN_JSAPI.getValue());
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
                resultMap = payDetailsService.doJoinTransAppIdQueryList(paramMap, startIndex, maxResult);
            }
        }
        return resultMap;
    }
        
    @RequestMapping(value = "statList")
    @ResponseBody
    public List<AppidCollectionStatVO> statList(String openid, String queryStoreOid) {
        String logPrefix = "处理微信公众号收款汇总列表请求 - ";
        logger.info(logPrefix + "开始");
        logger.info(logPrefix + "参数openid：{}, queryStoreOid：{}", openid, queryStoreOid);
        List<AppidCollectionStatVO> statList = new ArrayList<AppidCollectionStatVO>();

        // 检查参数
        if (StringUtils.isBlank(openid)) {
            logger.warn(logPrefix + "openid为空");
        } else {
            // 根据openid 查找绑定商户、商户员工，并返回商户级别
            Map<String, Object> bindMap = payNoticeBindWeixinService.doJoinTransQueryBindInfo(openid);
            SysUser dealerUser = (SysUser) bindMap.get("dealerUser");
            SysUser cashierUser = (SysUser) bindMap.get("cashierUser");
            if (dealerUser == null && cashierUser == null) {
                logger.warn(logPrefix + "此openid未关联商户或商户员工，openid：{}", openid);
            } else {
                // 根据级别查询不同收款记录
                if (dealerUser != null) {// 商户查看所有门店的数据
                    if (StringUtils.isNotBlank(queryStoreOid)) {
                        statList = rptDealerStatService.doJoinTransQueryStoreList(queryStoreOid, 0, 4);
                    } else {
                        statList = rptDealerStatService.doJoinTransQueryDealerList(dealerUser.getDealer().getIwoid(), 0, 4);
                    }
                } else if (SysUserUtil.isStoreManager(cashierUser)) {// 店长查看门店所有数据
                    statList = rptDealerStatService.doJoinTransQueryStoreList(cashierUser.getDealerEmployee().getStore().getIwoid(), 0, 4);
                } else {
                    PayNoticeBindWeixin bindCashier = (PayNoticeBindWeixin) bindMap.get("bindCashier");
                    if (bindCashier.getPayDealerEmployee() != null) {// 一人一码
                        statList = rptDealerStatService.doJoinTransQueryDealerEList(bindCashier.getPayDealerEmployee().getIwoid(), 0, 4);
                    } else {
                        statList = rptDealerStatService.doJoinTransQueryStoreList(cashierUser.getDealerEmployee().getStore().getIwoid(), 0, 4);
                    }
                }
            }
        }
        logger.info(logPrefix + "结束");
        return statList;
    }
    
}
