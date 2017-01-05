package com.zbsp.wepaysp.common.mobile.result;

/**
 * 通讯通用结果码
 */
public enum CommonResult {
    
    /** 成功 */
    SUCCESS(0, "处理成功"),
    
    /** 系统错误 */
    SYS_ERROR(1, "系统错误"),
    
    /** 数据包解析错误 */
    PARSE_ERROR(100, "数据包解析错误"),
    
    /** 参数缺失 */
    ARGUMENT_MISS(101, "参数缺失"),
    
    /** 参数无效 */
    INVALID_ARGUMENT(102, "参数无效"),
    
    /** 客户端类型无效 */
    INVALID_APPTYPE(103, "客户端类型无效"),
    
    /**数据不存在*/
    DATA_NOT_EXIST(104, "数据不存在");
    
    private int code;

    private String desc;

    private CommonResult(int code, String desc) {
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
