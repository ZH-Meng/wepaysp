package com.zbsp.alipay.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class AlipayEcoEduKtSchoolinfoModifyRequestBuilder
    extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isBlank(bizContent.schoolName))
            throw new NullPointerException("schoolName should not be NULL!");
        if (StringUtils.isBlank(bizContent.schoolType))
            throw new NullPointerException("schoolType should not be NULL!");
        if (StringUtils.isBlank(bizContent.provinceCode))
            throw new NullPointerException("provinceCode should not be NULL!");
        if (StringUtils.isBlank(bizContent.provinceName))
            throw new NullPointerException("provinceName should not be NULL!");
        if (StringUtils.isBlank(bizContent.cityCode))
            throw new NullPointerException("cityCode should not be NULL!");
        if (StringUtils.isBlank(bizContent.cityName))
            throw new NullPointerException("cityName should not be NULL!");
        if (StringUtils.isBlank(bizContent.districtCode))
            throw new NullPointerException("districtCode should not be NULL!");
        if (StringUtils.isBlank(bizContent.districtName))
            throw new NullPointerException("districtName should not be NULL!");
        if (StringUtils.isBlank(bizContent.isvName))
            throw new NullPointerException("isvName should not be NULL!");
        if (StringUtils.isBlank(bizContent.isvNotifyUrl))
            throw new NullPointerException("isvNotifyUrl should not be NULL!");
        if (StringUtils.isBlank(bizContent.isvPid))
            throw new NullPointerException("isvPid should not be NULL!");
        if (StringUtils.isBlank(bizContent.isvPhone))
            throw new NullPointerException("isvPhone should not be NULL!");
        if (StringUtils.isBlank(bizContent.schoolPid))
            throw new NullPointerException("schoolPid should not be NULL!");

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

    public String getSchoolIcon() {
        return bizContent.schoolIcon;
    }

    public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolIcon(String schoolIcon) {
        bizContent.schoolIcon = schoolIcon;
        return this;
    }

    public String getSchoolIconType() {
        return bizContent.schoolIconType;
    }

    public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setSchoolIconType(String schoolIconType) {
        bizContent.schoolIconType = schoolIconType;
        return this;
    }

    public String getBankcardNo() {
        return bizContent.bankcardNo;
    }

    public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setBankcardNo(String bankcardNo) {
        bizContent.bankcardNo = bankcardNo;
        return this;
    }

    public String getBankUid() {
        return bizContent.bankUid;
    }

    public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setBankUid(String bankUid) {
        bizContent.bankUid = bankUid;
        return this;
    }

    public String getBankNotifyrl() {
        return bizContent.bankNotifyrl;
    }

    public AlipayEcoEduKtSchoolinfoModifyRequestBuilder setBankNotifyrl(String bankNotifyrl) {
        bizContent.bankNotifyrl = bankNotifyrl;
        return this;
    }

    public static class BizContent {

        @SerializedName("school_name")
        private String schoolName;

        @SerializedName("school_icon")
        private String schoolIcon;

        @SerializedName("school_icon_type")
        private String schoolIconType;

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

        @SerializedName("bankcard_no")
        private String bankcardNo;

        @SerializedName("bank_uid")
        private String bankUid;

        @SerializedName("bank_notify_url")
        private String bankNotifyrl;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("BizContent [schoolName=").append(schoolName).append(", schoolIcon=").append(schoolIcon).append(", schoolIconType=").append(schoolIconType).append(", schoolStdcode=")
                .append(schoolStdcode).append(", schoolType=").append(schoolType).append(", provinceCode=").append(provinceCode).append(", provinceName=").append(provinceName).append(", cityCode=")
                .append(cityCode).append(", cityName=").append(cityName).append(", districtCode=").append(districtCode).append(", districtName=").append(districtName).append(", isvName=")
                .append(isvName).append(", isvNotifyUrl=").append(isvNotifyUrl).append(", isvPid=").append(isvPid).append(", schoolPid=").append(schoolPid).append(", isvPhone=").append(isvPhone)
                .append(", bankcardNo=").append(bankcardNo).append(", bankUid=").append(bankUid).append(", bankNotifyrl=").append(bankNotifyrl).append("]");
            return builder.toString();
        }

    }

}
