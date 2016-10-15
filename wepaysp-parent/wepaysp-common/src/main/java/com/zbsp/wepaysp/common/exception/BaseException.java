package com.zbsp.wepaysp.common.exception;

/**
 * 基础异常
 * 
 * @author zhangcs
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -8429604276172803453L;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
