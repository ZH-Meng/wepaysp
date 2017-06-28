package com.zbsp.wepaysp.manage.web.action.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.PayTotalVO;

/**
 * 支付宝交易明细
 * 
 * @author 孟郑宏
 */
public class AliPayDetailsAction
    extends PageAction {
	
    private static final long serialVersionUID = -1628575995917257516L;
    private AliPayDetailsVO aliPayDetailsVO;
    private String beginTime;
    private String endTime;
    private List<AliPayDetailsVO> aliPayDetailsVoList;
    private AliPayDetailsService aliPayDetailsService;
    private List<PartnerVO> partnerVoList;
    private PartnerService partnerService;
    private int userLevel;
    private int partnerVoListLevel;
    private String listType;
    private PayTotalVO totalVO;
    private String queryMinAmount;

    @SuppressWarnings("unchecked")
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (aliPayDetailsVO == null) {
                aliPayDetailsVO = new AliPayDetailsVO();
            }
            // 初始化查询参数默认值
            initDefaultDatesArgs();
            
            boolean flag = false;

            /*根据用户的级别设置不同的查询条件*/
            if (manageUser.getUserLevel() == SysUser.UserLevel.partner.getValue()) {// 服务商
                if (manageUser.getDataPartner() != null) {
                    int partnerLevel = manageUser.getDataPartner().getLevel();
                    if (partnerLevel == 1 || partnerLevel == 2 || partnerLevel == 3) {
                        if (partnerLevel == 1) {// 一级服务商
                            partnerVoListLevel = 2;
                            
                            // 获取二级服务商集合
                            paramMap.put("parentPartnerOid", manageUser.getDataPartner().getIwoid());
                            partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, 0, -1);
                            // 当前台查询条件选择某个二级服务商
                            paramMap.clear();
                            paramMap.put("partner2Oid", aliPayDetailsVO.getPartner2Oid());
                        } else if (partnerLevel == 2) {// 二级服务商
                            partnerVoListLevel = 3;
                            
                            // 获取三级服务商集合
                            paramMap.put("parentPartnerOid", manageUser.getDataPartner().getIwoid());
                            partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, 0, -1);
                            // 当前台查询条件选择某个三级服务商
                            paramMap.clear();
                            paramMap.put("partner3Oid", aliPayDetailsVO.getPartner3Oid());
                        }
                        
                        paramMap.put("partner" + partnerLevel + "Oid", manageUser.getDataPartner().getIwoid());
                        flag = true;
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.salesman.getValue()) {// 业务员
            	if (manageUser.getDataPartnerEmployee() != null) {
            		paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
            		flag = true;
            	}
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.dealer.getValue()) {// 商户
                if (manageUser.getDataDealer() != null) {
                    paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                    flag = true;
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.cashier.getValue()) {// 收银员
                if (manageUser.getDataDealerEmployee() != null) {
                    paramMap.put("dealerEmployeeOid", manageUser.getDataDealerEmployee().getIwoid());
                    flag = true;
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.shopManager.getValue()) {// 分店店长
                if (manageUser.getDataDealerEmployee() != null && manageUser.getDataDealerEmployee().getStore() != null) {
                    paramMap.put("storeOid", manageUser.getDataDealerEmployee().getStore().getIwoid());
                    flag = true;
                }
            }

            if (!flag) {
                logger.warn("当前用户无权查看交易明细！");
                setAlertMessage("当前用户无权查看交易明细！");
                return "accessDenied";
            }
            // 用户级别，页面根据级别动态展示查询条件以及结果列表
            userLevel = manageUser.getUserLevel();
            
            /*paramMap.put("beginTime", TimeUtil.getDayStart(convertS2D(beginTime)));
            paramMap.put("endTime", TimeUtil.getDayEnd(convertS2D(endTime)));*/
            paramMap.put("beginTime", convertS2D(beginTime));
            paramMap.put("endTime", convertS2D(endTime));
            
            paramMap.put("partnerEmployeeId", aliPayDetailsVO.getPartnerEmployeeId());
            paramMap.put("dealerId", aliPayDetailsVO.getDealerId());
            paramMap.put("dealerEmployeeId", aliPayDetailsVO.getDealerEmployeeId());
            paramMap.put("storeId", aliPayDetailsVO.getStoreId());
            paramMap.put("outTradeNo", aliPayDetailsVO.getOutTradeNo());
            paramMap.put("tradeNo", aliPayDetailsVO.getTradeNo());
            paramMap.put("minAmout", aliPayDetailsVO.getTotalAmount());
            
            rowCount = aliPayDetailsService.doJoinTransQueryAliPayDetailsCount(paramMap);
            if (rowCount > 0) {
                Map<String, Object> resultMap = aliPayDetailsService.doJoinTransQueryAliPayDetails(paramMap, start, size);
                aliPayDetailsVoList = (List<AliPayDetailsVO>) MapUtils.getObject(resultMap, "payList");
            	// 合计
            	totalVO = (PayTotalVO) MapUtils.getObject(resultMap, "total");
            } else {
                totalVO = new PayTotalVO();
            }
        } catch (Exception e) {
            logger.error("支付宝交易明细查询列表错误：" + e.getMessage());
            setAlertMessage("支付宝交易明细查询列表错误！");
        }
        
        return "aliPayDetailsList";
    }

    /**
     * 服务商、业务员访问
     * 
     * @return
     */
    public String list() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "partner";
        return goCurrent();
    }
    
    /**
     * 商户、分店店长、收银员访问
     * 
     * @return
     */
    public String list4Dealer() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "dealer";
        return goCurrent();
    }
    
    private void initDefaultDatesArgs() {
        if (StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)) {
            // 默认查询时间为今天
            beginTime = convertD2S(TimeUtil.getDayStart(new Date()));
            endTime = convertD2S(TimeUtil.getDayEnd(new Date()));
        }
    }

    private String convertD2S(Date date) {
        return DateUtil.getDate(date, "yyyy-MM-dd HH:mm:ss");
    }
    
    private Date convertS2D(String dateStr) {
        return DateUtil.getDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public AliPayDetailsVO getAliPayDetailsVO() {
        return aliPayDetailsVO;
    }

    public void setAliPayDetailsVO(AliPayDetailsVO aliPayDetailsVO) {
        this.aliPayDetailsVO = aliPayDetailsVO;
    }

    public String getBeginTime() {
        return beginTime;
    }
    
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
    
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public List<AliPayDetailsVO> getAliPayDetailsVoList() {
        return aliPayDetailsVoList;
    }

    public void setAliPayDetailsService(AliPayDetailsService aliPayDetailsService) {
        this.aliPayDetailsService = aliPayDetailsService;
    }

    public List<PartnerVO> getPartnerVoList() {
        return partnerVoList;
    }
    
    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public int getPartnerVoListLevel() {
        return partnerVoListLevel;
    }

    public String getListType() {
        return listType;
    }

    public PayTotalVO getTotalVO() {
        return totalVO;
    }
    
    public String getQueryMinAmount() {
        return queryMinAmount;
    }

    public void setQueryMinAmount(String queryMinAmount) {
        this.queryMinAmount = queryMinAmount;
    }

}
