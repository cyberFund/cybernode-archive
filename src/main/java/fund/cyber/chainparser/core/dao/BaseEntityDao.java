package fund.cyber.chainparser.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class BaseEntityDao<T, KEY> {

    @PersistenceContext
    protected EntityManager em;

    protected final Class<T> clazz;

    public BaseEntityDao(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public T merge(final T entity) {
        return em.merge(entity);
    }

    public void persist(final T entity) {
        em.persist(entity);
    }

    public void flush() {
        em.flush();
    }

    public void clear() {
        em.clear();
    }

    public void refresh(final T entity) {
        em.refresh(entity);
    }

    public void delete(final T entity) {
        em.remove(entity);
    }

    protected T getEntity(final KEY id) {
        return em.find(clazz, id);
    }

    protected long count() {
        return count(null);
    }

    protected long count(final Predicator<T> p) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        final Root<T> root = criteria.from(clazz);
        if (p != null) {
            criteria.where(p.predicate(builder, root));
        }
        criteria.select(builder.count(root));
        return em.createQuery(criteria).getSingleResult();
    }

    protected List<T> getList(final Class<T> clazz, final int offset, final int limit) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(clazz);

        final TypedQuery<T> query = em.createQuery(criteria);
        query.setFirstResult(offset);
        query.setMaxResults(offset + limit);

        return query.getResultList();
    }

    protected T getByUniqueField(final String fieldName, final Object value) {
        return getByPredicate((builder, root) -> builder.equal(root.get(fieldName), value));
    }

    protected T getWithMaxValue(final String fieldName) {
        return getWithMaxValue(fieldName, null);
    }

    protected T getWithMaxValue(final String fieldName, final Predicator<T> p) {
        return getWithExtremeValue(fieldName, p, true, false);
    }

    protected T getWithMinValue(final String fieldName, final Predicator<T> p, final boolean distinct) {
        return getWithExtremeValue(fieldName, p, false, distinct);
    }

    protected T getWithMinValue(final String fieldName, final Predicator<T> p) {
        return getWithExtremeValue(fieldName, p, false, false);
    }

    protected T getWithExtremeValue(final String fieldName, final Predicator<T> p, final boolean max, final boolean distinct) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(clazz);
        final Root<T> root = criteria.from(clazz);
        if (p != null) {
            criteria.where(p.predicate(builder, root));
        }
        if (distinct) {
            criteria.distinct(true);
        }
        criteria.orderBy(max ? builder.desc(root.get(fieldName)) : builder.asc(root.get(fieldName)));

        final TypedQuery<T> query = em.createQuery(criteria);
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            // do nothing
            return null;
        }
    }

    protected T getByPredicate(final Predicator<T> p) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(clazz);
        final Root<T> root = criteria.from(clazz);
        criteria.where(p.predicate(builder, root));
        final TypedQuery<T> query = em.createQuery(criteria);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            // do nothing
            return null;
        }

    }

    protected List<T> getAll() {
        return getListByPredicate(null);
    }

    protected List<T> getListByPredicate(final Predicator<T> p) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(clazz);
        final Root<T> root = criteria.from(clazz);
        if (p != null) {
            criteria.where(p.predicate(builder, root));
        }
        return em.createQuery(criteria).getResultList();

    }
}
