package com.zbsp.wepaysp.api.util;

import com.zbsp.alipay.trade.service.AlipayTradeService;
import com.zbsp.wepaysp.api.service.SysConfig;

/**
 * 支付宝支付接口工具类
 * 
 * @author 孟郑宏
 */
public class AliPayUtil {
    
    /** 获取缺省AliPay交易服务，公共参数已通过配置文件构建*/
    public static AlipayTradeService getDefaultAlipayTradeService() {
        return SysConfig.tradeService;
    }
    
}
