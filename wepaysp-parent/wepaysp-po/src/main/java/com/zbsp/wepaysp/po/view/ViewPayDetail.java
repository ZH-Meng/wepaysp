package com.zbsp.wepaysp.po.view;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "view_pay_detail")
public class ViewPayDetail
    implements java.io.Serializable {

    private static final long serialVersionUID = 8091626237357444081L;
    private ViewPayDetailId id;

    public ViewPayDetail() {
    }

    public ViewPayDetail(ViewPayDetailId id) {
        this.id = id;
    }

    @EmbeddedId
    @AttributeOverrides({ @AttributeOverride(name = "partnerOid", column = @Column(name = "PARTNER_OID", length = 32)),
        @AttributeOverride(name = "partnerLevel", column = @Column(name = "PARTNER_LEVEL")),
        @AttributeOverride(name = "partner1Oid", column = @Column(name = "PARTNER1_OID", length = 32)),
        @AttributeOverride(name = "partner2Oid", column = @Column(name = "PARTNER2_OID", length = 32)),
        @AttributeOverride(name = "partner3Oid", column = @Column(name = "PARTNER3_OID", length = 32)),
        @AttributeOverride(name = "partnerEmployeeOid", column = @Column(name = "PARTNER_EMPLOYEE_OID", length = 32)),
        @AttributeOverride(name = "dealerOid", column = @Column(name = "DEALER_OID", length = 32)),
        @AttributeOverride(name = "storeOid", column = @Column(name = "STORE_OID", length = 32)),
        @AttributeOverride(name = "dealerEmployeeOid", column = @Column(name = "DEALER_EMPLOYEE_OID", length = 32)),
        @AttributeOverride(name = "payType", column = @Column(name = "PAY_TYPE", length = 1)),
        @AttributeOverride(name = "transactionId", column = @Column(name = "TRANSACTION_ID", length = 32)),
        @AttributeOverride(name = "outTradeNo", column = @Column(name = "OUT_TRADE_NO", length = 32)),
        @AttributeOverride(name = "transBeginTime", column = @Column(name = "TRANS_BEGIN_TIME", length = 0)),
        @AttributeOverride(name = "transEndTime", column = @Column(name = "TRANS_END_TIME", length = 0)),
        @AttributeOverride(name = "tradeStatus", column = @Column(name = "TRADE_STATUS")),
        @AttributeOverride(name = "totalFee", column = @Column(name = "TOTAL_FEE")),
        @AttributeOverride(name = "cashFee", column = @Column(name = "CASH_FEE")),
        @AttributeOverride(name = "couponFee", column = @Column(name = "COUPON_FEE")),
        @AttributeOverride(name = "refundFee", column = @Column(name = "REFUND_FEE")) })
    public ViewPayDetailId getId() {
        return this.id;
    }

    public void setId(ViewPayDetailId id) {
        this.id = id;
    }

}
