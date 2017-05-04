package com.zbsp.wepaysp.mobile.common.constant;

/**
 * 通讯通用结果码
 */
public enum H5CommonResult {
    
    /** 成功 */
    SUCCESS("success", "处理成功"),
    
    /** 系统错误 */
    SYS_ERROR("error", "系统或网络异常"),
    
    /** 参数缺失 */
    ARGUMENT_MISS("param_miss", "参数缺失"),
    
    /** 参数无效 */
    INVALID_ARGUMENT("param_invalid", "参数无效"),
    
    /**数据不存在*/
    DATA_NOT_EXIST("data_not_exist", "数据不存在"),
    
    /**获取access_token失败*/
    ACCESS_TOKEN_FAIL("access_token_fail", "授权失败");
    
    private String code;

    private String desc;

    private H5CommonResult(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
