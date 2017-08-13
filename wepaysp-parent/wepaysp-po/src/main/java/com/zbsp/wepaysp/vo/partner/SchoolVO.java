package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 学校VO
 * 
 * @author zhaozh
 */
public class SchoolVO
    implements Serializable {

    private static final long serialVersionUID = 2712399168814035693L;

    private String iwoid;
    private String shcoolName;
    private String schoolPid;
	private String schoolType;
	private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;   



	private String isvName;
    private String isvNotifyUrl;
    private String isvPid;
    private String isvPhone;

	private String partnerOid;
    private String partnerCompany;
    private String dealerId;

    private String company;

    private String techSupportPerson;
    private String techSupportPhone;
    private String subAppid;
    private String subMchId;
    private String remark;

    private String loginId;
    private String loginPwd;
    private String coreDataFlag;// on;off 顶级服务商可以编辑商户核心数据，其他用户不可以
    private String partnerEmployeeOid;
    private String partnerEmployeeName;



    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getPartnerCompany() {
        return partnerCompany;
    }

    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }



    public String getTechSupportPerson() {
        return techSupportPerson;
    }

    public void setTechSupportPerson(String techSupportPerson) {
        this.techSupportPerson = techSupportPerson;
    }

    public String getTechSupportPhone() {
        return techSupportPhone;
    }

    public void setTechSupportPhone(String techSupportPhone) {
        this.techSupportPhone = techSupportPhone;
    }

    public String getSubAppid() {
        return subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getCoreDataFlag() {
        return coreDataFlag;
    }

    public void setCoreDataFlag(String coreDataFlag) {
        this.coreDataFlag = coreDataFlag;
    }

    public String getPartnerEmployeeOid() {
        return partnerEmployeeOid;
    }

    public void setPartnerEmployeeOid(String partnerEmployeeOid) {
        this.partnerEmployeeOid = partnerEmployeeOid;
    }

    public String getPartnerEmployeeName() {
        return partnerEmployeeName;
    }

    public void setPartnerEmployeeName(String partnerEmployeeName) {
        this.partnerEmployeeName = partnerEmployeeName;
    }
 
    
    public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
 
	
	public String getShcoolName() {
		return shcoolName;
	}

	public void setShcoolName(String shcoolName) {
		this.shcoolName = shcoolName;
	}
    public String getSchoolPid() {
		return schoolPid;
	}

	public void setSchoolPid(String schoolPid) {
		this.schoolPid = schoolPid;
	}

	public String getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(String schoolType) {
		this.schoolType = schoolType;
	}
	
    public String getIsvName() {
		return isvName;
	}

	public void setIsvName(String isvName) {
		this.isvName = isvName;
	}

	public String getIsvNotifyUrl() {
		return isvNotifyUrl;
	}

	public void setIsvNotifyUrl(String isvNotifyUrl) {
		this.isvNotifyUrl = isvNotifyUrl;
	}

	public String getIsvPid() {
		return isvPid;
	}

	public void setIsvPid(String isvPid) {
		this.isvPid = isvPid;
	}

	public String getIsvPhone() {
		return isvPhone;
	}

	public void setIsvPhone(String isvPhone) {
		this.isvPhone = isvPhone;
	}


}
