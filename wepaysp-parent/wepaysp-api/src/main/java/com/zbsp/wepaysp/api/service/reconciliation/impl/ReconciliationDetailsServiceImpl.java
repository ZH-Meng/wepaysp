package com.zbsp.wepaysp.api.service.reconciliation.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.reconciliation.ReconciliationDetailsService;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.po.reconciliation.ReconciliationDetails;

public class ReconciliationDetailsServiceImpl extends BaseService implements ReconciliationDetailsService {

	@Override
	public void doTransReconciliation(Date reconciliationDate) {

		List<ReconciliationDetails> reconciliationDetailsList = new ArrayList<ReconciliationDetails>();

		Date startTime = TimeUtil.getDayStart(reconciliationDate);
		Date endTime = TimeUtil.getDayEnd(reconciliationDate);
 
		HashMap<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		List<?> reList = commonDAO.findObjectListByNamedQuery("reconciliation", paramMap);

		for (Iterator<?> it = reList.iterator(); it.hasNext();) {
			 Object[] curRow = (Object[]) it.next(); 
			 Timestamp tradeTime = (Timestamp) curRow[0];
			 String outTradeNo = (String) curRow[1];
			 String transactionId = (String) curRow[2];
			 BigInteger totalFee = (BigInteger) curRow[3];
			 BigInteger tradeStatus = (BigInteger) curRow[4];
			 String  resultCode = (String) curRow[5];
			 BigInteger billTotalFee = (BigInteger) curRow[6];
			 String billResultCode = (String) curRow[7];
			 BigInteger reconciliationResult = (BigInteger) curRow[8];
			 BigInteger payPlatform = (BigInteger) curRow[9];
			 
			 ReconciliationDetails detail = new ReconciliationDetails();
			 detail.setIwoid(Generator.generateIwoid());
			 detail.setBillResultCode(billResultCode);
			 detail.setBillTotalFee(billTotalFee.intValue());
			 detail.setOutTradeNo(outTradeNo);
			 detail.setPayPlatform(payPlatform.intValue());
			 detail.setReconciliationResult(reconciliationResult.intValue());
			 detail.setResultCode(resultCode);
			 detail.setTotalFee(totalFee.intValue());
			 detail.setTradeStatus(tradeStatus.intValue());
			 detail.setTradeTime(tradeTime);
			 detail.setTransactionId(transactionId);
			 
			 reconciliationDetailsList.add(detail);			 
		}
		commonDAO.saveList(reconciliationDetailsList);
	 

	}

}
