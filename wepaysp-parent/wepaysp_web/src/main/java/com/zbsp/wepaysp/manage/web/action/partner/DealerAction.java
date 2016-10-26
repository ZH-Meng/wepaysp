package com.zbsp.wepaysp.manage.web.action.partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.service.partner.DealerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;

public class DealerAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -7078956274536886116L;

    private Map<String, Object> session;
    private DealerVO dealerVO;
    private List<DealerVO> dealerVoList;
    private DealerService dealerService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (dealerVO == null) {
                dealerVO = new DealerVO();
            }
            paramMap.put("state", dealerVO.getState());
            paramMap.put("loginId", dealerVO.getLoginId());
            paramMap.put("company", dealerVO.getCompany());

            //paramMap.put("partnerOid", dealerVO.getPartnerOid());
            paramMap.put("currentUserOid", manageUser.getIwoid());

            dealerVoList = dealerService.doJoinTransQueryDealerList(paramMap, start, size);
            rowCount = dealerService.doJoinTransQueryDealerCount(paramMap);
        } catch (Exception e) {
            logger.error("商户查询列表错误：" + e.getMessage());
            setAlertMessage("商户查询列表错误：" + e.getMessage());
        }
        return "dealerList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToCreateDealer() {
        logger.info("跳转创建商户页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (dealerVO == null) {
            dealerVO = new DealerVO();
        }
        if (manageUser.getDataPartner() != null) {
            dealerVO.setPartnerOid(manageUser.getDataPartner().getIwoid());
        }
        return "createDealer";
    }

    public String createDealer() {
        logger.info("开始创建商户.");
        try {
            if (dealerVO == null) {
                logger.warn("创建商户失败，参数" + dealerVO + "为空！");
                setAlertMessage("创建商户失败！");
            }

            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dealerService.doTransAddDealer(dealerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("商户" + dealerVO.getCompany() + "添加成功");
            setAlertMessage("商户" + dealerVO.getCompany() + "添加成功");
            dealerVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("商户添加失败：" + e.getMessage());
            setAlertMessage("商户添加失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误：" + e.getMessage());
        }
        return list();
    }

    public String goToUpdateDealer() {
        logger.info("跳转修改商户页面.");
        if (dealerVO != null && StringUtils.isNotBlank(dealerVO.getIwoid())) {
            dealerVO = dealerService.doJoinTransQueryDealerByOid(dealerVO.getIwoid());
        } else {
            logger.warn("非法修改商户，参数dealerVO为空，dealerVO.getIwoid()或者！");
            setAlertMessage("非法修改商户！");
        }
        return "updateDealer";
    }

    public String updateDealer() {
        logger.info("开始修改商户.");
        try {
            if (dealerVO == null) {
                logger.warn("修改商户失败，参数" + dealerVO + "为空！");
                setAlertMessage("修改商户失败！");
            }

            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dealerService.doTransUpdateDealer(dealerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("商户" + dealerVO.getCompany() + "修改成功");
            setAlertMessage("商户" + dealerVO.getCompany() + "修改成功");
            dealerVO = null;
        } catch (NotExistsException e) {
            logger.error("商户修改失败：" + e.getMessage());
            setAlertMessage("商户修改失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误：" + e.getMessage());
        }
        return list();
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public DealerVO getDealerVO() {
        return dealerVO;
    }

    public void setDealerVO(DealerVO dealerVO) {
        this.dealerVO = dealerVO;
    }

    public List<DealerVO> getDealerVoList() {
        return dealerVoList;
    }

    public void setDealerVoList(List<DealerVO> dealerVoList) {
        this.dealerVoList = dealerVoList;
    }

    public void setDealerService(DealerService dealerService) {
        this.dealerService = dealerService;
    }

}
