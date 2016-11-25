package fund.cyber.chainparser.core.service;

import com.sun.javaws.exceptions.InvalidArgumentException;
import fund.cyber.chainparser.core.dao.AddressDao;
import fund.cyber.chainparser.core.dao.BlockDao;
import fund.cyber.chainparser.core.dao.TransactionDao;
import fund.cyber.chainparser.core.dao.TransactionInputDao;
import fund.cyber.chainparser.core.dao.TransactionOutputDao;
import fund.cyber.chainparser.model.Address;
import fund.cyber.chainparser.model.Block;
import fund.cyber.chainparser.model.Transaction;
import fund.cyber.chainparser.model.TransactionInput;
import fund.cyber.chainparser.model.TransactionOutput;
import fund.cyber.chainparser.model.dto.StatusDto;
import org.apache.commons.lang.StringUtils;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.bitcoinj.core.Sha256Hash.wrap;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Component
@Transactional(propagation = REQUIRED)
public class BlockChainService extends CommonService {

    private static final Logger log = LoggerFactory.getLogger(BlockChainService.class);

    @Autowired
    private BlockDao blockDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionInputDao transactionInputDao;

    @Autowired
    private TransactionOutputDao transactionOutputDao;

    @Autowired
    private AddressDao addressDao;

    private final NetworkParameters params = new MainNetParams();

    public StoredBlock get(final Sha256Hash sha256Hash) {
        if (sha256Hash == null) {
            return null;
        }
        final Block block = blockDao.getByHash(sha256Hash.toString());
        return construct(block);
    }

    public StoredBlock getChainHead() {
        final Block block = blockDao.getChainHead();
        return construct(block);
    }

    public boolean setChainHead(final StoredBlock chainHead) {
        final Block block = blockDao.getByHash(chainHead.getHeader().getHash().toString());
        if (block == null) {
            return false;
        }
        block.setChainhead(true);

        final List<Block> blocks = blockDao.getAbove(block.getHeight());
        for (final Block blk: blocks) {
            blk.setChainhead(false);
        }
        return true;

    }

    private StoredBlock construct(final Block block) {
        return construct(block, false);
    }

    private StoredBlock construct(final Block block, boolean brief) {
        if (block == null) {
            return null;
        }

        final List<org.bitcoinj.core.Transaction> transactions = new ArrayList<>();
        if (!brief) {
            block.getTransactions().sort(null);
            for (final Transaction tx : block.getTransactions()) {
                final org.bitcoinj.core.Transaction transaction = setVersion(new org.bitcoinj.core.Transaction(params), tx.getVersion());
                transaction.setLockTime(tx.getLockTime());
                tx.getInputs().sort(null);
                tx.getInputs().forEach(input -> transaction.addInput(convert(transaction, input)));
                tx.getOutputs().sort(null);
                tx.getOutputs().forEach(output -> transaction.addOutput(convert(transaction, output)));
                transactions.add(transaction);
            }
        }
        final Sha256Hash prev = wrap(block.getPrevBlock() != null ? block.getPrevBlock().getHash() : StringUtils.repeat("0", 64));

        final org.bitcoinj.core.Block header = new org.bitcoinj.core.Block(params, block.getVersion(),
                prev, wrap(block.getMerkleRoot()), block.getTime(),
                block.getDifficulty(), block.getNonce(), transactions);

        return new StoredBlock(header, block.getChainwork(), block.getHeight());
    }

    private org.bitcoinj.core.Transaction setVersion(final org.bitcoinj.core.Transaction transaction, final long version) {
        final NetworkParameters params = transaction.getParams();
        final byte[] bytes = transaction.bitcoinSerialize();
        Utils.uint32ToByteArrayLE(version, bytes, 0);
        return new org.bitcoinj.core.Transaction(params, bytes);
    }

    private org.bitcoinj.core.TransactionOutput convert(final org.bitcoinj.core.Transaction transaction, final TransactionOutput output) {
        final Coin value = Coin.valueOf(output.getValue());
        final byte[] scriptBytes = Utils.HEX.decode(output.getScriptBytes());
        return new org.bitcoinj.core.TransactionOutput(params, transaction, value, scriptBytes);
    }

    private org.bitcoinj.core.TransactionInput convert(final org.bitcoinj.core.Transaction transaction, final TransactionInput input) {
        final TransactionOutPoint outPoint;
        if (input.getOutput() != null) {
            outPoint = new TransactionOutPoint(params, input.getOutput().getPosition(), wrap(input.getOutput().getTransaction().getHash()));
        } else {
            outPoint = new TransactionOutPoint(params, 0xFFFFFFFFL, Sha256Hash.ZERO_HASH);
        }
        return new org.bitcoinj.core.TransactionInput(params, transaction, Utils.HEX.decode(input.getScriptBytes()), outPoint);
    }

