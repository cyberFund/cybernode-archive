package fund.cyber.node.model.bitcore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Output {
    private double value;
    private long n;
    private Script scriptPubKey;
    private String spentTxId;
    private long spentIndex;
    private long spentHeight;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getN() {
        return n;
    }

    public void setN(long n) {
        this.n = n;
    }

    public Script getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(Script scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public String getSpentTxId() {
        return spentTxId;
    }

    public void setSpentTxId(String spentTxId) {
        this.spentTxId = spentTxId;
    }

    public long getSpentIndex() {
        return spentIndex;
    }

    public void setSpentIndex(long spentIndex) {
        this.spentIndex = spentIndex;
    }

    public long getSpentHeight() {
        return spentHeight;
    }

    public void setSpentHeight(long spentHeight) {
        this.spentHeight = spentHeight;
    }
}
