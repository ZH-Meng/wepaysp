package com.zbsp.wepaysp.dao;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

/**
 * 可用于ORM框架下的通用DAO.
 * 
 * @author 杨帆
 */
public interface CommonORMDAO {

    /**
     * 删除对象(记录)
     * 
     * @param entity 要删除的对象(记录)
     */
    public void delete(Object entity);

    /**
     * 根据传入的sql和参数执行更新或删除操作.
     * <p />
     * 一般用于批量更新或批量删除
     * 
     * @param sql 要执行的sql
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @return 影响的记录数
     */
    public int executeBatch(String sql, Map<String, Object> paramMap, boolean isNative);

    /**
     * 根据对象类型和记录编号查找记录(对象)详情.
     * 不查找lazy-load属性.
     * 
     * @param objectClass 对象类型
     * @param objectID 记录编号
     * @return 要查找的对象(记录),未找到时为null
     */
    public <T> T findObject(Class<T> objectClass, Serializable objectID);

    /**
     * 根据对象类型和记录编号查找记录(对象)详情并给对象加锁.
     * 不查找lazy-load属性.
     * 
     * @param objectClass 对象类型
     * @param objectID 记录编号
     * @param lockModeType 锁类型
     * @return 要查找的对象(记录),未找到时为null
     */
    public <T> T findObject(Class<T> objectClass, Serializable objectID, LockModeType lockModeType);
    
    /**
     * 根据传入的sql和参数查询对象信息
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @return 符合条件的对象信息
     */
    public <T> T findObject(String sql, Map<String, Object> paramMap, boolean isNative);

    /**
     * 根据传入的sql和参数查询对象信息
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @param lockModeType 锁类型
     * @return 符合条件的对象信息
     */
    public <T> T findObject(String sql, Map<String, Object> paramMap, boolean isNative, LockModeType lockModeType);
    
    /**
     * 根据传入的sql和参数查询对象数组.
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @return 符合条件的对象数组
     */
    public Object[] findObjectArray(String sql, Map<String, Object> paramMap, boolean isNative);

    /**
     * 根据传入的SQL和参数查询对象数组
     * 
     * @param sql 要执行的SQL语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @param firstResult 起始记录, 如果小于0则从0开始
     * @param maxResult 最大记录, 此值大于-1时有效
     * @return 符合条件的对象数组
     */
    public Object[] findObjectArray(String sql, Map<String, Object> paramMap, boolean isNative, int firstResult,
        int maxResult);
    
    /**
     * 根据传入的sql和参数查询对象信息列表.
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @return 符合条件的信息列表
     */
    public List<?> findObjectList(String sql, Map<String, Object> paramMap, boolean isNative);

    /**
     * 根据传入的SQL和参数查询信息列表
     * 
     * @param sql 要执行的SQL语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @param firstResult 起始记录, 如果小于0则从0开始
     * @param maxResult 最大记录, 此值大于-1时有效
     * @return 符合条件的信息列表
     */
    public List<?> findObjectList(String sql, Map<String, Object> paramMap, boolean isNative, int firstResult,
        int maxResult);
    
    /**
     * 根据传入的sql和参数查询对象信息列表.
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @param lockModeType 锁类型
     * @return 符合条件的信息列表
     */
    public List<?> findObjectList(String sql, Map<String, Object> paramMap, boolean isNative, LockModeType lockModeType);
    
    /**
     * 根据传入的sql和参数查询对象信息列表.
     * 
     * @param sql 要执行的sql语句
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @param lockModeType 锁类型
     * @param firstResult 起始记录, 如果小于0则从0开始
     * @param maxResult 最大记录, 此值大于-1时有效
     * @return 符合条件的信息列表
     */
    public List<?> findObjectList(String sql, Map<String, Object> paramMap, boolean isNative, LockModeType lockModeType, int firstResult,
        int maxResult);
    
    /**
     * 通过NamedQuery查询信息列表.
     * 
     * @param queryName 要执行的命名SQL名称
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @return 符合条件的信息列表
     */
    public List<?> findObjectListByNamedQuery(String queryName, Map<String, Object> paramMap);
    
    /**
     * 根据传入的sql和参数查询符合条件的记录数量.
     * <p />
     * 一般用于执行只有一个结果的聚合查询.
     * 
     * @param sql 要执行的sql
     * @param paramMap 参数列表, key为参数名, value为参数值
     * @param isNative true是原生SQL
     * @return 符合条件的记录数量
     */
    public int queryObjectCount(String sql, Map<String, Object> paramMap, boolean isNative);
    
    /**
     * 保存对象(记录)信息
     * 
     * @param entity  要保存的对象(记录)
     * @param isFlush 是否进行flush操作.
     */
    public void save(Object entity, boolean isFlush);
    
    /**
     * 批量保存对象信息.
     * 
     * @param objectList 要保存的对象信息列表
     */
    public void saveList(List<?> objectList);

    /**
     * 批量保存对象信息.
     * 
     * @param objectList 要保存的对象信息列表
     * @param batchSize 多少条数据刷新一次缓存
     */
    public void saveList(List<?> objectList, int batchSize);

    /**
     * 修改对象(记录)
     * 
     * @param entity 要修改的对象(记录)
     */
    public void update(Object entity);

    /**
     * 批量更新对象信息.
     * 
     * @param objectList 要更新的对象信息列表
     */
    public void updateList(List<?> objectList);

    /**
     * 批量更新对象信息.
     * 
     * @param objectList 要更新的对象信息列表
     * @param batchSize 多少条数据刷新一次缓存
     */
    public void updateList(List<?> objectList, int batchSize);
}
