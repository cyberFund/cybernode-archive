package fund.cyber.node.model.bitcore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {
	private String hash;
    private Long size;
    private Long height;
    private Long version;
    private String merkleroot;
    private List<String> tx;
    private Long time;
    private BigInteger nonce;
    private String bits;
    private Double difficulty;
    private String chainwork;
    private Long confirmations;
    private String previousblockhash;
    private String nextblockhash;
    private Double reward;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMerkleroot() {
        return merkleroot;
    }

    public void setMerkleroot(String merkleroot) {
        this.merkleroot = merkleroot;
    }

    public List<String> getTx() {
        return tx;
    }

    public void setTx(List<String> tx) {
        this.tx = tx;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getBits() {
        return bits;
    }

    public void setBits(String bits) {
        this.bits = bits;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }

    public String getChainwork() {
        return chainwork;
    }

    public void setChainwork(String chainwork) {
        this.chainwork = chainwork;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public String getPreviousblockhash() {
        return previousblockhash;
    }

    public void setPreviousblockhash(String previousblockhash) {
        this.previousblockhash = previousblockhash;
    }

    public String getNextblockhash() {
        return nextblockhash;
    }

    public void setNextblockhash(String nextblockhash) {
        this.nextblockhash = nextblockhash;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }
}
