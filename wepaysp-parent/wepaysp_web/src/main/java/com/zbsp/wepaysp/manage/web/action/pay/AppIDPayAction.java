package com.zbsp.wepaysp.manage.web.action.pay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 公众号支付
 * 
 * @author 孟郑宏
 */
public class AppIDPayAction
    extends BaseAction {

    private static final long serialVersionUID = -7528241554406736862L;

    private String dealerOid;
    private String storeOid;
    private String money;
    private WeixinPayDetailsService weixinPayDetailsService;

    private WeixinPayDetailsVO weixinPayDetailsVO;
    
    /**
     * 微信浏览器回调
     * 
     * @return
     */
    public String wxCallBack() {
        logger.info("微信支付开始回调...");

        // 商户标识

        // 授权信息

        return "wxCallBack";
    }

    /**
     * 创建订单->微信下单->更新订单->跳转字符
     * 
     * @return
     */
    public String createOrder() {
        //TODO 获取当前微信版本号 
        logger.info("开始创建订单.");        
        BigDecimal orderMoney = new BigDecimal(money);// 订单金额
        String orderDealer = dealerOid;// 订单商户
        // 通知地址、门店Oid
        
        weixinPayDetailsVO = new WeixinPayDetailsVO();
        weixinPayDetailsVO.setPayType(WeixinPayDetails.PayType.JSAPI.getValue());//交易类型公众号
        
        
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 保存交易明细
        weixinPayDetailsService.doTransCreatePayDetails(weixinPayDetailsVO, "WXBrower", null, null);
        
        // 提交微信支付
        
        // 更新交易明细
        
        return "cashierDesk";
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

}
