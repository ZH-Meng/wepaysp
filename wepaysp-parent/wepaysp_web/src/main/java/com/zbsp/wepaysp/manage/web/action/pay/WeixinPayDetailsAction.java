package com.zbsp.wepaysp.manage.web.action.pay;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信交易明细
 * 
 * @author 孟郑宏
 */
public class WeixinPayDetailsAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -4213656644621035327L;
    private Map<String, Object> session;
    private WeixinPayDetailsVO weixinPayDetailsVO;
    private String beginTime;
    private String endTime;
    private List<WeixinPayDetailsVO> weixinPayDetailsVoList;
    private WeixinPayDetailsService weixinPayDetailsService;
    private List<PartnerVO> partnerVoList;
    private PartnerService partnerService;
    private int userLevel;
    private int partnerVoListLevel;
    private String listType;
    private String maxQueryTime;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Date beforeDay = TimeUtil.getLastTimeUnit(new Date(), Calendar.DAY_OF_MONTH);
        
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (weixinPayDetailsVO == null) {
                weixinPayDetailsVO = new WeixinPayDetailsVO();
            }
            
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
                            paramMap.put("partner2Oid", weixinPayDetailsVO.getPartner2Oid());
                        } else if (partnerLevel == 2) {// 二级服务商
                            partnerVoListLevel = 3;
                            
                            // 获取三级服务商集合
                            paramMap.put("parentPartnerOid", manageUser.getDataPartner().getIwoid());
                            partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, 0, -1);
                            // 当前台查询条件选择某个三级服务商
                            paramMap.clear();
                            paramMap.put("partner3Oid", weixinPayDetailsVO.getPartner3Oid());
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
            }

            if (!flag) {
                logger.warn("当前用户无权查看交易明细！");
                setAlertMessage("当前用户无权查看交易明细！");
                return "accessDenied";
            }
            // 用户级别，页面根据级别动态展示查询条件以及结果列表
            userLevel = manageUser.getUserLevel();
            
            weixinPayDetailsVO.setBeginTime(convertS2D(beginTime));
            weixinPayDetailsVO.setEndTime(convertS2D(endTime));
            paramMap.put("beginTime", weixinPayDetailsVO.getBeginTime());
            paramMap.put("endTime", weixinPayDetailsVO.getEndTime());
            
            paramMap.put("partnerEmployeeId", weixinPayDetailsVO.getPartnerEmployeeId());
            paramMap.put("dealerId", weixinPayDetailsVO.getDealerId());
            paramMap.put("dealerEmployeeId", weixinPayDetailsVO.getDealerEmployeeId());
            paramMap.put("storeId", weixinPayDetailsVO.getStoreId());
            
            if (weixinPayDetailsVO.getEndTime() != null && TimeUtil.timeAfter(weixinPayDetailsVO.getEndTime(), beforeDay)) {
                logger.warn("最大查询日期不能大于前一天");
                setAlertMessage("最大查询日期不能大于前一天");
            }
            weixinPayDetailsVoList = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsList(paramMap, start, size);
            rowCount = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsCount(paramMap);
            maxQueryTime = DateUtil.getDate(beforeDay, "yyyy-MM-dd");
            if (StringUtils.isBlank(endTime)) {
                endTime = maxQueryTime;
            }
        } catch (Exception e) {
            logger.error("微信交易明细查询列表错误：" + e.getMessage());
            setAlertMessage("微信交易明细查询列表错误：" + e.getMessage());
        }
        
        return "weixinPayDetailsList";
    }

    /**
     * 服务商、业务员访问
     * 
     * @return
     */
    public String list() {
        initPageData(100);
        listType = "partner";
        return goCurrent();
    }
    
    /**
     * 商户、收银员访问
     * 
     * @return
     */
    public String listForDealer() {
        initPageData(100);
        listType = "dealer";
        return goCurrent();
    }

    private Date convertS2D(String dateStr) {
        return DateUtil.getDate(dateStr, "yyyy-MM-dd");
    }

    private String convertD2S(Date date) {
        return DateUtil.getDate(date, "yyyy-MM-dd");
    }
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public WeixinPayDetailsVO getWeixinPayDetailsVO() {
        return weixinPayDetailsVO;
    }

    public void setWeixinPayDetailsVO(WeixinPayDetailsVO weixinPayDetailsVO) {
        this.weixinPayDetailsVO = weixinPayDetailsVO;
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
    
    public List<WeixinPayDetailsVO> getWeixinPayDetailsVoList() {
        return weixinPayDetailsVoList;
    }

    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
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

    public String getMaxQueryTime() {
        return maxQueryTime;
    }

}
