package com.zbsp.wepaysp.vo.weixin;

/**
 * 微信支付（门店/收银员级别）通知绑定收银员VO
 * 
 * @author 孟郑宏
 */

public class PayNoticeBindWeixinVO
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3801534516962277126L;
    private String iwoid;
    private String payDealerEmployeeOid;
    private String storeOid;
    private String bindDealerEmployeeOid;
    private String bindDealerEmployeeId;
    private String bindDealerEmployeeName;
    private String openid;
    private String nickname;
    private Integer sex;
    private String type;
    private String state;

    public PayNoticeBindWeixinVO() {
    }

    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getPayDealerEmployeeOid() {
        return payDealerEmployeeOid;
    }

    public void setPayDealerEmployeeOid(String payDealerEmployeeOid) {
        this.payDealerEmployeeOid = payDealerEmployeeOid;
    }

    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public String getBindDealerEmployeeOid() {
        return bindDealerEmployeeOid;
    }

    public void setBindDealerEmployeeOid(String bindDealerEmployeeOid) {
        this.bindDealerEmployeeOid = bindDealerEmployeeOid;
    }

    public String getBindDealerEmployeeId() {
        return bindDealerEmployeeId;
    }

    public void setBindDealerEmployeeId(String bindDealerEmployeeId) {
        this.bindDealerEmployeeId = bindDealerEmployeeId;
    }

    public String getBindDealerEmployeeName() {
        return bindDealerEmployeeName;
    }

    public void setBindDealerEmployeeName(String bindDealerEmployeeName) {
        this.bindDealerEmployeeName = bindDealerEmployeeName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
