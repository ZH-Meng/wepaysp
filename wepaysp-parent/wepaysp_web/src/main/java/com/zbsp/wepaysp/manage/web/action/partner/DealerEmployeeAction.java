package com.zbsp.wepaysp.manage.web.action.partner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;

/**
 * 商户员工（收银员）管理
 * 
 * @author mengzh
 *
 */
public class DealerEmployeeAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 3725169645580498305L;
    private Map<String, Object> session;
    private DealerEmployeeVO dealerEmployeeVO;
    private List<DealerEmployeeVO> dealerEmployeeVoList;
    private DealerEmployeeService dealerEmployeeService;
    private StoreService storeService;
    private List<StoreVO> storeVoList;
    private String resetFlag;
    private String qRCodeName;
    private String dealerEmployeeOid;
    private String storeOid;
    
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (dealerEmployeeVO == null) {
                dealerEmployeeVO = new DealerEmployeeVO();
            }
            paramMap.put("employeeName", dealerEmployeeVO.getEmployeeName());
            paramMap.put("moblieNumber", dealerEmployeeVO.getMoblieNumber());
            
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
           
            // 只有商户可以重置退款权限密码
            //if (!checkUser(manageUser, "yes".equals(resetFlag) ? "reset" :"query")) {
            if ("yes".equals(resetFlag)) {
                if (!checkUser(manageUser, "reset")) {
                    return "accessDenied";
                }
            } else {
                // 门店页面查看商户员工，用户级别限制屏蔽
                if (StringUtils.isNotBlank(storeOid)) {
                    paramMap.put("storeOid", storeOid);
                } else if (!checkUser(manageUser, "query")) {
                    return "accessDenied";
                }
            }
            
            if (isDealer(manageUser)) {
                paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
            } else if (isStoreManager(manageUser)) {
                //paramMap.put("dealerEmployeeOid", manageUser.getDataDealerEmployee().getIwoid());
            	paramMap.put("storeOid", manageUser.getDataDealerEmployee().getStore().getIwoid());
            }
            
            //rowCount = dealerEmployeeService.doJoinTransQueryDealerEmployeeCount(paramMap);
            dealerEmployeeVoList = dealerEmployeeService.doJoinTransQueryDealerEmployeeList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("商户员工查询列表错误：" + e.getMessage());
            setAlertMessage("商户员工查询列表错误！");
        }
        return "dealerEmployeeList";
    }

    /**
     * 商户可以查看自己的商户员工，商户员工可以查看所属商户下的商户员工
     * 
     * @return
     */
    public String list() {
        initPageData(PageAction.defaultSmallPageSize);
        return goCurrent();
    }

    public String goToCreateDealerEmployee() {
        logger.info("跳转添加商户员工页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!checkUser(manageUser, "add")) {
			return "accessDenied";
        }
		// 获取当前用户关联的商户下所有门店
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
		storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
		dealerEmployeeVO = null;
        return "createDealerEmployee";
    }

    public String createDealerEmployee() {
        logger.info("开始添加商户员工.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
    		if (!checkUser(manageUser, "add")) {
    			return "accessDenied";
            }

            if (dealerEmployeeVO == null) {
                logger.warn("添加商户员工失败，参数" + dealerEmployeeVO + "为空！");
                setAlertMessage("添加商户员工失败！");
            }

            dealerEmployeeVO.setDealerOid(manageUser.getDataDealer().getIwoid());
            dealerEmployeeService.doTransAddDealerEmployee(dealerEmployeeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("商户员工" + dealerEmployeeVO.getEmployeeName() + "添加成功");
            setAlertMessage("商户员工" + dealerEmployeeVO.getEmployeeName() + "添加成功");
            dealerEmployeeVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("商户员工添加失败：" + e.getMessage());
            setAlertMessage("商户员工添加失败：" + e.getMessage());
            // 获取当前用户关联的商户下所有门店
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
            storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
            return "createDealerEmployee";
        } catch (NotExistsException e) {
            logger.error("商户员工添加失败：" + e.getMessage());
            setAlertMessage("商户员工添加失败：" + e.getMessage());
            // 获取当前用户关联的商户下所有门店
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
            storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
            return "createDealerEmployee";
        } catch (Exception e) {
            logger.error("商户员工添加错误：" + e.getMessage());
            setAlertMessage("商户员工添加错误！");
            return "error";
        }
        return list();
    }

    public String goToUpdateDealerEmployee() {
        logger.info("跳转修改商户员工页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
		if (!checkUser(manageUser, "update")) {
			return "accessDenied";
        }
        if (dealerEmployeeVO != null && StringUtils.isNotBlank(dealerEmployeeVO.getIwoid())) {
            dealerEmployeeVO = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(dealerEmployeeVO.getIwoid());
            // 获取当前用户关联的商户下所有门店
    		Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
    		storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
        } else {
            logger.warn("非法修改商户员工，参数dealerEmployeeVO为空，dealerEmployeeVO.getIwoid()或者！");
            setAlertMessage("非法修改商户员工！");
            return list();
        }
        return "updateDealerEmployee";
    }

    public String updateDealerEmployee() {
        logger.info("开始修改商户员工.");
        try {
            if (dealerEmployeeVO != null && StringUtils.isNotBlank(dealerEmployeeVO.getIwoid())) {
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        		if (!checkUser(manageUser, "update")) {
        			return "accessDenied";
                }

                dealerEmployeeService.doTransUpdateDealerEmployee(dealerEmployeeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("商户员工" + dealerEmployeeVO.getEmployeeName() + "修改成功");
                setAlertMessage("商户员工" + dealerEmployeeVO.getEmployeeName() + "修改成功");
                dealerEmployeeVO = null;
            } else {
                logger.warn("修改商户员工失败，参数dealerEmployeeVO或者dealerEmployeeVO.getIwoid()为空！");
                setAlertMessage("修改商户员工失败！");
                return list();
            }
        } catch (AlreadyExistsException e) {
            logger.error("商户员工修改失败：" + e.getMessage());
            setAlertMessage("商户员工修改失败：" + e.getMessage());
            dealerEmployeeVO = null;
            return list();
        } catch (NotExistsException e) {
            logger.error("商户员工修改失败：" + e.getMessage());
            setAlertMessage("商户员工修改失败：" + e.getMessage());
            dealerEmployeeVO = null;
            return list();
        } catch (Exception e) {
            logger.error("商户员工修改错误：" + e.getMessage());
            setAlertMessage("商户员工修改错误！");
            return "error";
        }
        return list();
    }
    
    public String resetRefundList() {
        initPageData(100);
        resetFlag = "yes";
        return goCurrent();
    }
    
    public String goToResetRefundPwd() {
        logger.info("跳转重置商户员工退款权限密码页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
		if (!checkUser(manageUser, "reset")) {
			return "accessDenied";
        }
        if (dealerEmployeeVO != null && StringUtils.isNotBlank(dealerEmployeeVO.getIwoid())) {
            dealerEmployeeVO = dealerEmployeeService.doJoinTransQueryDealerEmployeeByOid(dealerEmployeeVO.getIwoid());
        } else {
            logger.warn("非法重置商户员工退款权限密码，参数dealerEmployeeVO为空，dealerEmployeeVO.getIwoid()或者！");
            setAlertMessage("非法重置商户员工退款权限密码！");
            return list();
        }
        return "resetRefundPwd";
    }
    
    public String resetRefundPwd() {
    	 logger.info("开始重置商户员工退款权限密码.");
         try {
             if (dealerEmployeeVO != null && StringUtils.isNotBlank(dealerEmployeeVO.getIwoid())) {
                 ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         		if (!checkUser(manageUser, "reset")) {
         			return "accessDenied";
                 }

         		dealerEmployeeVO = dealerEmployeeService.doTransResetRefundPwd(dealerEmployeeVO.getIwoid(), dealerEmployeeVO.getRefundPassword(), manageUser.getIwoid(), manageUser.getUserId(), (String) session.get("currentLogFunctionOid"));
                 logger.info("重置商户员工" + dealerEmployeeVO.getEmployeeName() + "退款权限密码成功");
                 setAlertMessage("重置商户员工" + dealerEmployeeVO.getEmployeeName() + "退款权限密码成功");
                 dealerEmployeeVO = null;
             } else {
                 logger.warn("重置商户员工退款权限密码失败，参数dealerEmployeeVO或者dealerEmployeeVO.getIwoid()为空！");
                 setAlertMessage("重置商户员工退款权限密码失败！");
                 return list();
             }
         } catch (NotExistsException e) {
             logger.error("重置商户员工退款权限密码失败：" + e.getMessage());
             setAlertMessage("重置商户员工退款权限密码失败：" + e.getMessage());
             return list();
         } catch (Exception e) {
             logger.error("重置商户员工退款权限密码错误：" + e.getMessage());
             setAlertMessage("重置商户员工退款权限密码错误！");
             return "error";
         }
         return list();
    }
    
    public String goModifyRefundPwd() {
        logger.info("跳转修改商户员工退款权限密码页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
		if (!checkUser(manageUser, "modifyRefundPwd")) {
			return "accessDenied";
        }
		dealerEmployeeVO = new DealerEmployeeVO();
		dealerEmployeeVO.setIwoid(manageUser.getDataDealerEmployee().getIwoid());
		
        return "modifyRefundPwd";
    }
    
    public String modifyRefundPwd() {
   	 logger.info("开始修改商户员工退款权限密码.");
        try {
            if (dealerEmployeeVO != null && StringUtils.isNotBlank(dealerEmployeeVO.getIwoid())) {
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        		if (!checkUser(manageUser, "modifyRefundPwd")) {
        			return "accessDenied";
                }
        		dealerEmployeeVO = dealerEmployeeService.doTransModifyRefundPwd(dealerEmployeeVO.getIwoid(), dealerEmployeeVO.getOldRefundPassword(), dealerEmployeeVO.getRefundPassword(),  manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("商户员工" + dealerEmployeeVO.getEmployeeName() + "修改退款权限密码成功");
                setAlertMessage("商户员工" + dealerEmployeeVO.getEmployeeName() + "修改退款权限密码成功");
            } else {
                logger.warn("修改商户员工退款权限密码失败，参数dealerEmployeeVO或者dealerEmployeeVO.getIwoid()为空！");
                setAlertMessage("修改商户员工退款权限密码失败！");
                return list();
            }
        } catch (NotExistsException e) {
            logger.error("修改商户员工退款权限密码失败：" + e.getMessage());
            setAlertMessage("修改商户员工退款权限密码失败：" + e.getMessage());
            return list();
        } catch (Exception e) {
            logger.error("修改商户员工退款权限密码错误：" + e.getMessage());
            setAlertMessage("修改商户员工退款权限密码错误！");
            return "error";
        }
        return "modifyRefundPwd";
   }
    
    /**
     * 下载收银员级别支付二维码图片<br>
     * <pre>
     * 校验权限；
     * 判断此收银员是否生成过二维码，且二维码文件是否存在；
     * 如果二维码不存在则生成二维码图片
     * <pre>
     * @return
     */
    public String downloadPayQRCode() {
        logger.info("下载收银员级别支付二维码图片.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 代理商、业务员、商户、店长能下载收银员级别二维码
            if (isPartner(manageUser) || isPartnerEmployee(manageUser) || isDealer(manageUser) || isStoreManager(manageUser))  {
                if (StringUtils.isNotBlank(dealerEmployeeOid)) {
                	dealerEmployeeVO = dealerEmployeeService.doTransGetPayQRCode(dealerEmployeeOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                } else {
                    logger.warn("非法下载收银员级别支付二维码图片，参数dealerEmployeeOid为空！");
                    setAlertMessage("下载收银员级别支付二维码图片失败！");
                    return list();
                }
            } else {
                logger.warn("无权下载收银员级别支付二维码");
                setAlertMessage("无权下载收银员级别支付二维码");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("下载收银员级别支付二维码错误：" + e.getMessage());
            setAlertMessage("下载收银员级别支付二维码错误！");
            return list();
        }
        return "getQRCodeImg";
    }
    
    public InputStream getQRCodeImg() {
        InputStream inputStream = null;
        try {
            File qrFile = new File(dealerEmployeeVO.getQrCodePath());
            inputStream = new FileInputStream(qrFile);
            qRCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
            logger.info("下载收银员级别支付二维码图片成功.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    public boolean checkUser(ManageUser manageUser, String operCode) {
    	String operDesc = "查看商户员工列表";
    	if ("query".equals(operCode)) {
    		operDesc = "查看商户员工列表";
    	} else if ("add".equals(operCode)) {
    		operDesc = "添加商户员工";
    	} else if ("update".equals(operCode)) {
    		operDesc = "修改商户员工";
    	} else if ("reset".equals(operCode)) {
    		operDesc = "重置商户员工退款权限密码";
    	} else if ("modifyRefundPwd".equals(operCode)) {
    		operDesc = "修改商户员工退款权限密码";
    	}
    	
    	if ("add".equals(operCode) || "update".equals(operCode) || "reset".equals(operCode)) {
    		if (!isDealer(manageUser) && !isStoreManager(manageUser)) {
                logger.warn("非商户或店长不能" + operDesc);
                setAlertMessage("非商户或店长不能" + operDesc);
                return false;
    		}
    	} else if("query".equals(operCode)) {
    		if (!isDealer(manageUser) && !isStoreManager(manageUser)) {
                logger.warn("非商户或店长不能" + operDesc);
                setAlertMessage("非商户或店长不能" + operDesc);
                return false;
    		}
    	} else if("modifyRefundPwd".equals(operCode)) {
    		if (!isDealerEmployee(manageUser)) {
                logger.warn("非商户员工不能" + operDesc);
                setAlertMessage("非商户员工不能" + operDesc);
                return false;
    		}
    	} else {
    		return false;
    	}
    	return true;
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
            if (level != SysUser.UserLevel.dealer.getValue() || manageUser.getDataDealer() == null) {// 非商户
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是收银员
     * 
     * @return
     */
    private boolean isDealerEmployee(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if ((level != SysUser.UserLevel.cashier.getValue() && level != SysUser.UserLevel.shopManager.getValue()) || manageUser.getDataDealerEmployee() == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是店长
     * 
     * @return
     */
    private boolean isStoreManager(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.shopManager.getValue() || manageUser.getDataDealerEmployee() == null) {
                return false;
            }
        }
        return true;
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

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public DealerEmployeeVO getDealerEmployeeVO() {
        return dealerEmployeeVO;
    }

    public void setDealerEmployeeVO(DealerEmployeeVO dealerEmployeeVO) {
        this.dealerEmployeeVO = dealerEmployeeVO;
    }

    public List<DealerEmployeeVO> getDealerEmployeeVoList() {
        return dealerEmployeeVoList;
    }

    public void setDealerEmployeeVoList(List<DealerEmployeeVO> dealerEmployeeVoList) {
        this.dealerEmployeeVoList = dealerEmployeeVoList;
    }

    public void setDealerEmployeeService(DealerEmployeeService dealerEmployeeService) {
        this.dealerEmployeeService = dealerEmployeeService;
    }

	public void setStoreService(StoreService storeService) {
		this.storeService = storeService;
	}

	public List<StoreVO> getStoreVoList() {
		return storeVoList;
	}
	
	public void setResetFlag(String resetFlag) {
		this.resetFlag = resetFlag;
	}

	public String getResetFlag() {
		return resetFlag;
	}

	public String getQRCodeName() {
		return qRCodeName;
	}

	public void setDealerEmployeeOid(String dealerEmployeeOid) {
		this.dealerEmployeeOid = dealerEmployeeOid;
	}

    
    public String getStoreOid() {
        return storeOid;
    }
    
    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }
	
}
