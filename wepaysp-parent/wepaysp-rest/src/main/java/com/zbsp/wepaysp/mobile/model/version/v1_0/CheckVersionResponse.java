package com.zbsp.wepaysp.mobile.model.version.v1_0;

import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;

/**
 * 客户端版本检查响应
 */
public class CheckVersionResponse extends MobileResponse {

    /** 新版本号码 */
    private int versionNumber;

    /** 新版本名称 */
    private String versionName = "";

    /** 新版本功能描述 */
    private String description = "";

    /** 非必填：新版本下载地址 */
    private String downloadUrl = "";

    /** 最低版本 */
    private int minNumber;

    /** 最低版本名称 */
    private String minVersion = "";

    /** 是否每次提醒0：否1：是 */
    private int tipType;
    
    private Long fileSize;

    public CheckVersionResponse() {
    }

    public CheckVersionResponse(int result, String message, String responseId) {
        super(result, message, responseId);
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(int minNumber) {
        this.minNumber = minNumber;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
    
    public int getTipType() {
        return tipType;
    }
    
    public void setTipType(int tipType) {
        this.tipType = tipType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "CheckVersionResponse [versionNumber=" + versionNumber + ", versionName=" + versionName + ", description=" + description + ", downloadUrl=" + downloadUrl 
            + ", minNumber=" + minNumber + ", minVersion=" + minVersion + ", tipType=" + tipType  + ", fileSize=" + fileSize  + ", " + super.toString() + " ]";
    }

    @Override
    public CheckVersionResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