    public void put(final StoredBlock storedBlock) {
        final Block stored = blockDao.getByHash(storedBlock.getHeader().getHash().toString());
        final Block block;
        if (stored != null) {
            block = stored;
        } else {
            block = convert(storedBlock);
            blockDao.persist(block);
        }
        if (storedBlock.getHeader().getTransactions() == null) {
            return;
        }
        addTransactions(block, storedBlock.getHeader().getTransactions());
        block.setIndexed(true);
    }

    public void update(final org.bitcoinj.core.Block newBlock) {
        final Block oldBlock = blockDao.getByHash(newBlock.getHash().toString());
        if (oldBlock == null || oldBlock.isIndexed()) {
            return;
        }
        if (oldBlock.getTransactions() == null || oldBlock.getTransactions().size() == 0) {
            addTransactions(oldBlock, newBlock.getTransactions());
        } else {
            updateTransactions(oldBlock.getTransactions(), newBlock.getTransactions());
        }
        oldBlock.setIndexed(true);
        blockDao.persist(oldBlock);
        //FIXME
        transactionInputDao.flush();
        transactionInputDao.clear();
        transactionOutputDao.flush();
        transactionOutputDao.clear();
        transactionDao.flush();
        transactionDao.clear();
        addressDao.flush();
        addressDao.clear();
        blockDao.flush();
        blockDao.clear();
    }

    private void addTransactions(final Block block, final List<org.bitcoinj.core.Transaction> transactions) {
        for (int i = 0; i < transactions.size(); i++) {
            final org.bitcoinj.core.Transaction transaction = transactions.get(i);
            final Transaction tx = convert(transaction);
            block.getTransactions().add(tx);
            tx.setBlock(block);
            tx.setPosition(i);
            transactionDao.persist(tx);
            addInputs(tx, transaction.getInputs());
            addOutputs(tx, transaction.getOutputs());

            //FIXME
            transactionInputDao.flush();
            transactionInputDao.clear();
            transactionOutputDao.flush();
            transactionOutputDao.clear();
            transactionDao.flush();
            transactionDao.clear();
            addressDao.flush();
            addressDao.clear();
        }
    }

    private void updateTransactions(final List<Transaction> oldTxs, final List<org.bitcoinj.core.Transaction> newTxs) {
        for (final org.bitcoinj.core.Transaction newTx : newTxs) {
            final String hash = newTx.getHash().toString();
            final Optional<Transaction> oldTx = oldTxs.stream().filter(tx -> tx.getHash().equals(hash)).findFirst();
            if (!oldTx.isPresent()) {
                log.error("Can not found tx: " + hash);
                continue;
            }
            updateInputs(newTx, oldTx.get());
        }
    }

