package com.zbsp.wepaysp.common.bean;


public class MessageBean {

    private String message;         //信息
    private String alertMessage;        //弹出提示对话框
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public void setAlertMessage(String message) {
        alertMessage = "<script language='javascript'>alert('" + message + "')</script>";
    }

    public String getAlertMessage() {
        return alertMessage;
    }
}
