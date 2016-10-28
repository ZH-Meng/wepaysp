package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 门店VO
 * 
 * @author 孟郑宏
 */
public class StoreVO
    implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5666536367295381705L;
    private String iwoid;
    private String dealerOid;
    private String dealerCompany;
    private String partnerCompany;
    private String storeId;
    private String storeName;
    private String storeAddress;
    private String storeTel;
    private String qrCode;
    private String remark;
    
    public String getIwoid() {
        return iwoid;
    }
    
    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }
    
    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public String getDealerCompany() {
        return dealerCompany;
    }
    
    public void setDealerCompany(String dealerCompany) {
        this.dealerCompany = dealerCompany;
    }
    
    public String getPartnerCompany() {
        return partnerCompany;
    }
    
    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }

    public String getStoreId() {
        return storeId;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    public String getStoreAddress() {
        return storeAddress;
    }
    
    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }
    
    public String getStoreTel() {
        return storeTel;
    }
    
    public void setStoreTel(String storeTel) {
        this.storeTel = storeTel;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
