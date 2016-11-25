package fund.cyber.node.core.service;

import fund.cyber.node.core.dao.BlockDao;
import fund.cyber.node.model.Block;
import fund.cyber.node.model.dto.BlockDto;
import org.bitcoinj.core.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.bitcoinj.core.Sha256Hash.wrap;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Component
@Transactional(propagation = REQUIRED)
public class BlockService extends CommonService {

    @Autowired
    private BlockDao blockDao;

    public List<Sha256Hash> getEmptyBlocks() {
        return blockDao.getEmpty().stream().map(block -> wrap(block.getHash())).collect(toList());
    }

    public List<Sha256Hash> getUncompletedBlocks() {
        return blockDao.getUncompleted().stream().map(block -> wrap(block.getHash())).collect(toList());
    }

    public BlockDto get(final String hash) {
        final Block block = blockDao.getByHash(hash);
        return show(block);
    }

    public Integer getEmptyBlockMinHeight() {
        final Block empty = blockDao.getEmptyBlockMinHeight();
        return empty != null ? empty.getHeight() : null;
    }

    public Set<String> getHashesForUpdate() {
        final Block empty = blockDao.getEmptyBlockMinHeight();
        if(empty == null) {
            return new HashSet<>();
        }
        final List<String> list = blockDao.getIncompleteBlockHeightsLowerThan(empty.getHeight());
        return new HashSet<>(list);
    }

    public Integer getMinHeightForUpdate() {
        final Block empty = blockDao.getEmptyBlockMinHeight();
        final Block incomplete = blockDao.getIncompleteBlockMinHeight();
        if (empty == null && incomplete == null) {
            return null;
        }
        return Math.min(empty != null ? empty.getHeight() : Integer.MAX_VALUE, incomplete != null ? incomplete.getHeight() : Integer.MAX_VALUE);
    }

    public Integer getBlockHeight(final Sha256Hash hash) {
        return blockDao.getBlockHeight(hash.toString());
    }
}
