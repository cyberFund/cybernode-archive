package fund.cyber.node.model;

public class MigrationLog {
    private long id;
    private long hash;

    public MigrationLog() {
    }

    public MigrationLog(long id, long hash) {
        this.id = id;
        this.hash = hash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }
}
