package fund.cyber.node.model.dto;

public class AddressTransactionDto extends TransactionDto {
    private long effect;

    public long getEffect() {
        return effect;
    }

    public void setEffect(long effect) {
        this.effect = effect;
    }
}
