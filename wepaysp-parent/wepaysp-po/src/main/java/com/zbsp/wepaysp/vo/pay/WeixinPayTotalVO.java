package com.zbsp.wepaysp.vo.pay;

import java.io.Serializable;

/**
 * 微信支付合计VO
 * 
 * @author 孟郑宏
 */
public class WeixinPayTotalVO
    implements Serializable {

    private static final long serialVersionUID = -6648657406583514621L;
    private long totalAmount;
    private long totalMoney;
    
    public long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public long getTotalMoney() {
        return totalMoney;
    }
    
    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }
    
}
