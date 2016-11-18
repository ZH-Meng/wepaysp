/*
 * CommonHibernateDAOImpl.java
 * 创建者：杨帆
 * 创建日期：2015年4月20日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.zbsp.wepaysp.api.dao.CommonORMDAO;

/**
 * 通用ORMDAO的hibernate实现
 * 
 * @author 杨帆
 */
public class CommonHibernateDAOImpl extends HibernateDaoSupport implements CommonORMDAO {

    private final Logger logger = LogManager.getLogger();
    private static final String LOG_PREFIX = "[CommonHibernateDAOImpl]-";
    
    /**
     * 将javax.persistence.LockModeType转换为org.hibernate.LockMode
     * 
     * @param lockModeType JPA中的锁类型
     * @return Hibernate中的锁类型
     */
    private LockMode convertLockModeType(LockModeType lockModeType) {
        LockMode lockMode;

        switch (lockModeType) {
            case OPTIMISTIC:
                lockMode = LockMode.OPTIMISTIC;
                break;
            case OPTIMISTIC_FORCE_INCREMENT:
                lockMode = LockMode.OPTIMISTIC_FORCE_INCREMENT;
                break;
            case PESSIMISTIC_FORCE_INCREMENT:
                lockMode = LockMode.PESSIMISTIC_FORCE_INCREMENT;
                break;
            case PESSIMISTIC_READ:
                lockMode = LockMode.PESSIMISTIC_READ;
                break;
            case PESSIMISTIC_WRITE:
                lockMode = LockMode.PESSIMISTIC_WRITE;
                break;
            case READ:
                lockMode = LockMode.READ;
                break;
            case WRITE:
                lockMode = LockMode.WRITE;
                break;
            default:
                lockMode = LockMode.NONE;
                break;
        }
        
        return lockMode;
    }

    @Override
    public void delete(Object entity) {
        getHibernateTemplate().delete(entity);
    }

    @Override
    public int executeBatch(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        Integer result;
        try {
            result = getHibernateTemplate().execute(new HibernateCallback<Integer>() {
                @Override
                public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }
                    return query.executeUpdate();
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行批量SQL错误]", re);
            throw re;
        }

        return result.intValue();
    }

