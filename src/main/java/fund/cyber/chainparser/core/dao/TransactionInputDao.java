package fund.cyber.chainparser.core.dao;

import fund.cyber.chainparser.model.TransactionInput;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionInputDao extends BaseEntityDao<TransactionInput, Long> {

    public TransactionInputDao() {
        super(TransactionInput.class);
    }

    @Override
    public long count() {
        return super.count();
    }

}
