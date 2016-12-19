package com.zbsp.wepaysp.manage.web.action.appid;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.EnumDefine.PayClientType;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.vo.ErrResult;

/**
 * 移动端支付入口
 * 
 * @author 孟郑宏
 */
public class MobliePayIndexAction
    extends BaseAction {

    private static final long serialVersionUID = -1247787696462196268L;

    /** 中转参数 */
    private String partnerOid;
    private String dealerOid;
    private String storeOid;
    private String dealerEmployeeOid;

    private String payClient;
    private String payUrl;

    /**
     * 支付客户端检测
     * 
     * @return
     */
    public String payClientCheck() {
        logger.info("开始检查支付客户端.");
        // 检查二维码包含的支付参数完整性
        if (StringUtils.isBlank(partnerOid) || StringUtils.isBlank(dealerOid) || StringUtils.isBlank(storeOid)) {
            logger.warn("支付参数不完整，partnerOid=" + partnerOid + "，dealerOid=" + dealerOid +"，storeOid=" + storeOid);
            setErrResult(new ErrResult("param_miss", "参数缺失"));
            return "accessDeniedH5";
        }
        
        HttpServletRequest httpRequest = ServletActionContext.getRequest();
        String userAgent = httpRequest.getHeader("User-Agent").toLowerCase();
        if (userAgent.indexOf("micromessenger") != -1) {
            logger.info("支付客户端为微信");
            Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partnerOid);
            if (partnerMap == null || partnerMap.isEmpty()) {
                logger.warn("服务商信息配置不存在，partnerOid=" + partnerOid);
                setErrResult(new ErrResult("param_invalid", "参数无效"));
                return "accessDeniedH5";
            }
            String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);// 微信公众号ID
            Map<String, String> urlParamMap = new HashMap<String, String>();
            urlParamMap.put("partnerOid", partnerOid);
            urlParamMap.put("dealerOid", dealerOid);
            urlParamMap.put("storeOid", storeOid);
            urlParamMap.put("dealerEmployeeOid", dealerEmployeeOid);
            urlParamMap.put("showwxpaytitle", "1");
            
            payClient = PayClientType.APP_WEIXIN.getValue();
            payUrl = Generator.generatePayURL(payClient, appid, SysConfig.wxPayCallBackURL, urlParamMap);
        } else if (userAgent.indexOf("alipayclient") != -1) {
            logger.info("支付客户端为支付宝");
            payClient = PayClientType.APP_ALI.getValue();
            
        } else {// 未知客户端
            payClient = PayClientType.UN_KNOWN.getValue();
        }
        logger.info("检查支付客户端结束.");
        return "payClientCheckResult";
    }

    public String getPayClient() {
        return payClient;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

}
