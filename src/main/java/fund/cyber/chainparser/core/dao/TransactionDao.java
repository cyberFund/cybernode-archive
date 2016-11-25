package fund.cyber.chainparser.core.dao;

import fund.cyber.chainparser.model.Address;
import fund.cyber.chainparser.model.Block;
import fund.cyber.chainparser.model.Transaction;
import fund.cyber.chainparser.model.TransactionInput;
import fund.cyber.chainparser.model.TransactionOutput;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

import static javax.persistence.criteria.JoinType.LEFT;

@Repository
public class TransactionDao extends BaseEntityDao<Transaction, Long> {

    public static final String HASH_FIELD = "hash";

    public TransactionDao() {
        super(Transaction.class);
    }

    @Override
    public long count() {
        return super.count();
    }

    public List<Transaction> getTransactionByAddress(final Address address) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
        final Root<Transaction> root = criteria.from(Transaction.class);

        final Join<Transaction, Block> block = root.join("block", LEFT);
        final Join<Transaction, TransactionOutput> output = root.join("outputs", LEFT);
        final Join<Transaction, TransactionInput> input = root.join("inputs", LEFT);
        final Join<TransactionInput, TransactionOutput> prevOutput = input.join("output", LEFT);
        criteria.where(builder.or(builder.equal(output.get("address"), address), builder.equal(prevOutput.get("address"), address)));
        criteria.orderBy(builder.desc(block.get("height")));
        criteria.distinct(true);

        final TypedQuery<Transaction> query = em.createQuery(criteria);

        return query.getResultList();
    }

    public Transaction getByHash(final String hash) {
        return getByUniqueField(HASH_FIELD, hash);
    }
}
