package com.zbsp.wepaysp.api.service.main.init.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.SystemInitException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.partner.Partner;

public class SysConfigServiceImpl
    extends BaseService
    implements SysConfigService {

    private String payCallBackURL;
    private String wxPayNotifyURL;
    private String bindCallBackURL;
    private String qRCodeRootPath;

    /** 
     * 系统支持多个服务商配置，key=partnerOid，value为服务商配置Map
     *  服务商配置Map key ：SysEnvKey.WX_APPID、SysEnvKey.WX_MCH_ID、SysEnvKey.WX_SECRET、SysEnvKey.WX_CERT_LOCAL_PATH、SysEnvKey.CERT_PASSWORD、SysEnvKey.WX_KEY
     *  */
    private Map<String, Map<String, String>> partnerConfigMap = new HashMap<String, Map<String, String>>();

    private PartnerService partnerService; 
    /**
     * 初始化方法
     * @throws SystemInitException 
     */
    public void init() throws SystemInitException {
        // 检查注入参数完整性
        if (StringUtils.isBlank(payCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：payCallBackURL");
        } else {
            logger.info("初始化系统配置信息：payCallBackURL=" + payCallBackURL);
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
            logger.info("初始化系统配置信息：qRCodeRootPath=" + qRCodeRootPath);
        }
        
        // 初始化系统静态配置
        SysConfig.payCallBackURL = payCallBackURL;
        SysConfig.bindCallBackURL = bindCallBackURL;
        SysConfig.qRCodeRootPath = qRCodeRootPath;

        // 加载状态为非冻结服务商配置信息到内存，FIXME 考虑清除和更新的问题
        List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(null);
        if (topPartnerList == null || topPartnerList.size() < 1) {
            throw new SystemInitException("初始化系统配置信息失败，没有查到非冻结的顶级服务商");
        } else {
            logger.info("当前系统非冻结的顶级服务商个数：" + topPartnerList.size() + "，开始初始化");
        }
        
        for(Partner topPartner : topPartnerList) {
            Map<String, String> pMap = new HashMap<String, String>();
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
            pMap.put(SysEnvKey.CERT_PASSWORD, topPartner.getKeyPassword());
            pMap.put(SysEnvKey.WX_KEY, topPartner.getPartnerKey());
            partnerConfigMap.put(topPartner.getIwoid(), pMap);
        }
    }

    @Override
    public Map<String, String> getPartnerCofigInfoByPartnerOid(String partnerOid) {
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "partnerOid不能为空");
        if (!partnerConfigMap.containsKey(partnerOid)) {
            Map<String, String> pMap = new HashMap<String, String>();
            
            logger.info("缓存中服务商信息不存在，partnerOid=" + partnerOid + "开始从数据库查找");
            List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(partnerOid);
            if (topPartnerList == null || topPartnerList.isEmpty() || topPartnerList.get(0) == null) {
                logger.error("数据库使用中的服务商信息不存在，partnerOid=" + partnerOid);
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
                    pMap.put(SysEnvKey.CERT_PASSWORD, topPartner.getKeyPassword());
                    pMap.put(SysEnvKey.WX_KEY, topPartner.getPartnerKey());
                    partnerConfigMap.put(partnerOid, pMap);
                }
            }
            return pMap;
        } else {
            return partnerConfigMap.get(partnerOid);
        }
    }

    @Override
    public Map<String, String> getPartnerCofigInfoByOutTradeNo(String outTradeNo) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPayCallBackURL(String payCallBackURL) {
        this.payCallBackURL = payCallBackURL;
    }
    
    public void setWxPayNotifyURL(String wxPayNotifyURL) {
        this.wxPayNotifyURL = wxPayNotifyURL;
    }

    public void setBindCallBackURL(String bindCallBackURL) {
        this.bindCallBackURL = bindCallBackURL;
    }

    public void setqRCodeRootPath(String qRCodeRootPath) {
        this.qRCodeRootPath = qRCodeRootPath;
    }

    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

}
