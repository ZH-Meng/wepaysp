package com.zbsp.wepaysp.api.service.pay;

/**
 * 微信官方交易明细service
 */
public interface WeixinBillService {
    
    /**
     * 保存下载的交易明细
     * 并生成退款明细记录
	 * 更新交易记录中的退款金额
     * 
     * @param bills 下载成功的交易明细
     */
    public void doTransSaveBill(String[] bills);    
  
    
}
