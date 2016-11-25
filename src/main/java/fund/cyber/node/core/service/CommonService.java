package fund.cyber.node.core.service;

import fund.cyber.node.model.Block;
import fund.cyber.node.model.Transaction;
import fund.cyber.node.model.TransactionInput;
import fund.cyber.node.model.TransactionOutput;
import fund.cyber.node.model.dto.AddressTransactionDto;
import fund.cyber.node.model.dto.BlockDto;
import fund.cyber.node.model.dto.TransactionDto;
import fund.cyber.node.model.dto.TransactionInputDto;
import fund.cyber.node.model.dto.TransactionOutputDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public abstract class CommonService {

    public BlockDto show(final Block block) {
        final BlockDto blockDto = new BlockDto();
        blockDto.setVersion(block.getVersion());
        blockDto.setPrevBlock(block.getPrevBlock().getHash());
        blockDto.setMerkleRoot(block.getMerkleRoot());
        blockDto.setTime(Calendar.getInstance());
        blockDto.getTime().setTimeInMillis(block.getTime());
        blockDto.setDifficultyTarget(block.getDifficulty());
        blockDto.setNonce(block.getNonce());
        blockDto.setHash(block.getHash());
        blockDto.setChainWork(block.getChainwork());
        blockDto.setHeight(block.getHeight());
        if (block.getTransactions() != null) {
            blockDto.setTransactions(block.getTransactions().stream().map(this::show).collect(Collectors.toList()));
        } else {
            blockDto.setTransactions(new ArrayList<>());
        }
        return blockDto;
    }
    
    protected TransactionDto show(final Transaction tx) {
        final TransactionDto txDto = new TransactionDto();
        return showInternal(txDto, tx);
    }

    protected AddressTransactionDto show(final Transaction tx, final String address) {
        AddressTransactionDto txDto = new AddressTransactionDto();
        txDto = showInternal(txDto, tx);
        final long effect = txDto.getInputs().stream().map(input -> input.getAddress() != null && input.getAddress().equals(address) ? -input.getValue() : 0).reduce(0L, (a, b) -> a + b) +
                txDto.getOutputs().stream().map(output -> output.getAddress() != null && output.getAddress().equals(address) ? output.getValue() : 0).reduce(0L, (a, b) -> a + b);
        txDto.setEffect(effect);
        return txDto;
    }

    private <T extends TransactionDto> T showInternal(final T txDto, final Transaction tx) {
        txDto.setHash(tx.getHash());
        txDto.setBlock(tx.getBlock().getHash());
        txDto.setLockTime(tx.getLockTime());
        txDto.setVersion(tx.getVersion());
        txDto.setInputs(tx.getInputs().stream().map(this::show).collect(Collectors.toList()));
        txDto.setOutputs(tx.getOutputs().stream().map(this::show).collect(Collectors.toList()));
        txDto.setInputSum(txDto.getInputs().stream().map(TransactionInputDto::getValue).reduce(0L, (a, b) -> a + b));
        txDto.setOutputSum(txDto.getOutputs().stream().map(TransactionOutputDto::getValue).reduce(0L, (a, b) -> a + b));
        txDto.setFee(txDto.getInputSum() - txDto.getOutputSum());
        return txDto;
    }

    protected TransactionInputDto show(final TransactionInput input) {
        final TransactionInputDto inputDto = new TransactionInputDto();
        if (input.getOutput() != null) {
            inputDto.setAddress(input.getOutput().getAddress().getHash());
            inputDto.setValue(input.getOutput().getValue());
            inputDto.setOutputTransaction(input.getOutput().getTransaction().getHash());
        }
        inputDto.setScript(input.getScript());
        inputDto.setScriptBytes(input.getScriptBytes());
        return inputDto;
    }

    protected TransactionOutputDto show(final TransactionOutput output) {
        final TransactionOutputDto outputDto = new TransactionOutputDto();
        if (output.getAddress() != null) {
            outputDto.setAddress(output.getAddress().getHash());
        }
        outputDto.setValue(output.getValue());
        if (output.getInput() != null) {
            outputDto.setSpentTransaction(output.getInput().getTransaction().getHash());
        }
        outputDto.setScript(output.getScript());
        outputDto.setScriptBytes(output.getScriptBytes());
        return outputDto;
    }

}
