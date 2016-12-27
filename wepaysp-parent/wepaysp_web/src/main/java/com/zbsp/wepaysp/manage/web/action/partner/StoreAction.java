package com.zbsp.wepaysp.manage.web.action.partner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.constant.EnumDefine.QRCodeType;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

/**
 * 门店管理
 * 
 * @author mengzh
 *
 */
public class StoreAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 1469376572233280708L;
    private Map<String, Object> session;
    private StoreVO storeVO;
    private List<StoreVO> storeVoList;
    private StoreService storeService;
    private String qRCodeName;
    private String storeOid;
    private String dealerOid;
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
    private List<PayNoticeBindWeixinVO> payNoticeBindWeixinVoList;
    private List<DealerEmployeeVO> dealerEmployeeVoList;
    private DealerEmployeeService dealerEmployeeService;
    private String payNoticeBindWeixinOid;
    
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (storeVO == null) {
                storeVO = new StoreVO();
            }
            paramMap.put("storeName", storeVO.getStoreName());
            paramMap.put("storeTel", storeVO.getStoreTel());
            
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (manageUser.getUserLevel().intValue() < SysUser.UserLevel.dealer.getValue()) {// 服务商、业务员在 商户页面查看门店信息
                if (StringUtils.isBlank(dealerOid)) {
                    logger.warn("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    setAlertMessage("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    return "accessDenied";
                }
                paramMap.put("dealerOid", dealerOid);
                storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
            } else if (SysUserUtil.isDealer(manageUser)) {// 商户
                paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
            } else if (SysUserUtil.isStoreManager(manageUser)) {// 店长
                storeOid = manageUser.getDataDealerEmployee().getStore().getIwoid();
                storeVoList = new ArrayList<StoreVO>();
                storeVoList.add(storeService.doJoinTransQueryStoreByOid(storeOid));
            } else {// 商户
                logger.warn("无权查看门店列表");
                setAlertMessage("无权查看门店列表");
                return "accessDenied";
            }
            //rowCount = storeService.doJoinTransQueryStoreCount(paramMap);
        } catch (Exception e) {
            logger.error("门店查询列表错误：" + e.getMessage());
            setAlertMessage("门店查询列表错误！");
        }
        return "storeList";
    }

    public String list() {
        initPageData(PageAction.defaultSmallPageSize);
        return goCurrent();
    }
    
    /**
     * 根据商户Oid查看此商户开设的门店
     */
    public String listByDealerOid() {
    	if (StringUtils.isBlank(dealerOid)) {
    		logger.warn("dealerOid不能为空");
        	setAlertMessage("非法查看门店信息列表");
        	return ERROR;
    	}
    	initPageData(100);
        return goCurrent();
    }

    public String goToCreateStore() {
        logger.info("跳转创建门店页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!SysUserUtil.isDealer(manageUser)) {
            logger.warn("非商户用户不能创建门店");
            setAlertMessage("非商户用户不能创建门店");
            return "accessDenied";
        }
        storeVO = null;
        return "createStore";
    }

    public String createStore() {
        logger.info("开始创建门店.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!SysUserUtil.isDealer(manageUser)) {
                logger.warn("非商户用户不能创建门店");
                setAlertMessage("非商户用户不能创建门店");
                return "accessDenied";
            }

            if (storeVO == null) {
                logger.warn("创建门店失败，参数" + storeVO + "为空！");
                setAlertMessage("创建门店失败！");
            }
            storeVO.setDealerOid(manageUser.getDataDealer().getIwoid());
            storeService.doTransAddStore(storeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("门店" + storeVO.getStoreName() + "添加成功");
            setAlertMessage("门店" + storeVO.getStoreName() + "添加成功");
            storeVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("门店添加失败：" + e.getMessage());
            setAlertMessage("门店添加失败：" + e.getMessage());
            return "createStore";
        } catch (Exception e) {
            logger.error("门店添加错误：" + e.getMessage());
            setAlertMessage("门店添加错误！");
            return "error";
        }
        return list();
    }

    public String goToUpdateStore() {
        logger.info("跳转修改门店页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!SysUserUtil.isDealer(manageUser)) {
            logger.warn("非商户用户不能修改门店");
            setAlertMessage("非商户用户不能修改门店");
            return "accessDenied";
        }
        if (storeVO != null && StringUtils.isNotBlank(storeVO.getIwoid())) {
            storeVO = storeService.doJoinTransQueryStoreByOid(storeVO.getIwoid());
        } else {
            logger.warn("非法修改门店，参数storeVO为空，storeVO.getIwoid()或者！");
            setAlertMessage("非法修改门店！");
            return list();
        }
        return "updateStore";
    }

    public String updateStore() {
        logger.info("开始修改门店.");
        try {
            if (storeVO != null && StringUtils.isNotBlank(storeVO.getIwoid())) {
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!SysUserUtil.isDealer(manageUser)) {
                    logger.warn("非商户用户不能修改门店");
                    setAlertMessage("非商户用户不能修改门店");
                    return "accessDenied";
                }

                storeService.doTransUpdateStore(storeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("门店" + storeVO.getStoreName() + "修改成功");
                setAlertMessage("门店" + storeVO.getStoreName() + "修改成功");
            } else {
                logger.warn("修改门店失败，参数storeVO或者storeVO.getIwoid()为空！");
                setAlertMessage("修改门店失败！");
                return list();
            }
        } catch (NotExistsException e) {
            logger.error("门店修改失败：" + e.getMessage());
            setAlertMessage("门店修改失败：" + e.getMessage());
            storeVO = null;
            return list();
        } catch (Exception e) {
            logger.error("门店修改错误：" + e.getMessage());
            setAlertMessage("门店修改错误！");
            storeVO = null;
            return list();
        }
        return "updateStore";
    }

    /**
     * 下载门店级别支付二维码图片<br>
     * <pre>
     * 校验权限；
     * 判断此门店是否生成过二维码，且二维码文件是否存在；
     * 如果二维码不存在则生成二维码图片
     * <pre>
     * @return
     */
    public String downloadPayQRCode() {
        logger.info("下载门店级别支付二维码图片.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 代理商、业务员、商户、店长能下载门店级别二维码
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isDealer(manageUser) || SysUserUtil.isStoreManager(manageUser)) {
                if (StringUtils.isNotBlank(storeOid)) {
                	storeVO = storeService.doTransGetQRCode(QRCodeType.PAY.getValue(), storeOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                } else {
                    logger.warn("非法下载门店级别支付二维码图片，参数storeOid为空！");
                    setAlertMessage("下载门店级别支付二维码图片失败！");
                    return list();
                }
            } else {
                logger.warn("无权下载门店级别支付二维码");
                setAlertMessage("无权下载门店级别支付二维码");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("下载门店级别支付二维码错误：" + e.getMessage());
            setAlertMessage("下载门店级别支付二维码错误！");
            return list();
        }
        return "getQRCodeImg";
    }
    
    /**下载门店级别支付二维码*/
    public InputStream getQRCodeImg() {
        InputStream inputStream = null;
        try {
            File qrFile = new File(storeVO.getQrCodePath());
            inputStream = new FileInputStream(qrFile);
            qRCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
            logger.info("下载门店级别支付二维码图片成功.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    /**
     * 跳转微信支付通知绑定微信账户页面
     * <pre>
     * 		查询当前门店绑定的微信用户信息列表；
     * 		在结果页面加载二维码图片；
     * </pre>
     * @return
     */
    public String goToBindWxID() {
        logger.info("跳转微信支付通知绑定微信账户页面");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 代理商、业务员、商户、店长能绑定
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isDealer(manageUser) || SysUserUtil.isStoreManager(manageUser)) {
                if (StringUtils.isNotBlank(storeOid)) {
                    // 查询已绑定的信息
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("storeOid", storeOid);
                    paramMap.put("type", PayNoticeBindWeixin.Type.store.getValue());
                    payNoticeBindWeixinVoList = payNoticeBindWeixinService.doJoinTransQueryPayNoticeBindWeixinList(paramMap);
                    
                    // 查询门店下员工
                    paramMap.clear();
                    paramMap.put("storeOid", storeOid);
                    dealerEmployeeVoList = dealerEmployeeService.doJoinTransQueryDealerEmployeeList(paramMap, 0, -1);
                } else {
                    logger.warn("非法绑定微信支付通知，参数storeOid为空！");
                    setAlertMessage("绑定微信支付通知失败！");
                    return list();
                }
            } else {
                logger.warn("无权绑定微信支付通知");
                setAlertMessage("无权绑定微信支付通知");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("绑定微信支付通知错误：" + e.getMessage());
            setAlertMessage("绑定微信支付通知错误！");
            return list();
        }
        return "storeBindWxID";
    }

    /**
     * 加载绑定微信支付通知二维码
     * @return
     */
    public String loadBindQRCode() {
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (StringUtils.isNotBlank(storeOid)) {
                // 加载绑定二维码
                storeVO = storeService.doTransGetQRCode(QRCodeType.BIND_PAY_NOTICE.getValue(), storeOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            }
        } catch (Exception e) {
            logger.error("加载绑定二维码错误：" + e.getMessage());
        }
    	return "getBindQRCodeImg";
    }
    
    /**
     * 返回绑定微信支付通知二维码图片流
     * @return
     */
    public InputStream getBindQRCodeImg() {
        InputStream inputStream = null;
        try {
            File qrFile = new File(storeVO.getBindQrCodePath());
            inputStream = new FileInputStream(qrFile);
            qRCodeName=URLEncoder.encode(qrFile.getName(),"utf-8");
            logger.info("加载门店级别绑定支付通知二维码图片成功.");
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
        	String partner1Oid = storeService.doJoinTransGetTopPartnerOid(storeOid);
			if (StringUtils.isNotBlank(partner1Oid)) {
				Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partner1Oid);
				if (partnerMap != null && !partnerMap.isEmpty()) {
					String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);
					File qrFile = new File(SysConfig.appidQrCodePath + File.separator + appid + ".png");
					inputStream = new FileInputStream(qrFile);
					qRCodeName = URLEncoder.encode(qrFile.getName(), "utf-8");
					logger.info("加载公众号（" + appid + "）二维码图片成功.");
				}
			}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    /**删除微信支付通知绑定*/
    public String deleteBindWxID() {
    	logger.info("删除微信支付通知绑定");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // 代理商、业务员、商户、店长能删除绑定
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isDealer(manageUser) || SysUserUtil.isStoreManager(manageUser)) {
                if (StringUtils.isNotBlank(payNoticeBindWeixinOid) && StringUtils.isNotBlank(storeOid)) {
                    // 删除绑定
                    payNoticeBindWeixinService.doTransDeletePayNoticeBindWeixin(payNoticeBindWeixinOid);
                    logger.info("删除微信支付通知绑定成功，跳转绑定页面");
                    return goToBindWxID();
                } else {
                    logger.warn("非法删除微信支付通知绑定，参数payNoticeBindWeixinOid为空！");
                    setAlertMessage("删除微信支付通知绑定信息失败！");
                    return "accessDenied";
                }
            } else {
                logger.warn("无权删除微信支付通知绑定");
                setAlertMessage("无权删除微信支付通知绑定");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("删除微信支付通知绑定错误：" + e.getMessage());
            setAlertMessage("删除微信支付通知绑定错误！");
            return ERROR;
        }
    }
    
    /**批量更新微信支付通知绑定*/
    public String batchUpdateBindWxID() {
    	logger.info("开始批量更新微信支付通知绑定");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // 代理商、业务员、商户、店长能更新绑定
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isDealer(manageUser) || SysUserUtil.isStoreManager(manageUser)) {
                if (payNoticeBindWeixinVoList != null && !payNoticeBindWeixinVoList.isEmpty() && StringUtils.isNotBlank(storeOid)) {
                    // 更新绑定
                	payNoticeBindWeixinService.doTransUpdatePayNoticeBindWeixinList(payNoticeBindWeixinVoList, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                	logger.info("批量更新微信支付通知绑定成功，跳转绑定页面");
                    return goToBindWxID();
                } else {
                    logger.warn("非法批量更新微信支付通知绑定，参数payNoticeBindWeixinOid为空！");
                    setAlertMessage("批量更新微信支付通知绑定失败！");
                    return "accessDenied";
                }
            } else {
                logger.warn("无权批量更新微信支付通知绑定");
                setAlertMessage("无权批量更新微信支付通知绑定");
                return "accessDenied";
            }
        } catch (Exception e) {
            logger.error("批量更新微信支付通知绑定错误：" + e.getMessage());
            setAlertMessage("批量更新微信支付通知绑定错误！");
            return ERROR;
        }
    }
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public StoreVO getStoreVO() {
        return storeVO;
    }

    public void setStoreVO(StoreVO storeVO) {
        this.storeVO = storeVO;
    }

    public List<StoreVO> getStoreVoList() {
        return storeVoList;
    }

    public void setStoreVoList(List<StoreVO> storeVoList) {
        this.storeVoList = storeVoList;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

	public String getQRCodeName() {
		return qRCodeName;
	}

	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}
	
    public String getStoreOid() {
		return storeOid;
	}

	public String getDealerOid() {
        return dealerOid;
    }
    
    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }
    
    public List<PayNoticeBindWeixinVO> getPayNoticeBindWeixinVoList() {
        return payNoticeBindWeixinVoList;
    }

    public void setPayNoticeBindWeixinVoList(List<PayNoticeBindWeixinVO> payNoticeBindWeixinVoList) {
        this.payNoticeBindWeixinVoList = payNoticeBindWeixinVoList;
    }
    
    public void setPayNoticeBindWeixinService(PayNoticeBindWeixinService payNoticeBindWeixinService) {
        this.payNoticeBindWeixinService = payNoticeBindWeixinService;
    }

	public List<DealerEmployeeVO> getDealerEmployeeVoList() {
		return dealerEmployeeVoList;
	}

	public void setDealerEmployeeService(DealerEmployeeService dealerEmployeeService) {
		this.dealerEmployeeService = dealerEmployeeService;
	}

	public void setPayNoticeBindWeixinOid(String payNoticeBindWeixinOid) {
		this.payNoticeBindWeixinOid = payNoticeBindWeixinOid;
	}

}