    @Override
    public <T> T findObject(Class<T> objectClass, Serializable objectID) {
        T result;
        try {
            result = getHibernateTemplate().get(objectClass, objectID);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找对象错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public <T> T findObject(final Class<T> objectClass, final Serializable objectID, LockModeType lockModeType) {
        T result;
        try {
            result = getHibernateTemplate().get(objectClass, objectID, convertLockModeType(lockModeType));
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找并锁定对象错误]", re);
            throw re;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        T result;

        try {
            result = getHibernateTemplate().execute(new HibernateCallback<T>() {

                @Override
                public T doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    List<T> resultList = query.list();

                    T obj = null;
                    if (resultList != null && resultList.size() > 0) {
                        obj = resultList.get(0);
                    }

                    return obj;
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative, final LockModeType lockModeType) {
        T result;

        try {
            result = getHibernateTemplate().execute(new HibernateCallback<T>() {
                @SuppressWarnings("unchecked")
                public T doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    List<T> resultList = query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).list();

                    T obj = null;
                    if (resultList != null && resultList.size() > 0) {
                        obj = resultList.get(0);
                    }

                    return obj;
                }
            });
        } catch (NoResultException e) {
            return null;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找并锁定对象错误]", re);
            throw re;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        Object[] result;
        
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {
                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();
                        
                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.list();
                }
                
            }).toArray(new Object[0]);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象数组错误]", re);
            throw re;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative, 
            final int firstResult, final int maxResult) {
        Object[] result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {
                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// JPA QL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();
                        
                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    query.setFirstResult(firstResult < 0 ? 0 : firstResult);

                    if (maxResult > -1) {
                        query.setMaxResults(maxResult);
                    }

                    return query.list();
                }
            }).toArray(new Object[0]);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找对象数组错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public List<?> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        List<?> result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.list();
                }

            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象列表错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public List<?> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
            final int firstResult, final int maxResult) {
        List<?> result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    query.setFirstResult(firstResult < 0 ? 0 : firstResult);

                    if (maxResult > -1) {
                        query.setMaxResults(maxResult);
                    }

                    return query.list();
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找对象列表错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public List<?> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
            final LockModeType lockModeType, final int firstResult, final int maxResult) {
        List<?> result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }
                    
                    query.setFirstResult(firstResult < 0 ? 0 : firstResult);

                    if (maxResult > -1) {
                        query.setMaxResults(maxResult);
                    }
                    
                    return query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).list();
                }

            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找并锁定对象列表错误]", re);
            throw re;
        }

        return result;
    }
    
    @Override
    public List<?> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
            final LockModeType lockModeType) {
        List<?> result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }
                    
                    return query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).list();
                }

            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找并锁定对象列表错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public List<?> findObjectListByNamedQuery(final String queryName, final Map<String, Object> paramMap) {
        List<?> result;
        try {
            result = getHibernateTemplate().executeFind(new HibernateCallback<Object>() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query = session.getNamedQuery(queryName);

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.list();
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行命名SQL查找对象列表错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public int queryObjectCount(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        Long result;
        try {
            result = getHibernateTemplate().execute(new HibernateCallback<Long>() {
                @Override
                public Long doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = session.createSQLQuery(sql);
                    } else {// HQL时
                        query = session.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();
                        
                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return (Long) query.uniqueResult();
                }
                
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行聚合SQL统计对象数量错误]", re);
            throw re;
        }

        if (result == null) {
            return 0;
        }

        return result.intValue();
    }

    @Override
    public void save(Object entity, boolean isFlush) {
        try {
            if (BeanUtils.getProperty(entity, "createTime") == null) {
                BeanUtils.setProperty(entity, "createTime", new Date());
            }
            if (BeanUtils.getProperty(entity, "creator") == null) {
                BeanUtils.setProperty(entity, "creator", "api");
            }
        } catch (IllegalAccessException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        } catch (InvocationTargetException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        } catch (NoSuchMethodException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        }

        try {
            getHibernateTemplate().persist(entity);
            if (isFlush) {
                getHibernateTemplate().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象错误]", re);
            throw re;
        }
    }

    @Override
    public void saveList(final List<?> objectList) {
        try {
            getHibernateTemplate().execute(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    int size = objectList.size();

                    for (int i = 0; i < size; i++) {
                        Object entity = objectList.get(i);

                        try {
                            if (BeanUtils.getProperty(entity, "createTime") == null) {
                                BeanUtils.setProperty(entity, "createTime", new Date());
                            }
                            if (BeanUtils.getProperty(entity, "creator") == null) {
                                BeanUtils.setProperty(entity, "creator", "api");
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        }

                        session.persist(entity);
                        if (i % 20 == 0) {
                            session.flush();
                            session.clear();
                        }
                    }
                    return null;
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public void saveList(final List<?> objectList, final int batchSize) {
        try {
            getHibernateTemplate().execute(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    int size = objectList.size();

                    for (int i = 0; i < size; i++) {
                        Object entity = objectList.get(i);

                        try {
                            if (BeanUtils.getProperty(entity, "createTime") == null) {
                                BeanUtils.setProperty(entity, "createTime", new Date());
                            }
                            if (BeanUtils.getProperty(entity, "creator") == null) {
                                BeanUtils.setProperty(entity, "creator", "api");
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        }

                        session.persist(entity);
                        if (i % batchSize == 0) {
                            session.flush();
                            session.clear();
                        }
                    }
                    return null;
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[指定缓存大小保存对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public void update(Object entity) {
        try {
            BeanUtils.setProperty(entity, "modifyTime", new Date());

            if (BeanUtils.getProperty(entity, "modifier") == null) {
                BeanUtils.setProperty(entity, "modifier", "api");
            }
        } catch (IllegalAccessException e) {
            logger.warn(LOG_PREFIX + "[修改对象警告]", e);
        } catch (InvocationTargetException e) {
            logger.warn(LOG_PREFIX + "[修改对象警告]", e);
        } catch (NoSuchMethodException e) {
            logger.warn(LOG_PREFIX + "[修改对象警告]", e);
        }

        try {
            getHibernateTemplate().merge(entity);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象错误]", re);
            throw re;
        }
    }

    @Override
    public void updateList(final List<?> objectList) {
        try {
            getHibernateTemplate().execute(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    int size = objectList.size();

                    for (int i = 0; i < size; i++) {
                        Object entity = objectList.get(i);

                        try {
                            BeanUtils.setProperty(entity, "modifyTime", new Date());

                            if (BeanUtils.getProperty(entity, "modifier") == null) {
                                BeanUtils.setProperty(entity, "modifier", "api");
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[修改对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[修改对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[修改对象列表警告]", e);
                        }

                        session.merge(entity);
                        if (i % 20 == 0) {
                            session.flush();
                            session.clear();
                        }
                    }
                    return null;
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public void updateList(final List<?> objectList, int batchSize) {
        try {
            getHibernateTemplate().execute(new HibernateCallback<Object>() {

                @Override
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    int size = objectList.size();

                    for (int i = 0; i < size; i++) {
                        Object entity = objectList.get(i);

                        try {
                            BeanUtils.setProperty(entity, "modifyTime", new Date());

                            if (BeanUtils.getProperty(entity, "modifier") == null) {
                                BeanUtils.setProperty(entity, "modifier", "api");
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小修改对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小修改对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小修改对象列表警告]", e);
                        }

                        session.merge(entity);
                        if (i % 20 == 0) {
                            session.flush();
                            session.clear();
                        }
                    }
                    return null;
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[指定缓存大小修改对象列表错误]", re);
            throw re;
        }
    }
}
