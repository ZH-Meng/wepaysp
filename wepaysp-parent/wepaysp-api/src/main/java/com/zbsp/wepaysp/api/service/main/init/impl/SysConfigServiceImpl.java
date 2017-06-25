package com.zbsp.wepaysp.api.service.main.init.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppService;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.ServerType;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.SystemInitException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.alipay.AlipayApp;
import com.zbsp.wepaysp.po.partner.Partner;

public class SysConfigServiceImpl
    extends BaseService
    implements SysConfigService {

    private String payClientCheckURL;
    private String wxPayCallBackURL;
    private String wxPayNotifyURL;
    private String bindCallBackURL;
    private String wxPayMessageLinkURL;
    private String qRCodeRootPath;
    private String appidQrCodePath;
    private String serverType;
    
    private String appId4Face2FacePay;
    private String alipayAuthCallBackURL;
    private String alipayWapPayURL;
    private String alipayWapPayReturnURL;
    private String alipayWapPayNotifyURL;
    private boolean alipayReportFlag;
    private boolean onlineFlag;

    private PartnerService partnerService;
    private AlipayAppService alipayAppService;
    
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
        
        // 上线开关
        SysConfig.onlineFlag=onlineFlag;
        if (onlineFlag) {
            logger.info("初始化系统配置信息：onlineFlag = true，上线开关：打开");
        } else {
            logger.info("初始化系统配置信息：onlineFlag = false，上线开关：关闭");
        }
        
        // 检查WEB、REST的必要参数及初始化
        if (ServerType.WEB_MANAGE.equals(serType)) {
            initWebManage();
        } else if (ServerType.REST.equals(serType)) {
            initRest();
        }
        
        // 支付宝支持当面付2.0的应用ID
        if (StringUtils.isBlank(appId4Face2FacePay)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：appId4Face2FacePay");
        } else {
            logger.info("初始化系统配置信息：appId4Face2FacePay=" + appId4Face2FacePay);
            SysConfig.appId4Face2FacePay = appId4Face2FacePay;
        }
        
        // 检查appId4Face2FacePay是否存在
        Map<String, Object> app = SysConfig.alipayAppMap.get(appId4Face2FacePay);
        if (app == null) {
            throw new SystemInitException("初始化系统配置信息appId4Face2FacePay错误，应用不存在appid=" + appId4Face2FacePay);   
        }
        
        if (onlineFlag && AlipayApp.AppType.SANDBOXIE.toString().equals(app.get(SysEnvKey.ALIPAY_APP_TYPE))) {
            throw new SystemInitException("初始化系统配置信息appId4Face2FacePay错误，上线不能使用沙箱应用");
        }
        
        // 支付宝支付的配置 FIXME 改为从数据库中读取
        AliPayUtil.init(app);
        
    }
    
    /** 
     * 初始化REST后台必要配置信息(微信支付、支付宝支付相关URL)<br>
     *  初始化DB服务商记录中对接微信支付系统的必要信息<br>
     *  初始化DB服务商记录中对接支付宝支付系统的必要信息
     */
    private void initRest() throws SystemInitException {
        // 支付扫码客户端检查URL
        if (StringUtils.isBlank(payClientCheckURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：payClientCheckURL");
        } else {
            logger.info("初始化系统配置信息：payClientCheckURL=" + payClientCheckURL);
            SysConfig.payClientCheckURL = payClientCheckURL;
        }
        
        // 微信公众号支付授权后回调系统下单URL
        if (StringUtils.isBlank(wxPayCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：wxPayCallBackURL");
        } else {
            logger.info("初始化系统配置信息：wxPayCallBackURL=" + wxPayCallBackURL);
            SysConfig.wxPayCallBackURL = wxPayCallBackURL;
        }
        
        // 微信统一下单传递的支付结果异步通知URL
        if (StringUtils.isBlank(wxPayNotifyURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：wxPayNotifyURL");
        } else {
            logger.info("初始化系统配置信息：wxPayNotifyURL=" + wxPayNotifyURL);
            SysConfig.wxPayNotifyURL = wxPayNotifyURL;
        }
        // 微信公众号收款消息链接URL
        if (StringUtils.isBlank(wxPayMessageLinkURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：wxPayMessageLinkURL");
        } else {
            logger.info("初始化系统配置信息：wxPayMessageLinkURL=" + wxPayMessageLinkURL);
            SysConfig.wxPayMessageLinkURL = wxPayMessageLinkURL;
        }
        
        // 收银员使用微信扫码绑定支付结果发送消息二维码授权回调URL
        if (StringUtils.isBlank(bindCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：bindCallBackURL");
        } else {
            logger.info("初始化系统配置信息：bindCallBackURL=" + bindCallBackURL);
            SysConfig.bindCallBackURL = bindCallBackURL;
        }
        
        // 支付宝手机网站支付下单URL FIXME 考虑同微信一样先授权再回调下单
        if (StringUtils.isBlank(alipayWapPayURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：alipayWapPayURL");
        } else {
            logger.info("初始化系统配置信息：alipayWapPayURL=" + alipayWapPayURL);
            SysConfig.alipayWapPayURL = alipayWapPayURL;
        }
        
        // 支付宝支付结果异步通知URL
        if (StringUtils.isBlank(alipayWapPayNotifyURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：alipayWapPayNotifyURL");
        } else {
            logger.info("初始化系统配置信息：alipayWapPayNotifyURL=" + alipayWapPayNotifyURL);
            SysConfig.alipayWapPayNotifyURL = alipayWapPayNotifyURL;
        }
        
        // 支付宝支付完成后前台回跳URL
        if (StringUtils.isBlank(alipayWapPayReturnURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：alipayWapPayReturnURL");
        } else {
            logger.info("初始化系统配置信息：alipayWapPayReturnURL=" + alipayWapPayReturnURL);
            SysConfig.alipayWapPayReturnURL = alipayWapPayReturnURL;
        }
            
        if (alipayReportFlag) {
            logger.info("初始化系统配置信息：alipayReportFlag = true, 支付宝支付交易保障服务开关：打开");
        } else {
            logger.info("初始化系统配置信息：alipayReportFlag = false, 支付宝支付交易保障服务开关：关闭");
        }
        SysConfig.alipayReportFlag = alipayReportFlag;
        
        // 服务商信息
        initTopPartnerInfos();
    }

    /** 
     * 初始化WEB后台管理必要配置信息(各路径、URL)<br>
     */
    private void initWebManage() throws SystemInitException {
        // 系统中生成二维码存放根目录
        if (StringUtils.isBlank(qRCodeRootPath)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：qRCodeRootPath");
        } else {
            File dirs = new File(qRCodeRootPath);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            logger.info("初始化系统配置信息：qRCodeRootPath=" + qRCodeRootPath);
            SysConfig.qRCodeRootPath = qRCodeRootPath;
        }
        
        // 系统中存放微信公众号二维码的绝对目录
        if (StringUtils.isBlank(appidQrCodePath)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：appidQrCodePath");
        } else {
            File dirs = new File(appidQrCodePath);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            logger.info("初始化系统配置信息：appidQrCodePath=" + appidQrCodePath);
            SysConfig.appidQrCodePath = appidQrCodePath;
        }
        
        // 商户授权支付宝应用，统一授权后回调URL
        if (StringUtils.isBlank(alipayAuthCallBackURL)) {
            throw new SystemInitException("初始化系统配置信息失败，参数缺失：alipayAuthCallBackURL");
        } else {
            logger.info("初始化系统配置信息：alipayAuthCallBackURL=" + alipayAuthCallBackURL);
            SysConfig.alipayAuthCallBackURL = alipayAuthCallBackURL;
        }
        
        initTopPartnerInfos();
        
        // FIXME 微信支付完全迁移到REST之后移除以下配置
        SysConfig.payClientCheckURL = payClientCheckURL;
        SysConfig.wxPayCallBackURL = wxPayCallBackURL;
        SysConfig.wxPayNotifyURL = wxPayNotifyURL;
        SysConfig.bindCallBackURL = bindCallBackURL;
        SysConfig.wxPayMessageLinkURL = wxPayMessageLinkURL;
    }
    
    /**初始化服务商的相关信息*/
    private void initTopPartnerInfos() throws SystemInitException {
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
            
            // FIXME 避免REST和WEB启动时都获取，会使一个无效，如果只有REST支持微信支付，可开启
            // new WeixinUtil().getBaseAccessToken(topPartner.getIwoid()); // 启动获取Base_acction_token

            if (StringUtils.isBlank(topPartner.getIsvPartnerId())) {
                throw new SystemInitException("服务商(parterOid=" + topPartner.getIwoid() + ")没有配置支付宝PID");
            }
            // 查找服务商支付宝应用
            List<AlipayApp> alipayApps = alipayAppService.doJoinTransQueryAppByPartnerOid(topPartner.getIwoid());
            
            if (alipayApps == null || alipayApps.isEmpty()) {
                throw new SystemInitException("服务商(parterOid=" + topPartner.getIwoid() + ")没有配置支付宝信息");
            }
            
            for(AlipayApp app : alipayApps) {
            	// 所有服务商所有的应用加载至内存，根据配置文件配置的当面付和手机网站appid获取对应的配置信息
                Map<String, Object> alipayMap = new HashMap<String, Object>();
                
                // 校验完整性
                if (StringUtils.isBlank(app.getAlipayPublicKey()) || StringUtils.isBlank(app.getSignType()) || StringUtils.isBlank(app.getPublicKey()) || 
                    StringUtils.isBlank(app.getPrivateKey()) || app.getMaxQueryRetry() == null || app.getQueryDuration() == null || app.getMaxCancelRetry() == null || app.getCancelDuration() == null){
                    logger.warn("服务商(parterOid={}))的支付宝应用(appid={})的信息不完整，忽略", topPartner.getIwoid(), app.getAppId());
                    continue;
                }
                
                // 服务商对应的应用ID
                if (AlipayApp.AppType.SANDBOXIE.toString().equals(app.getAppType())) {
                    pMap.put(SysEnvKey.ALIPAY_SANDBOXIE_APPID, app.getAppId());
                } else if (AlipayApp.AppType.SERVICE_WINDOW.toString().equals(app.getAppType())) {
                    pMap.put(SysEnvKey.ALIPAY_SERVICE_WINDOW__APPID, app.getAppId());
                } else if (AlipayApp.AppType.ORDINARY.toString().equals(app.getAppType())) {//FIXME 暂只支持1个普通应用
                    pMap.put(SysEnvKey.ALIPAY_ORDINARYE__APPID, app.getAppId());
                }
                alipayMap.put(SysEnvKey.ALIPAY_APP_ID, app.getAppId());
                alipayMap.put(SysEnvKey.ALIPAY_APP_TYPE, app.getAppType());
                
                alipayMap.put(SysEnvKey.ALIPAY_PUBLIC_KEY, app.getAlipayPublicKey());
                alipayMap.put(SysEnvKey.ALIPAY_APP_SIGN_TYPE, app.getSignType());
                alipayMap.put(SysEnvKey.ALIPAY_APP_PRIVATE_KEY, app.getPrivateKey());
                alipayMap.put(SysEnvKey.ALIPAY_APP_PUBLIC_KEY, app.getPublicKey());
                alipayMap.put(SysEnvKey.ALIPAY_APP_MAX_QUERY_RETRY, app.getMaxQueryRetry());
                alipayMap.put(SysEnvKey.ALIPAY_APP_QUERY_DURATION, app.getQueryDuration());
                alipayMap.put(SysEnvKey.ALIPAY_APP_MAX_CANCEL_RETRY, app.getMaxCancelRetry());
                alipayMap.put(SysEnvKey.ALIPAY_APP_CANCEL_DURATION, app.getCancelDuration());
                
                SysConfig.alipayAppMap.put(app.getAppId(), alipayMap);
                logger.info("配置服务商(parterOid={}))的支付宝应用(appid={})的信息到内存", topPartner.getIwoid(), app.getAppId());
            }
            
            logger.info("配置顶级服务商（parterOid={})的微信支付信息到内存：app_id : {}, mch_id : {}", topPartner.getIwoid(), topPartner.getAppId(), topPartner.getMchId());
            
            SysConfig.partnerConfigMap.put(topPartner.getIwoid(), pMap);
            SysConfig.partnerConfigMap2.put(topPartner.getAppId(), pMap);
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
    
    public void setAppId4Face2FacePay(String appId4Face2FacePay) {
        this.appId4Face2FacePay = appId4Face2FacePay;
    }

    public void setAlipayAuthCallBackURL(String alipayAuthCallBackURL) {
		this.alipayAuthCallBackURL = alipayAuthCallBackURL;
	}
    
    public void setAlipayWapPayReturnURL(String alipayWapPayReturnURL) {
        this.alipayWapPayReturnURL = alipayWapPayReturnURL;
    }

    public void setAlipayWapPayNotifyURL(String alipayWapPayNotifyURL) {
        this.alipayWapPayNotifyURL = alipayWapPayNotifyURL;
    }
    
    public void setAlipayWapPayURL(String alipayWapPayURL) {
        this.alipayWapPayURL = alipayWapPayURL;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    
    public void setOnlineFlag(boolean onlineFlag) {
        this.onlineFlag = onlineFlag;
    }

    public void setAlipayReportFlag(boolean alipayReportFlag) {
        this.alipayReportFlag = alipayReportFlag;
    }

    public void setWxPayMessageLinkURL(String wxPayMessageLinkURL) {
		this.wxPayMessageLinkURL = wxPayMessageLinkURL;
	}

	public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    public void setAlipayAppService(AlipayAppService alipayAppService) {
        this.alipayAppService = alipayAppService;
    }

}
