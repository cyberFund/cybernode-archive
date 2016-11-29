package fund.cyber.node.model;

public class MigrationLog {
    private Long id;
    private Long hash;

    public MigrationLog() {
    }

    public MigrationLog(Long id, Long hash) {
        this.id = id;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHash() {
        return hash;
    }

    public void setHash(Long hash) {
        this.hash = hash;
    }
}
