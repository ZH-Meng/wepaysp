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
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.api.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.vo.partner.PartnerEmployeeVO;

/**
 *代理商员工（业务员）管理
 * 
 * @author mengzh
 *
 */
public class PartnerEmployeeAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 4657328929941526804L;
    private Map<String, Object> session;
    private PartnerEmployeeVO partnerEmployeeVO;
    private List<PartnerEmployeeVO> partnerEmployeeVoList;
    private PartnerEmployeeService partnerEmployeeService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (partnerEmployeeVO == null) {
                partnerEmployeeVO = new PartnerEmployeeVO();
            }
            paramMap.put("employeeName", partnerEmployeeVO.getEmployeeName());
            paramMap.put("moblieNumber", partnerEmployeeVO.getMoblieNumber());
            
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (!checkUser(manageUser, "query")) {
				return "accessDenied";
            }
            
            if (SysUserUtil.isPartner(manageUser)) {
                paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
            } else if (SysUserUtil.isPartnerEmployee(manageUser)) {
                paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
            }
            
            //rowCount = partnerEmployeeService.doJoinTransQueryPartnerEmployeeCount(paramMap);
            partnerEmployeeVoList = partnerEmployeeService.doJoinTransQueryPartnerEmployeeList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("代理商员工查询列表错误：" + e.getMessage());
            setAlertMessage("代理商员工查询列表错误！");
        }
        return "partnerEmployeeList";
    }

    /**
     * 代理商可以查看自己的代理商员工，代理商员工可以查看所属代理商下的代理商员工
     * 
     * @return
     */
    public String list() {
        initPageData(PageAction.defaultSmallPageSize);
        return goCurrent();
    }

    public String goToCreatePartnerEmployee() {
        logger.info("跳转添加代理商员工页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!checkUser(manageUser, "add")) {
			return "accessDenied";
        }
		partnerEmployeeVO = null;
        return "createPartnerEmployee";
    }

    public String createPartnerEmployee() {
        logger.info("开始添加代理商员工.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		if (!checkUser(manageUser, "add")) {
    			return "accessDenied";
            }

            if (partnerEmployeeVO == null) {
                logger.warn("添加代理商员工失败，参数" + partnerEmployeeVO + "为空！");
                setAlertMessage("添加代理商员工失败！");
            }
            partnerEmployeeVO.setPartnerOid(manageUser.getDataPartner().getIwoid());
            partnerEmployeeService.doTransAddPartnerEmployee(partnerEmployeeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("代理商员工" + partnerEmployeeVO.getEmployeeName() + "添加成功");
            setAlertMessage("代理商员工" + partnerEmployeeVO.getEmployeeName() + "添加成功");
            partnerEmployeeVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("代理商员工添加失败：" + e.getMessage());
            setAlertMessage("代理商员工添加失败：" + e.getMessage());
            return "createPartnerEmployee";
        } catch (Exception e) {
            logger.error("代理商员工添加错误：" + e.getMessage());
            setAlertMessage("代理商员工添加错误！");
            return "error";
        }
        return list();
    }

    public String goToUpdatePartnerEmployee() {
        logger.info("跳转修改代理商员工页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
		if (!checkUser(manageUser, "update")) {
			return "accessDenied";
        }
        if (partnerEmployeeVO != null && StringUtils.isNotBlank(partnerEmployeeVO.getIwoid())) {
            partnerEmployeeVO = partnerEmployeeService.doJoinTransQueryPartnerEmployeeByOid(partnerEmployeeVO.getIwoid());
        } else {
            logger.warn("非法修改代理商员工，参数partnerEmployeeVO为空，partnerEmployeeVO.getIwoid()或者！");
            setAlertMessage("非法修改代理商员工！");
            return list();
        }
        return "updatePartnerEmployee";
    }

    public String updatePartnerEmployee() {
        logger.info("开始修改代理商员工.");
        try {
            if (partnerEmployeeVO != null && StringUtils.isNotBlank(partnerEmployeeVO.getIwoid())) {
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        		if (!checkUser(manageUser, "update")) {
        			return "accessDenied";
                }

                partnerEmployeeService.doTransUpdatePartnerEmployee(partnerEmployeeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("代理商员工" + partnerEmployeeVO.getEmployeeName() + "修改成功");
                setAlertMessage("代理商员工" + partnerEmployeeVO.getEmployeeName() + "修改成功");
            } else {
                logger.warn("修改代理商员工失败，参数partnerEmployeeVO或者partnerEmployeeVO.getIwoid()为空！");
                setAlertMessage("修改代理商员工失败！");
                return list();
            }
        } catch (NotExistsException e) {
            logger.error("代理商员工修改失败：" + e.getMessage());
            setAlertMessage("代理商员工修改失败：" + e.getMessage());
            return list();
        } catch (Exception e) {
            logger.error("代理商员工添加错误：" + e.getMessage());
            setAlertMessage("代理商员工添加错误！");
            return "error";
        }
        return "updatePartnerEmployee";
    }
    
    public boolean checkUser(ManageUser manageUser, String operCode) {
    	String operDesc = "查看代理商员工列表";
    	if ("query".equals(operCode)) {
    		operDesc = "查看代理商员工列表";
    	} else if ("add".equals(operCode)) {
    		operDesc = "添加代理商员工";
    	} else if ("update".equals(operCode)) {
    		operDesc = "修改代理商员工";
    	}
    	
    	if ("add".equals(operCode) || "update".equals(operCode)) {
    		if (!SysUserUtil.isPartner(manageUser)) {
                logger.warn("非代理商用户不能" + operDesc);
                setAlertMessage("非代理商用户不能" + operDesc);
                return false;
    		}     
    	} else if("query".equals(operCode)) {
    		if (!SysUserUtil.isPartner(manageUser) && !SysUserUtil.isPartnerEmployee(manageUser)) {
                logger.warn("非代理商或代理商员工用户不能" + operDesc);
                setAlertMessage("非代理商或代理商员工用户不能" + operDesc);
                return false;
    		}
    	} else {
    		return false;
    	}
    	return true;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public PartnerEmployeeVO getPartnerEmployeeVO() {
        return partnerEmployeeVO;
    }

    public void setPartnerEmployeeVO(PartnerEmployeeVO partnerEmployeeVO) {
        this.partnerEmployeeVO = partnerEmployeeVO;
    }

    public List<PartnerEmployeeVO> getPartnerEmployeeVoList() {
        return partnerEmployeeVoList;
    }

    public void setPartnerEmployeeVoList(List<PartnerEmployeeVO> partnerEmployeeVoList) {
        this.partnerEmployeeVoList = partnerEmployeeVoList;
    }

    public void setPartnerEmployeeService(PartnerEmployeeService partnerEmployeeService) {
        this.partnerEmployeeService = partnerEmployeeService;
    }

}
