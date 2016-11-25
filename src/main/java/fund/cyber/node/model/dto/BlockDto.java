package fund.cyber.node.model.dto;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

public class BlockDto {

    private long version;
    private String prevBlock;
    private String merkleRoot;
    private Calendar time;
    private long difficultyTarget;
    private long nonce;

    private List<TransactionDto> transactions;

    private String hash;
    private BigInteger chainWork;
    private int height;

    public BlockDto() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getPrevBlock() {
        return prevBlock;
    }

    public void setPrevBlock(String prevBlock) {
        this.prevBlock = prevBlock;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public long getDifficultyTarget() {
        return difficultyTarget;
    }

    public void setDifficultyTarget(long difficultyTarget) {
        this.difficultyTarget = difficultyTarget;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public void setChainWork(BigInteger chainWork) {
        this.chainWork = chainWork;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
