package fund.cyber.node.core.chain;

import fund.cyber.node.core.service.BlockChainService;
import fund.cyber.node.model.dto.StatusDto;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

//@Service
public class DataFetcher implements InitializingBean {

    @Autowired
    private BlockStore blockStore;

    @Autowired
    private BlockChainService blockChainService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private BlockChain chain;

    @Autowired
    private NetworkParameters params;

    @Autowired
    private FileParser fileParser;

    private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

    private PeerGroup peerGroup;
    private Peer firstPeer;

    private boolean blocksFetching = true;
    private boolean transactionsFetching = false;


    @Override
    public void afterPropertiesSet() throws Exception {

        BriefLogFormatter.init();
        System.out.println("Connecting to node");

        peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        final PeerAddress addr = new PeerAddress(InetAddress.getLocalHost(), params.getPort());
        peerGroup.addAddress(addr);
        peerGroup.waitForPeers(1).get();
        firstPeer = peerGroup.getConnectedPeers().get(0);

        firstPeer.startBlockChainDownload();
    }


    public void startBlocksFetch() {
        blocksFetching = true;
        chain.drainOrphanBlocks();
        peerGroup.getDownloadPeer().getPendingBlockDownloads().clear();
        peerGroup.getDownloadPeer().startBlockChainDownload();
    }

    public void stopBlocksFetch() {
        blocksFetching = false;
        peerGroup.getConnectedPeers().forEach(peer -> peer.setDownloadData(false));
    }

    public void startTransactionFetch() {
        fileParser.startReadBlocksFromDisk();
    }

    public void stopTransactionFetch() throws IOException {
        fileParser.stopReadBlocksFromDisk();
    }

    public StatusDto getStatus() {
        final StatusDto status = blockChainService.getStatus();
        status.setBlockFetching(blocksFetching && firstPeer.isDownloadData());
        status.setTransactionFetching(transactionsFetching);
        return status;
    }
}