package com.zbsp.wepaysp.api.service.pay.impl;

import java.util.List;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AlipayBillDetailsService;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.pay.AlipayBillDetails;


public class AlipayBillDetailsServiceImpl
    extends BaseService
    implements AlipayBillDetailsService {

    @Override
    public void doTransBatchAdd(List<AlipayBillDetails> billDetailsList) {
        Validator.checkArgument(billDetailsList == null, "billDetailsList不能为空");
        commonDAO.saveList(billDetailsList, 100);
    }

}
