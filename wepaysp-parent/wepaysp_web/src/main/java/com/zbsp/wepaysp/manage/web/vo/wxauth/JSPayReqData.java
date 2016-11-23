package com.zbsp.wepaysp.manage.web.vo.wxauth;

import java.io.Serializable;

/**
 * 公众号支付 JS API 请求参数类
 * 
 * @author 孟郑宏
 */
public class JSPayReqData
    implements Serializable {

    private static final long serialVersionUID = -3962259263581846277L;

    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String dataPackage; // 比如：prepay_id=u802345jgfjsdfgsdg888"
    private String signType;
    private String paySign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getDataPackage() {
        return dataPackage;
    }

    public void setDataPackage(String dataPackage) {
        this.dataPackage = dataPackage;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

}
