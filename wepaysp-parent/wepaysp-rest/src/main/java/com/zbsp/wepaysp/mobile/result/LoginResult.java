package com.zbsp.wepaysp.mobile.result;


public enum LoginResult {
    
    /**登录失败*/   
    LOGIN_FAIL(1000, "登录失败"),
    
    /**"ID或密码不正确*/
    LOGIN_ID_PASSWD_FAIL(1010,"ID或密码不正确");
    
    private int code;
    
    private String desc;
    
    private LoginResult(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
