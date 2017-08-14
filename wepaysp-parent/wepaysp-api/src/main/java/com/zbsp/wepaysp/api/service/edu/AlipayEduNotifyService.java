package com.zbsp.wepaysp.api.service.edu;

import com.zbsp.wepaysp.po.edu.AlipayEduNotify;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;

/** 
 * 支付宝教育缴费异步通知 service
 */
public interface AlipayEduNotifyService {
    
    /**
     * 记录通知
     * @param notifyVO
     * @return
     */
    public AlipayEduNotify doTransSaveEduNotify(AlipayWapPayNotifyVO notifyVO);
    
}
