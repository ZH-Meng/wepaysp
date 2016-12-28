package com.zbsp.wepaysp.mobile.common;

/**
 * 通讯通用错误代码
 */
public enum CommonResultCode {
	/** 成功 */                        	                      success(0),
    /** 系统错误 */                                          sysError(1),
    /** 非法业务识别码 */                               invalidBusinessId(100),
    /** 数据包解析错误 */                               parseError(101),
    /** 内容校验失败 */                                  verifyError(102),
    /** 无效的客户端类型 */                           invalidAppType(103),
    /** 未通过业务参数校验 */                        illegalArgument(200),
    /** token失效，需重新登录 */                  tokenInvalid(300),
    /** session失效，需重新登录 */                sessionLose(400);

    private int value;

    public int getValue() {
        return value;
    }

    private CommonResultCode(int value) {
        this.value = value;
    }
}
