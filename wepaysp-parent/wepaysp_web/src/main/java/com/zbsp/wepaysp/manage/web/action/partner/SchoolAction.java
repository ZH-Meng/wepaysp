package com.zbsp.wepaysp.manage.web.action.partner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.alibaba.fastjson.JSON;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.dic.zone.AlipayEduRegionService;
import com.zbsp.wepaysp.api.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.SchoolService;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.vo.dic.zone.CityVo;
import com.zbsp.wepaysp.vo.dic.zone.DistrictVO;
import com.zbsp.wepaysp.vo.dic.zone.ProvinceVo;
import com.zbsp.wepaysp.vo.partner.PartnerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.SchoolVO;

/**
 * 学校管理
 * 
 * @author zhaozh
 *
 */
public class SchoolAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -7078956274536886116L;

    private Map<String, Object> session;
    private SchoolVO schoolVO;
    private List<SchoolVO> schoolVoList;
    private SchoolService schoolService;
    private List<PartnerEmployeeVO> partnerEmployeeVoList;
    private PartnerEmployeeService partnerEmployeeService;
    private String qrCodeName;
    private String schoolOid; 
    private String partnerOid;
    private String alipayAuthUrl;
    
    private AlipayEduRegionService alipayEduRegionService;  
    public void setAlipayEduRegionService(AlipayEduRegionService alipayEduRegionService) {
		this.alipayEduRegionService = alipayEduRegionService;
	}
 
 
    
    /**
     * 学校管理-查看登陆代理商发展的学校列表
     */
    public String list() {
        initPageData(100);
        return goCurrent();
    }

    
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (schoolVO == null) {
                schoolVO = new SchoolVO();
            }
            if ("on".equals(schoolVO.getCoreDataFlag())) {
                if (SysUserUtil.isTopPartner(manageUser)) {
                	 paramMap.put("coreDataFlag", schoolVO.getCoreDataFlag());
                	 paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                } else {
                	logger.warn("非顶级服务商（代理商）不能管理学校交易核心数据");
                	setAlertMessage("非服务商（代理商）不能管理学校交易核心数据");
                	return "accessDenied";
                }
            } else {
                if ((SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isPartner(manageUser)) && StringUtils.isNotBlank(partnerOid)) {// 查看子代理或者下下级代理商的学校信息
                    paramMap.put("partnerOid", partnerOid);
                } else if (SysUserUtil.isPartnerEmployee(manageUser)) {
                    paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
                } else if (SysUserUtil.isPartner(manageUser)) {
                	paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                } else {
                	logger.warn("非服务商（代理商）、业务员不能管理学校信息");
                	setAlertMessage("非服务商（代理商）、业务员不能管理学校信息");
                	return "accessDenied";
                }
            }
 
            paramMap.put("loginId", schoolVO.getLoginId());
            paramMap.put("shcoolName", schoolVO.getShcoolName()); 
            paramMap.put("partnerEmployeeName", schoolVO.getPartnerEmployeeName()); 
            
            
            schoolVoList = schoolService.doJoinTransQuerySchoolList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("学校查询列表错误：" + e.getMessage());
            setAlertMessage("学校查询列表错误！");
        }

        return "schoolList";
    }


    
    /**
     * 根据代理商Oid查看此代理商发展的学校
     */
//    public String listByPartnerOid() {
//    	if (StringUtils.isBlank(partnerOid)) {
//    		logger.warn("partnerOid不能为空");
//        	setAlertMessage("非法查看学校信息列表");
//        	return ERROR;
//    	}
//    	initPageData(100);
//        return goCurrent();
//    }

    /**
     * 学校交易核心数据管理
     * 
     * @return
     */
