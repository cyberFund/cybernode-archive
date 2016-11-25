package fund.cyber.node.model;

public class MigrationLog {
    private int id;
    private int hash;

    public MigrationLog() {
    }

    public MigrationLog(int id, int hash) {
        this.id = id;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }
}