    private void addInputs(final Transaction transaction, final List<org.bitcoinj.core.TransactionInput> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            final org.bitcoinj.core.TransactionInput transactionInput = inputs.get(i);
            final TransactionInput input = convert(transactionInput);
            transaction.getInputs().add(input);
            input.setTransaction(transaction);
            input.setPosition(i);
            transactionInputDao.persist(input);
        }
    }

    private void updateInputs(final org.bitcoinj.core.Transaction newTx, final Transaction storedTx) {
        for (int i = 0; i < newTx.getInputs().size(); i++) {
            final org.bitcoinj.core.TransactionInput newInput = newTx.getInputs().get(i);
            final String hash = newInput.getOutpoint().getHash().toString();
            final long position = (long)i;
            final Optional<TransactionInput> inputToUpdate = storedTx.getInputs().stream().filter(oldInput -> oldInput.getPosition() == position).findFirst();
            if (!inputToUpdate.isPresent()) {
                log.error("Can not found input: " + hash + "," + position);
                continue;
            }
            setInputOutpoint(inputToUpdate.get(), newInput.getOutpoint());
        }
    }

    private void addOutputs(final Transaction transaction, final List<org.bitcoinj.core.TransactionOutput> outputs) {
        for (int i = 0; i < outputs.size(); i++) {
            final org.bitcoinj.core.TransactionOutput transactionOutput = outputs.get(i);
            final TransactionOutput output = convert(transactionOutput);
            transaction.getOutputs().add(output);
            output.setTransaction(transaction);
            output.setPosition(i);
            transactionOutputDao.persist(output);
        }
    }

    private TransactionInput convert(final org.bitcoinj.core.TransactionInput transactionInput) {
        final TransactionInput input = new TransactionInput();
        input.setScriptBytes(Utils.HEX.encode(transactionInput.getScriptBytes()));
        try {
            input.setScript(transactionInput.getScriptSig().toString());
        } catch (final ScriptException se) {
            log.warn("Output script error: " + Utils.HEX.encode(transactionInput.getScriptBytes()) + ", tx: " + transactionInput.getParentTransaction().getHash());
        }
        input.setSequence(transactionInput.getSequenceNumber());
        setInputOutpoint(input, transactionInput.getOutpoint());
        return input;
    }

    private void setInputOutpoint(final TransactionInput input, final TransactionOutPoint outpoint) {
        final String hash = outpoint.getHash().toString();
        final long index = outpoint.getIndex();
        if (hash.equals(StringUtils.repeat("0", 64))) {
            return;
        }
        final Transaction tx = transactionDao.getByHash(hash);
        if (tx == null) {
            return;
        }
        /*
        if (tx == null) {
            tx = new Transaction();
            tx.setOutputs(new ArrayList<>());
            tx.setHash(hash);
            transactionDao.persist(tx);
        }
        */
        final TransactionOutput output = transactionOutputDao.get(tx, index);
        /*
        if (output == null) {
            output = new TransactionOutput();
            output.setTransaction(tx);
            output.setPosition(index);
            transactionOutputDao.persist(output);
            tx.getOutputs().add(output);
        }
        */
        input.setOutput(output);
    }

    private TransactionOutput convert(final org.bitcoinj.core.TransactionOutput transactionOutput) {

        final TransactionOutput output = new TransactionOutput();
        output.setScriptBytes(Utils.HEX.encode(transactionOutput.getScriptBytes()));
        try {
            output.setScript(transactionOutput.getScriptPubKey().toString());
        } catch (ScriptException e) {
            log.warn("Output script error: " + Utils.HEX.encode(transactionOutput.getScriptBytes()) + ", tx: " + transactionOutput.getParentTransaction().getHash());
        }

        output.setValue(transactionOutput.getValue().longValue());
        output.setScriptType(transactionOutput.getScriptPubKey().getScriptType().toString());
        try {
            final String hash = transactionOutput.getScriptPubKey().getToAddress(params, true).toString();
            Address address = addressDao.getByHash(hash);
            if (address == null) {
                address = new Address(hash);
                addressDao.persist(address);
            }
            output.setAddress(address);
        } catch (final IllegalArgumentException | ScriptException se) {
            log.warn("Can not convert to address");
        }
        return output;
    }

    private Transaction convert(final org.bitcoinj.core.Transaction transaction) {
        final Transaction tx = new Transaction();
        tx.setInputs(new ArrayList<>());
        tx.setOutputs(new ArrayList<>());
        tx.setHash(transaction.getHash().toString());
        tx.setVersion(transaction.getVersion());
        tx.setLockTime(transaction.getLockTime());
        tx.setInputs(new ArrayList<>());
        tx.setOutputs(new ArrayList<>());
        return tx;
    }

    private Block convert(final StoredBlock storedBlock) {
        final Block block = new Block();
        block.setDifficulty(storedBlock.getHeader().getDifficultyTarget());
        block.setHash(storedBlock.getHeader().getHash().toString());
        block.setMerkleRoot(storedBlock.getHeader().getMerkleRoot().toString());
        block.setNonce(storedBlock.getHeader().getNonce());
        block.setTime(storedBlock.getHeader().getTimeSeconds());
        block.setVersion(storedBlock.getHeader().getVersion());
        block.setHeight(storedBlock.getHeight());
        block.setChainwork(storedBlock.getChainWork());
        block.setTransactions(new ArrayList<>());
        final Block prev = blockDao.getByHash(storedBlock.getHeader().getPrevBlockHash().toString());
        block.setPrevBlock(prev);
        return block;
    }

    public Map<String, StoredBlock> getAllBlocks() {
        return blockDao.getAll().stream().collect(Collectors.toMap(Block::getHash, block -> construct(block, true)));
    }

    public StatusDto getStatus() {
        final StatusDto status = new StatusDto(Calendar.getInstance());
        status.setHeaders(blockDao.count());
        status.setBlocks(blockDao.countIndexed());
        //FIXME counts works slow
        status.setTransactions(transactionDao.count());
        status.setInputs(transactionInputDao.count());
        status.setOutputs(transactionOutputDao.count());
        final Block block = blockDao.getLastIndexedBlock();
        if (block != null) {
            status.setLastIndexedBlock(block.getHash());
        }
        return status;
    }
}
