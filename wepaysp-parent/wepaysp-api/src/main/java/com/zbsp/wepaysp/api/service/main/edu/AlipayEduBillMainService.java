package com.zbsp.wepaysp.api.service.main.edu;

import java.util.Map;

/**
 * 支付宝教育缴费service（不带事务）
 */
public interface AlipayEduBillMainService {

    /**处理异步通知*/
    public Map<String, Object> handleAlipayPayEduBillNotify(Map<String, String> paramMap);
    
    /**
     * 关闭订单 totalBillOid不为空时为全部关闭（明细），totalBillOid为空且billOid不为空时，关闭某一条明细
     * @param totalBillOid 账单Oid
     * @param billOid 明细Oid
     * @return
     */
    public Map<String, Object> closeEduBill(String totalBillOid, String billOid);
    
}
