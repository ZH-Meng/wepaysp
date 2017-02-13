package com.zbsp.wepaysp.mobile.controller;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * springMVC 控制器基类
 * 
 * @author 孟郑宏
 */
public abstract class BaseController {
    
    protected Logger logger = LogManager.getLogger(getClass());
    
    /** 客户端 rest请求 验签密钥*/
    protected static final String KEY = ResourceBundle.getBundle("rest").getString("rest_key");
    
    /** 开发阶段标志*/
    protected static final boolean DEV_FLAG = Boolean.valueOf(ResourceBundle.getBundle("rest").getString("dev_mode"));
    
}
