package fund.cyber.node.model.dto;

import java.util.List;

public class TransactionDto {

    private long version;
    private List<TransactionInputDto> inputs;
    private List<TransactionOutputDto> outputs;
    private long lockTime;
    private String hash;
    private String block;
    private long inputSum;
    private long fee;
    private long outputSum;

    public TransactionDto() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public long getInputSum() {
        return inputSum;
    }

    public void setInputSum(long inputSum) {
        this.inputSum = inputSum;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public List<TransactionInputDto> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInputDto> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutputDto> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutputDto> outputs) {
        this.outputs = outputs;
    }

    public void setOutputSum(long outputSum) {
        this.outputSum = outputSum;
    }

    public long getOutputSum() {
        return outputSum;
    }
}
