package com.zbsp.wepaysp.api.service.main.init.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.ServerType;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.SystemInitException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.partner.Partner;

public class SysConfigServiceImpl
    extends BaseService
    implements SysConfigService {

    private String payClientCheckURL;
    private String wxPayCallBackURL;
    private String wxPayNotifyURL;
    private String bindCallBackURL;
    private String qRCodeRootPath;
    private String appidQrCodePath;
    private String serverType;

    private PartnerService partnerService;
    
    /**
     * 初始化方法<br>
     *  初始化系统静态配置<br>
     * 检查注入参数完整性<br>
     * 加载状态为非冻结服务商配置信息到内存<br>
     * 启动获取Base_acction_token<br>
     * @throws SystemInitException 
     */
    public void init() throws SystemInitException {
        // 检查注入参数完整性
        ServerType serType = null;
        if (StringUtils.isBlank(serverType)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：serverType");
        } else {
            try {
                serType = Enum.valueOf(ServerType.class, serverType);
                logger.info("初始化系统配置信息：serverType=" + serType);
            } catch (Exception e) {
                throw new SystemInitException("初始化系统配置信息失败" + e.getMessage());
            }
        }
        
        
        if (StringUtils.isBlank(payClientCheckURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：payClientCheckURL");
        } else {
            logger.info("初始化系统配置信息：payClientCheckURL=" + payClientCheckURL);
        }
        if (StringUtils.isBlank(wxPayCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：wxPayCallBackURL");
        } else {
            logger.info("初始化系统配置信息：wxPayCallBackURL=" + wxPayCallBackURL);
        }
        if (StringUtils.isBlank(wxPayNotifyURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：wxPayNotifyURL");
        } else {
            logger.info("初始化系统配置信息：wxPayNotifyURL=" + wxPayNotifyURL);
        }
        if (StringUtils.isBlank(bindCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：bindCallBackURL");
        } else {
            logger.info("初始化系统配置信息：bindCallBackURL=" + bindCallBackURL);
        }
        if (StringUtils.isBlank(qRCodeRootPath)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：qRCodeRootPath");
        } else {
        	File dirs = new File(qRCodeRootPath);
        	if (!dirs.exists()) {
        		dirs.mkdirs();
        	}
            logger.info("初始化系统配置信息：qRCodeRootPath=" + qRCodeRootPath);
        }
        if (StringUtils.isBlank(appidQrCodePath)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：appidQrCodePath");
        } else {
        	File dirs = new File(appidQrCodePath);
        	if (!dirs.exists()) {
        		dirs.mkdirs();
        	}
            logger.info("初始化系统配置信息：appidQrCodePath=" + appidQrCodePath);
        }
        
        // 初始化系统静态配置
        SysConfig.payClientCheckURL= payClientCheckURL;
        SysConfig.wxPayCallBackURL = wxPayCallBackURL;
        SysConfig.wxPayNotifyURL = wxPayNotifyURL;
        SysConfig.bindCallBackURL = bindCallBackURL;
        SysConfig.qRCodeRootPath = qRCodeRootPath;
        SysConfig.appidQrCodePath = appidQrCodePath;

        // 加载状态为非冻结服务商配置信息到内存，FIXME 考虑清除和更新的问题
        List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(null, null);
        if (topPartnerList == null || topPartnerList.size() < 1) {
            throw new SystemInitException("初始化系统配置信息失败，没有查到非冻结的顶级服务商");
        } else {
            logger.info("当前系统非冻结的顶级服务商个数：" + topPartnerList.size() + "，开始初始化");
        }
        
        for(Partner topPartner : topPartnerList) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            // 校验服务商配置的完整性
            if (StringUtils.isBlank(topPartner.getAppId()) || StringUtils.isBlank(topPartner.getMchId()) || StringUtils.isBlank(topPartner.getAppSecret()) || 
                StringUtils.isBlank(topPartner.getKeyPath()) || StringUtils.isBlank(topPartner.getKeyPassword()) || StringUtils.isBlank(topPartner.getPartnerKey())){
                throw new SystemInitException("初始化系统配置信息失败，顶级服务商（parterOid=" + topPartner + "，company：" + topPartner.getCompany() +"）信息配置不完整");
            }  else {
                logger.info("初始化顶级服务商（parterOid=" + topPartner.getIwoid() + "，company：" + topPartner.getCompany() +"）配置到内存");
            }
            pMap.put(SysEnvKey.WX_APP_ID, topPartner.getAppId());
            pMap.put(SysEnvKey.WX_MCH_ID, topPartner.getMchId());
            pMap.put(SysEnvKey.WX_SECRET, topPartner.getAppSecret());
            pMap.put(SysEnvKey.WX_CERT_LOCAL_PATH, topPartner.getKeyPath());
            pMap.put(SysEnvKey.WX_CERT_PASSWORD, topPartner.getKeyPassword());
            pMap.put(SysEnvKey.WX_KEY, topPartner.getPartnerKey());
            SysConfig.partnerConfigMap.put(topPartner.getIwoid(), pMap);
            SysConfig.partnerConfigMap2.put(topPartner.getAppId(), pMap);
            
            // 启动获取Base_acction_token
            //new WeixinUtil().getBaseAccessToken(topPartner.getIwoid());
        }
        
        if (ServerType.REST.equals(serType)) {
            AliPayUtil.init();
            
            //FIXME WEB中的公众号支付迁移后，需要将微信的也添加
        }
    }

    @Override
    public Map<String, Object> getPartnerCofigInfoByPartnerOid(String partnerOid) {
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "partnerOid不能为空");
        if (!SysConfig.partnerConfigMap.containsKey(partnerOid)) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            
            logger.info("缓存中服务商信息不存在，partnerOid=" + partnerOid + "开始从数据库查找");
            List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(partnerOid, 1);
            if (topPartnerList == null || topPartnerList.isEmpty() || topPartnerList.get(0) == null) {
                logger.error("数据库非冻结的的服务商信息不存在，partnerOid=" + partnerOid);
            } else {
                Partner topPartner = topPartnerList.get(0);
                if (StringUtils.isBlank(topPartner.getAppId()) || StringUtils.isBlank(topPartner.getMchId()) || StringUtils.isBlank(topPartner.getAppSecret()) || 
                    StringUtils.isBlank(topPartner.getKeyPath()) || StringUtils.isBlank(topPartner.getKeyPassword()) || StringUtils.isBlank(topPartner.getPartnerKey())){
                    logger.error("顶级服务商信息配置不完整，partnerOid=" + partnerOid);
                } else {
                    // 数据库存在，将其缓存
                    pMap.put(SysEnvKey.WX_APP_ID, topPartner.getAppId());
                    pMap.put(SysEnvKey.WX_MCH_ID, topPartner.getMchId());
                    pMap.put(SysEnvKey.WX_SECRET, topPartner.getAppSecret());
                    pMap.put(SysEnvKey.WX_CERT_LOCAL_PATH, topPartner.getKeyPath());
                    pMap.put(SysEnvKey.WX_CERT_PASSWORD, topPartner.getKeyPassword());
                    pMap.put(SysEnvKey.WX_KEY, topPartner.getPartnerKey());
                    SysConfig.partnerConfigMap.put(partnerOid, pMap);
                }
            }
            return pMap;
        } else {
            return SysConfig.partnerConfigMap.get(partnerOid);
        }
    }
    
	@Override
	public Map<String, Object> getPartnerCofigInfoByAppid(String appid) {
		Validator.checkArgument(StringUtils.isBlank(appid), "appid不能为空");
        if (!SysConfig.partnerConfigMap2.containsKey(appid)) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            
            logger.info("缓存中服务商信息不存在，appid=" + appid + "开始从数据库查找");
            List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(appid, 2);
            if (topPartnerList == null || topPartnerList.isEmpty() || topPartnerList.get(0) == null) {
                logger.error("数据库非冻结的服务商信息不存在，appid=" + appid);
            } else {
                Partner topPartner = topPartnerList.get(0);
                if (StringUtils.isBlank(topPartner.getAppId()) || StringUtils.isBlank(topPartner.getMchId()) || StringUtils.isBlank(topPartner.getAppSecret()) || 
                    StringUtils.isBlank(topPartner.getKeyPath()) || StringUtils.isBlank(topPartner.getKeyPassword()) || StringUtils.isBlank(topPartner.getPartnerKey())){
                    logger.error("顶级服务商信息配置不完整，appid=" + appid);
                } else {
                    // 数据库存在，将其缓存
                    pMap.put(SysEnvKey.WX_APP_ID, topPartner.getAppId());
                    pMap.put(SysEnvKey.WX_MCH_ID, topPartner.getMchId());
                    pMap.put(SysEnvKey.WX_SECRET, topPartner.getAppSecret());
                    pMap.put(SysEnvKey.WX_CERT_LOCAL_PATH, topPartner.getKeyPath());
                    pMap.put(SysEnvKey.WX_CERT_PASSWORD, topPartner.getKeyPassword());
                    pMap.put(SysEnvKey.WX_KEY, topPartner.getPartnerKey());
                    SysConfig.partnerConfigMap2.put(appid, pMap);
                }
            }
            return pMap;
        } else {
            return SysConfig.partnerConfigMap2.get(appid);
        }
	}

    public void setPayClientCheckURL(String payClientCheckURL) {
        this.payClientCheckURL = payClientCheckURL;
    }
    
    public void setWxPayCallBackURL(String wxPayCallBackURL) {
        this.wxPayCallBackURL = wxPayCallBackURL;
    }

    public void setWxPayNotifyURL(String wxPayNotifyURL) {
        this.wxPayNotifyURL = wxPayNotifyURL;
    }

    public void setBindCallBackURL(String bindCallBackURL) {
        this.bindCallBackURL = bindCallBackURL;
    }

    public void setQRCodeRootPath(String qRCodeRootPath) {
        this.qRCodeRootPath = qRCodeRootPath;
    }
    
    public void setAppidQrCodePath(String appidQrCodePath) {
		this.appidQrCodePath = appidQrCodePath;
	}
    
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

}
