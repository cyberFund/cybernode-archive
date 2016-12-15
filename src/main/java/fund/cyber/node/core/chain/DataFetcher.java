package fund.cyber.node.core.chain;

import fund.cyber.node.core.service.BitcoreService;
import fund.cyber.node.core.service.BlockDbService;
import fund.cyber.node.model.bitcore.Block;
import fund.cyber.node.model.bitcore.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataFetcher {

    @Autowired
    private BitcoreService bitcoreService;

    @Autowired
    private BlockDbService blockDbService;

    @Autowired
    private TaskExecutor taskExecutor;

    private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

    private boolean blocksFetching = true;

    public void startBlocksFetch() {
        blocksFetching = true;
        taskExecutor.execute(() -> {
            final Block last = blockDbService.getMaxHeightBlock();
            long height = last != null ? last.getHeight()+1 : 0;
            while (blocksFetching) {
                final Header header;
                try {
                    header = bitcoreService.getHeader(height);
                    if (header == null) {
                        return;
                    }
                    final Block block = new Block();
                    block.setHeight(height);
                    block.setHash(header.getBlockHash());
                    blockDbService.putBlock(block);
                    height++;
                } catch (final IOException e) {
                    log.error("BlockFetching error", e);
                    blocksFetching = false;
                }

            }
        });
    }

    public void stopBlocksFetch() {
        blocksFetching = false;
    }

    public Block getStatus() {
        return blockDbService.getMaxHeightBlock();
    }
}