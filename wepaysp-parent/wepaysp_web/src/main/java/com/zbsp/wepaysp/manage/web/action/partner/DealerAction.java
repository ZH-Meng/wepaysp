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
import com.zbsp.wepaysp.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.vo.partner.DealerVO;
import com.zbsp.wepaysp.vo.partner.PartnerEmployeeVO;

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
    private List<PartnerEmployeeVO> partnerEmployeeVoList;
    private PartnerEmployeeService partnerEmployeeService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (dealerVO == null) {
                dealerVO = new DealerVO();
            }
            if ("on".equals(dealerVO.getCoreDataFlag())) {
                if (isTopPartner(manageUser)) {
                	 paramMap.put("coreDataFlag", dealerVO.getCoreDataFlag());
                	 paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                } else {
                	logger.warn("非顶级服务商（代理商）不能管理商户交易核心数据");
                	setAlertMessage("非服务商（代理商）不能管理商户交易核心数据");
                	return "accessDenied";
                }
            } else {
                if (isPartnerEmployee(manageUser)) {
                    paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
                } else if (isPartner(manageUser)) {
                	paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                } else {
                	logger.warn("非服务商（代理商）、业务员不能管理商户信息");
                	setAlertMessage("非服务商（代理商）、业务员不能管理商户信息");
                	return "accessDenied";
                }
            }

            paramMap.put("state", dealerVO.getState());
            paramMap.put("moblieNumber", dealerVO.getMoblieNumber());
            paramMap.put("loginId", dealerVO.getLoginId());
            paramMap.put("company", dealerVO.getCompany());

            // rowCount = dealerService.doJoinTransQueryDealerCount(paramMap);
            dealerVoList = dealerService.doJoinTransQueryDealerList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("商户查询列表错误：" + e.getMessage());
            setAlertMessage("商户查询列表错误！");
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
            dealerVO = new DealerVO();
            
            if (isPartnerEmployee(manageUser)) {
                dealerVO.setPartnerEmployeeOid(manageUser.getDataPartnerEmployee().getIwoid());
                dealerVO.setPartnerEmployeeName(manageUser.getDataPartnerEmployee().getEmployeeName());
            } else if (isPartner(manageUser)) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                partnerEmployeeVoList = partnerEmployeeService.doJoinTransQueryPartnerEmployeeList(paramMap, 0, -1);
                if (partnerEmployeeVoList == null || partnerEmployeeVoList.isEmpty()) {
                    logger.warn("没有业务员不能添加商户信息");
                    setAlertMessage("请先添加代理商员工（业务员）");
                }
            } else {
                logger.warn("非服务商（代理商）、业务员不能添加商户信息");
                setAlertMessage("非服务商（代理商）、业务员不能添加商户信息");
                return "accessDenied";
            }
            
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误！");
            return list();
        }
        return "createDealer";
    }
    
    public String createDealer() {
        logger.info("开始创建商户.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isPartner(manageUser) && !isPartnerEmployee(manageUser)) {
                logger.warn("创建商户失败：非服务商（代理商）、业务员不能添加商户信息");
                setAlertMessage("创建商户失败：非服务商（代理商）、业务员不能添加商户信息");
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
            setAlertMessage("商户添加错误！");
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
                        logger.warn("非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    logger.info("跳转修改商户页面.");
                    
                    if (!isPartner(manageUser) && !isPartnerEmployee(manageUser)) {
                        logger.warn("非服务商（代理商）、业务员不能修改商户信息");
                        setAlertMessage("非服务商（代理商）、业务员不能修改商户信息");
                        return "accessDenied";
                    }
                }
                
                dealerVO = dealerService.doJoinTransQueryDealerByOid(dealerVO.getIwoid());
                if (dealerVO == null) {
                	logger.warn("修改商户失败：dealerVO 未空");
                    setAlertMessage("商户不存在！");
                    return list();
                }
                dealerVO.setCoreDataFlag(coreDataFlag);
                
                // 校验被修改商户与当前用户的关系
                if (isPartner(manageUser) && !"on".equals(dealerVO.getCoreDataFlag())) {
                	if (!manageUser.getDataPartner().getIwoid().equals(dealerVO.getPartnerOid())) {
                    	logger.warn("非法操作：只能修改本代理发展的商户信息");
                        setAlertMessage("非法操作：只能修改本代理发展的商户信息");
                        return list();
                	}
                	Map<String, Object> paramMap = new HashMap<String, Object>();
                	paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                	partnerEmployeeVoList = partnerEmployeeService.doJoinTransQueryPartnerEmployeeList(paramMap, 0, -1);
                } else if (isPartnerEmployee(manageUser)) {
                	if (!manageUser.getDataPartnerEmployee().getIwoid().equals(dealerVO.getPartnerEmployeeOid())) {
                    	logger.warn("非法操作：只能修改自己发展的商户信息");
                        setAlertMessage("非法操作：只能修改自己发展的商户信息");
                        return list();
                	}
                } 
                
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
            setAlertMessage("修改商户失败！");
            dealerVO = null;
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
                logger.warn("非商户用户不能维护商户基本信息");
                setAlertMessage("非商户用户不能维护商户基本信息");
                return "accessDenied";
            }
            dealerVO = dealerService.doJoinTransQueryDealerByOid(manageUser.getDataDealer().getIwoid());
            // 重新组装需要编辑的商户基本信息（当然也不包含公众号等核心信息）
            DealerVO temp = new DealerVO();
            temp.setIwoid(dealerVO.getIwoid());
            temp.setCompany(dealerVO.getCompany());
            temp.setMoblieNumber(dealerVO.getMoblieNumber());
            temp.setQqNumber(dealerVO.getQqNumber());
            temp.setEmail(dealerVO.getEmail());
            dealerVO = temp;
        } catch (Exception e) {
            logger.error("修改商户基本信息错误：" + e.getMessage());
            setAlertMessage("修改商户基本信息错误！");
            return "error";
        }

        return "updateDealerBase";
    }

    public String updateDealerBase() {
        logger.info("开始修改商户基本信息.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isDealer(manageUser)) {
                logger.warn("非商户用户不能维护商户基本信息");
                setAlertMessage("非商户用户不能维护商户基本信息");
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
            setAlertMessage("商户基本信息修改错误！");
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
                        logger.warn("非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    if (!isPartner(manageUser) && !isPartnerEmployee(manageUser)) {
                        logger.warn("非服务商（代理商）、业务员不能修改商户信息");
                        setAlertMessage("非服务商（代理商）、业务员不能修改商户信息");
                        return "accessDenied";
                    }
                    //TODO 校验商户当前用户的关系
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
            setAlertMessage("商户修改错误！");
            return "updateDealer";
        }
        return "updateDealer";
    }
    

    /**
     * 是否是业务员
     * 
     * @return
     */
    private boolean isPartnerEmployee(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level == SysUser.UserLevel.salesman.getValue() && manageUser.getDataPartnerEmployee() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是服务商
     * 
     * @return
     */
    private boolean isPartner(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level == SysUser.UserLevel.partner.getValue() && manageUser.getDataPartner() != null) {
                return true;
            }
        }
        return false;
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
            if (level == SysUser.UserLevel.dealer.getValue() && manageUser.getDataDealer() != null) {
                return true;
            }
        }
        return false;
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

    public List<PartnerEmployeeVO> getPartnerEmployeeVoList() {
        return partnerEmployeeVoList;
    }

    public void setPartnerEmployeeService(PartnerEmployeeService partnerEmployeeService) {
        this.partnerEmployeeService = partnerEmployeeService;
    }

}