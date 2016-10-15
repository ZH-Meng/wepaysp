package com.zbsp.wepaysp.common.exception;

/**
 * 记录已存在异常
 * 
 * @author zhangcs
 */
public class AlreadyExistsException extends BaseException {

    private static final long serialVersionUID = -942453947586229728L;

    public AlreadyExistsException(String msg) {
        setMsg(msg);
    }
}
