package com.zbsp.wepaysp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;

import com.zbsp.wepaysp.dao.CommonORMDAO;

/**
 * BaseService
 * 
 * @author 杨帆
 */
public class BaseService {

    // 公共DAO对象
    protected CommonORMDAO commonDAO;

    // 日志对象
    protected final Logger logger = LogManager.getLogger(getClass());

    // 事务管理对象
    protected PlatformTransactionManager transactionManager;

    public CommonORMDAO getCommonDAO() {
        return commonDAO;
    }

    public void setCommonDAO(CommonORMDAO commonDAO) {
        this.commonDAO = commonDAO;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
