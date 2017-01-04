package com.zbsp.wepaysp.po.app;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "app_manage_t")
public class AppManage implements Serializable {
    private static final long serialVersionUID = -8024309904054734894L;
    
    private String iwoid;
    private Integer softType;
    private int versionNumber;
    private String versionName;
    private int supportMinVersionNumber;
    private String supportMinVersionName;
    private Integer tipType;
    private String versionDesc;
    private String downloadUrl;
    private String filePath;
    private Long fileSize;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum SoftType {
        /** 客户端类别：Android */        android(1),
        /** 客户端类别：IOS */              ios(2),
        /** 客户端类别：PC */              pos(3);

        private int value;

        public int getValue() {
            return value;
        }

        private SoftType(int value) {
            this.value = value;
        }
    }
    
    public static enum TipType {
        /** 强制更新：否 */              no(0),
        /** 强制更新：是 */              yes(1);

        private int value;

        public int getValue() {
            return value;
        }

        private TipType(int value) {
            this.value = value;
        }
    }
    
    public AppManage() {
    }

    @Id
    @Column(name = "iwoid", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "soft_type", nullable = false)
    public Integer getSoftType() {
        return this.softType;
    }

    public void setSoftType(Integer softType) {
        this.softType = softType;
    }

    @Column(name = "version_number", nullable = false)
    public int getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Column(name = "version_name", nullable = false, length = 20)
    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Column(name = "support_min_version_number", nullable = false)
    public int getSupportMinVersionNumber() {
        return this.supportMinVersionNumber;
    }

    public void setSupportMinVersionNumber(int supportMinVersionNumber) {
        this.supportMinVersionNumber = supportMinVersionNumber;
    }

    @Column(name = "support_min_version_name", nullable = false, length = 20)
    public String getSupportMinVersionName() {
        return this.supportMinVersionName;
    }

    public void setSupportMinVersionName(String supportMinVersionName) {
        this.supportMinVersionName = supportMinVersionName;
    }

    @Column(name = "tip_type", nullable = false)
    public Integer getTipType() {
        return this.tipType;
    }

    public void setTipType(Integer tipType) {
        this.tipType = tipType;
    }

    @Column(name = "version_desc", nullable = false, length = 300)
    public String getVersionDesc() {
        return this.versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    @Column(name = "download_url", length = 500)
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Column(name = "file_path", length = 300)
    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "file_size")
    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(name = "creator", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modifier", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time", length = 29)
    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "remark", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
