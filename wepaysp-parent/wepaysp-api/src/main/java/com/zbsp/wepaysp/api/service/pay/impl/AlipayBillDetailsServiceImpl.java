package com.zbsp.wepaysp.api.service.pay.impl;

import java.util.List;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AlipayBillDetailsService;
import com.zbsp.wepaysp.api.service.pay.AlipayRefundDetailsService;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.pay.AlipayBillDetails;
import com.zbsp.wepaysp.po.pay.AlipayRefundDetails;


public class AlipayBillDetailsServiceImpl
    extends BaseService
    implements AlipayBillDetailsService {

    private AlipayRefundDetailsService alipayRefundDetailsService;
    
    @Override
    public void doTransBatchAdd(List<AlipayBillDetails> billDetailsList, List<AlipayRefundDetails> refundList) {
        Validator.checkArgument(billDetailsList == null, "billDetailsList不能为空");
        commonDAO.saveList(billDetailsList, 100);
        
        if (refundList != null && !refundList.isEmpty()) {
            logger.info(StringHelper.combinedString("有{}笔退款需要同步"), refundList.size());
            alipayRefundDetailsService.doTransBatchAdd(refundList);
        }
    }
    
    public void setAlipayRefundDetailsService(AlipayRefundDetailsService alipayRefundDetailsService) {
        this.alipayRefundDetailsService = alipayRefundDetailsService;
    }

}
