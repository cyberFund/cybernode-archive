package fund.cyber.node.model.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Status DTO to test REST service
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class StatusDto implements Serializable {
    private Calendar time;
    private long headers;
    private long blocks;
    private long transactions;
    private long inputs;
    private long outputs;
    private boolean blockFetching;
    private boolean transactionFetching;
    private String lastIndexedBlock;

    public StatusDto(Calendar time) {
        this.time = time;
    }

    public StatusDto() {
    }

    public Calendar getTime() {
        return this.time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public long getHeaders() {
        return headers;
    }

    public void setHeaders(long headers) {
        this.headers = headers;
    }

    public long getBlocks() {
        return blocks;
    }

    public void setBlocks(long blocks) {
        this.blocks = blocks;
    }

    public long getTransactions() {
        return transactions;
    }

    public void setTransactions(long transactions) {
        this.transactions = transactions;
    }

    public long getInputs() {
        return inputs;
    }

    public void setInputs(long inputs) {
        this.inputs = inputs;
    }

    public long getOutputs() {
        return outputs;
    }

    public void setOutputs(long outputs) {
        this.outputs = outputs;
    }

    public boolean isBlockFetching() {
        return blockFetching;
    }

    public void setBlockFetching(boolean blockFetching) {
        this.blockFetching = blockFetching;
    }

    public boolean isTransactionFetching() {
        return transactionFetching;
    }

    public void setTransactionFetching(boolean transactionFetching) {
        this.transactionFetching = transactionFetching;
    }

    public String getLastIndexedBlock() {
        return lastIndexedBlock;
    }

    public void setLastIndexedBlock(String lastIndexedBlock) {
        this.lastIndexedBlock = lastIndexedBlock;
    }
}
