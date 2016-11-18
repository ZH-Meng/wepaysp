package com.zbsp.wepaysp.api.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import com.zbsp.wepaysp.api.dao.CommonORMDAO;

/**
 * 通用ORMDAO的JPA实现
 * 
 * @author Magic
 */
public class CommonDAOJPAImpl extends JpaDaoSupport implements CommonORMDAO {

    private final Logger logger = LogManager.getLogger();
    private static final String LOG_PREFIX = "[CommonDAOJPAImpl]-";
    
    @Override
    public void delete(Object entity) {
        getJpaTemplate().remove(entity);
    }

    @Override
    public int executeBatch(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        Integer result;
        try {
            result = getJpaTemplate().execute(new JpaCallback<Integer>() {

                public Integer doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
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
            result = getJpaTemplate().find(objectClass, objectID);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找对象错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public <T> T findObject(final Class<T> objectClass, final Serializable objectID, final LockModeType lockModeType) {
        T result;
        try {
            result = getJpaTemplate().execute(new JpaCallback<T>() {

                @Override
                public T doInJpa(EntityManager em) throws PersistenceException {
                    return em.find(objectClass, objectID, lockModeType);
                }
            });
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找并锁定对象错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        T result;

        try {
            result = getJpaTemplate().execute(new JpaCallback<T>() {

                public T doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    List<T> resultList = query.getResultList();

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
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative, final LockModeType lockModeType) {
        T result;

        try {
            result = getJpaTemplate().execute(new JpaCallback<T>() {
                @SuppressWarnings("unchecked")
                public T doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    List<T> resultList = query.setLockMode(lockModeType).getResultList();

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

    @Override
    @SuppressWarnings("unchecked")
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        Object[] result;

        try {
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.getResultList();
                }
            }).toArray(new Object[0]);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象数组错误]", re);
            throw re;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final int firstResult, final int maxResult) {
        Object[] result;
        try {
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
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

                    return query.getResultList();
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
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.getResultList();
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
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
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

                    return query.getResultList();
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
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
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

                    return query.setLockMode(lockModeType).getResultList();
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
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.setLockMode(lockModeType).getResultList();
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
            result = getJpaTemplate().executeFind(new JpaCallback<Object>() {
                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query = em.createNamedQuery(queryName);

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.getResultList();
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
        Number result;
        try {
            result = (Number) getJpaTemplate().execute(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
                    Query query;

                    if (isNative) {// 原生SQL时
                        query = em.createNativeQuery(sql);
                    } else {// JPA QL时
                        query = em.createQuery(sql);
                    }

                    if (paramMap != null) {
                        Iterator<String> keySetIterator = paramMap.keySet().iterator();

                        while (keySetIterator.hasNext()) {
                            String paramName = keySetIterator.next();
                            query.setParameter(paramName, paramMap.get(paramName));
                        }
                    }

                    return query.getSingleResult();
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

            Method modifyMethod = entity.getClass().getMethod("getModifyTime");
            Column columnAnnotation = modifyMethod.getAnnotation(Column.class);
            
            if (columnAnnotation != null && !columnAnnotation.nullable()) {
                if (BeanUtils.getProperty(entity, "modifyTime") == null) {
                    BeanUtils.setProperty(entity, "modifyTime", new Date());
                }
                
                if (BeanUtils.getProperty(entity, "modifier") == null) {
                    BeanUtils.setProperty(entity, "modifier", "api");
                }
            }
        } catch (IllegalAccessException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        } catch (InvocationTargetException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        } catch (NoSuchMethodException e) {
            logger.warn(LOG_PREFIX + "[保存对象警告]", e);
        }

        try {
            getJpaTemplate().persist(entity);
            if (isFlush) {
                getJpaTemplate().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象错误]", re);
            throw re;
        }

    }

    @Override
    public void saveList(final List<?> objectList) {
        try {
            getJpaTemplate().execute(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
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
                            
                            Method modifyMethod = entity.getClass().getMethod("getModifyTime");
                            Column columnAnnotation = modifyMethod.getAnnotation(Column.class);
                            
                            if (columnAnnotation != null && !columnAnnotation.nullable()) {
                                if (BeanUtils.getProperty(entity, "modifyTime") == null) {
                                    BeanUtils.setProperty(entity, "modifyTime", new Date());
                                }
                                
                                if (BeanUtils.getProperty(entity, "modifier") == null) {
                                    BeanUtils.setProperty(entity, "modifier", "api");
                                }
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                        }

                        em.persist(entity);
                        if (i % 20 == 0) {
                            em.flush();
                            em.clear();
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
            getJpaTemplate().execute(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
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

                            Method modifyMethod = entity.getClass().getMethod("getModifyTime");
                            Column columnAnnotation = modifyMethod.getAnnotation(Column.class);
                            
                            if (columnAnnotation != null && !columnAnnotation.nullable()) {
                                if (BeanUtils.getProperty(entity, "modifyTime") == null) {
                                    BeanUtils.setProperty(entity, "modifyTime", new Date());
                                }
                                
                                if (BeanUtils.getProperty(entity, "modifier") == null) {
                                    BeanUtils.setProperty(entity, "modifier", "api");
                                }
                            }
                        } catch (IllegalAccessException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        } catch (InvocationTargetException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        } catch (NoSuchMethodException e) {
                            logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                        }

                        em.persist(entity);
                        if (i % batchSize == 0) {
                            em.flush();
                            em.clear();
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
            getJpaTemplate().merge(entity);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象错误]", re);
            throw re;
        }
    }

    @Override
    public void updateList(final List<?> objectList) {
        try {
            getJpaTemplate().execute(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
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

                        em.merge(entity);
                        if (i % 20 == 0) {
                            em.flush();
                            em.clear();
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
    public void updateList(final List<?> objectList, final int batchSize) {
        try {
            getJpaTemplate().execute(new JpaCallback<Object>() {

                public Object doInJpa(EntityManager em) throws PersistenceException {
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

                        em.merge(entity);
                        if (i % batchSize == 0) {
                            em.flush();
                            em.clear();
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
