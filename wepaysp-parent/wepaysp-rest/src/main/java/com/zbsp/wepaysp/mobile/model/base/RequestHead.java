package com.zbsp.wepaysp.mobile.model.base;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 通讯请求头对象
 */
public class RequestHead implements Serializable, Cloneable {

    private static final long serialVersionUID = 6713170463080944347L;
    
    private final Logger logger = LogManager.getLogger();
    private static final String LOG_PREFIX = "[RequestHead]-";
    
    // 业务识别码
    private String businessId;
    // 请求唯一码
    private String requestId;
    // 包体签名
    private String signature = "null";
    // 客户端类型
    private int appType;
    // 设备Id
    private String deviceId;
    // 操作系统版本信息
    private String deviceOPSysInfo;
    // 设备型号
    private String deviceModel;

    public static enum AppType {
        /** 客户端类型：IOS */             ios(0),
        /** 客户端类型：Android */        android(1);

        private int value;

        public int getValue() {
            return value;
        }

        private AppType(int value) {
            this.value = value;
        }
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceOPSysInfo() {
        return deviceOPSysInfo;
    }

    public void setDeviceOPSysInfo(String deviceOPSysInfo) {
        this.deviceOPSysInfo = deviceOPSysInfo;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(LOG_PREFIX + "[通讯请求头克隆错误]", e);
            return null;
        }
    }
}
