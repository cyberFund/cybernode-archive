package fund.cyber.node.model.bitcore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tx {
    private String txid;
    private long version;
    private long locktime;
    private String blockhash;
    private long blockheight;
    private long confirmations;
    private long time;
    private long blocktime;
    private boolean isCoinBase;
    private double valueOut;
    private double valueIn;
    private double fees;
    private long size;
    private List<Input> vin;
    private List<Output> vout;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getLocktime() {
        return locktime;
    }

    public void setLocktime(long locktime) {
        this.locktime = locktime;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public long getBlockheight() {
        return blockheight;
    }

    public void setBlockheight(long blockheight) {
        this.blockheight = blockheight;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(long blocktime) {
        this.blocktime = blocktime;
    }

    public boolean isCoinBase() {
        return isCoinBase;
    }

    public void setCoinBase(boolean coinBase) {
        isCoinBase = coinBase;
    }

    public double getValueOut() {
        return valueOut;
    }

    public void setValueOut(double valueOut) {
        this.valueOut = valueOut;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<Input> getVin() {
        return vin;
    }

    public void setVin(List<Input> vin) {
        this.vin = vin;
    }

    public List<Output> getVout() {
        return vout;
    }

    public void setVout(List<Output> vout) {
        this.vout = vout;
    }

    public double getValueIn() {
        return valueIn;
    }

    public void setValueIn(double valueIn) {
        this.valueIn = valueIn;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }
}
