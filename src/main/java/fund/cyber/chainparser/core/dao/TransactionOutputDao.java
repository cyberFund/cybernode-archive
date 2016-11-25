package fund.cyber.chainparser.core.dao;

import fund.cyber.chainparser.model.Transaction;
import fund.cyber.chainparser.model.TransactionOutput;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import static javax.persistence.criteria.JoinType.LEFT;

@Repository
public class TransactionOutputDao extends BaseEntityDao<TransactionOutput, Long> {

    public TransactionOutputDao() {
        super(TransactionOutput.class);
    }

    @Override
    public long count() {
        return super.count();
    }

    public TransactionOutput get(final Transaction transaction, final long index) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<TransactionOutput> criteria = builder.createQuery(TransactionOutput.class);
        final Root<TransactionOutput> root = criteria.from(TransactionOutput.class);
        criteria.where(builder.and(builder.equal(root.get("transaction"), transaction), builder.equal(root.get("position"), index)));
        final TypedQuery<TransactionOutput> query = em.createQuery(criteria);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            // do nothing
            return null;
        }
    }
}
