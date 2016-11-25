package fund.cyber.chainparser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "transaction")
public class Transaction extends BaseEntity implements Comparable<Transaction> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "version")
    private long version;

    @OneToMany(mappedBy = "transaction", fetch = LAZY)
    private List<TransactionInput> inputs;

    @OneToMany(mappedBy = "transaction", fetch = LAZY)
    private List<TransactionOutput> outputs;

    @Column(name = "lock")
    private long lockTime;

    @Column(name = "hash")
    private String hash;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @Column(name = "position")
    private long position;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
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

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public int compareTo(Transaction tx) {
        return (int)Math.signum(getPosition() - tx.getPosition());
    }
}
