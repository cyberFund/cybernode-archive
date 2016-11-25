package fund.cyber.node.core.dao;

import fund.cyber.node.model.Block;
import fund.cyber.node.model.Transaction;
import fund.cyber.node.model.TransactionInput;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

import static javax.persistence.criteria.JoinType.LEFT;

@Repository
public class BlockDao extends BaseEntityDao<Block, Long> {

    private static final int EMPTY_BLOCK_LIST_MAX_RESULT = 100;
    public static final String HASH_FIELD = "hash";
    public static final String ID_FIELD = "id";
    public static final String CHAINWORK_FIELD = "chainwork";
    public static final String CHAINHEAD_FIELD = "chainhead";
    public static final String HEIGHT_FIELD = "height";
    public static final String TRANSACTIONS_FIELD = "transactions";
    public static final String INPUTS_FIELD = "inputs";
    public static final String OUTPUT_FIELD = "output";
    public static final String POSITION_FIELD = "position";
    public static final String FULL_FIELD = "indexed";

    public BlockDao() {
        super(Block.class);
    }

    @Override
    public long count() {
        return super.count();
    }

    public Block getByHash(final String hash) {
        return getByUniqueField(HASH_FIELD, hash);
    }

    public Block getById(final long id) {
        return getByUniqueField(ID_FIELD, id);
    }

    public List<Block> getAll() {
        return super.getAll();
    }

    //FIXME need to write unit test
    public Block getChainHead() {
        return getWithMaxValue(CHAINWORK_FIELD,(builder, root) -> builder.equal(root.get(CHAINHEAD_FIELD), true));
    }

    public Block getLastBlock() {
        return getWithMaxValue(CHAINWORK_FIELD);
    }

    public Block getLastIndexedBlock() {
        return getWithMinValue(HEIGHT_FIELD, (builder, root) -> builder.equal(root.get(FULL_FIELD), true));
    }

    public List<Block> getAbove(final int height) {
        return getListByPredicate((builder, root) -> builder.greaterThan(root.get(HEIGHT_FIELD), height));
    }

    public Long countIndexed() {
        return count((builder, root) -> builder.equal(root.get(FULL_FIELD), true));
    }

    public Block getEmptyBlockMinHeight() {
        return getWithMinValue(HEIGHT_FIELD, (builder, root) -> builder.equal(root.get(FULL_FIELD), false));
    }

    public Block getIncompleteBlockMinHeight() {
        return getWithMinValue(HEIGHT_FIELD, (builder, root) -> {
            final Join<Block, Transaction> tx
                    = root.join(TRANSACTIONS_FIELD, LEFT);
            final Join<Transaction, TransactionInput> input = tx.join(INPUTS_FIELD, LEFT);
            return builder.and(builder.isNull(input.get(OUTPUT_FIELD)), builder.greaterThan(tx.get(POSITION_FIELD), 0));
        });
    }

    public List<String> getIncompleteBlockHeightsLowerThan(final int height) {

        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<String> criteria = builder.createQuery(String.class);
        final Root<Block> root = criteria.from(Block.class);
        final Join<Block, Transaction> tx
                = root.join(TRANSACTIONS_FIELD, LEFT);
        final Join<Transaction, TransactionInput> input = tx.join(INPUTS_FIELD, LEFT);
        criteria.where(builder.and(
                builder.lessThan(root.get(HEIGHT_FIELD), height),
                builder.isNull(input.get(OUTPUT_FIELD)),
                builder.greaterThan(tx.get(POSITION_FIELD), 0)));
        criteria.select(root.get(HASH_FIELD));
        criteria.distinct(true);
        return em.createQuery(criteria).getResultList();

    }

    public List<Block> getEmpty() {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Block> criteria = builder.createQuery(Block.class);

        final Root<Block> root = criteria.from(Block.class);
        final Join<Block, Transaction> transaction = root.join(TRANSACTIONS_FIELD, LEFT);
        criteria.where(builder.isNull(transaction.get(ID_FIELD)));

        final TypedQuery<Block> query = em.createQuery(criteria);
        query.setFirstResult(0);
        query.setMaxResults(EMPTY_BLOCK_LIST_MAX_RESULT);
        return query.getResultList();
    }

    public List<Block> getUncompleted() {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Block> criteria = builder.createQuery(Block.class);

        final Root<Block> root = criteria.from(Block.class);
        final Join<Block, Transaction> tx = root.join(TRANSACTIONS_FIELD, LEFT);
        final Join<Transaction, TransactionInput> input = tx.join(INPUTS_FIELD, LEFT);
        criteria.where(builder.and(builder.isNull(input.get(OUTPUT_FIELD)), builder.greaterThan(tx.get(POSITION_FIELD), 0)));
        criteria.distinct(true);

        final TypedQuery<Block> query = em.createQuery(criteria);
        query.setFirstResult(0);
        query.setMaxResults(EMPTY_BLOCK_LIST_MAX_RESULT);
        return query.getResultList();
    }


    public Integer getBlockHeight(final String hash) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Integer> criteria = builder.createQuery(Integer.class);

        final Root<Block> root = criteria.from(Block.class);
        criteria.where(builder.equal(root.get(HASH_FIELD), hash));
        criteria.select(root.get(HEIGHT_FIELD));

        try {
            return em.createQuery(criteria).getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }
}
