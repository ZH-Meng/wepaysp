package com.zbsp.wepaysp.po.partner;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "store_t", catalog = "wepay_sp")
public class Store
    implements java.io.Serializable {

    private static final long serialVersionUID = 8172532995767139541L;
    private String iwoid;
    private Dealer dealer;
    private String storeId;
    private String storeName;
    private String storeAddress;
    private String storeTel;
    private String qrCode;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public Store() {
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
    @JoinColumn(name = "DEALER_OID")
    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Column(name = "STORE_ID", length = 32)
    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Column(name = "STORE_NAME", length = 256)
    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Column(name = "STORE_ADDRESS", length = 256)
    public String getStoreAddress() {
        return this.storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    @Column(name = "STORE_TEL", length = 32)
    public String getStoreTel() {
        return this.storeTel;
    }

    public void setStoreTel(String storeTel) {
        this.storeTel = storeTel;
    }

    @Column(name = "QR_CODE", length = 1024)
    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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

}
