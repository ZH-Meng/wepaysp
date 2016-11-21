package com.zbsp.wepaysp.manage.web.action.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.xml.sax.SAXException;

import com.tencent.common.Signature;
import com.tencent.common.XMLParser;
import com.zbsp.wepaysp.common.constant.WxApiUrl;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.util.WxPayResultSimulator;
import com.zbsp.wepaysp.manage.web.vo.wxauth.AccessTokenResultVO;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
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

    private String dealerOid;
    private String storeOid;
    private String money;
    private WeixinPayDetailsService weixinPayDetailsService;

    private WeixinPayDetailsVO weixinPayDetailsVO;

    /**
     * code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。<br>
     * 若用户禁止授权，则重定向后不会带上code参数
     */
    private String code;
    private Map<String, Object> session;

    /**
     * 微信浏览器回调
     * 
     * @return
     */
    public String wxCallBack() {
        logger.info("微信支付开始回调...");
        if (StringUtils.isBlank(code)) {
            logger.warn("用户访问公众号，选择禁止授权.");
            // TODO 因为scope 选择snsapi_base，不弹出授权页面，直接跳转，所以暂不处理此情况
        }
        /*
         * 调用API获取access_token、openid 访问 
         * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
         */
        // TODO
        
        //模拟返回JSON
        String jsonResult = WxPayResultSimulator.jsAPIAuthSucc(WxApiUrl.JSAPI_ACCESS_TOKEN);
        AccessTokenResultVO accessTokenResult = JSONUtil.parseObject(jsonResult, AccessTokenResultVO.class);
        
        session.put("wx_access_token", accessTokenResult.getAccess_token());
        session.put("wx_refresh_token", accessTokenResult.getRefresh_token());
        session.put("wx_openid", accessTokenResult.getOpenid());
        session.put("wx_unionid", accessTokenResult.getUnionid());
        
        // access_token的缓存或刷新方案
        
        // 商户标识

        return "wxCallBack";
    }

    /**
     * 创建订单->微信下单->更新订单->跳转字符
     * 
     * @return
     */
    public String createOrder() {
        logger.info("开始微信下单.");
        BigDecimal orderMoney = new BigDecimal(money);// 订单金额
        String orderDealer = dealerOid;// 订单商户
        // 通知地址、门店Oid

        weixinPayDetailsVO = new WeixinPayDetailsVO();
        weixinPayDetailsVO.setPayType(WeixinPayDetails.PayType.JSAPI.getValue());// 交易类型公众号

        // 保存交易明细
        weixinPayDetailsVO = weixinPayDetailsService.doTransCreatePayDetails(weixinPayDetailsVO, "WXBrower", null, null);

        // 提交微信支付

        // 微信支付响应处理

        // 模拟
        String resultXml = "<xml>"
               + "<return_code><![CDATA[SUCCESS]]></return_code>"
               + "<return_msg><![CDATA[OK]]></return_msg>"
               + "<appid><![CDATA[wx2421b1c4370ec43b]]></appid>"
               + "<mch_id><![CDATA[10000100]]></mch_id>"
               + "<nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>"
               + "<sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>"
               + "<result_code><![CDATA[SUCCESS]]></result_code>"
               + "<prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>"
               + "<trade_type><![CDATA[JSAPI]]></trade_type>"
            + "</xml>";
        
        Map<String, Object> resultMap = null;
        try {
            // 转化响应Map
            resultMap = XMLParser.getMapFromXML(resultXml);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        
        String returnCode = MapUtils.getString(resultMap, "return_code");
        if ("SUCCESS".equals(returnCode)) {// 下单请求通信成功
            // 校验签名
            String sign = MapUtils.getString(resultMap, "sign");
            /*String checkSign = Signature.getSign(resultMap);
            if (sign.equals(checkSign)) {// 签名成功
                // 更新交易明细
            } else {
                logger.warn("微信下单回包签名校验失败！");
                return ERROR;
            }*/
        } else {
            logger.error("微信下单通信失败：" + resultMap.get("return_msg"));
            return ERROR;
        }

        return "JSPAY";
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

}
