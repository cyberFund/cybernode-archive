package fund.cyber.chainparser.core.service;

import fund.cyber.chainparser.core.dao.TransactionDao;
import fund.cyber.chainparser.model.Transaction;
import fund.cyber.chainparser.model.dto.TransactionDto;
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
