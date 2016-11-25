package fund.cyber.chainparser.core.chain;

import fund.cyber.chainparser.core.service.BlockChainService;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class DatabaseBlockStore implements BlockStore, InitializingBean {

    private final BlockChainService blockChainService;

    private static final Logger log = LoggerFactory.getLogger(DatabaseBlockStore.class);

    private Map<String, StoredBlock> blocks = new HashMap<>();

    @Autowired
    public DatabaseBlockStore(final BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
        Assert.notNull(blockChainService, "BlockChainService must not be null!");
    }

    //FIXME support different network parameters
    @Override
    public void afterPropertiesSet() throws Exception {

        blocks = blockChainService.getAllBlocks();

        if (blockChainService.getChainHead() != null) {
            return;
        }

        try {
            final NetworkParameters params = getParams();
            final Block genesis = params.getGenesisBlock().cloneAsHeader();
            final StoredBlock storedGenesis = new StoredBlock(genesis, genesis.getWork(), 0);
            put(storedGenesis);
            setChainHead(storedGenesis);
        } catch (BlockStoreException | VerificationException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }


    @Override
    public void put(final StoredBlock storedBlock) throws BlockStoreException {
        log.debug("put");
        blockChainService.put(storedBlock);
        log.debug("USER PUT THREAD FINISH!!!");
    }

    @Override
    public StoredBlock get(final Sha256Hash sha256Hash) throws BlockStoreException {
        if (blocks.containsKey(sha256Hash.toString())) {
            return blocks.get(sha256Hash.toString());
        }
        return blockChainService.get(sha256Hash);
    }

    @Override
    public StoredBlock getChainHead() throws BlockStoreException {
        return blockChainService.getChainHead();
    }

    @Override
    public void setChainHead(final StoredBlock storedBlock) throws BlockStoreException {
        blockChainService.setChainHead(storedBlock);
    }

    @Override
    public void close() throws BlockStoreException {
        //Not needed
    }

    @Override
    public NetworkParameters getParams() {
        return new MainNetParams();
    }

}
