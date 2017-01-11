package com.zbsp.wepaysp.mobile.model.result;

import com.tencent.protocol.unified_order_protocol.JSPayReqData;

/**
 * 创建订单JSON结果
 * 
 * @author 孟郑宏
 */
public class CreateOrderResult {
    private String result;
    private String desc;
    private JSPayReqData jSPayReqData;
    private String weixinPayDetailOid;
    
    public CreateOrderResult(String result, String desc, JSPayReqData jSPayReqData, String weixinPayDetailOid) {
        this.result = result;
        this.desc = desc;
        this.jSPayReqData = jSPayReqData;
        this.weixinPayDetailOid = weixinPayDetailOid;
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
    
    public JSPayReqData getjSPayReqData() {
        return jSPayReqData;
    }
    
    public void setjSPayReqData(JSPayReqData jSPayReqData) {
        this.jSPayReqData = jSPayReqData;
    }
    
    public String getWeixinPayDetailOid() {
        return weixinPayDetailOid;
    }
    
    public void setWeixinPayDetailOid(String weixinPayDetailOid) {
        this.weixinPayDetailOid = weixinPayDetailOid;
    }
        
}
