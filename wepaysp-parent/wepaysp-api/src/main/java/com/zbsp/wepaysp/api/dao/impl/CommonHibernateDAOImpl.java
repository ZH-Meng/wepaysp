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
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.jpa.internal.util.LockModeTypeHelper;
import org.hibernate.query.Query;
import org.hibernate.query.QueryProducer;
import org.springframework.beans.factory.annotation.Autowired;

import com.zbsp.wepaysp.api.dao.CommonORMDAO;


/**
 * 通用ORMDAO的hibernate实现
 * 
 * @author 杨帆
 * @author Liuht
 */
public class CommonHibernateDAOImpl
    implements CommonORMDAO {

    private final Logger logger = LogManager.getLogger();
    private static final String LOG_PREFIX = "[CommonHibernateDAOImpl]-";
    
    @Autowired
    private SessionFactory sessionFactory;
    
    protected final Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 将javax.persistence.LockModeType转换为org.hibernate.LockMode
     * 
     * @param lockModeType
     *            JPA中的锁类型
     * @return Hibernate中的锁类型
     */
    private LockMode convertLockModeType(LockModeType lockModeType) {
        return LockModeTypeHelper.getLockMode(lockModeType);
    }

    @Override
    public void delete(Object entity) {
        currentSession().delete(entity);
    }

    @Override
    public int executeBatch(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        try {
            Query<?> query;

            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行批量SQL错误]", re);
            throw re;
        }

    }

    @Override
    public <T> T findObject(Class<T> objectClass, Serializable objectID) {
        try {
            return currentSession().get(objectClass, objectID);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找对象错误]", re);
            throw re;
        }
    }

    @Override
    public <T> T findObject(final Class<T> objectClass, final Serializable objectID, LockModeType lockModeType) {
        try {
            return currentSession().get(objectClass, objectID, convertLockModeType(lockModeType));
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[根据主键查找并锁定对象错误]", re);
            throw re;
        }
    }
    
    public <T> void refresh(T entity, LockModeType lockModeType) {
        try {
            currentSession().refresh(entity, convertLockModeType(lockModeType));
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[刷新并锁定对象错误]", re);
            throw re;
        }
    }
    
    @Override
    public <T> T findUniqueObject(Class<T> objectClass, String propName, Serializable propVal) {
        try {
            TypedQuery<T> typedQuery = createTypedQuery(objectClass, propName);
            typedQuery.setParameter(propName, propVal);
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]-[记录不唯一]");
            return null;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]", re);
            throw re;
        }
    }

    @Override
    public <T> List<T> findByProperty(Class<T> objectClass, String propName, Serializable propVal) {
        try {
            TypedQuery<T> typedQuery = createTypedQuery(objectClass, propName);
            typedQuery.setParameter(propName, propVal);
            return typedQuery.getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]", re);
            throw re;
        }
    }

    /**
     * 创建Criteria查询对象
     *
     * @param <T>
     * @param entityClass
     * @param criterions
     * @return
     */
    private <T> TypedQuery<T> createTypedQuery(Class<T> entityClass, String ... propParams) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> retObj = criteriaQuery.from(entityClass);
        criteriaQuery.select(retObj);
        for (String propParam : propParams) {
            Class<?> propClazz = null;
            try {
                propClazz = PropertyUtils.getPropertyType(entityClass.newInstance(), propParam);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error(LOG_PREFIX + "[执行Criteria查询对象错误]", e);
            } catch (InstantiationException e) {
                logger.error(LOG_PREFIX + "[无法实例化对象]", e);
            }
            Objects.requireNonNull(propClazz, "属性：" + propParam + "值类型无法获取");
            ParameterExpression<?> paramExpres = criteriaBuilder.parameter(propClazz, propParam);
            criteriaQuery.where(criteriaBuilder.equal(retObj.get(propParam), paramExpres));
        }
        TypedQuery<T> typedQuery = currentSession().createQuery(criteriaQuery);
        return typedQuery;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        try {
            
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]-[记录不唯一]");
            return null;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findObject(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final LockModeType lockModeType) {
        try {
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return (T) query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象错误]-[记录不唯一]");
            return null;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找并锁定对象错误]", re);
            throw re;
        }
    }

    @Override
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        try {
            Query<?> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getResultList().toArray(new Object[0]);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象数组错误]", re);
            throw re;
        }

    }

    @Override
    public Object[] findObjectArray(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final int firstResult, final int maxResult) {
        try {
            Query<?> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getResultList().toArray(new Object[0]);
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找对象数组错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        try {
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象列表错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final int firstResult, final int maxResult) {
        try {
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找对象列表错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final LockModeType lockModeType, final int firstResult, final int maxResult) {
        try {
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL分页查找并锁定对象列表错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findObjectList(final String sql, final Map<String, Object> paramMap, final boolean isNative,
        final LockModeType lockModeType) {
        try {
            Query<T> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.setLockOptions(new LockOptions(convertLockModeType(lockModeType))).getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找并锁定对象列表错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findObjectListByNamedQuery(final String queryName, final Map<String, Object> paramMap) {
        try {
            
            QueryProducer session = currentSession();
            Query<T> query = session.getNamedQuery(queryName);

            if (paramMap != null) {
                Iterator<String> keySetIterator = paramMap.keySet().iterator();

                while (keySetIterator.hasNext()) {
                    String paramName = keySetIterator.next();
                    query.setParameter(paramName, paramMap.get(paramName));
                }
            }

            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行命名SQL查找对象列表错误]", re);
            throw re;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public int queryObjectCount(final String sql, final Map<String, Object> paramMap, final boolean isNative) {
        try {
            Query<Long> query;
            QueryProducer session = currentSession();
            if (isNative) {// 原生SQL时
                query = session.createNativeQuery(sql);
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

            return query.getSingleResult().intValue();

        } catch (NoResultException e) {
            return 0;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行聚合SQL统计对象数量错误]", re);
            throw re;
        }
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
            if (logger.isDebugEnabled()) {
                logger.warn(LOG_PREFIX + "[保存对象警告]", e);
            }
        }

        try {
            currentSession().persist(entity);
            if (isFlush) {
                currentSession().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象错误]", re);
            throw re;
        }
    }

    @Override
    public <T> void saveList(final List<T> objectList) {
        try {
            int size = objectList.size();
            Session session = currentSession();
            session.setJdbcBatchSize(20);

            for (int i = 0; i < size; i++) {
                T entity = objectList.get(i);

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
                    if (logger.isDebugEnabled()) {
                        logger.warn(LOG_PREFIX + "[保存对象列表警告]", e);
                    }
                }

                session.persist(entity);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象列表错误]", re);
            throw re;
        }
    }
    
    @Override
    public <T> void batchSave(List<T> objectList, int batchSize) {
        try {
            StatelessSession statelessSession = sessionFactory.openStatelessSession();
            statelessSession.setJdbcBatchSize(batchSize);

            for (T entity : objectList) {
                statelessSession.insert(entity);
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[指定缓存大小保存对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public <T> void saveList(final List<T> objectList, final int batchSize) {
        try {
            int size = objectList.size();
            Session session = currentSession();
            session.setJdbcBatchSize(20);

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
                    if (logger.isDebugEnabled()) {
                        logger.warn(LOG_PREFIX + "[指定缓存大小保存对象列表警告]", e);
                    }
                }

                session.persist(entity);
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[指定缓存大小保存对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public void update(Object entity) {
        update(entity, false);
    }
    
    @Override
    public void update(Object entity, boolean isFlush) {
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
            if (logger.isDebugEnabled()) {
                logger.warn(LOG_PREFIX + "[修改对象警告]", e);
            }
        }

        try {
            currentSession().merge(entity);
            if (isFlush) {
                currentSession().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象错误]", re);
            throw re;
        }
    }

    @Override
    public <T> void updateList(final List<T> objectList) {
        try {
            int size = objectList.size();
            Session session = currentSession();
            session.setJdbcBatchSize(20);

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
                    if (logger.isDebugEnabled()) {
                        logger.warn(LOG_PREFIX + "[修改对象列表警告]", e);
                    }
                }

                session.merge(entity);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public <T> void updateList(final List<T> objectList, int batchSize) {
        try {
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
                    if (logger.isDebugEnabled()) {
                        logger.warn(LOG_PREFIX + "[指定缓存大小修改对象列表警告]", e);
                    }
                }

                Session session = currentSession();
                session.merge(entity);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[指定缓存大小修改对象列表错误]", re);
            throw re;
        }
    }
    
    @Override
    public void saveWithoutCreator(Object entity, boolean isFlush) {
        try {
            currentSession().persist(entity);
            if (isFlush) {
                currentSession().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[保存对象错误]", re);
            throw re;
        }
    }
    
    @Override
    public void updateWithoutModifer(Object entity) {
        updateWithoutModifer(entity, false);
    }

    @Override
    public void updateWithoutModifer(Object entity, boolean isFlush) {
        try {
            currentSession().merge(entity);
            if (isFlush) {
                currentSession().flush();
            }
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[修改对象错误]", re);
            throw re;
        }
    }

    @Override
    public <T> List<T> findVoListByNativeSql(String sql, String resultMapping, Map<String, Object> paramMap) {
        try {
            @SuppressWarnings("unchecked")
            Query<T> query = currentSession().createNativeQuery(sql, resultMapping);

            if (paramMap != null) {
                Iterator<String> keySetIterator = paramMap.keySet().iterator();

                while (keySetIterator.hasNext()) {
                    String paramName = keySetIterator.next();
                    query.setParameter(paramName, paramMap.get(paramName));
                }
            }
            return query.getResultList();
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[执行SQL查找对象列表错误]", re);
            throw re;
        }
    }

    @Override
    public List<Object> execProcedures(String sql, final List<Object> outParams, final Object...inParams) {
        try {
            List<Object> returnParam = new ArrayList<>();
            currentSession().doWork( connection -> {
                try (CallableStatement function = connection.prepareCall(sql)) {
                    int paramIndex = 1;
                    if(null != outParams && !outParams.isEmpty()){
                        for (Object outParam : outParams) {
                            if(outParam instanceof Integer || outParam instanceof Long){
                                function.registerOutParameter(paramIndex++, Types.INTEGER );
                            } else if(outParam instanceof String){
                                function.registerOutParameter(paramIndex++, Types.VARCHAR );
                            } 
                        }
                    }
                    if (inParams != null && inParams.length > 0) {
                        for (Object paramValue : inParams) {
                            if(paramValue instanceof Date){
                                java.sql.Date dbParamValue = new java.sql.Date(((Date) paramValue).getTime());
                                function.setDate(paramIndex++, dbParamValue);
                            } else if(paramValue instanceof String) {
                                function.setString(paramIndex++, (String) paramValue);
                            } else if(paramValue instanceof Integer) {
                                function.setInt(paramIndex++, ((Integer) paramValue).intValue());
                            } else if(paramValue instanceof Long) {
                                function.setLong(paramIndex++, ((Long) paramValue).longValue());
                            } else {
                                function.setNull(paramIndex++, Types.NULL);
                            }
                        }
                    }
                    function.execute();
                    paramIndex = 1;
                    for (Object outParam : outParams) {
                        if(outParam instanceof Integer || outParam instanceof Long){
                            returnParam.add(function.getInt(paramIndex++));
                        } else if(outParam instanceof String){
                            returnParam.add(function.getString(paramIndex++));
                        } 
                    }
                }
            });
            return returnParam;
        } catch (RuntimeException re) {
            logger.error(LOG_PREFIX + "[调用存储过程错误]", re);
            throw re;
        }
    }

}
