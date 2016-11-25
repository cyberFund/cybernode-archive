package fund.cyber.chainparser.core.dao;

import fund.cyber.chainparser.core.chain.DataFetcher;
import fund.cyber.chainparser.model.Address;
import fund.cyber.chainparser.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDao extends BaseEntityDao<Address, Long> {

    public static final String HASH_FIELD = "hash";

    public AddressDao() {
        super(Address.class);
    }

    public Address getByHash(final String hash) {
        return getByUniqueField(HASH_FIELD, hash);
    }

    @Override
    public long count() {
        return super.count();
    }

}
