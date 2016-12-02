package fund.cyber.node.model.bitcore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Input {
    private String coinbase;
    private long sequence;
    private long n;
    private String txid;
    private long vout;
    private Script scriptSig;
    private String addr;
    private BigInteger valueSat;
    private double value;
    private String doubleSpentTxID;

    public String getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(String coinbase) {
        this.coinbase = coinbase;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getN() {
        return n;
    }

    public void setN(long n) {
        this.n = n;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public long getVout() {
        return vout;
    }

    public void setVout(long vout) {
        this.vout = vout;
    }

    public Script getScriptSig() {
        return scriptSig;
    }

    public void setScriptSig(Script scriptSig) {
        this.scriptSig = scriptSig;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public BigInteger getValueSat() {
        return valueSat;
    }

    public void setValueSat(BigInteger valueSat) {
        this.valueSat = valueSat;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDoubleSpentTxID() {
        return doubleSpentTxID;
    }

    public void setDoubleSpentTxID(String doubleSpentTxID) {
        this.doubleSpentTxID = doubleSpentTxID;
    }
}
