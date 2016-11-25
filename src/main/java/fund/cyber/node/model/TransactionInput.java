package fund.cyber.node.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "input")
public class TransactionInput extends BaseEntity implements Comparable<TransactionInput> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "sequence")
    private long sequence;

    @Column(name = "position")
    private long position;

    @Column(name = "script_bytes")
    private String scriptBytes;

    @Column(name = "script")
    private String script;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "output_id")
    private TransactionOutput output;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
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

    public TransactionOutput getOutput() {
        return output;
    }

    public void setOutput(TransactionOutput output) {
        this.output = output;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public int compareTo(final TransactionInput input) {
        return (int)Math.signum(getPosition() - input.getPosition());
    }

}

