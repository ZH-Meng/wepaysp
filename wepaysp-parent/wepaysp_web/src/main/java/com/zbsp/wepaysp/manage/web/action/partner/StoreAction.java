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
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.vo.partner.StoreVO;

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
    
    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (storeVO == null) {
                storeVO = new StoreVO();
            }
            paramMap.put("storeName", storeVO.getStoreName());
            paramMap.put("storeTel", storeVO.getStoreTel());
            // 所属商户
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (manageUser.getUserLevel() == null || manageUser.getUserLevel().intValue() > SysUser.UserLevel.dealer.getValue()) {// 系统用户或者商户级别以下的应用用户
                logger.warn("商户级别以下用户不能查看门店列表");
                setAlertMessage("商户级别以下用户不能查看门店列表");
                return "accessDenied";
            }
            
            if (manageUser.getUserLevel().intValue() < SysUser.UserLevel.dealer.getValue()) {// 服务商、业务员在 商户页面查看门店信息
                if (StringUtils.isBlank(dealerOid)) {
                    logger.warn("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    setAlertMessage("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    return "accessDenied";
                }
                paramMap.put("dealerOid", dealerOid);
            } else {// 商户
                if (!SysUserUtil.isDealer(manageUser)) {
                    logger.warn("门店查询列表失败：当前用户属于商户级别，但是没有关联商户信息");
                    setAlertMessage("门店查询列表失败：当前用户属于商户级别，但是没有关联商户信息");
                    return "accessDenied";
                } else {
                    paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                }
            }
            storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
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
            // 代理商、业务员、商户能下载门店级别二维码
            if (SysUserUtil.isPartner(manageUser) || SysUserUtil.isPartnerEmployee(manageUser) || SysUserUtil.isDealer(manageUser)) {
                if (StringUtils.isNotBlank(storeOid)) {
                	storeVO = storeService.doTransGetPayQRCode(storeOid, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
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
    
    public String getDealerOid() {
        return dealerOid;
    }
    
    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

}
