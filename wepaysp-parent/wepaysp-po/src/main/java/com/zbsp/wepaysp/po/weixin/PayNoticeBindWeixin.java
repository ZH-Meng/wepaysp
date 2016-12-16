package com.zbsp.wepaysp.po.weixin;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Store;

@Entity
@Table(name = "pay_notice_bind_weixin_t")
public class PayNoticeBindWeixin
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2586932124207327622L;
    private String iwoid;
    private DealerEmployee payDealerEmployee;
    private Store store;
    private DealerEmployee bindDealerEmployee;
    private String openid;
    private String nickname;
    private Integer sex;
    private String type;
    private String state;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public PayNoticeBindWeixin() {
    }
    
    public static enum State {
        /** 1开启 */        open("1"),
        /** 2关闭 */        closed("2");
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private State(String value) {
            this.value = value;
        }
    }
    
    public static enum Type {
        /** 1绑定门店支付通知 */        store("1"),
        /** 2绑定收银员支付通知 */     dealerEmployee("2");
        private String value;
        
        public String getValue() {
            return value;
        }
        
        private Type(String value) {
            this.value = value;
        }
    }
    
    public static enum Sex {
        /** 0未知*/    unknown(0),
        /** 1男*/        male(1),
        /** 2女*/     female(2);
        private int value;
        
        public int getValue() {
            return value;
        }
        
        private Sex(int value) {
            this.value = value;
        }
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAY_DEALER_EMPLOYEE_OID")
    public DealerEmployee getPayDealerEmployee() {
        return payDealerEmployee;
    }

    public void setPayDealerEmployee(DealerEmployee payDealerEmployee) {
        this.payDealerEmployee = payDealerEmployee;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_OID")
    public Store getStore() {
        return store;
    }
    
    public void setStore(Store store) {
        this.store = store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIND_DEALER_EMPLOYEE_OID")
    public DealerEmployee getBindDealerEmployee() {
        return bindDealerEmployee;
    }

    public void setBindDealerEmployee(DealerEmployee bindDealerEmployee) {
        this.bindDealerEmployee = bindDealerEmployee;
    }

    @Column(name = "OPENID", nullable = false, length = 32)
    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Column(name = "NICKNAME", length = 256)
    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column(name = "SEX")
    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Column(name = "TYPE", length = 1)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "STATE", nullable = false, length = 1)
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "CREATOR", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATE_TIME", nullable = false, length = 0)
    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Column(name = "MODIFIER", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Column(name = "MODIFY_TIME", length = 0)
    public Timestamp getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "REMARK", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PayNoticeBindWeixin [payDealerEmployee=" + (payDealerEmployee != null ? payDealerEmployee.getEmployeeName() : "") + ", store=" + (store != null ? store.getStoreName() : "")  + ", "
            + "bindDealerEmployee=" +  (bindDealerEmployee != null ? bindDealerEmployee.getEmployeeName() : "") + ", "
                + "openid=" + openid + ", nickname=" + nickname + ", sex=" + sex + ", type=" + type + ", state=" + state + "]";
    }
    

}
