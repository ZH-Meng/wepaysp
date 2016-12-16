package com.zbsp.wepaysp.manage.web.vo.appid;

/**
 * 支付通知绑定结果
 * 
 * @author 孟郑宏
 */
public class PayNoticeBindResult {

    private String result;
    private String desc;

    public PayNoticeBindResult(String result, String desc) {
        this.result = result;
        this.desc = desc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
