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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.SysEnums.QRCodeType;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppAuthDetailsService;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.api.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.vo.alipay.AlipayAppAuthDetailsVO;
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
    private String qrCodeName;
    private String dealerOid; 
    private String partnerOid;
    private String alipayAuthUrl;
    
    private AlipayAppAuthDetailsService alipayAppAuthDetailsService;
    private String authStatusDesc;// 已授权、未授权
    
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (dealerVO == null) {
                dealerVO = new DealerVO();
            }
            if ("on".equals(dealerVO.getCoreDataFlag())) {
                if (SysUserUtil.isTopPartner(manageUser)) {
                	 paramMap.put("coreDataFlag", dealerVO.getCoreDataFlag());
                	 paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                } else {
                	logger.warn("非顶级服务商（代理商）不能管理商户交易核心数据");
                	setAlertMessage("非服务商（代理商）不能管理商户交易核心数据");
                	return "accessDenied";
                }
            } else {
                if ((SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isPartner(manageUser)) && StringUtils.isNotBlank(partnerOid)) {// 查看子代理或者下下级代理商的商户信息
                    paramMap.put("partnerOid", partnerOid);
                } else if (SysUserUtil.isPartnerEmployee(manageUser)) {
                    paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());
                } else if (SysUserUtil.isPartner(manageUser)) {
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

    /**
     * 商户信息管理-查看登陆代理商发展的商户
     */
    public String list() {
        initPageData(100);
        return goCurrent();
    }
    
    /**
     * 根据代理商Oid查看此代理商发展的商户
     */
    public String listByPartnerOid() {
    	if (StringUtils.isBlank(partnerOid)) {
    		logger.warn("partnerOid不能为空");
        	setAlertMessage("非法查看商户信息列表");
        	return ERROR;
    	}
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
            if (dealerVO == null) {
                dealerVO = new DealerVO();
            }
            
            if (SysUserUtil.isPartnerEmployee(manageUser)) {
                dealerVO.setPartnerEmployeeOid(manageUser.getDataPartnerEmployee().getIwoid());
                dealerVO.setPartnerEmployeeName(manageUser.getDataPartnerEmployee().getEmployeeName());
            } else if (SysUserUtil.isPartner(manageUser)) {
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
            if (!SysUserUtil.isPartner(manageUser) && !SysUserUtil.isPartnerEmployee(manageUser)) {
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
            return "goToCreateDealer";
        } catch (Exception e) {
            logger.error("商户添加错误：" + e.getMessage());
            setAlertMessage("商户添加错误！");
            return "goToCreateDealer";
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
                    if (!SysUserUtil.isTopPartner(manageUser)) {//角色权限校验通过，如果给其他角色配置了一级服务商的菜单仍不能使用
                        logger.warn("非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    logger.info("跳转修改商户页面.");
                    
                    if (!SysUserUtil.isPartner(manageUser) && !SysUserUtil.isPartnerEmployee(manageUser)) {
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
                if (SysUserUtil.isPartner(manageUser) && !"on".equals(dealerVO.getCoreDataFlag())) {
                	if (!manageUser.getDataPartner().getIwoid().equals(dealerVO.getPartnerOid())) {
                    	logger.warn("非法操作：只能修改本代理发展的商户信息");
                        setAlertMessage("非法操作：只能修改本代理发展的商户信息");
                        return list();
                	}
                	Map<String, Object> paramMap = new HashMap<String, Object>();
                	paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                	partnerEmployeeVoList = partnerEmployeeService.doJoinTransQueryPartnerEmployeeList(paramMap, 0, -1);
                } else if (SysUserUtil.isPartnerEmployee(manageUser)) {
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
            if (!SysUserUtil.isDealer(manageUser)) {
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
            if (!SysUserUtil.isDealer(manageUser)) {
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
                    if (!SysUserUtil.isTopPartner(manageUser)) {
                        logger.warn("非顶级服务商（代理商）不能修改商户交易核心数据");
                        setAlertMessage("非服务商（代理商）不能修改商户交易核心数据");
                        return "accessDenied";
                    }
                } else {
                    if (!SysUserUtil.isPartner(manageUser) && !SysUserUtil.isPartnerEmployee(manageUser)) {
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
     * 下载商户级别支付二维码图片<br>
     * <pre>
     * 校验权限；
     * 判断此商户是否生成过二维码，且二维码文件是否存在；
     * 如果二维码不存在则生成二维码图片
     * <pre>
     * @return
     */
    public String downloadPayQRCode() {
        logger.info("下载商户级别支付二维码图片.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser)) {// 服务商或业务员下载商户二维码
                if (StringUtils.isNotBlank(dealerOid)) {
                    dealerVO = dealerService.doTransGetQRCode(QRCodeType.PAY.getValue(), dealerOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                } else {
                    logger.warn("非法下载商户级别支付二维码图片，参数dealerOid为空！");
                    setAlertMessage("下载商户级别支付二维码图片失败！");
                    return list();
                }
            } else if (SysUserUtil.isDealer(manageUser)) {// 商户下载自己二维码
                dealerVO = dealerService.doTransGetQRCode(QRCodeType.PAY.getValue(), manageUser.getDataDealer().getIwoid(), manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            } else {
                logger.warn("无权下载商户级别支付二维码");
                setAlertMessage("无权下载商户级别支付二维码");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("下载商户级别支付二维码错误：" + e.getMessage());
            setAlertMessage("下载商户级别支付二维码错误！");
            return list();
        }
        return "getQrCodeImg";
    }
    
    public InputStream getQrCodeImg() {
        InputStream inputStream = null;
        try {
            File qrFile = new File(dealerVO.getQrCodePath());
            inputStream = new FileInputStream(qrFile);
            /*String fileNameTemp = "二维码-" + dealerVO.getCompany() + ".png";
            qrCodeName = new String(fileNameTemp.getBytes("GBK"), "ISO8859-1");*/
            qrCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
            logger.info("下载商户级别支付二维码图片成功.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    /**
     * 加载绑定微信收款汇总通知二维码
     * @return
     */
    public String loadBindQRCode() {
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (StringUtils.isNotBlank(dealerOid)) {
                // 加载绑定二维码
                dealerVO = dealerService.doTransGetQRCode(QRCodeType.BIND_PAY_NOTICE.getValue(), dealerOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            } 
        } catch (Exception e) {
            logger.error("加载绑定二维码错误：" + e.getMessage());
        }
        return "getBindQRCodeImg";
    }
    
    /**
     * 返回绑定微信收款汇总通知二维码图片流
     * @return
     */
    public InputStream getBindQRCodeImg() {
        InputStream inputStream = null;
        try {
            File qrFile = new File(dealerVO.getBindQrCodePath());
            inputStream = new FileInputStream(qrFile);
            qrCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
            logger.info("加载商户级别绑定收款汇总通知二维码图片成功.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    public String loadAppidQRCode() {
        return "getAppidQRCodeImg";
    }
    
    /**
     * 返回绑定微信公众号二维码图片流
     * @return
     */
    public InputStream getAppidQRCodeImg() {
        InputStream inputStream = null;
        try {
            if (StringUtils.isNotBlank(dealerOid)) {
                String partner1Oid = dealerService.doJoinTransGetTopPartnerOid(dealerOid);
                if (StringUtils.isNotBlank(partner1Oid)) {
                    Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partner1Oid);
                    if (partnerMap != null && !partnerMap.isEmpty()) {
                        String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);
                        File qrFile = new File(SysConfig.appidQrCodePath + File.separator + appid + ".png");
                        inputStream = new FileInputStream(qrFile);
                        qrCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
                        logger.info("加载公众号（" + appid + "）二维码图片成功.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    /**
     * 跳转商户管理支付宝页面
     * <pre>
     * 		支付宝第三方应用授权；
     * </pre>
     * @return
     */
    public String goToAlipayManage() {
        logger.info("跳转商户管理支付宝页面");
        try {
        	ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        	 if (!SysUserUtil.isPartner(manageUser)) {
                 logger.warn("无权操作");
                 setAlertMessage("无权操作");
                 return "accessDenied";
             }
        	 if (StringUtils.isBlank(dealerOid)) {
        		 logger.warn("非法操作，参数dealerOid为空！");
	             setAlertMessage("非法操作！");
	             return "accessDenied";
        	 }
        	 // 拼接支付第三方应用授权链接，商户识别码dealerOid，回调处理时，根据dealerOid更新dealer_t中授权的支付宝商户PID字段
        	 
    		 Map<String, String> urlParamMap = new HashMap<String, String>();
             urlParamMap.put("dealerOid", dealerOid);
            alipayAuthUrl = Generator.generateQRURL(SysConfig.onlineFlag ? QRCodeType.ALIPAY_APP_AUTH.getValue() : QRCodeType.ALIPAY_APP_AUTH_DEV.getValue(), 
                SysConfig.appId4Face2FacePay, SysConfig.alipayAuthCallBackURL, urlParamMap);
    		 
    		 // 查看商户是否授权当面付应用
    		 AlipayAppAuthDetailsVO authDetailsVO = alipayAppAuthDetailsService.doJoinTranQueryAppAuthDetailByDealer(dealerOid, SysConfig.appId4Face2FacePay);
    		 if (authDetailsVO != null && AlipayAppAuthDetails.AppAuthStatus.VALID.toString().equals(authDetailsVO.getStatus())) {
    		     authStatusDesc = "已授权";
    		 } else {
    		     authStatusDesc = "未授权";
    		 }
        } catch (Exception e) {
            logger.error("跳转商户管理支付宝页面错误：" + e.getMessage());
            setAlertMessage("跳转商户管理支付宝页面错误！");
            return ERROR;
        }
        return "dealerAlipayManage";
    }
    
    /**
     * 加载支付宝授权二维码
     * @return
     */
    public String loadAliapyAppAuthCode() {
	    try {
	        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        if (StringUtils.isNotBlank(dealerOid)) {
	            // 加载授权二维码
	        	dealerVO = dealerService.doTransGetQRCode(SysConfig.onlineFlag ? QRCodeType.ALIPAY_APP_AUTH.getValue() : QRCodeType.ALIPAY_APP_AUTH_DEV.getValue(), 
	        			dealerOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
	        } 
	    } catch (Exception e) {
	        logger.error("加载支付宝授权二维码错误：" + e.getMessage());
	    }
	return "getAliapyAppAuthCodeImg";
}

/**
 * 返回支付宝授权二维码图片流
 * @return
 */
public InputStream getAliapyAppAuthCodeImg() {
    InputStream inputStream = null;
    try {
        File qrFile = new File(dealerVO.getAlipayAuthCodePath());
        inputStream = new FileInputStream(qrFile);
        qrCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
        logger.info("加载支付宝授权二维码图片成功.");
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    return inputStream;
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
    
    public String getQrCodeName() {
        return qrCodeName;
    }
    
    public String getDealerOid() {
		return dealerOid;
	}

	public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
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
    
    public String getAuthStatusDesc() {
        return authStatusDesc;
    }
    
    public void setAlipayAppAuthDetailsService(AlipayAppAuthDetailsService alipayAppAuthDetailsService) {
        this.alipayAppAuthDetailsService = alipayAppAuthDetailsService;
    }
	
}
