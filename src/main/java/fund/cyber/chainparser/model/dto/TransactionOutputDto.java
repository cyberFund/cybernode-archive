package fund.cyber.chainparser.model.dto;

public class TransactionOutputDto {

    private long value;

    private String address;

    private String scriptBytes;

    private String script;

    private String spentTransaction;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScriptBytes() {
        return scriptBytes;
    }

    public void setScriptBytes(String scriptBytes) {
        this.scriptBytes = scriptBytes;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getSpentTransaction() {
        return spentTransaction;
    }

    public void setSpentTransaction(String spentTransaction) {
        this.spentTransaction = spentTransaction;
    }
}