//    public String transCoreDataList() {
//        initPageData(100);
//        if (schoolVO == null) {
//            schoolVO = new SchoolVO();
//        }
//        schoolVO.setCoreDataFlag("on");
//        return goCurrent();
//    }

    public String goToCreateSchool() {
        logger.info("跳转创建学校页面.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (schoolVO == null) {
                schoolVO = new SchoolVO();
            }
            
            if (SysUserUtil.isPartnerEmployee(manageUser)) {
                schoolVO.setPartnerEmployeeOid(manageUser.getDataPartnerEmployee().getIwoid());
                schoolVO.setPartnerEmployeeName(manageUser.getDataPartnerEmployee().getEmployeeName());
            } else if (SysUserUtil.isPartner(manageUser)) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                partnerEmployeeVoList = partnerEmployeeService.doJoinTransQueryPartnerEmployeeList(paramMap, 0, -1);
                if (partnerEmployeeVoList == null || partnerEmployeeVoList.isEmpty()) {
                    logger.warn("没有业务员不能添加学校信息");
                    setAlertMessage("请先添加代理商员工（业务员）");
                }
            } else {
                logger.warn("非服务商（代理商）、业务员不能添加学校信息");
                setAlertMessage("非服务商（代理商）、业务员不能添加学校信息");
                return "accessDenied";
            }
            
        } catch (Exception e) {
            logger.error("学校添加错误：" + e.getMessage());
            setAlertMessage("学校添加错误！");
            return list();
        }
        return "createSchool";
    }
    
    public String createSchool() {
        logger.info("开始创建学校.");
        try {
        	String schoolNo = null;
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!SysUserUtil.isPartner(manageUser) && !SysUserUtil.isPartnerEmployee(manageUser)) {
                logger.warn("创建学校失败：非服务商（代理商）、业务员不能添加学校信息");
                setAlertMessage("创建学校失败：非服务商（代理商）、业务员不能添加学校信息");
                return "accessDenied";
            }

            if (schoolVO == null) {
                logger.warn("创建学校失败，参数" + schoolVO + "为空！");
                setAlertMessage("创建学校失败！");
            }
                        
            schoolVO.setIsvNotifyUrl( SysConfig.alipayEduNotifyURL);
            
            schoolService.doTransAddSchool(schoolVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"),schoolNo);
            logger.info("学校" + schoolVO.getShcoolName() + "添加成功");
            setAlertMessage("学校" + schoolVO.getShcoolName() + "添加成功");
            schoolVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("学校添加失败：" + e.getMessage());
            setAlertMessage("学校添加失败：" + e.getMessage());
            return "goToCreateSchool";
        } catch (Exception e) {
            logger.error("学校添加错误：" + e.getMessage());
            setAlertMessage("学校添加错误！");
            return "goToCreateSchool";
        }
        return list();
    }

    
    
    public String ajaxDistrict(){  
    	
        HttpServletResponse response = ServletActionContext.getResponse();  
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");
        HttpServletRequest request = ServletActionContext.getRequest();
        PrintWriter writer=null;  
        String cityCode = request.getParameter("cityCode");
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
        	logger.error("区县信息解析错误"); 
        }  
        List<DistrictVO> districtList=alipayEduRegionService.doJoinTransfindAllDistrict(cityCode);
        String str=JSON.toJSONString(districtList);        
        writer.print(str);
        return null;  
    }  
    
    public String ajaxProvince(){  
        HttpServletResponse response = ServletActionContext.getResponse();  
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer=null;  
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
        	logger.error("省份信息解析错误"); 
        }  
        List<ProvinceVo> provinceList=alipayEduRegionService.doJoinTransfindAllProvince();         
        String str=JSON.toJSONString(provinceList);        
        writer.print(str);
        return null;  
    }  
    
    public String ajaxCity(){  
    	
        HttpServletResponse response = ServletActionContext.getResponse();  
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");
        HttpServletRequest request = ServletActionContext.getRequest();
        PrintWriter writer=null;  
        String provinceCode = request.getParameter("provinceCode");
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
        	logger.error("地市信息解析错误"); 
        }  
        List<CityVo> cityList=alipayEduRegionService.doJoinTransfindAllCity(provinceCode);
        String str=JSON.toJSONString(cityList);        
        writer.print(str);
        return null;  
    }  
     

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public SchoolVO getSchoolVO() {
        return schoolVO;
    }

    public void setSchoolVO(SchoolVO schoolVO) {
        this.schoolVO = schoolVO;
    }

    public List<SchoolVO> getSchoolVoList() {
        return schoolVoList;
    }

    public void setSchoolVoList(List<SchoolVO> schoolVoList) {
        this.schoolVoList = schoolVoList;
    }

    public void setSchoolService(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    public List<PartnerEmployeeVO> getPartnerEmployeeVoList() {
        return partnerEmployeeVoList;
    }

    public void setPartnerEmployeeService(PartnerEmployeeService partnerEmployeeService) {
        this.partnerEmployeeService = partnerEmployeeService;
    }
    
    public String getQrCodeName() {
        return qrCodeName;
    }
    
    public String getSchoolOid() {
		return schoolOid;
	}

	public void setSchoolOid(String schoolOid) {
        this.schoolOid = schoolOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getPartnerOid() {
        return partnerOid;
    }

	public String getAlipayAuthUrl() {
		return alipayAuthUrl;
	}
	
}
