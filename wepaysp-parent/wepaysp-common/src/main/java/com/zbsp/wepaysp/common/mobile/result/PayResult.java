package com.zbsp.wepaysp.common.mobile.result;

public enum PayResult {

	/** 支付失败 */
	PAY_FAIL(1000, "支付失败"),
    /** 用户支付中 */
    PAYING(1001, "用户支付中，请手动刷新查看结果！");

	private int code;

	private String desc;

	private PayResult(int code, String desc) {
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
