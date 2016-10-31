package com.zbsp.wepaysp.manage.web.action.pay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private List<WeixinPayDetailsVO> weixinPayDetailsVoList;
    private WeixinPayDetailsService weixinPayDetailsService;
    private List<PartnerVO> partnerVoList;
    private PartnerService partnerService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (weixinPayDetailsVO == null) {
                weixinPayDetailsVO = new WeixinPayDetailsVO();
            }

            boolean flag = false;
            // 一级服务商、二级服务商
            // 获取子服务商列表

            if (manageUser.getUserLevel() == SysUser.UserLevel.partner.getValue()) {// 服务商
                if (manageUser.getDataPartner() != null) {
                    int partnerLevel = manageUser.getDataPartner().getLevel();
                    if (partnerLevel == 1 || partnerLevel == 2 || partnerLevel == 3) {
                        if (partnerLevel != 3) {
                            paramMap.clear();
                            paramMap.put("parentPartnerOid", manageUser.getDataPartner().getIwoid());
                            partnerService.doJoinTransQueryPartnerList(paramMap, 0, -1);
                        }
                        paramMap.clear();
                        paramMap.put("partner" + partnerLevel + "Oid", manageUser.getDataPartner().getIwoid());
                        flag = true;
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.dealer.getValue()) {// 商户
                if (manageUser.getDataDealer() != null) {
                    paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                    flag = true;
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.salesman.getValue()) {// 业务员
                if (manageUser.getDataPartnerEmployee() != null) {
                    paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
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

            weixinPayDetailsVoList = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsList(paramMap, start, size);
            rowCount = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsCount(paramMap);
        } catch (Exception e) {
            logger.error("微信交易明细查询列表错误：" + e.getMessage());
            setAlertMessage("微信交易明细查询列表错误：" + e.getMessage());
        }

        return "weixinPayDetailsList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
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

}
