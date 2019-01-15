/*
 * CommonServiceImpl.java
 * 创建者：Liuht
 * 创建日期：2017年7月14日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import com.zbsp.wepaysp.api.dao.CommonORMDAO;
import com.zbsp.wepaysp.api.service.CommonService;

/**
 * @author Liuht
 */
public class CommonServiceImpl
    implements CommonService {

    // 公共DAO对象
    @Autowired
    protected CommonORMDAO commonDAO;

    // 单次批处理大小
    protected int batchSize = 300;

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

    /**
     * 删除实体
     * 
     * @param entity
     */
    public <T> void doTransDelete(T entity) {
        commonDAO.delete(entity);
    }

    /**
     * 保存实体
     * 
     * @param entity
     */
    public <T> void doTransSaveEntity(T entity, boolean isFlush) {
        checkArgument(null == entity, "保存对象不能为null");
        commonDAO.save(entity, isFlush);
    }

    /**
     * 保存实体
     * 
     * @param entity
     */
    public <T> void doNewTransSaveEntity(T entity, boolean isFlush) {
        checkArgument(null == entity, "保存对象不能为null");
        commonDAO.save(entity, isFlush);
    }

    /**
     * 更新实体
     * 
     * @param entity
     */
    public <T> void doTransUpdateEntity(T entity) {
        checkArgument(null == entity, "保存对象不能为null");
        commonDAO.update(entity);
    }

    /**
     * 批量保存实体
     * 
     * @param objectList
     */
    public <T> void doTransBatchSaveEntity(List<T> objectList) {
        checkArgument(CollectionUtils.isEmpty(objectList), "保存对象不能为null");
        if (objectList.size() > batchSize) {
            commonDAO.saveList(objectList, batchSize);
        } else {
            commonDAO.saveList(objectList);
        }
    }

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T doJoinTransFindObjectByOid(Class<T> clazz, Serializable oid) {
        checkArgument(clazz == null, "查询结果类型不能为null");
        checkArgument(oid == null, "对象唯一标识不能为null");
        return commonDAO.findObject(clazz, oid);
    }

    /**
     * 根据对象类型和对象属性查找记录(对象)详情
     * 
     * @param <T>
     * @param entityClass
     * @param propName
     * @param propVal
     * @return
     */
    public <T> T doJoinTransFindUniqueByProperty(Class<T> entityClass, String propName, Serializable propVal) {
        checkArgument(entityClass == null, "查询结果类型不能为null");
        checkArgument(propName == null, "唯一属性不能为null");
        checkArgument(propVal == null, "属性值不能为null");
        return commonDAO.findUniqueObject(entityClass, propName, propVal);
    }

    /**
     * 按属性查找对象列表.
     * 
     * @param <T>
     * @param entityClass
     * @param propName
     * @param propVal
     * @return
     */
    public <T> List<T> doJoinTransFindByProperty(Class<T> entityClass, String propName, Serializable propVal) {
        checkArgument(entityClass == null, "查询结果类型不能为null");
        checkArgument(propName == null, "唯一属性不能为null");
        checkArgument(propVal == null, "属性值不能为null");
        return commonDAO.findByProperty(entityClass, propName, propVal);
    }

    /**
     * 检查表达式是否满足
     * 
     * @param expression
     *            表达式是否满足
     * @param errorMessage
     *            满足时返回的提示
     */
    private void checkArgument(boolean expression, String errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            return;
        }
    }

}
