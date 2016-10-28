package com.zbsp.wepaysp.manage.web.action.partner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -7078956274536886116L;

    private Map<String, Object> session;
    private PartnerVO partnerVO;
    private String contractBegin = "";
    private String contractEnd = "";
    private List<PartnerVO> partnerVoList;
    private PartnerService partnerService;
    private String isChildPage;// 子代理商列表页面：1，子代理的子代理列表页面为孙子代理列表页面：2
    private String returnParentFlag;// 子代理商列表是否可以返回上级 on页面显示返回上级按钮，off则不显示
    private String returnParentPartnerOid;// returnParentFlag='on'时起作用，返回上级的父代理商oid
    private String doReturnParent;// 返回上级动作参数，yes表示返回上级

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (partnerVO == null) {
                partnerVO = new PartnerVO();
            }
            paramMap.put("state", partnerVO.getState());
            paramMap.put("loginId", partnerVO.getLoginId());
            paramMap.put("contactor", partnerVO.getContactor());
            paramMap.put("company", partnerVO.getCompany());

            paramMap.put("parentPartnerOid", partnerVO.getParentPartnerOid());
            paramMap.put("currentUserOid", manageUser.getIwoid());

            if ("yes".equals(doReturnParent)) {// 返回上级列表
                paramMap.put("parentPartnerOid", returnParentPartnerOid);
            }

            String parentPOid = MapUtils.getString(paramMap, "parentPartnerOid");
            if (StringUtils.isBlank(parentPOid)) {// 初始页面
                isChildPage = "1";
            } else {
                PartnerVO parentPartner = partnerService.doJoinTransQueryPartnerByOid(parentPOid);
                Partner userPartner = manageUser.getDataPartner();
                if (parentPartner.getIwoid().equals(userPartner.getIwoid())) {// 默认页面
                    isChildPage = "1";
                    returnParentFlag = "off";
                } else if (parentPartner.getLevel().intValue() > userPartner.getLevel().intValue()) {// 非当前代理的子代理商列表页面，level 越大级别越小
                    isChildPage = "2";
                    returnParentFlag = "on";
                    returnParentPartnerOid = parentPartner.getParentPartnerOid();
                } else {
                    throw new IllegalAccessException("不能查询级别比自己高的代理商列表！");
                }
                partnerVO.setParentPartnerOid(parentPartner.getIwoid());
            }

            partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, start, size);
            rowCount = partnerService.doJoinTransQueryPartnerCount(paramMap);
        } catch (Exception e) {
            logger.error("代理商查询列表错误：" + e.getMessage());
            setAlertMessage("代理商查询列表错误：" + e.getMessage());
        }
        return "partnerList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToCreatePartner() {
        logger.info("跳转创建代理商页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (partnerVO == null) {
            partnerVO = new PartnerVO();
        }
        if (manageUser.getDataPartner() != null) {
            partnerVO.setParentPartnerOid(manageUser.getDataPartner().getIwoid());
        }
        return "createPartner";
    }

    public String createPartner() {
        logger.info("开始创建代理商.");
        try {
            if (partnerVO == null) {
                logger.warn("创建代理商失败，参数" + partnerVO + "为空！");
                setAlertMessage("创建代理商失败！");
            }
            partnerVO.setContractBegin(convertS2D(contractBegin));
            partnerVO.setContractEnd(convertS2D(contractEnd));

            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            partnerService.doTransAddPartner(partnerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("代理商" + partnerVO.getCompany() + "添加成功");
            setAlertMessage("代理商" + partnerVO.getCompany() + "添加成功");
            partnerVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("代理商添加失败：" + e.getMessage());
            setAlertMessage("代理商添加失败：" + e.getMessage());
            return "createPartner";
        } catch (Exception e) {
            logger.error("代理商添加错误：" + e.getMessage());
            setAlertMessage("代理商添加错误：" + e.getMessage());
            return "createPartner";
        }
        return list();
    }

    public String goToUpdatePartner() {
        logger.info("跳转修改代理商页面.");
        if (partnerVO != null && StringUtils.isNotBlank(partnerVO.getIwoid())) {
            partnerVO = partnerService.doJoinTransQueryPartnerByOid(partnerVO.getIwoid());
            contractBegin = convertD2S(partnerVO.getContractBegin());
            contractEnd = convertD2S(partnerVO.getContractEnd());
        } else {
            logger.warn("非法修改代理商，参数partnerVO为空，partnerVO.getIwoid()或者！");
            setAlertMessage("非法修改代理商！");
            return list();
        }
        return "updatePartner";
    }

    public String updatePartner() {
        logger.info("开始修改代理商.");
        try {
            if (partnerVO != null && StringUtils.isNotBlank(partnerVO.getIwoid())) {
                partnerVO.setContractBegin(convertS2D(contractBegin));
                partnerVO.setContractEnd(convertS2D(contractEnd));
                
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                partnerService.doTransUpdatePartner(partnerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("代理商" + partnerVO.getCompany() + "修改成功");
                setAlertMessage("代理商" + partnerVO.getCompany() + "修改成功");
                partnerVO = null;
            } else {
                logger.warn("修改代理商失败，参数partnerVO或者partnerVO.getIwoid()为空！");
                setAlertMessage("修改代理商失败！");
            }
        } catch (NotExistsException e) {
            logger.error("代理商修改失败：" + e.getMessage());
            setAlertMessage("代理商修改失败：" + e.getMessage());
            return list();
        } catch (Exception e) {
            logger.error("代理商修改错误：" + e.getMessage());
            setAlertMessage("代理商修改错误：" + e.getMessage());
            return "updatePartner";
        }
        return "updatePartner";
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

    public PartnerVO getPartnerVO() {
        return partnerVO;
    }

    public void setPartnerVO(PartnerVO partnerVO) {
        this.partnerVO = partnerVO;
    }

    public String getContractBegin() {
        return contractBegin;
    }

    public void setContractBegin(String contractBegin) {
        this.contractBegin = contractBegin;
    }

    public String getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(String contractEnd) {
        this.contractEnd = contractEnd;
    }

    public List<PartnerVO> getPartnerVoList() {
        return partnerVoList;
    }

    public void setPartnerVoList(List<PartnerVO> partnerVoList) {
        this.partnerVoList = partnerVoList;
    }

    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    public String getIsChildPage() {
        return isChildPage;
    }

    public String getReturnParentFlag() {
        return returnParentFlag;
    }

    public String getReturnParentPartnerOid() {
        return returnParentPartnerOid;
    }

    public void setReturnParentPartnerOid(String returnParentPartnerOid) {
        this.returnParentPartnerOid = returnParentPartnerOid;
    }

    public void setDoReturnParent(String doReturnParent) {
        this.doReturnParent = doReturnParent;
    }

}
