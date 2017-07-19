package com.zbsp.wepaysp.vo.pay;

import java.io.Serializable;

/**
 * 退款合计VO
 * 
 * @author 孟郑宏
 */
public class RefundTotalVO
    implements Serializable {

    private static final long serialVersionUID = 8202170172148676424L;
    private long totalRefundAmount;
    private long totalRefundMoney;

    public long getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(long totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    public long getTotalRefundMoney() {
        return totalRefundMoney;
    }

    public void setTotalRefundMoney(long totalRefundMoney) {
        this.totalRefundMoney = totalRefundMoney;
    }

}
