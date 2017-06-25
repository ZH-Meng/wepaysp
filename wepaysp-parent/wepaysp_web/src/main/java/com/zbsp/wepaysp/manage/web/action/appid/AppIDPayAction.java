package com.zbsp.wepaysp.manage.web.action.appid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;

import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.constant.WxEnums.GrantType;
import com.zbsp.wepaysp.common.constant.WxEnums.WxPayResult;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.vo.ErrResult;
import com.zbsp.wepaysp.manage.web.vo.appid.CreateOrderResult;
import com.tencent.WXPay;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenReqData;
import com.tencent.protocol.appid.sns_access_token_protocol.GetAuthAccessTokenResData;
import com.tencent.protocol.unified_order_protocol.JSPayReqData;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.partner.DealerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 公众号支付
 * 
 * @author 孟郑宏
 */
public class AppIDPayAction
    extends BaseAction {

    private static final long serialVersionUID = -7528241554406736862L;

    private String partnerOid;
    private String dealerOid;
    private String dealerName;
    private String storeOid;
    private String dealerEmployeeOid;
    private String money;

    /** -----授权回调回传-静默用户同意------ */
    /**
     * code作为换取access_token和openid的票据，每次用户授权带上的code将不一样，code只能使用一次，（5分钟未被使用自动过期，不确定）<br>
     * 若用户禁止授权，则重定向后不会带上code参数
     */
    private String code;
    //private String state;
    
    /** 用户在公众号的标识 */
    private String openid;
    /** JS支付 请求包 */
    private JSPayReqData jsPayReqData;

    private String weixinPayDetailOid;
    private String payResult;
    private WeixinPayDetailsVO weixinPayDetailsVO;
    private String tradeStatus;
    private DealerService dealerService;
    private WeixinPayDetailsMainService weixinPayDetailsMainService;
    
    /**
     * 微信浏览器授权后回调，目前使用公众号支付不需要access_token，只是下单时需要access_token<br>
     * 其他：微信公众平台开发（其中有一个支付接口）会用access_token（有时效性，调用API有限），需要考虑 access_token缓存或刷新方案
     * 
     * @return
     */
    public String wxCallBack() {
        logger.info("微信支付开始回调...");
        if (!checkCallBackParam()) {
            return "accessDeniedH5";
        }
        
        if (StringUtils.isBlank(partnerOid)) {
        	logger.error("微信支付非法回调，partnerOid为空");
        	setErrResult(new ErrResult("param_miss", "参数缺失"));
        	return "accessDeniedH5";
        }
        
        // 从内存中获取服务商配置信息
        Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(partnerOid);
        if (partnerMap == null || partnerMap.isEmpty()) {
            logger.error("微信支付通知绑定微信账户微信回调访问的服务商不存在，partnerOid：" + partnerOid);
            setErrResult(new ErrResult("param_invalid", "参数无效"));
            return "accessDeniedH5";
        }
        
        // 通过code换取网页授权access_token 和 openid
        GetAuthAccessTokenReqData authReqData = new GetAuthAccessTokenReqData(GrantType.AUTHORIZATION_CODE.getValue(), 
            MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID), MapUtils.getString(partnerMap, SysEnvKey.WX_SECRET), code, null);
        
        GetAuthAccessTokenResData authResult = null;
        try {
            logger.info("开始获取网页授权access_token 和 openid");
            
            String jsonResult = WXPay.requestGetAuthAccessTokenService(authReqData, 
                MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH), MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
            authResult = JSONUtil.parseObject(jsonResult, GetAuthAccessTokenResData.class);
            
            // 校验获取access_token
            if (checkAccessTokenResult(authResult)) {
            	// 设置openid，下单时需要，暂通过request及前台隐藏于传递给下单请求
                openid = authResult.getOpenid();
                logger.info("auth_access_token：" + authResult.getAccess_token() + "，expires_in：" + authResult.getExpires_in() + ",openid = " + openid);
                // TODO 设置过期时间
                /*由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
                refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
                如果需要定期同步用户的昵称，则需要考虑刷新access_token*/
            } else {
                logger.warn("获取网页授权Access_token失败，错误码：" + authResult.getErrcode() + "，错误描述：" + authResult.getErrmsg());
                setErrResult(new ErrResult("access_token_fail", "授权失败"));
                return "accessDeniedH5";
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(),
                "获取网页授权Access_token失败", "，异常信息：" + e.getMessage()));
            logger.error(e.getMessage(), e);
            setErrResult(new ErrResult("sys_error", "系统或网络异常"));
            return "accessDeniedH5";
        }
        logger.info("微信支付回调成功.");
        return "wxCallBack";
    }
    
    /**
     * 创建订单->微信下单->更新订单->跳转字符
     * 
     * @return
     * @throws IOException 
     */
    public void createOrder() throws IOException {
        logger.info("开始微信下单.");
        HttpServletResponse response=ServletActionContext.getResponse();
        response.setContentType("text/html;charset=UTF-8"); 
        PrintWriter out = response.getWriter();  
        CreateOrderResult result = null;
        if (StringUtils.isBlank(money) && NumberUtils.isNumber(money)) {
            /*logger.warn("金额无效！");
            setAlertMessage("金额无效，请重新输入，单位为元！");
            return "wxCallBack";*/
            result = new CreateOrderResult("moneyInvalid", "金额无效，请重新输入，单位为元！", null, null);
        }
        if (StringUtils.isBlank(dealerOid) || StringUtils.isBlank(openid)) {
            /*logger.warn("商户Oid和openid都不能为空！");
            setAlertMessage("参数缺失，请重试！");
            // FIXME 非法提交订单时，重新准备隐藏信息
            return "wxCallBack";*/
            result = new CreateOrderResult("paramMiss", "参数缺失，请重试！", null, null);

        }
        if (StringUtils.isBlank(SysConfig.wxPayNotifyURL)) {
            logger.error("系统配置异常：微信支付统一下单通知URL为空！");
            //return ERROR;
            result = new CreateOrderResult("error", "系统错误！", null, null);
        }

        // 保存交易明细
        WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
        payDetailsVO.setPayType(PayType.WEIXIN_JSAPI.getValue());// 交易类型公众号
        payDetailsVO.setDealerOid(dealerOid);
        payDetailsVO.setOpenid(openid);
        payDetailsVO.setStoreOid(storeOid);// 一店一码时，不为空
        payDetailsVO.setDealerEmployeeOid(dealerEmployeeOid);// 一收银员一码时，不为空
        payDetailsVO.setNotifyUrl(SysConfig.wxPayNotifyURL);// 通知地址

        BigDecimal orderMoney = new BigDecimal(money);// 订单金额
        payDetailsVO.setTotalFee(orderMoney.multiply(new BigDecimal(100)).intValue());// 元转化为分

        try {
            Map<String, Object> resultMap = weixinPayDetailsMainService.createPayAndInvokeWxPay(payDetailsVO, openid, null, null);
            String resCode = MapUtils.getString(resultMap, "resultCode");
            String resDesc = MapUtils.getString(resultMap, "resultDesc");
            payDetailsVO = (WeixinPayDetailsVO) MapUtils.getObject(resultMap, "wexinPayDetailsVO");
            weixinPayDetailOid = payDetailsVO != null ? payDetailsVO.getIwoid() : "";
            jsPayReqData = (JSPayReqData) MapUtils.getObject(resultMap, "jsPayReqData");// JS支付接口请求参数包

            if (!StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), resCode)) {// 公众号下单失败
                logger.warn("公众号下单失败，错误码：" + resCode + "，错误描述：" + resDesc);
                /*setAlertMessage(resDesc);
                return "wxCallBack";*/
                result = new CreateOrderResult(resCode, resDesc, null, null);
            } else {
                result = new CreateOrderResult("success", "下单成功", jsPayReqData, weixinPayDetailOid);
                logger.info("公众号下单成功，跳转支付页面！");
            }

        } catch (InvalidValueException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
            result = new CreateOrderResult("error", "下单失败", null, null);
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
            result = new CreateOrderResult("error", "下单失败", null, null);
        } catch (NotExistsException e) {
            logger.warn(e.getMessage());
            setAlertMessage("下单失败！");
            result = new CreateOrderResult("error", "下单失败", null, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setAlertMessage("下单失败！");
            result = new CreateOrderResult("error", "下单错误", null, null);
        }
        out.println(JSONUtil.toJSONString(result, false));
        //return "JSPAY";
    }
    
    /**
     * 微信异步通知支付结果
     */
    public void wxPayNotify() {// FIXME 迁移至restFul 接口
        // 校验签名
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        StringBuffer xmlStr = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();

            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }
            logger.info("微信支付结果通知内容：" + xmlStr.toString());
            
            String resultXML = weixinPayDetailsMainService.handleWxPayNotify(xmlStr.toString());
            response.getWriter().write(resultXML);
            logger.info("系统处理支付结果通知：" + resultXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * H5支付结果返回后检查真实交易状态
     * @return
     */
    public String jsPayResult() {
        // 根据 weixinPayDetailOid 查询系统真实结果
        try {
            Map<String, Object> resultMap = weixinPayDetailsMainService.checkPayResult(payResult, weixinPayDetailOid);
            tradeStatus = MapUtils.getString(resultMap, "tradeStatus");
            weixinPayDetailsVO = (WeixinPayDetailsVO) MapUtils.getObject(resultMap, "weixinPayDetailsVO");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "jsPayResult";
    }
    
    /**
     * 检查微信网页授权回调的系统URL中参数是否完整和正确
     * 
     * @return
     */
    private boolean checkCallBackParam() {
        if (StringUtils.isBlank(code)) {
            logger.warn("用户访问公众号，选择禁止授权.");
            // 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
            return false;
        }
        // 校验商户等参数
        if (StringUtils.isBlank(partnerOid) || StringUtils.isBlank(dealerOid)) {
            logger.warn("授权链接有误或者回调错误，导致参数缺失，partnerOid：" + partnerOid + ",dealerOid：" + dealerOid);
            setErrResult(new ErrResult("param_miss", "参数缺失"));
            return false;
        }
        // 获取并校验商户信息，回显到页面
        DealerVO accessDealer = dealerService.doJoinTransQueryDealerByOid(dealerOid);
        if (accessDealer == null) {
            logger.warn("微信支付回调访问的商户不存在，dealerOid：" + dealerOid);
            setErrResult(new ErrResult("param_invalid", "参数无效"));
            return false;
        } else {
            dealerName = accessDealer.getCompany();
        }
        return true;
    }

    /**
     * 校验http get 获取access_token的结果
     * 
     * @param getAuthAccessTokenResData
     * @return
     */
    private boolean checkAccessTokenResult(GetAuthAccessTokenResData getAuthAccessTokenResData) {
        boolean result = false;
        if (getAuthAccessTokenResData == null) {
            logger.warn("getBaseAccessTokenResData为空");
        } else {
            logger.debug(getAuthAccessTokenResData.toString());
            if (StringUtils.isNotBlank(getAuthAccessTokenResData.getAccess_token()) && StringUtils.isNotBlank(getAuthAccessTokenResData.getOpenid())) {
                result = true;
            } else if (StringUtils.isNotBlank(getAuthAccessTokenResData.getErrcode())) {
                result = false;
            } else {
                logger.warn("get auth access_token result invalid");
            }
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
    
    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }
    
    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    public void setMoney(String money) {
        this.money = money;
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
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getWeixinPayDetailOid() {
        return weixinPayDetailOid;
    }
    
    public void setWeixinPayDetailOid(String weixinPayDetailOid) {
        this.weixinPayDetailOid = weixinPayDetailOid;
    }

    public JSPayReqData getJsPayReqData() {
        return jsPayReqData;
    }
    
    public String getPayResult() {
        return payResult;
    }
    
    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }
    
    public String getTradeStatus() {
        return tradeStatus;
    }
    
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public WeixinPayDetailsVO getWeixinPayDetailsVO() {
        return weixinPayDetailsVO;
    }

    public void setDealerService(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    public void setWeixinPayDetailsMainService(WeixinPayDetailsMainService weixinPayDetailsMainService) {
        this.weixinPayDetailsMainService = weixinPayDetailsMainService;
    }

}
