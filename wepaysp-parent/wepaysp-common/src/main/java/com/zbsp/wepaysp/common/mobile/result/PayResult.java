package com.zbsp.wepaysp.common.mobile.result;

public enum PayResult {

	/** 支付失败 */
	PAY_FAIL(1000, "支付失败");

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
