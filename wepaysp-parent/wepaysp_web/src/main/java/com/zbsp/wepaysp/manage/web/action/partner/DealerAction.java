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
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.partner.DealerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;

/**
 * 商户管理
 * 
 * @author mengzh
 *
 */
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
            if ("on".equals(dealerVO.getCoreDataFlag())) {
                if (!isTopPartner(manageUser)) {
                    logger.warn("角色分配不当：非顶级服务商（代理商）不能管理商户交易核心数据");
                    setAlertMessage("角色分配不当：非服务商（代理商）不能管理商户交易核心数据");
                    return "accessDenied";
                }
            } else {
                if (!isPartner(manageUser)) {
                    logger.warn("角色分配不当：非服务商（代理商）不能管理商户信息");
                    setAlertMessage("角色分配不当：非服务商（代理商）不能管理商户信息");
                    return "accessDenied";
                }
            }

            paramMap.put("state", dealerVO.getState());
            paramMap.put("moblieNumber", dealerVO.getMoblieNumber());
            paramMap.put("loginId", dealerVO.getLoginId());
            paramMap.put("company", dealerVO.getCompany());

            // paramMap.put("partnerOid", dealerVO.getPartnerOid());
            paramMap.put("currentUserOid", manageUser.getIwoid());
            paramMap.put("coreDataFlag", dealerVO.getCoreDataFlag());

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

    /**
     * 商户交易核心数据管理
     * 
     * @return
     */
    public String transCoreDataList() {
        initPageData(100);
        if (dealerVO == null) {
            dealerVO = new DealerVO();
        }
        dealerVO.setCoreDataFlag("on");
        return goCurrent();
    }

    public String goToCreateDealer() {
        logger.info("跳转创建商户页面.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isPartner(manageUser)) {
                logger.warn("角色分配不当：非服务商（代理商）不能创建商户");
                setAlertMessage("角色分配不当：非服务商（代理商）不能创建商户");
                return "accessDenied";
            }
            
            if (dealerVO == null) {
                dealerVO = new DealerVO();
            }
/*            if (manageUser.getDataPartner() != null) {
                dealerVO.setPartnerOid(manageUser.getDataPartner().getIwoid());
            }*/
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误：" + e.getMessage());
            return list();
        }
        return "createDealer";
    }
    
    public String createDealer() {
        logger.info("开始创建商户.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isPartner(manageUser)) {
                logger.warn("创建商户失败：非服务商（代理商）不能创建商户");
                setAlertMessage("创建商户失败：非服务商（代理商）不能创建商户");
                return "accessDenied";
            }

            if (dealerVO == null) {
                logger.warn("创建商户失败，参数" + dealerVO + "为空！");
                setAlertMessage("创建商户失败！");
            }

            dealerService.doTransAddDealer(dealerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("商户" + dealerVO.getCompany() + "添加成功");
            setAlertMessage("商户" + dealerVO.getCompany() + "添加成功");
            dealerVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("商户添加失败：" + e.getMessage());
            setAlertMessage("商户添加失败：" + e.getMessage());
            return "createDealer";
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误：" + e.getMessage());
            return "createDealer";
        }
        return list();
    }

    /**
     * 修改商户信息，dealerVO.getCoreDataFlag() = "on" 时为顶级服务商修改，其他为普通修改商户信息
     * 
     * @return
     */
    public String goToUpdateDealer() {
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String coreDataFlag = "off";
            if (dealerVO != null && StringUtils.isNotBlank(dealerVO.getIwoid())) {
                if ("on".equals(dealerVO.getCoreDataFlag())) {// 顶级服务商权限
                	coreDataFlag = "on";
                    logger.info("跳转修改商户页面-含交易核心数据.");
                    if (!isTopPartner(manageUser)) {
                        logger.warn("角色分配不当：非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("角色分配不当：非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    logger.info("跳转修改商户页面.");
                    if (!isPartner(manageUser)) {
                        logger.warn("角色分配不当：非服务商（代理商）不能修改商户信息");
                        setAlertMessage("角色分配不当：非服务商（代理商）不能修改商户信息");
                        return "accessDenied";
                    }
                }
                
                dealerVO = dealerService.doJoinTransQueryDealerByOid(dealerVO.getIwoid());
                dealerVO.setCoreDataFlag(coreDataFlag);
                if (!"on".equals(coreDataFlag)) {
                    dealerVO.setSubAppid(null);
                    dealerVO.setSubMchId(null);                    
                }
            } else {
                logger.warn("非法修改商户，参数dealerVO或者dealerVO.getIwoid()为空！");
                setAlertMessage("非法修改商户！");
                return list();
            }
        } catch (Exception e) {
            logger.error("修改商户失败：" + e.getMessage());
            setAlertMessage("修改商户失败：" + e.getMessage());
            return list();
        }
        return "updateDealer";
    }

    /**
     * 商户交易核心数据修改 -顶级服务商权限
     * 
     * @return
     */
    public String goToUpdateDealerCore() {
        if (dealerVO == null) {
            dealerVO = new DealerVO();
        }
        dealerVO.setCoreDataFlag("on");
        return goToUpdateDealer();
    }
    
    
    /**
     * 商户基本信息维护 -商户权限
     * 
     * @return
     */
    public String goToUpdateDealerBase() {
        logger.info("跳转修改商户页面-基本信息维护.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isDealer(manageUser)) {
                logger.warn("角色分配不当：非商户用户不能维护商户基本信息");
                setAlertMessage("角色分配不当：非商户用户不能维护商户基本信息");
                return "accessDenied";
            }
            if (manageUser.getDataDealer() != null) {
                dealerVO = dealerService.doJoinTransQueryDealerByOid(manageUser.getDataDealer().getIwoid());
                // 重新组装需要编辑的商户基本信息（当然也不包含公众号等核心信息）
                DealerVO temp = new DealerVO();
                temp.setIwoid(dealerVO.getIwoid());
                temp.setCompany(dealerVO.getCompany());
                temp.setMoblieNumber(dealerVO.getMoblieNumber());
                temp.setQqNumber(dealerVO.getQqNumber());
                temp.setEmail(dealerVO.getEmail());
                dealerVO = temp;
            } else {
                logger.warn("当前用户未关联商户，无法修改商户基本信息！");
                setAlertMessage("当前用户未关联商户，无法修改商户基本信息！");
                return "error";
            }
        } catch (Exception e) {
            logger.error("修改商户基本信息错误：" + e.getMessage());
            setAlertMessage("修改商户基本信息错误：" + e.getMessage());
            return "error";
        }

        return "updateDealerBase";
    }

    public String updateDealerBase() {
        logger.info("开始修改商户基本信息.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isDealer(manageUser)) {
                logger.warn("角色分配不当：非商户用户不能维护商户基本信息");
                setAlertMessage("角色分配不当：非商户用户不能维护商户基本信息");
                return "accessDenied";
            }
            if (dealerVO != null && StringUtils.isNotBlank(dealerVO.getIwoid())) {
                dealerService.doTransUpdateDealerBase(dealerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("商户基本信息修改成功");
                setAlertMessage("商户基本信息修改成功");
            } else {
                logger.warn("非法修改商户基本信息，参数dealerVO或者dealerVO.getIwoid()为空！");
                setAlertMessage("非法修改商户基本信息！");
            }
        } catch (NotExistsException e) {
            logger.error("商户基本信息修改失败：" + e.getMessage());
            setAlertMessage("商户基本信息修改失败：" + e.getMessage());
            return "error";
        } catch (Exception e) {
            logger.error("商户基本信息修改错误：" + e.getMessage());
            setAlertMessage("商户基本信息修改错误：" + e.getMessage());
            return "error";
        }
        return "updateDealerBase";
    }
    
    public String updateDealer() {
        logger.info("开始修改商户.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (dealerVO != null && StringUtils.isNotBlank(dealerVO.getIwoid())) {
                if ("on".equals(dealerVO.getCoreDataFlag())) {// 顶级服务商权限
                    if (!isTopPartner(manageUser)) {
                        logger.warn("角色分配不当：非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("角色分配不当：非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    if (!isPartner(manageUser)) {
                        logger.warn("角色分配不当：非服务商（代理商）不能修改商户信息");
                        setAlertMessage("角色分配不当：非服务商（代理商）不能修改商户信息");
                        return "accessDenied";
                    }
                }
                
                dealerService.doTransUpdateDealer(dealerVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("商户" + dealerVO.getCompany() + "修改成功");
                setAlertMessage("商户" + dealerVO.getCompany() + "修改成功");
            } else {
                logger.warn("修改商户失败，参数dealerVO或者dealerVO.getIwoid()为空！");
                setAlertMessage("修改商户失败！");
            }
        } catch (NotExistsException e) {
            logger.error("商户修改失败：" + e.getMessage());
            setAlertMessage("商户修改失败：" + e.getMessage());
            return list();
        } catch (Exception e) {
            logger.error("商户修改错误：" + e.getMessage());
            setAlertMessage("商户修改错误：" + e.getMessage());
            return "updateDealer";
        }
        return "updateDealer";
    }

    /**
     * 是否是服务商，角色权限校验通过，如果给商户角色配置了服务商的菜单仍不能使用
     * 
     * @return
     */
    private boolean isPartner(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level > SysUser.UserLevel.partner.getValue()) {// 非服务商
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是顶级服务商，角色权限校验通过，如果给其他角色配置了一级服务商的菜单仍不能使用
     * 
     * @return
     */
    private boolean isTopPartner(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level == SysUser.UserLevel.partner.getValue() && manageUser.getDataPartner() != null && manageUser.getDataPartner().getLevel() == 1) {// 顶级服务商
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是商户
     * 
     * @return
     */
    private boolean isDealer(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.dealer.getValue()) {// 非商户
                return false;
            }
        }
        return true;
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
