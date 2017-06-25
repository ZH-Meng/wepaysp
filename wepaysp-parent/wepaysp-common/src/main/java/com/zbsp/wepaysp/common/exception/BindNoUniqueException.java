package com.zbsp.wepaysp.common.exception;

/**
 * 绑定不唯一异常
 */
public class BindNoUniqueException extends BaseException {

    private static final long serialVersionUID = -942453947586229728L;

    public BindNoUniqueException(String msg) {
        setMsg(msg);
    }
}
