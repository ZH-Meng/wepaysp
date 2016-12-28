package com.zbsp.wepaysp.common.security.exception;

public class AesEncryptException
    extends Exception {

    /**
     * 类的序列化版本UID。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造详细消息为 null 的新异常。
     */
    public AesEncryptException() {
        super();
    }

    /**
     * 构造带指定详细消息的新异常。
     * 
     * @param message
     *            详细消息
     */
    public AesEncryptException(String message) {
        super(message);
    }

    /**
     * 构造带指定详细消息和原因的新异常。
     * 
     * @param message
     *            详细消息
     * @param cause
     *            原因
     */
    public AesEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造新异常（它通常包含 cause 的类和详细消息）。
     * 
     * @param cause
     *            原因
     */
    public AesEncryptException(Throwable cause) {
        super(cause);
    }
}
