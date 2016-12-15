package com.zbsp.wepaysp.manage.web.action.pay;

import com.zbsp.wepaysp.manage.web.action.BaseAction;

/**
 * 微信支付通知绑定-不能登录拦截
 * 
 * @author 孟郑宏
 */
public class AppIDBindAction
    extends BaseAction {

    /**
     * 微信扫码后，点击确认登录（同意授权）微信回调地址
     * 
     * @return
     */
    public String bindWxIDCallBack() {
        logger.info("微信支付通知绑定微信账户微信回调成功");
        logger.info("微信支付通知绑定微信账户开始");
        return "bindWxID";
    }
}
