package fund.cyber.node.core.service;

import fund.cyber.node.core.dao.TransactionDao;
import fund.cyber.node.model.Transaction;
import fund.cyber.node.model.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Component
@Transactional(propagation = REQUIRED)
public class TransactionService extends CommonService {

    @Autowired
    private TransactionDao transactionDao;

    public TransactionDto getTransaction(final String hash) {
        final Transaction tx = transactionDao.getByHash(hash);
        return show(tx);
    }
}
