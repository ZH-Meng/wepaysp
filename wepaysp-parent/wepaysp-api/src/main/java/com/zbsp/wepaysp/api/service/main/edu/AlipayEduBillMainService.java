package com.zbsp.wepaysp.api.service.main.edu;

import java.util.Map;

/**
 * 支付宝教育缴费service（不带事务）
 */
public interface AlipayEduBillMainService {

    /**处理异步通知*/
    public Map<String, Object> handleAlipayPayEduBillNotify(Map<String, String> paramMap);
    
}
