package com.zbsp.wepaysp.mobile.controller;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseController {
    
    protected static final String KEY = ResourceBundle.getBundle("key").getString("key");
    
    protected Logger logger = LogManager.getLogger(getClass());
    
    /** 开发阶段标志*/
    protected static final boolean DEV_FLAG = true;
    
}
