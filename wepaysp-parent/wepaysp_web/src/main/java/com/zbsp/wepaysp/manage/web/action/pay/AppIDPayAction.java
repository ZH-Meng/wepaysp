package com.zbsp.wepaysp.manage.web.action.pay;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.zbsp.wepaysp.common.constant.WxApiUrl;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.http.common.HttpConfig;
import com.zbsp.wepaysp.common.http.common.HttpConfig.ParamType;
import com.zbsp.wepaysp.common.http.exception.HttpProcessException;
import com.zbsp.wepaysp.common.http.httpclient.HttpClientUtil;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.vo.wxauth.AccessTokenResultVO;
import com.zbsp.wepaysp.manage.web.vo.wxauth.JSPayReqData;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;
import com.zbsp.wepaysp.vo.partner.PartnerVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 公众号支付
 * 
 * @author 孟郑宏
 */
public class AppIDPayAction
    extends BaseAction
    implements SessionAware {

    private static final long serialVersionUID = -7528241554406736862L;
    private Map<String, Object> session;

    private String partnerOid;
    private String dealerOid;
    private String dealerName;
    private String storeOid;
    private String money;

    /**
     * code作为换取access_token和openid的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。<br>
     * 若用户禁止授权，则重定向后不会带上code参数
     */
    private String code;
    /** 用户在公众号的标识 */
    private String openid;
    private String wxPayNotifyURL;
    /** JS支付 请求包 */
    private JSPayReqData jsPayReqData;

    private DealerService dealerService;
    private PartnerService partnerService;
    private WeixinPayDetailsMainService weixinPayDetailsMainService;

    /**
     * 微信浏览器授权后回调，目前使用公众号支付不需要access_token，只是下单时需要access_token<br>
     * 其他：微信公众平台开发（其中有一个支付接口）会用access_token（有时效性，调用API有限），需要考虑 access_token缓存或刷新方案
     * 
     * @return
     */
    public String wxCallBack() {
        logger.info("微信支付开始回调...");
        if (!checkCallBankParam()) {
            return ERROR;
        }
        // 根据partnerOid查找APPID、SECRET
        PartnerVO accessPartner = partnerService.doJoinTransQueryPartnerByOid(partnerOid);// FIXME 改为先从内存获取
        if (accessPartner == null) {
            logger.warn("微信支付回调访问的服务商不存在，partnerOid：" + partnerOid);
            return ERROR;
        }
        // FIXME 通过PartnerVO获取
        String appid = "wx8a60a03a3b75acf7";
        String appsecret = "0f7dbf1be06f76af581f5a17058d09d6";
        String getAccessTokenURL = WxApiUrl.JSAPI_GET_ACCESS_TOKEN.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
        // String refreshAccessTokenURL = WxApiUrl.JSAPI_REFRESH_ACCESS_TOKEN.replace("APPID", "").replace("REFRESH_TOKEN", "");// 刷新access_token的URL设置方式

        HttpConfig httpConfig = HttpConfig.custom(ParamType.NONE);// 参数在URL拼接完成
        AccessTokenResultVO accessTokenResult = null;
        try {
            // 调用API获取access_token、openid
            String jsonResult = HttpClientUtil.get(httpConfig.url(getAccessTokenURL).encoding("UTF-8"));
            // 转化JSON结果
            accessTokenResult = JSONUtil.parseObject(jsonResult, AccessTokenResultVO.class);
            // 校验获取access_token、openid结果
            if (checkAccessTokenResult(accessTokenResult) && StringUtils.isNotBlank(accessTokenResult.getOpenid())) {
                // 设置openid，下单时需要，暂通过request及前台隐藏于传递给下单请求
                openid = accessTokenResult.getOpenid();
            } else {
                logger.warn("获取openid失败");
                return ERROR;
            }
        } catch (HttpProcessException e) {
            logger.warn("http请求错误，" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        logger.info("微信支付回调成功.");
        return "wxCallBack";
    }

    /**
     * 创建订单->微信下单->更新订单->跳转字符
     * 
     * @return
     */
    public String createOrder() {
        logger.info("开始微信下单.");
        if (StringUtils.isBlank(money) && NumberUtils.isNumber(money)) {
            logger.warn("金额无效！");
            setAlertMessage("金额无效，请重新输入，单位为元！");
            return "wxCallBack";
        }
        if (StringUtils.isBlank(dealerOid) || StringUtils.isBlank(openid)) {
            logger.warn("商户Oid和openid都不能为空！");
            setAlertMessage("参数缺失，请重试！");
            // FIXME 重新准备隐藏信息
            return "wxCallBack";
        }
        if (StringUtils.isBlank(wxPayNotifyURL)) {
            logger.debug("微信支付统一下单通知URL为空！");
            return ERROR;
        }

        // 保存交易明细
        WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
        payDetailsVO.setPayType(WeixinPayDetails.PayType.JSAPI.getValue());// 交易类型公众号
        payDetailsVO.setDealerOid(dealerOid);
        payDetailsVO.setOpenid(openid);
        payDetailsVO.setStoreId(storeOid);
        payDetailsVO.setNotifyUrl(wxPayNotifyURL);// 通知地址

        BigDecimal orderMoney = new BigDecimal(money);// 订单金额
        payDetailsVO.setTotalFee(orderMoney.multiply(new BigDecimal(100)).intValue());// 元转化为分

        try {
            Map<String, Object> resultMap = weixinPayDetailsMainService.createPayAndInvokeWxPay(payDetailsVO, null, null, null);
            String resCode = MapUtils.getString(resultMap, "resultCode");
            String resDesc = MapUtils.getString(resultMap, "resultDesc");
            jsPayReqData = (JSPayReqData) MapUtils.getObject(resultMap, "jsPayRequestData");// JS支付接口请求参数包

            if (!StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), resCode)) {// 公众号下单失败
                logger.warn("公众号下单失败，错误码：" + resCode + "，错误描述：" + resDesc);
                setAlertMessage(resDesc);
            } else {
                logger.info("公众号下单成功，跳转支付页面！");
            }

        } catch (InvalidValueException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
        } catch (NotExistsException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setAlertMessage("下单失败！");
        }

        return "JSPAY";
    }

    /**
     * 检查微信网页授权回调的系统URL中参数是否完整和正确
     * 
     * @return
     */
    private boolean checkCallBankParam() {
        if (StringUtils.isBlank(code)) {
            logger.warn("用户访问公众号，选择禁止授权.");
            // TODO 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
            return false;
        }
        // 校验商户等参数
        if (StringUtils.isBlank(partnerOid) && StringUtils.isBlank(dealerOid)) {
            logger.warn("授权链接有误或者回调错误，导致参数缺失，partnerOid：" + partnerOid + ",dealerOid：" + dealerOid);
            return false;
        }
        // 获取并校验商户信息，回显到页面
        DealerVO accessDealer = dealerService.doJoinTransQueryDealerByOid(dealerOid);
        if (accessDealer == null) {
            logger.warn("微信支付回调访问的商户不存在，dealerOid：" + dealerOid);
            return false;
        } else {
            dealerName = accessDealer.getCompany();
        }
        return true;
    }

    /**
     * 校验http get 获取access_token的结果
     * 
     * @param accessTokenResultVO
     * @return
     */
    private boolean checkAccessTokenResult(AccessTokenResultVO accessTokenResultVO) {
        boolean result = false;
        if (accessTokenResultVO == null) {
            logger.warn("accessTokenResultVO为空");
        } else {
            logger.debug(accessTokenResultVO.toString());
        }
        if (StringUtils.isNotBlank(accessTokenResultVO.getAccess_token()) && accessTokenResultVO.getExpires_in() != null && StringUtils.isNotBlank(accessTokenResultVO.getRefresh_token()) && StringUtils.isNotBlank(accessTokenResultVO.getOpenid())) {
            result = true;
        } else if (StringUtils.isNotBlank(accessTokenResultVO.getErrcode())) {
            result = true;
        } else {
            logger.warn("get or refresh access_token result invalid");
        }
        return result;
    }

    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }
    
    public void setWxPayNotifyURL(String wxPayNotifyURL) {
        this.wxPayNotifyURL = wxPayNotifyURL;
    }

    public JSPayReqData getJsPayReqData() {
        return jsPayReqData;
    }

    public void setDealerService(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    public void setWeixinPayDetailsMainService(WeixinPayDetailsMainService weixinPayDetailsMainService) {
        this.weixinPayDetailsMainService = weixinPayDetailsMainService;
    }

}
