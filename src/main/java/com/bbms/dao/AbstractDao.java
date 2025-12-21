package com.bbms.dao;

import com.bbms.config.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements GenericDao<T, ID> {

    private static final Logger logger = LogManager.getLogger(AbstractDao.class);
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    @Override
    public T save(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            logger.debug("Saved entity: {}", entityClass.getSimpleName());
            return entity;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to save entity", e);
            throw e;
        }
    }

    @Override
    public T update(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();
            logger.debug("Updated entity: {}", entityClass.getSimpleName());
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update entity", e);
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            session.remove(session.merge(entity));
            tx.commit();
            logger.debug("Deleted entity: {}", entityClass.getSimpleName());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete entity", e);
            throw e;
        }
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = getSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = getSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        }
    }

    @Override
    public long count() {
        try (Session session = getSession()) {
            return session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class)
                    .getSingleResult();
        }
    }

    protected List<T> executeQuery(String hql, Object... params) {
        try (Session session = getSession()) {
            var query = session.createQuery(hql, entityClass);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.getResultList();
        }
    }

    protected Optional<T> executeSingleQuery(String hql, Object... params) {
        List<T> results = executeQuery(hql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
