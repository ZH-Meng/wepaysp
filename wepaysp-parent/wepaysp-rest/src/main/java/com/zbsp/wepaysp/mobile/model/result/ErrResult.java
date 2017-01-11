package com.zbsp.wepaysp.mobile.model.result;

/**
 * 适用H5页面的错误结果
 * 
 * @author 孟郑宏
 */
public class ErrResult {
    private String errCode;
    private String errDesc;
    
    public ErrResult(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    public String getErrCode() {
        return errCode;
    }
    
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    
    public String getErrDesc() {
        return errDesc;
    }
    
    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    @Override
    public String toString() {
        return "ErrResult [errCode=" + errCode + ", errDesc=" + errDesc + "]";
    }
    
}
