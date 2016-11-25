package fund.cyber.chainparser.core.service;

import fund.cyber.chainparser.core.dao.AddressDao;
import fund.cyber.chainparser.core.dao.BlockDao;
import fund.cyber.chainparser.core.dao.TransactionDao;
import fund.cyber.chainparser.core.dao.TransactionInputDao;
import fund.cyber.chainparser.core.dao.TransactionOutputDao;
import fund.cyber.chainparser.model.Address;
import fund.cyber.chainparser.model.Transaction;
import fund.cyber.chainparser.model.TransactionInput;
import fund.cyber.chainparser.model.TransactionOutput;
import fund.cyber.chainparser.model.dto.AddressDto;
import fund.cyber.chainparser.model.dto.AddressTransactionDto;
import fund.cyber.chainparser.model.dto.TransactionDto;
import fund.cyber.chainparser.model.dto.TransactionInputDto;
import fund.cyber.chainparser.model.dto.TransactionOutputDto;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Component
@Transactional(propagation = REQUIRED)
public class AddressService extends CommonService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private AddressDao addressDao;

    private final NetworkParameters params = new MainNetParams();

    public AddressDto getAddress(final String hash) {
        final AddressDto result = new AddressDto(hash);
        final Address address = addressDao.getByHash(hash);
        if (address == null) {
            return result;
        }
        result.setHash(hash);

        final List<Transaction> transactions = transactionDao.getTransactionByAddress(address);
        if (transactions.size() == 0) {
            log.error("Address with no transaction should not be stored in the Database");
            return result;
        }

        result.setTransactions(transactions.stream().map(tx -> show(tx, hash)).collect(Collectors.toList()));
        result.setBalance(result.getTransactions().stream().map(AddressTransactionDto::getEffect).reduce(0L, (a, b) -> a + b));

        return result;
    }
}

