package com.zbsp.wepaysp.api.service.reconciliation;

import java.util.Date;

/**
 * 对账service
 */
public interface ReconciliationDetailsService {
    
    /**
     * 按日期进行对账
     * 并生成对账差异记录明细记录
     * 
     * @param reconciliationDate 对账日期
     */
    public void doTransReconciliation(Date reconciliationDate);    
  
    
}
