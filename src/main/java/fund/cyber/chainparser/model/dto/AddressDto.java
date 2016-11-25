package fund.cyber.chainparser.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AddressDto {

    private String hash;
    private long balance;
    private List<AddressTransactionDto> transactions;

    public AddressDto() {
    }

    public AddressDto(final String hash) {
        this.hash = hash;
        this.transactions = new ArrayList<>();
        this.balance = 0;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<AddressTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AddressTransactionDto> transactions) {
        this.transactions = transactions;
    }
}
