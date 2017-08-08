package com.zbsp.alipay.trade.model.builder;

import com.google.gson.annotations.SerializedName;

public class AlipayEcoEduKtSchoolinfoModifyRequestBuilder extends RequestBuilder {

	private BizContent bizContent = new BizContent();

	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AlipayEcoEduKtSchoolinfoModifyRequestBuilder{");
		sb.append("bizContent=").append(bizContent);
		sb.append(", super=").append(super.toString());
		sb.append('}');
		return sb.toString();
	}
	
	
	public String getSchoolName() {
		return bizContent.schoolName;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolName(String schoolName) {
		bizContent.schoolName = schoolName;
		return this;
	}

	public String getSchoolStdcode() {
		return bizContent.schoolStdcode;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolStdcode(String schoolStdcode) {
		bizContent.schoolStdcode = schoolStdcode;
		return this;
	}

	public String getSchoolType() {
		return bizContent.schoolType;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolType(String schoolType) {
		bizContent.schoolType = schoolType;
		return this;
	}

	public String getProvinceCode() {
		return bizContent.provinceCode;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setProvinceCode(String provinceCode) {
		bizContent.provinceCode = provinceCode;
		return this;
	}

	public String getProvinceName() {
		return bizContent.provinceName;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setProvinceName(String provinceName) {
		bizContent.provinceName = provinceName;
		return this;
	}

	public String getCityCode() {
		return bizContent.cityCode;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setCityCode(String cityCode) {
		bizContent.cityCode = cityCode;
		return this;
	}

	public String getCityName() {
		return bizContent.cityName;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setCityName(String cityName) {
		bizContent.cityName = cityName;
		return this;
	}

	public String getDistrictCode() {
		return bizContent.districtCode;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setDistrictCode(String districtCode) {
		bizContent.districtCode = districtCode;
		return this;
	}

	public String getDistrictName() {
		return bizContent.districtName;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setDistrictName(String districtName) {
		bizContent.districtName = districtName;
		return this;
	}

	public String getIsvName() {
		return bizContent.isvName;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setIsvName(String isvName) {
		bizContent.isvName = isvName;
		return this;
	}

	public String getIsvNotifyUrl() {
		return bizContent.isvNotifyUrl;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setIsvNotifyUrl(String isvNotifyUrl) {
		bizContent.isvNotifyUrl = isvNotifyUrl;
		return this;
	}

	public String getIsvPid() {
		return bizContent.isvPid;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setIsvPid(String isvPid) {
		bizContent.isvPid = isvPid;
		return this;
	}

	public String getSchoolPid() {
		return bizContent.schoolPid;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolPid(String schoolPid) {
		bizContent.schoolPid = schoolPid;
		return this;
	}

	public String getIsvPhone() {
		return bizContent.isvPhone;
	}

	public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setIsvPhone(String isvPhone) {
		bizContent.isvPhone = isvPhone;
		return this;
	}

	public static class BizContent {
		@SerializedName("school_name")
		private String schoolName;

		@SerializedName("school_stdcode")
		private String schoolStdcode;

		@SerializedName("school_type")
		private String schoolType;

		@SerializedName("province_code")
		private String provinceCode;

		@SerializedName("province_name")
		private String provinceName;

		@SerializedName("city_code")
		private String cityCode;

		@SerializedName("city_name")
		private String cityName;

		@SerializedName("district_code")
		private String districtCode;

		@SerializedName("district_name")
		private String districtName;

		@SerializedName("isv_name")
		private String isvName;

		@SerializedName("isv_notify_url")
		private String isvNotifyUrl;

		@SerializedName("isv_pid")
		private String isvPid;

		@SerializedName("school_pid")
		private String schoolPid;

		@SerializedName("isv_phone")
		private String isvPhone;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("BizContent{");
			sb.append("schoolName='").append(schoolName).append('\'');
			sb.append("schoolStdcode='").append(schoolStdcode).append('\'');
			sb.append("schoolType='").append(schoolType).append('\'');

			sb.append("provinceCode='").append(provinceCode).append('\'');
			sb.append("provinceName='").append(provinceName).append('\'');
			sb.append("cityCode='").append(cityCode).append('\'');
			sb.append("cityName='").append(cityName).append('\'');
			sb.append("districtCode='").append(districtCode).append('\'');
			sb.append("districtName='").append(districtName).append('\'');

			sb.append("isvName='").append(isvName).append('\'');
			sb.append("isvNotifyUrl='").append(isvNotifyUrl).append('\'');
			sb.append("isvPid='").append(isvPid).append('\'');
			sb.append("schoolPid='").append(schoolPid).append('\'');
			sb.append("isvPhone='").append(isvPhone).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}

}
