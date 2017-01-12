package com.zbsp.wepaysp.mobile.model.vo;

/**
 * 静默授权回调回传VO
 * 
 * @author 孟郑宏
 */
public class WxCallBackVO {

    private String partnerOid;
    private String dealerOid;
    private String storeOid;
    private String dealerEmployeeOid;
    
    /** 授权回调回传-用户同意才有值*/
    private String code;
    // private String state;
    
    /** 用户在公众号的标识 */
    private String openid;

    private String dealerName;

    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

}
