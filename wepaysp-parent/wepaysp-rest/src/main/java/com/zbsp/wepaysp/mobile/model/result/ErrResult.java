package com.zbsp.wepaysp.mobile.model.result;

/**
 * 适用H5页面的错误结果
 * 
 * @author 孟郑宏
 */
public class ErrResult {
    /**标题描述，适用于不同功能返回不同标题提示*/
    private String titleDesc;
    
    private String errCode;
    private String errDesc;
    
    public ErrResult() {}
    
    public ErrResult(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }
    
    public ErrResult(String titleDesc, String errCode, String errDesc) {
        this.titleDesc = titleDesc;
        this.errCode = errCode;
        this.errDesc = errDesc;
    }
    
    public String getTitleDesc() {
        return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
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
        return "ErrResult [titleDesc=" + titleDesc + ", errCode=" + errCode + ", errDesc=" + errDesc + "]";
    }
    
}
