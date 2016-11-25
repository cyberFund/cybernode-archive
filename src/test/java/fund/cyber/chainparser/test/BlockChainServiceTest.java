package fund.cyber.chainparser.test;

import fund.cyber.chainparser.core.service.BlockChainService;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:unitTestsContext.xml"})
public class BlockChainServiceTest {

    @Autowired
    BlockChainService blockChainService;

    @Test
    public void storeGenesis() {
        try {
            final NetworkParameters params = new MainNetParams();
            final Block genesis = params.getGenesisBlock();
            final StoredBlock storedGenesis = new StoredBlock(genesis, genesis.getWork(), 0);
            blockChainService.put(storedGenesis);
        } catch (final VerificationException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }
}
