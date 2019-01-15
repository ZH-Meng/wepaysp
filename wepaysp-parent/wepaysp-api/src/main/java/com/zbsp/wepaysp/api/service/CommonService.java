package com.zbsp.wepaysp.api.service;

import java.io.Serializable;
import java.util.List;

/**
 * BaseService
 * 
 * @author 杨帆
 * @modifier Liuht
 */
public interface CommonService {
    
    /**
     * 删除实体
     * @param entity
     */
    public <T> void doTransDelete(T entity);
    
    /**
     * 保存实体
     * @param entity
     */
    public <T> void doTransSaveEntity(T entity, boolean isFlush);
    
    /**
     * 保存实体
     * @param entity
     */
    public <T> void doNewTransSaveEntity(T entity, boolean isFlush);
    
    /**
     * 更新实体
     * @param entity
     */
    public <T> void doTransUpdateEntity(T entity);
    
    /**
     * 批量保存实体
     * @param objectList
     */
    public <T> void doTransBatchSaveEntity(List<T> objectList);

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T doJoinTransFindObjectByOid(Class<T> clazz, Serializable oid);

    /**
     * 根据对象类型和对象属性查找记录(对象)详情
     * 
     * @param <T>
     * @param entityClass
     * @param propName
     * @param propVal
     * @return
     */
    public <T> T doJoinTransFindUniqueByProperty(Class<T> entityClass, String propName, Serializable propVal);

    /**
     * 按属性查找对象列表.
     * @param <T>
     * @param entityClass
     * @param propName
     * @param propVal
     * @return
     */
    public <T> List<T> doJoinTransFindByProperty(Class<T> entityClass, String propName, Serializable propVal);
    
}
