package fund.cyber.node.core.dao;

import fund.cyber.node.model.Address;
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
