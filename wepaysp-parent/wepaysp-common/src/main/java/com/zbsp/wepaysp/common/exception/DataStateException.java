package com.zbsp.wepaysp.common.exception;

/**
 * 数据状态异常
 * 
 */
public class DataStateException extends BaseException {
    
    private static final long serialVersionUID = -1335721734646710143L;

    public DataStateException(String msg) {
        setMsg(msg);
    }
}
