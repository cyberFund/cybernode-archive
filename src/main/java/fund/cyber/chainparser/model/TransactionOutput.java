package fund.cyber.chainparser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "output")
public class TransactionOutput extends BaseEntity implements Comparable<TransactionOutput> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "value")
    private long value;

    @Column(name = "script_bytes")
    private String scriptBytes;

    @Column(name = "script")
    private String script;

    @Column(name = "script_type")
    private String scriptType;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "position")
    private long position;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="transaction_id")
    private Transaction transaction;

    @OneToOne(mappedBy = "output", fetch = LAZY)
    private TransactionInput input;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public TransactionInput getInput() {
        return input;
    }

    public void setInput(TransactionInput input) {
        this.input = input;
    }

    @Override
    public int compareTo(final TransactionOutput output) {
        return (int)Math.signum(getPosition() - output.getPosition());
    }
}
