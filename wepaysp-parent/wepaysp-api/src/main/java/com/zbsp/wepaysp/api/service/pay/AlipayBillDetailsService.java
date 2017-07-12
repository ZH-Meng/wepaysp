package com.zbsp.wepaysp.api.service.pay;

import java.util.List;

import com.zbsp.wepaysp.po.pay.AlipayBillDetails;

/**
 * 支付宝账单明细service 
 */
public interface AlipayBillDetailsService {

    /**
     * 批量保存账单明细
     */
    public void doTransBatchAdd(List<AlipayBillDetails> billDetailsList);
    
}
